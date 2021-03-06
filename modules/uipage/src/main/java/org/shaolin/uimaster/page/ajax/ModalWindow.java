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
import java.util.HashMap;
import java.util.Map;

import org.shaolin.bmdp.json.JSONObject;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.AjaxContextHelper;
import org.shaolin.uimaster.page.IJSHandlerCollections;
import org.shaolin.uimaster.page.ajax.json.IDataItem;

/**
 * <br>
 * <br>ModalWindow window = new ModalWindow("window1",refEntity);
 * <br>window.setBounds(200,200,500,250);
 * <br>window.open();
 * <br>
 * @author swu
 *
 */
public class ModalWindow extends Container implements Serializable
{
    private static final long serialVersionUID = -1744731434666233557L;

    private RefForm refEntity;

    private String title;

    private String icon;

    private boolean isOnly;

    private boolean draggable;

    private boolean fixable;

    private boolean isMin;

    private boolean isOnlyShowCloseBtn;
    
    private int x = -1;

    private int y = -1;

    private int width;

	private int height;

	private boolean autoResize = false;
	
	private boolean openInBottomDialog = false;
	
	private String frameInfo;
    
	public ModalWindow(String uiid, RefForm refEntity) {
		this(AjaxContextHelper.getAjaxContext().getEntityPrefix() + uiid, "",
				new CellLayout(), refEntity, AjaxContextHelper.getAjaxContext()
						.getRequestData().getFrameId());
	}

	public ModalWindow(String uiid, RefForm refEntity, String frameInfo) {
		this(AjaxContextHelper.getAjaxContext().getEntityPrefix() + uiid, "",
				new CellLayout(), refEntity, frameInfo);
	}

	public ModalWindow(String uiid, String title, RefForm refEntity) {
		this(AjaxContextHelper.getAjaxContext().getEntityPrefix() + uiid,
				title, new CellLayout(), refEntity, AjaxContextHelper
						.getAjaxContext().getRequestData().getFrameId());
	}

	public ModalWindow(String uiid, String title, RefForm refEntity,
			String frameInfo) {
		this(AjaxContextHelper.getAjaxContext().getEntityPrefix() + uiid,
				title, new CellLayout(), refEntity, frameInfo);
	}

    private ModalWindow(String uiid, String title, Layout layout, RefForm refEntity, String frameInfo)
    {
        super(uiid, layout);
        this.setListened(true);
        this.refEntity = refEntity;
        this.title = title;
        this.icon = "";
        this.isOnly = true;
        this.frameInfo = frameInfo;
        this.refEntity.setId(this.refEntity.getUiid());
        this.refEntity.setFrameInfo(frameInfo);

        AjaxContext ajaxContext = AjaxContextHelper.getAjaxContext();
		if (ajaxContext == null) {
			throw new IllegalArgumentException("Fail to add refEntity in the component tree!");
		} else {
			if (refEntity.getUIEntityName() == null) {
				refEntity.setUIEntityName(ajaxContext.getEntityName());
			}
            if (!ajaxContext.existElement(refEntity)) {
            	ajaxContext.addElement(refEntity);
            }
        }
    }

    public void open()
    {
        AjaxContext ajaxContext = AjaxContextHelper.getAjaxContext();
        ajaxContext.addElement(this);
        ajaxContext.addDataItem( createOpenData(ajaxContext) );
    }

    public void close()
    {
        AjaxContext ajaxContext = AjaxContextHelper.getAjaxContext();
        ajaxContext.removeElement(getId(), frameInfo);
        ajaxContext.addDataItem( createCloseData(ajaxContext) );
    }

    public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOnly() {
        return isOnly;
    }

    public void setIsOnly(boolean isOnly) {
        this.isOnly = isOnly;
    }

    /**
         *
         * @param x
         * @param y
         * @param width
         * @param height
         */
    public void setBounds(int x,int y,int width,int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isFixable() {
        return fixable;
    }

    public void setFixable(boolean fixable) {
        this.fixable = fixable;
    }

    public boolean isMin() {
        return isMin;
    }

    public void setShowCloseBtn(boolean value) {
    	this.isOnlyShowCloseBtn = value;
    }
    
    public void setMin(boolean isMin) {
        this.isMin = isMin;
    }

    public boolean isAutoResize() {
		return autoResize;
	}

	public void setAutoResize(boolean autoResize) {
		this.autoResize = autoResize;
	}
    
	public void openInBottomDialog(boolean openInBottomDialog) {
		this.openInBottomDialog = openInBottomDialog;
    }
	
    private IDataItem createOpenData(AjaxContext ajaxContext)
    {
        IDataItem dataItem = AjaxContextHelper.createDataItem();
        dataItem.setUiid(refEntity.getId());
        dataItem.setJsHandler(IJSHandlerCollections.OPEN_WINDOW);
        dataItem.setSibling(genAttributes());
        dataItem.setJs(refEntity.generateJS());
        dataItem.setData(refEntity.generateHTML());
        dataItem.setFrameInfo(this.frameInfo);
        return dataItem;
    }

    private String genAttributes()
    {
        Map map = new HashMap();
        map.put("title", this.getTitle());
        map.put("icon", this.getIcon());
        map.put("isOnly", String.valueOf(this.isOnly()));
        map.put("x", String.valueOf(this.x));
        map.put("y", String.valueOf(this.y));
        map.put("width", String.valueOf(this.width));
        map.put("height", String.valueOf(this.height));
        map.put("autoResize", this.autoResize);
        map.put("draggable", String.valueOf(this.draggable));
        map.put("fixable", String.valueOf(this.fixable));
        map.put("isMin", String.valueOf(this.isMin));
        map.put("isOnlyShowCloseBtn", String.valueOf(this.isOnlyShowCloseBtn));
        map.put("openInBottomDialog", String.valueOf(this.openInBottomDialog));
        map.put("id", this.getId());

        return (new JSONObject(map)).toString();
    }
    
    private IDataItem createCloseData(AjaxContext ajaxContext)
    {
        IDataItem dataItem = AjaxContextHelper.createDataItem();
        dataItem.setUiid(getId());
        dataItem.setJsHandler(IJSHandlerCollections.CLOSE_WINDOW);
        dataItem.setFrameInfo(this.frameInfo);
        return dataItem;
	}
}
