/*
 * Copyright 2015 The UIMaster Project
 *
 * The UIMaster Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.shaolin.bmdp.workflow.ws;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.shaolin.bmdp.json.JSONObject;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.bmdp.utils.HttpSender;
import org.shaolin.bmdp.workflow.be.INotification;
import org.shaolin.bmdp.workflow.coordinator.ICoordinatorService;
import org.shaolin.bmdp.workflow.internal.CoordinatorServiceImpl;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.od.formats.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * We used Node.js for better performance.
 * 
 * @author wushaol
 */
@ServerEndpoint("/wsnotificator")
public class NotificationService {

	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	private static final ConcurrentHashMap<String, Session> sessoins = new ConcurrentHashMap<String, Session>();
	
	public NotificationService() {
	}
	
	@OnOpen
	public void onOpen(final Session session, EndpointConfig config) {
		if (logger.isDebugEnabled()) { 
			logger.debug("{0} Client connected.", session.getId());
		}
		sessoins.put(session.getId(), session);
		
		CoordinatorServiceImpl.getScheduler().schedule(new Runnable(){
			public void run() {
				push(session);
			}
		}, 2, TimeUnit.SECONDS);
		
	}

	@OnClose
	public void onClose(Session session) {
		if (logger.isDebugEnabled()) { 
			logger.debug("{0} Client closed.", session.getId());
		}
		sessoins.remove(session.getId());
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		sessoins.remove(session.getId());
		logger.debug("Client error: " + session.getId(), throwable);
	}

	@OnMessage
	public void onMessage(String jsonMsg, Session session) throws IOException,
			InterruptedException {
		if (logger.isDebugEnabled()) { 
			logger.info("Received a message: {0}", jsonMsg);
		}
		try {
			AppContext.register(IServerServiceManager.INSTANCE);
			
			JSONObject data = new JSONObject(jsonMsg);
			String action = data.getString("action");
			
			if ("register".equals(action)) {
				session.getUserProperties().put("partyId", data.getLong("partyId"));
				session.getBasicRemote().sendText("_register_confirmed");
			} else if ("push".equals(action) && data.get("userId") != null) {
				push(findSession(data.getLong("userId")));
			}
		} catch (Exception e) {
			logger.warn("Parsing client data error! session id: " + session, e);
		}
	}
	
	private void push(final Session session) {
		if (session != null && !session.getUserProperties().containsKey("partyId")) {
			return;
		}
		Long userId = (Long)session.getUserProperties().get("partyId");
		Date queryDate = null;
		if (session.getUserProperties().containsKey("LastQueryDate")) {
			queryDate = (Date)session.getUserProperties().get("LastQueryDate");
		}
		session.getUserProperties().put("LastQueryDate", new Date());
		ICoordinatorService service = (ICoordinatorService)
				IServerServiceManager.INSTANCE.getService(ICoordinatorService.class);
		List<INotification> items = service.pullNotifications(userId, queryDate);
		
		for (INotification item : items) {
			StringBuilder sb = new StringBuilder();
			sb.append("<div class=\"uimaster_noti_item\"><div style=\"color:blue;\">");
			sb.append("[").append(item.getCreateDate()).append("] ").append(item.getSubject());
			sb.append("</div><div style=\"width:100%;\">");
			sb.append(item.getDescription()).append("</div></div>");
			try {
				session.getBasicRemote().sendText(sb.toString());
			} catch (IOException e) {
				logger.warn("Error sending the notifications! session id: " + session, e);
			}
		}
	}
	
	private static HttpSender sender = new HttpSender();
	
	/**
	 * push for node.js
	 * 
	 * @param message
	 * @param partyId
	 * @return
	 */
	public static boolean push(INotification message, long partyId) {
//		Original solution.
//		Session session = findSession(userId);
//		if (session == null) {
//			return false;
//		}
		try {
			JSONObject json = new JSONObject();
			if (message.getPartyId() == 0 && message.getOrgId() == 0) {
				json.put("toAll", true);
			} else {
				json.put("partyId", message.getPartyId());
			}
			json.put("DESCRIPTION", message.getDescription());
			json.put("CREATEDATE", FormatUtil.convertDataToUI(FormatUtil.DATE_TIME, message.getCreateDate(), null, null));
			json.put("latitude", message.getLatitude());
			json.put("longitude", message.getLongitude());
			json.put("partyType", message.getPartyType());
			
			String websocketServer = WebConfig.getWebSocketServer();
			if (websocketServer == null || websocketServer.trim().length() == 0) {
				logger.info("Websocket server is not configured! print locally: " + json.toString());
				return true;
			}
			if (websocketServer.startsWith("https")) {
				sender.doPostSSLWithJson(websocketServer + "/uimaster/notify", json.toString(), "utf-8");
			} else {
				sender.postJson(websocketServer + "/uimaster/notify", json.toString());
			}
//			Original solution.
//			session.getBasicRemote().sendText(sb.toString());
		} catch (Exception e) {
			logger.warn("Error occurred while sending the notification!", e);
			return false;
		}
		return true;
	}
	
	private static Session findSession(long userId) {
		Enumeration<Session> elements = sessoins.elements();
		while (elements.hasMoreElements()) {
			Session session = elements.nextElement();
			Object key = session.getUserProperties().get("partyId");
			if (key != null && (Long)key == userId) {
				return session;
			}
		}
		return null;
	}
}
