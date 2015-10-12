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
package org.shaolin.uimaster.page;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.shaolin.bmdp.persistence.HibernateUtil;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.ajax.handlers.AjaxHandlerException;
import org.shaolin.uimaster.page.ajax.handlers.CheckPropertyHandler;
import org.shaolin.uimaster.page.ajax.handlers.EventHandler;
import org.shaolin.uimaster.page.ajax.handlers.IAjaxHandler;
import org.shaolin.uimaster.page.ajax.handlers.PropertyChangeHandler;
import org.shaolin.uimaster.page.ajax.handlers.TabPaneEventHandler;
import org.shaolin.uimaster.page.ajax.handlers.TableEventHandler;
import org.shaolin.uimaster.page.ajax.handlers.WebServiceHandler;
import org.shaolin.uimaster.page.ajax.json.IRequestData;
import org.shaolin.uimaster.page.ajax.json.JSONException;
import org.shaolin.uimaster.page.ajax.json.JSONObject;
import org.shaolin.uimaster.page.exception.AjaxException;
import org.shaolin.uimaster.page.exception.AjaxInitializedException;

/**
 * ajax processor.
 * 
 */
public class AjaxProcessor implements Serializable
{

    private static final long serialVersionUID = -1744731434456233557L;

    private static final Logger log = Logger.getLogger(AjaxProcessor.class);

    public static final String EVENT_TYPE_FUNCTION = "true";

    public static final String EVENT_TYPE_PROPERTY_CHANGE = "false";
    
    public static final String EVENT_TYPE_CHECK_PROPERTY = "check";
    
    public static final String EVENT_TYPE_TABLE_PROPERTY = "table";
    
    public static final String EVENT_TYPE_TABPANE_PROPERTY = "tabpane";
    
    public static final String EVENT_WEBSERVICE = "webservice";
    
    
    /**
     * current fired event type.
     */
    private String eventType;

    private final AjaxContext context;

    public AjaxProcessor(HTMLSnapshotContext htmlContext) throws AjaxInitializedException
    {
        this.context = createAjaxContext(htmlContext);
        AjaxActionHelper.createAjaxContext(this.context);
        
        updateParam(htmlContext);
    }

    private IRequestData getRequestData(HttpServletRequest request) throws AjaxInitializedException
    {
        eventType = request.getParameter(AjaxContext.AJAX_USER_EVENT);
        IRequestData requestData = AjaxActionHelper.createRequestData();

        String framePrefix = request.getParameter(AjaxContext.AJAX_FRAME_PREFIX);
        if(framePrefix == null || framePrefix.equals("null"))
        {
        	log.warn("The 'framePrefix' equals null, please noticed if the current uipage has one more frames!");
        }
        framePrefix = (framePrefix == null || framePrefix.equals("null")) ? "" : framePrefix;
        if (EVENT_WEBSERVICE.equals(eventType)) {
        } else {
	        String uiid = request.getParameter(AjaxContext.AJAX_UIID);
	        if (uiid == null || uiid.trim().length() == 0)
	        {
	            throw new AjaxInitializedException("The uiid can not be empty!");
	        }
	        requestData.setUiid(uiid);

	        String entityName = request.getParameter(AjaxContext.AJAX_ACTION_PAGE);
	        entityName = (entityName == null || entityName.equals("null")) ? "" : entityName;
	        String entityUiid = "";
	        int lastPosition = uiid.lastIndexOf(".");
	        if (lastPosition != -1)
	        {
	            entityUiid = uiid.substring(0, lastPosition);
	        }
	        requestData.setEntityUiid(entityUiid);
	        requestData.setEntityName(entityName);
        }
        requestData.setFrameId(framePrefix);
        return requestData;
    }

    private AjaxContext createAjaxContext(HTMLSnapshotContext htmlContext)
            throws AjaxInitializedException
    {
        HttpServletRequest request = htmlContext.getRequest();
        IRequestData requestData = getRequestData(request);
        try
        {
            AjaxContext context;
            if (EVENT_TYPE_CHECK_PROPERTY.equals(eventType))
            {
            	Map uiMap = AjaxActionHelper.getFrameMap(request);
                context = new AjaxContext(uiMap, requestData);
            } 
            else if (EVENT_WEBSERVICE.equals(eventType))
            {
                context = new AjaxContext(new HashMap(), requestData);
                context.initData();
            }
            else
            {
                if (requestData.getEntityUiid().length() > 0)
                {
                    htmlContext.setHTMLPrefix(requestData.getEntityUiid() + ".");
                }
                else
                {
                    htmlContext.setHTMLPrefix("");
                }
                Map uiMap = AjaxActionHelper.getFrameMap(request);
                Widget comp = (Widget)uiMap.get(requestData.getUiid());
                if (comp == null)
                    throw new AjaxInitializedException("Can not find this component["
                            + requestData.getUiid() + "] in the UI map!");
                if (comp.getUIEntityName() == null)
                {
                    Widget entityComp = (Widget)uiMap.get(requestData.getEntityUiid());
                    if (entityComp == null)
                    {
                        entityComp = (Widget)uiMap.get("Form");
                    }
                    comp.setUIEntityName(entityComp.getUIEntityName());
                }
                String entityName = comp.getUIEntityName();
                if (EVENT_TYPE_FUNCTION.equals(eventType))
                {
                    entityName = requestData.getEntityName();
                }
                requestData.setEntityName(entityName);
                context = new AjaxContext(uiMap,requestData);
                context.initData();
            }
            return context;
        }
        catch (EvaluationException e)
        {
            throw new AjaxInitializedException("Fail to load uiid[" + requestData.getUiid()
                    + "], exception cause: " + e.getMessage());
        }
    }

    /**
     * @param htmlContext
     * @throws AjaxInitializedException
     */
    private void updateParam(HTMLSnapshotContext htmlContext) throws AjaxInitializedException
    {
        IRequestData requestData = this.context.getRequestData();
        HttpServletRequest request = htmlContext.getRequest();
        
        if (EVENT_TYPE_CHECK_PROPERTY.equals(eventType)) 
        {
            this.context.setHttpRequest(request);
        }
        else
        {
            try
            {
                this.context.setRequest(request, htmlContext.getResponse());
            }
            catch (EvaluationException e1)
            {
                throw new AjaxInitializedException(e1.getMessage(), e1);
            }
        }

        String data = request.getParameter(AjaxContext.AJAX_DATA);
        if (requestData.getData() == null)
        {
            Map params = new HashMap();
            requestData.setData(params);
        }
        if (data != null && data.length() > 0)
        {
            try
            {
                JSONObject json = new JSONObject(data);
                String[] keys = JSONObject.getNames(json);
                for (int i = 0; i < keys.length; i++)
                {
                    requestData.getData().put(keys[i], json.get(keys[i]));
                }
            }
            catch (JSONException e)
            {
                log.warn("Parse string[" + data + "] to JSON object exception!", e);
                requestData.getData().put("data", data);
            }
        }
    }

    /**
     * Execute event types:
     * <p>1.EVENT_TYPE_FUNCTION: UI function.</p> 
     * <p>2.EVENT_TYPE_PROPERTY_CHANGE: property change.</p>
     * <p>3.EVENT_TYPE_OBJECTLIST: object list notification.</p>
     * <p>4.EVENT_TYPE_BA: call BA.</p>
     * <p>5.EVENT_TYPE_CHECK_PROPERTY: check property</p>
     * 
     * @return JSON or user-defined string
     * @throws AjaxException
     */
    public String execute() throws AjaxException
    {
    	boolean errorFlag = false;
    	Session session = HibernateUtil.getSession();
        try
        {
            IAjaxHandler handler = null;
            if (EVENT_TYPE_PROPERTY_CHANGE.equals(eventType))
            {
                handler = new PropertyChangeHandler();
            }
            else if (EVENT_TYPE_CHECK_PROPERTY.equals(eventType))
            {
                handler = new CheckPropertyHandler();
            }
            else if (EVENT_TYPE_FUNCTION.equals(eventType))
            {
                handler = new EventHandler();
            }
            else if (EVENT_TYPE_TABLE_PROPERTY.equals(eventType)) 
            {
            	handler = new TableEventHandler();
            }
            else if (EVENT_TYPE_TABPANE_PROPERTY.equals(eventType)) 
            {
            	handler = new TabPaneEventHandler();
            }
            else if (EVENT_WEBSERVICE.equals(eventType)) 
            {
            	handler = new WebServiceHandler();
            }
            
            if (handler == null)
            {
                throw new AjaxException("Unsupported this event type[" + eventType + "]!");
            }
            return handler.trigger(context);
        }
        catch (AjaxHandlerException ex)
        {
        	errorFlag = true;
            throw ex;
        } finally {
			if (errorFlag) {
				HibernateUtil.releaseSession(session, false);
			} else {
				HibernateUtil.releaseSession(session, true);
			}
        }
    }
}
