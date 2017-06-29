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
package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.List;

import org.shaolin.uimaster.page.AjaxContextHelper;
import org.shaolin.uimaster.page.DisposableBfString;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.WebConfig;

public class ComboBox extends SingleChoice<ComboBox> implements Serializable
{
    private static final long serialVersionUID = -7820240489728224513L;

    private String cssClass;

    public ComboBox(String uiid)
    {
        this(AjaxContextHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }

    public ComboBox(String id, Layout layout)
    {
        super(id, layout);
    }

    public ComboBox addAttribute(String name, Object value, boolean update)
    {
        if (name.equals("class"))
        {
            cssClass = (String)value;
            super.addAttribute("cssClass", value, true);
        }
        else if (name.equals("value"))
        {
            // do not check for now to keep compatibility
//            if (value == null || this.optionValues == null)
//            {
//                return;
//            }
            // temporary fix
            if (WebConfig.getHiddenValueMask().equals(value))
            {
                return this;
            }
//            if (!checkValueExist((String)value))
//            {
//                return;
//            }
            super.addAttribute(name, value, update);
        }
        else
        {
            super.addAttribute(name, value, update);
        }
        
        return this;
    }

    public ComboBox addConstraint(String name, Object[] value, String message)
    {
        if (name != null)
        {
            if (name.toLowerCase().equals("selectvalue"))
            {
                if (message != null)
                {
                    super.addConstraint("selectValueText", "'"+packMessageText(message)+"'", false);
                }
                super.addConstraint("selectValue", joinArray(value));
            }
            else
            {
                super.addConstraint(name, value, message);
            }
        }
        return this;
    }

    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.combobox({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]");
        js.append(super.generateJS());
        js.append("});");
        return js.toString();
    }

    public String generateHTML()
    {
        List displayOptions = getOptionDisplayValues();
        List options = getOptionValues();
        String value = getValue();
        if (displayOptions == null)
        {
            displayOptions = options;
        }

        StringBuilder html = DisposableBfString.getBuffer();
        try {
        generateWidget(html);
        if (displayOptions != null && options != null)
        {
            if (displayOptions.size() == 0)
            {
                displayOptions = options;
            }

            if ( this.isReadOnly() )
            {
                addAttribute("allowBlank", "true", false);
                String cClass;
                if (cssClass != null && !cssClass.trim().equals("null"))
                {
                    cClass = "uimaster_comboBox_readOnly " + cssClass;
                }
                else
                {
                    cClass = "uimaster_comboBox_readOnly";
                }
                if (value == null && displayOptions.size() != 0)
                {
                    html.append("<input type=\"text\" readOnly=\"true\" class=\"" + cClass
                            + "\" value=\"");
                    if (this.isValueMask())
                    {
                        html.append(WebConfig.getHiddenValueMask());
                    }
                    else
                    {
                        html.append(HTMLUtil.formatHtmlValue(displayOptions.get(0) == null ? "_null"
                                : String.valueOf(displayOptions.get(0))));
                    }
                    html.append("\" />");
                    html.append("<input type=\"hidden\" name=\"");
                    html.append(getId());
                    html.append("\"");
                    generateAttributes(html);
                    generateEventListeners(html);
                    html.append(" value=\"");
                    if (this.isValueMask())
                    {
                        html.append(WebConfig.getHiddenValueMask());
                    }
                    else
                    {
                        html.append(HTMLUtil.formatHtmlValue((options.get(0) == null) ? "_null"
                                : String.valueOf(options.get(0))));
                    }
                    html.append("\" />");
                }
                else
                {
                    boolean isMatch = true;
                    for (int i = 0; i < displayOptions.size(); i++)
                    {
                        String optionValue = (options.get(i) == null) ? "_null" : String
                                .valueOf(options.get(i));
                        if (value != null && value.equalsIgnoreCase(optionValue))
                        {
                            html.append("<input type=\"text\" readOnly=\"true\" class=\"" + cClass
                                    + "\" value=\"");
                            if (this.isValueMask())
                            {
                                html.append(WebConfig.getHiddenValueMask());
                            }
                            else
                            {
                                html.append(HTMLUtil
                                        .formatHtmlValue(displayOptions.get(i) == null ? "_null"
                                                : String.valueOf(displayOptions.get(i))));
                            }
                            html.append("\" />");
                            html.append("<input type=\"hidden\" name=\"");
                            html.append(getId());
                            html.append("\"");
                            generateAttributes(html);
                            generateEventListeners(html);
                            html.append(" value=\"");
                            if (this.isValueMask())
                            {
                                html.append(WebConfig.getHiddenValueMask());
                            }
                            else
                            {
                                html.append(HTMLUtil.formatHtmlValue(optionValue));
                            }
                            html.append("\" />");
                            isMatch = true;
                            break;
                        }
                        isMatch = false;
                    }
                    if (!isMatch)
                    {
                        html.append("<input type=\"text\" readOnly=\"true\" class=\"" + cClass
                                + "\" value=\"");
                        if (this.isValueMask())
                        {
                            html.append(WebConfig.getHiddenValueMask());
                        }
                        else
                        {
                            html.append(HTMLUtil
                                    .formatHtmlValue(displayOptions.get(0) == null ? "_null" : String
                                            .valueOf(displayOptions.get(0))));
                        }
                        html.append("\" />");
                        html.append("<input type=\"hidden\" name=\"");
                        html.append(getId());
                        html.append("\"");
                        generateAttributes(html);
                        generateEventListeners(html);
                        html.append(" value=\"");
                        if (this.isValueMask())
                        {
                            html.append(WebConfig.getHiddenValueMask());
                        }
                        else
                        {
                            html.append(HTMLUtil.formatHtmlValue((options.get(0) == null) ? "_null"
                                    : String.valueOf(options.get(0))));
                        }
                        html.append("\" />");
                    }
                }
            }
            else
            {
                html.append("<select name=\"");
                html.append(getId());
                html.append("\" class=\"");
                html.append("uimaster_comboBox ");
                if (cssClass != null && !cssClass.trim().equals("null"))
                {
                    html.append(cssClass);
                }
                html.append("\"");
                generateAttributes(html);
                generateEventListeners(html);
                html.append(">");
                for (int i = 0; i < displayOptions.size(); i++)
                {
                    html.append("<option value=\"");
                    String optionValue = (options.get(i) == null) ? "_null" : String
                            .valueOf(options.get(i));
                    if (this.isValueMask())
                    {
                        html.append(WebConfig.getHiddenValueMask());
                    }
                    else
                    {
                        html.append(HTMLUtil.formatHtmlValue(optionValue));
                    }
                    html.append("\"");
                    if (value != null && value.equalsIgnoreCase(optionValue))
                    {
                        html.append(" selected");
                    }
                    html.append(">");
                    if (this.isValueMask())
                    {
                        html.append(WebConfig.getHiddenValueMask());
                    }
                    else
                    {
                        html.append(HTMLUtil.formatHtmlValue(displayOptions.get(i) == null ? "_null"
                                : String.valueOf(displayOptions.get(i))));
                    }
                    html.append("</option>");
                }
                html.append("</select>");
            }
        }
        else
        {
            if (getReadOnly() != null && getReadOnly().booleanValue())
            {
                addAttribute("allowBlank", "true", false);
                html.append("<input type=\"text\" name=\"");
                html.append(getId());
                html.append("\" readOnly=\"true\" class=\"uimaster_comboBox_readOnly ");
                if (cssClass != null && !cssClass.trim().equals("null"))
                {
                    html.append(cssClass);
                }
                html.append("\"");
                generateAttributes(html);
                generateEventListeners(html);
                html.append(" value=\"\" />");
            }
            else
            {
                html.append("<select name=\"");
                html.append(getId());
                html.append("\" class=\"uimaster_comboBox ");
                if (cssClass != null && !cssClass.trim().equals("null"))
                {
                    html.append(cssClass);
                }
                html.append("\"");
                generateAttributes(html);
                generateEventListeners(html);
                html.append(">");
                html.append("</select>");
            }
        }

        return html.toString();
        } finally {
			DisposableBfString.release(html);
		}
    }
}
