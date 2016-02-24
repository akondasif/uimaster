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
package org.shaolin.uimaster.page.widgets;

import java.io.IOException;

import org.shaolin.bmdp.runtime.security.UserContext;
import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;
import org.shaolin.uimaster.page.ajax.Calendar;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLDateType extends HTMLTextWidgetType
{
    private static Logger logger = LoggerFactory.getLogger(HTMLDateType.class);

    private static final long serialVersionUID = -5232602952223828765L;
    
    private boolean isRange = false;
    
	public HTMLDateType()
    {
    }

    public HTMLDateType(HTMLSnapshotContext context)
    {
        super(context);
    }

    public HTMLDateType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    public boolean isRange() {
		return isRange;
	}

	public void setRange(boolean isRange) {
		this.isRange = isRange;
	}
    
    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        generateWidget(context);
        generateContent(context);
        generateEndWidget(context);
    }

    public String getValue()
    {
        String value = (String)getAllAttribute("value");
        if (value == null)
        {
            value = (String)getAllAttribute("text");
        }
        return value == null ? "" : value;
    }

    private void generateContent(HTMLSnapshotContext context)
    {
        try
        {
        	boolean isReadOnly = isReadOnly() != null && isReadOnly().booleanValue();
			if (isReadOnly) {
				addAttribute("allowBlank", "true");
				addAttribute("readOnly", "true");
			}
            context.generateHTML("<input type=\"text\" name=\"");
            context.generateHTML(getName());
            context.generateHTML("\"");
            generateAttributes(context);
            generateEventListeners(context);
            context.generateHTML(" value=\"");
            context.generateHTML(HTMLUtil.formatHtmlValue(getValue()));
            context.generateHTML("\" />");
            
            if (!isReadOnly && isEditable()) {
	            String root = (UserContext.isMobileRequest() && UserContext.isAppClient()) 
	        			? WebConfig.getAppResourceContextRoot() : WebConfig.getResourceContextRoot();
	            if (isRange) {
	            	context.generateHTML("<img src='");
					context.generateHTML(root + "/images/controls/calendar/selectdate.gif");
					context.generateHTML("' />");
	            } else {
		            context.generateHTML("&nbsp;&nbsp;<img src='");
					context.generateHTML(root + "/images/controls/calendar/selectdate.gif");
					context.generateHTML("' onclick='javascript:defaultname.");
					context.generateHTML(this.getPrefix() + this.getUIID());
					context.generateHTML(".open();'/>");
	            }
            }
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
    {
        if ("initValidation".equals(attributeName) || "validator".equals(attributeName))
        {
        	return;
        }
        if ("editable".equals(attributeName))
        {
            if ("false".equals(String.valueOf(attributeValue)))
            {
                context.generateHTML(" readOnly=\"true\"");
            }
        }
        else if ("maxLength".equals(attributeName))
        {
            context.generateHTML(" maxlength=\"");
            context.generateHTML((String)attributeValue);
            context.generateHTML("\"");
        }
        else if ("txtFieldLength".equals(attributeName))
        {
            context.generateHTML(" size=\"");
            context.generateHTML((String)attributeValue);
            context.generateHTML("\"");
        }
        else if ("prompt".equals(attributeName))
        {
            if ( attributeValue != null && !((String)attributeValue).trim().equals("") )
            {
                context.generateHTML(" title=\"");
                context.generateHTML((String)attributeValue);
                context.generateHTML("\"");
            }
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
    }

    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        Calendar calendar = new Calendar(getName(), Layout.NULL);

        calendar.setReadOnly(isReadOnly());
        calendar.setUIEntityName(getUIEntityName());

        // we don't expect to anything except the pure value 
        // what we really need in the backend.
        calendar.setValue(getValue());

        // add necessary attribute especially the server side constraint check.
        setAJAXConstraints(calendar);
        setAJAXAttributes(calendar);
        
        calendar.setListened(true);
        calendar.setFrameInfo(getFrameInfo());

        return calendar;
    }

}
