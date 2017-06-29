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

import java.util.List;

import org.shaolin.uimaster.page.UserRequestContext;
import org.shaolin.bmdp.json.JSONException;
import org.shaolin.bmdp.json.JSONObject;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.ajax.ComboBox;
import org.shaolin.uimaster.page.ajax.Layout;
import org.shaolin.uimaster.page.ajax.Widget;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.javacc.VariableEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLComboBoxType extends HTMLSingleChoiceType
{
    private static final Logger logger = LoggerFactory.getLogger(HTMLComboBoxType.class);

    public HTMLComboBoxType(String id)
    {
        super(id);
    }

    @Override
	public void generateBeginHTML(UserRequestContext context, UIFormObject ownerEntity, int depth) {
		
	}

    @Override
    public void generateEndHTML(UserRequestContext context, UIFormObject ownerEntity, int depth)
    {
        try
        {
            List<String> displayOptions =  getOptionDisplayValues();
            List<String> options = getOptionValues();
            String value = getValue();
            if(displayOptions == null)
            {
                displayOptions = options;
            }
            generateWidget(context);
            if (displayOptions != null && options != null)
            {
                if (displayOptions.size() == 0)
                {
                    displayOptions = options;
                }
                if (displayOptions.size() != options.size())
                {
                    logger.error("display size && output size not equal!", new Exception());
                    displayOptions = options;
                }
                if (isReadOnly() != null && isReadOnly().booleanValue())
                {
                    addAttribute("allowBlank", "true");
                    String UIStyle = (String)getAttribute("UIStyle");
                    if (UIStyle != null && !UIStyle.trim().equals("null"))
                    {
                        UIStyle = "uimaster_comboBox_readOnly " + UIStyle;
                    }
                    else
                    {
                        UIStyle = "uimaster_comboBox_readOnly";
                    }
                    if ( value == null && displayOptions.size() != 0 )
                    {
                        context.generateHTML("<input type=text readOnly=\"true\" class=\"" + UIStyle + "\" value=\"");
                        context.generateHTML(HTMLUtil.formatHtmlValue(displayOptions.get(0) == null ? "_null" : String.valueOf(displayOptions.get(0))));
                        context.generateHTML("\" />");
                        context.generateHTML("<input type=hidden name=\"");
                        context.generateHTML(getName());
                        context.generateHTML("\"");
                        generateAttributes(context);
                        generateEventListeners(context);
                        context.generateHTML(" value=\"");;
                        context.generateHTML(HTMLUtil.formatHtmlValue((options.get(0) == null) ? "_null" : String.valueOf(options.get(0))));
                        context.generateHTML("\" />");
                    }
                    else
                    {
                        boolean isMatch = true;
                        for (int i = 0; i < displayOptions.size(); i++)
                        {
                            String optionValue = (options.get(i) == null) ? "_null" : String.valueOf(options.get(i));
                            if (value != null && value.equalsIgnoreCase(optionValue))
                            {
                                context.generateHTML("<input type=text readOnly=\"true\" class=\"" + UIStyle + "\" value=\"");
                                context.generateHTML(HTMLUtil.formatHtmlValue(displayOptions.get(i) == null ? "_null" : String.valueOf(displayOptions.get(i))));
                                context.generateHTML("\" />");
                                context.generateHTML("<input type=hidden name=\"");
                                context.generateHTML(getName());
                                context.generateHTML("\"");
                                generateAttributes(context);
                                generateEventListeners(context);
                                context.generateHTML(" value=\"");
                                context.generateHTML(HTMLUtil.formatHtmlValue(optionValue));
                                context.generateHTML("\" />");
                                isMatch = true;
                                break;
                            }
                            isMatch = false;
                        }
                        if ( !isMatch )
                        {
                            context.generateHTML("<input type=text readOnly=\"true\" class=\"" + UIStyle + "\" value=\"");
                            context.generateHTML(HTMLUtil.formatHtmlValue(displayOptions.get(0) == null ? "_null" : String.valueOf(displayOptions.get(0))));
                            context.generateHTML("\" />");
                            context.generateHTML("<input type=hidden name=\"");
                            context.generateHTML(getName());
                            context.generateHTML("\"");
                            generateAttributes(context);
                            generateEventListeners(context);
                            context.generateHTML(" value=\"");;
                            context.generateHTML(HTMLUtil.formatHtmlValue((options.get(0) == null) ? "_null" : String.valueOf(options.get(0))));
                            context.generateHTML("\" />");
                        }
                    }
                }
                else
                {
                    context.generateHTML("<select name=\"");
                    context.generateHTML(getName());
                    context.generateHTML("\"");
                    generateAttributes(context);
                    generateEventListeners(context);
                    context.generateHTML(">");
                    for (int i = 0; i < displayOptions.size(); i++)
                    {
                        context.generateHTML("<option value=\"");
                        String optionValue = (options.get(i) == null) ? "_null" : String.valueOf(options.get(i));
                        context.generateHTML(HTMLUtil.formatHtmlValue(optionValue));
                        context.generateHTML("\"");
                        if (value != null && value.equalsIgnoreCase(optionValue))
                        {
                            context.generateHTML(" selected");
                        }
                        context.generateHTML(">");
                        context.generateHTML(HTMLUtil.formatHtmlValue(displayOptions.get(i) == null ? "_null" : String.valueOf(displayOptions.get(i))));
                        context.generateHTML("</option>");
                    }
                    context.generateHTML("</select>");
                }
            }
            else
            {
                if (isReadOnly() != null && isReadOnly().booleanValue())
                {
                    addAttribute("allowBlank", "true");
                    addAttribute("readOnly", "true");
                    context.generateHTML("<input type=text name=\"");
                    context.generateHTML(getName());
                    context.generateHTML("\"");
                    generateAttributes(context);
                    generateEventListeners(context);
                    context.generateHTML(" value=\"\" />");
                }
                else
                {
                    context.generateHTML("<select name=\"");
                    context.generateHTML(getName());
                    context.generateHTML("\"");
                    generateAttributes(context);
                    generateEventListeners(context);
                    context.generateHTML(">");
                    context.generateHTML("</select>");
                }
            }
            generateEndWidget(context);
        }
        catch (Exception e)
        {
            logger.error("error. in entity: " + getUIEntityName(), e);
        }
    }
    
    public JSONObject createJsonModel(VariableEvaluator ee) throws JSONException 
    {
//        ComboBox comboBox = new ComboBox(getName(), Layout.NULL);
//        comboBox.setRealValueType(this.getRealValueDataType());
//        comboBox.setReadOnly(isReadOnly());
//        comboBox.setUIEntityName(getUIEntityName());
//
//        comboBox.setOptions(getOptionDisplayValues(), getOptionValues());
//        comboBox.setValue(getValue());
//
//        setAJAXConstraints(comboBox);
//        setAJAXAttributes(UserRequestContext.UserContext.get(), comboBox);
//        
//        comboBox.setListened(true);
//
//        return comboBox;
    	return super.createJsonModel(ee);
    }
    
    private static final long serialVersionUID = -7717716729284638113L;
}
