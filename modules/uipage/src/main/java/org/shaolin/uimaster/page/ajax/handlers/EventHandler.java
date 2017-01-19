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
package org.shaolin.uimaster.page.ajax.handlers;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.page.OpCallAjaxType;
import org.shaolin.bmdp.datamodel.page.OpInvokeWorkflowType;
import org.shaolin.bmdp.datamodel.page.OpType;
import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.spi.EventProcessor;
import org.shaolin.bmdp.runtime.spi.FlowEvent;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.ajax.Button;
import org.shaolin.uimaster.page.ajax.Dialog;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.cache.UIPageObject;

public class EventHandler implements IAjaxHandler {
	private static final String UI_INACTION_FLAG = "_uiinactionflag";
	private static Logger log = Logger.getLogger(EventHandler.class);
	public static final String AJAX_ACTION_NAME = "_actionName";

	@Override
	public String trigger(AjaxContext context) throws AjaxHandlerException {
		String actionName = context.getRequest().getParameter("_actionName");
		if ((actionName == null) || (actionName.length() == 0)) {
			throw new AjaxHandlerException(
					"The action name can not be empty!");
		}
		try {
			if (context.getRequest().getSession().getAttribute(UI_INACTION_FLAG) != null) {
				Dialog.showMessageDialog("\u5DF2\u5904\u7406\u4E2D\uFF0C\u8BF7\u7A0D\u7B49\u3002", "", Dialog.WARNING_MESSAGE, null);
				return context.getDataAsJSON();
			}
			context.getRequest().getSession().setAttribute(UI_INACTION_FLAG, "true");
			return trigger0(context, actionName);
		} finally {
			context.getRequest().getSession().removeAttribute(UI_INACTION_FLAG);
		}
	}
	
	public String trigger0(AjaxContext context, String actionName)
			throws AjaxHandlerException {
		Widget w = context.getElement(context.getRequestData().getUiid());
		if (w == null) {
			Dialog.showMessageDialog("\u4E8B\u4EF6\u6E90\u4E0D\u5B58\u5728\uFF01", "", Dialog.WARNING_MESSAGE, null);
			return context.getDataAsJSON();
		}
		//TODO: prevent duplication click.
		if (w.getClass() == Button.class) {
			context.setEventSource((Button)w);
		}
		if (!w.isVisible()) {
			return "{'value': 'event source does not have privilege.'}"; 
		}
		if (w.getAttribute("disabled") != null && "true".equals(w.getAttribute("disabled"))) {
			return "{'value': 'event source does not have privilege.'}"; 
		}
		if (log.isDebugEnabled()) {
			log.debug("executing the function of ajax call: " + actionName);
		}
		try {
		List<OpType> ops = null;
		Object value = null;
		if (PageCacheManager.isUIPage(context.getEntityName())) {
			UIPageObject uipage = PageCacheManager.getUIPageObject(context.getEntityName());
			ops = uipage.getUIForm().getEventHandler(actionName);
		} else {
			UIFormObject uiEntity = PageCacheManager.getUIFormObject(context.getEntityName());
			ops = uiEntity.getEventHandler(actionName);
		}
		if (ops == null) {
			log.warn("The action name " + actionName + " can't be found from current page!");
			return "";
		}
		for (OpType op : ops) {
				if (op instanceof OpCallAjaxType) {
					OpCallAjaxType callAjaxOp = (OpCallAjaxType) op;
					try {
						value = callAjaxOp.getExp().evaluate(context);
					} catch (EvaluationException ex) {
						if (context.isInvalidEventSource()) {
							break;
						}
						log.warn("This statement can not be evaluated: \n"+ callAjaxOp.getExp().getExpressionString(), ex);
						throw ex;
					}
				} else if (op instanceof OpInvokeWorkflowType) {
					OpInvokeWorkflowType wfOp = (OpInvokeWorkflowType) op;
					try {
						FlowEvent e = new FlowEvent(wfOp.getEventConsumer());
						//BuiltInAttributeConstant.KEY_AdhocNodeName
						e.setAttribute("_AdhocNodeName", wfOp.getAdhocNodeName());
						Object obj = wfOp.getExpression().evaluate(context);
						if (obj instanceof Map) {
							Map map = (Map)obj;
							if (map.size() > 0) {
								Iterator i = map.keySet().iterator();
								while (i.hasNext()) {
									String key = (String)i.next();
									Object v = map.get(key);
									if (v instanceof Serializable) {
										e.setAttribute(key, (Serializable)v);
									} else {
										log.warn("Variable " + key + " is not seriablizable.");
									}
								}
								e.setComments(context.getRequest().getParameter("_comments"));
								EventProcessor processor = (EventProcessor)AppContext.get().getService(
										Class.forName("org.shaolin.bmdp.workflow.internal.WorkFlowEventProcessor"));
								processor.process(e);
							}
						} else {
							//if (obj instanceof Boolean)
							if (log.isDebugEnabled()) {
								log.debug("Workflow action failed result: " + obj);
							}
						}
					} catch (EvaluationException ex) {
						if (context.isInvalidEventSource()) {
							break;
						}
						log.warn("This statement can not be evaluated: \n"+ wfOp.getExpression().getExpressionString(), ex);
						throw ex;
					}
				}
		}

		context.synchVariables();
		if (w.getClass() == Button.class) {
			if (context.isInvalidEventSource()) {
				//((Button)w).setValue(((Button)w).getValue() + " Invalid!");
			} else {
				((Button)w).setAsEnabled();
			}
		}
		if ((value != null) && (value.getClass() != Void.class)) {
			// if value is not null, return directly.
			if (log.isDebugEnabled()) {
				log.debug("evaluate result: " + value.toString());
			}
			if (((value instanceof Number)) || ((value instanceof String))
					|| ((value instanceof Boolean))) {
				return "{'value': '"+value.toString()+"'}";
			} else if ((value instanceof List)) {
				JSONArray json = new JSONArray((List) value);
				return json.toString();
			} else if (value instanceof JSONArray) {
				return ((JSONArray)value).toString();
			} else if (value instanceof JSONObject) {
				return ((JSONObject)value).toString();
			} else {
				JSONObject json = new JSONObject(value);
				return json.toString();
			}
		} else {
			if (log.isDebugEnabled()) {
				context.printUiMap();
			}
			if (context.itemSize() == 0) {
				// at least needs one.
				context.executeJavaScript("");
			}
			return context.getDataAsJSON();
		}
		} catch (Throwable ex) {
			String message = "Ajax executor has interrupted when execute " + actionName
					+ " ajax calling: " + ex.getMessage();
			throw new AjaxHandlerException(message, ex);
		} 
	}

}
