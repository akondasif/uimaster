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

import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.DisposableBfString;
import org.shaolin.uimaster.page.HTMLUtil;

public class Hidden extends TextWidget implements Serializable
{
    private static final long serialVersionUID = 7431046276471658474L;

    public Hidden(String uiid)
    {
        this(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.setListened(true);
    }

    public Hidden(String uiid, String value)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, value, new CellLayout());
        this.setListened(true);
    }
    
    public Hidden(String id, Layout layout)
    {
        super(id, layout);
    }
    
    public void setValue(String value)
    {
        addAttribute("value", value);
    }

    public String getValue()
    {
    	if (this.isSecure()) {
    		
    	}
        return (String)this.getAttribute("value");
    }
    
    public String generateJS()
    {
        StringBuffer js = new StringBuffer(200);
        js.append("defaultname.");
        js.append(getId());
        js.append("=new UIMaster.ui.hidden({");
        js.append("ui:elementList[\"");
        js.append(getId());
        js.append("\"]});");
        return js.toString();
    }    
    
    public String generateHTML()
    {
    	StringBuilder html = DisposableBfString.getBuffer();
    	try {
	        html.append("<input type=\"hidden\" name=\"");
	        html.append(getId());
	        html.append("\"");
	        generateAttributes(html);
	        generateEventListeners(html);
	        html.append(" value=\"");
	        html.append(HTMLUtil.formatHtmlValue(getValue()));
	        html.append("\" />");
	        
	        return html.toString();
	    } finally {
			DisposableBfString.release(html);
		}
    }

    /**
     * Whether this component can have editPermission.
     */
    public boolean isEditPermissionEnabled()
    {
        return false;
    }

}
