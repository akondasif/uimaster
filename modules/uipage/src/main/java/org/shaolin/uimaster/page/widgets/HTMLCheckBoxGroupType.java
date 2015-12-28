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
import java.util.List;

import org.shaolin.uimaster.page.HTMLSnapshotContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.CheckBoxGroup;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLCheckBoxGroupType extends HTMLMultiChoiceType
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLCheckBoxGroupType.class);

    public HTMLCheckBoxGroupType()
    {
    }

    public HTMLCheckBoxGroupType(HTMLSnapshotContext context)
    {
        super(context);
    }
    
    public HTMLCheckBoxGroupType(HTMLSnapshotContext context, String id)
    {
        super(context, id);
    }

    @Override
	public void generateBeginHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth) {
		
	}
    
    @Override
    public void generateEndHTML(HTMLSnapshotContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            String name = getName();
            List<String> displayOptions =  getOptionDisplayValues();
            List<String> options = getOptionValues();
            if(displayOptions == null)
            {
                displayOptions = options;
            }
            if ("false".equals((String) getAllAttribute("visible")))
            {
                context.generateHTML("<p style=\"display:none\">");
            }
            else
            {
                context.generateHTML("<p>");
            }
            if (displayOptions != null && options != null)
            {
                if (displayOptions.size() == 0)
                {
                    displayOptions = options;
                }
                if (displayOptions.size() != options.size())
                {
                    logger.error("display size && output size not equal!", new Exception());
                }
                generateWidget(context);
                if (isReadOnly() != null && isReadOnly().booleanValue())
                {
                    String value = "";
                    for (int i = 0; i < displayOptions.size(); i++)
                    {
                        if (equal(String.valueOf(options.get(i))))
                        {
                            if (value.equals(""))
                            {
                                value += String.valueOf(displayOptions.get(i));
                            }
                            else
                            {
                                value += "," + String.valueOf(displayOptions.get(i));
                            }
                        }
                    }
                    context.generateHTML(HTMLUtil.htmlEncode(value));
                    context.generateHTML("<input type=hidden name=\"");
                    context.generateHTML(name);
                    context.generateHTML("\"");
                    context.generateHTML(" value=\"");
                    context.generateHTML(HTMLUtil.formatHtmlValue(value));
                    context.generateHTML("\"");
                    generateAttributes(context);
                    generateEventListeners(context);
                    context.generateHTML(" />");
                }
                else
                {
                    boolean horizontalLayout = 
                    	Boolean.parseBoolean((String)getAllAttribute("horizontalLayout"));
                    int colCount = 15;
                    if (this.getAttribute("colCount") != null) {
                    	colCount = Integer.parseInt(this.getAttribute("colCount").toString());
                    }
                    for (int i = 0; i < displayOptions.size(); i++)
                    {
                        String entryValue = HTMLUtil.formatHtmlValue(String.valueOf(options.get(i)));
                        String entryDisplayValue = HTMLUtil.htmlEncode(String.valueOf(displayOptions.get(i)));
                        context.generateHTML("<input type=\"checkbox\" name=\"");
                        context.generateHTML(name);
                        context.generateHTML("\"");
                        context.generateHTML(" id=\"");
                        context.generateHTML(entryValue);
                        context.generateHTML("\"");
                        context.generateHTML(" value=\"");
                        context.generateHTML(entryValue);
                        context.generateHTML("\"");
                        generateAttributes(context);
                        generateEventListeners(context);
                        if (equal(String.valueOf(options.get(i))))
                        {
                            context.generateHTML(" checked");
                        }
                        context.generateHTML(" />");
                        context.generateHTML("<label for=\"");
                        context.generateHTML(entryValue);
                        context.generateHTML("\"  class=\"uimaster_checkout_text_gap\">");
                        context.generateHTML(entryDisplayValue);
                        context.generateHTML("</label>");
                        if (!horizontalLayout)
                        {
                            context.generateHTML("<br />");
                        } else if (i >= (colCount-1) && i % (colCount - 1) == 0) {
                        	// show how many columns in one row.
                        	context.generateHTML("<br />");
						}
                    }
                }
            }
            context.generateHTML("</p>");
            generateEndWidget(context);
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }

    public void generateAttribute(HTMLSnapshotContext context, String attributeName, Object attributeValue) throws IOException
    {
        if( "visible".equals(attributeName) )
        {
        }
        else
        {
            super.generateAttribute(context, attributeName, attributeValue);
        }
    }

    public Widget createAjaxWidget(VariableEvaluator ee)
    {
        CheckBoxGroup checkBoxGroup = new CheckBoxGroup(getName(), Layout.NULL);
        
        checkBoxGroup.setReadOnly(isReadOnly());
        checkBoxGroup.setUIEntityName(getUIEntityName());

        checkBoxGroup.setOptions(getOptionDisplayValues(), getOptionValues());
        checkBoxGroup.setValues(getValue());

        setAJAXConstraints(checkBoxGroup);
        setAJAXAttributes(checkBoxGroup);
        
        checkBoxGroup.setListened(true);
        checkBoxGroup.setFrameInfo(getFrameInfo());

        return checkBoxGroup;
    }
}
