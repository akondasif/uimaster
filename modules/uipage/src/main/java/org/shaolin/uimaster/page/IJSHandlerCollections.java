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

/**
 * System default js handler collections, enable to extensible variously js handles.
 * 
 * @author swu
 *
 */
public interface IJSHandlerCollections {

	/**
	 * error msg.
	 */
	public static final String MSG_ERROR = "error";
	
	/**
	 * error msg.
	 */
	public static final String MSG_INFO = "info";
	
	public static final String SESSION_TIME_OUT = "sessiontimeout";
	
	public static final String NO_PERMISSION = "nopermission";
	
	/**
     * html doc append.
     */
    public static final String HTML_APPEND = "append";
    
    /**
     * html doc pre-append.
     */
    public static final String HTML_PREPEND = "prepend";
    
    /**
     * insert error message.
     */
    public static final String HTML_APPEND_ERROR = "appendError";
    
    /**
     * permit the page to submit.
     */
    public static final String PERMIT_SUBMIT = "permitSubmit";
    
    /**
     * tell the browser to re-submit the page through page submit method
     */
    public static final String PAGE_RE_SUBMIT = "pageReSubmit";
    
    /**
     * html doc panel pending for a created panel without parent.
     */
    public static final String HTML_PANEL_PENDING = "panel_pending";
    
    /**
     * html doc insert before.
     */
    public static final String HTML_INSERTBEFORE = "before";
    
    /**
     * html doc insert after.
     */
    public static final String HTML_INSERTAFTER = "after";
    
    /**
     * html doc remove.
     */
    public static final String HTML_REMOVE = "remove";
    
    /**
     * html attribute removed.
     */
    public static final String HTML_REMOVE_ATTR = "remove_attr";
    
    /**
     * html event removed.
     */
    public static final String HTML_REMOVE_EVENT = "remove_event";
    
    /**
     * html css removed.
     */
    public static final String HTML_REMOVE_CSS = "remove_style";
    
    /**
     * html constraint removed.
     */
    public static final String HTML_REMOVE_CONST = "remove_const";
    
    /**
     * html attribute updated.
     */
    public static final String HTML_UPDATE_READONLY = "update_readonly";
    
    /**
     * html attribute updated.
     */
    public static final String HTML_UPDATE_ATTR = "update_attr";
    
    /**
     * html event updated.
     */
    public static final String HTML_UPDATE_EVENT = "update_event";
    
    /**
     * html css updated.
     */
    public static final String HTML_UPDATE_CSS = "update_style";
    
    /**
     * html constraint updated.
     */
    public static final String HTML_UPDATE_CONST = "update_const";
    
    /**
     * html constraint updated.
     */
    public static final String SHOW_CONSTRAINT = "show_constraint";
    
    /**
     * html constraint updated.
     */
    public static final String REMOVE_CONSTRAINT = "remove_constraint";
    
    
    /**
     * tab append item.
     */
    public static final String TAB_APPEND = "tab_append";
    
    /**
     * html constraint updated.
     */
    public static final String TABLE_UPDATE = "table_update";
    
    public static final String GALLERY_REFRESH = "gallery_refresh";
    
    /**
     */
    public static final String TREE_REFRESH = "tree_refresh";
    
    /**
     */
    public static final String TREE_EXPAND = "tree_expand";
    
    /**
     * 
     */
    public static final String CHART_ADDDATA = "chart_adddata";
    
    /**
     * 
     */
    public static final String CHART_REMOVEDATA = "chart_removedata";
    
    
    /**
     * java object result.
     */
    public static final String JAVA_OBJECT = "javaobject";
    
    /**
     * open window.
     */
    public static final String OPEN_WINDOW = "openwindow";
    
    /**
     * open dialog.
     */
    public static final String OPEN_DIALOG = "opendialog";
    
    /**
     * end of a line.
     */
    public static final String HARDRETURN = "hardreturn";
    
    /**
     * close a window
     */
    public static final String CLOSE_WINDOW = "closewindow";
    
    /**
     * execute a snippet of javascript
     */
    public static final String JAVASCRIPT = "js";
}
