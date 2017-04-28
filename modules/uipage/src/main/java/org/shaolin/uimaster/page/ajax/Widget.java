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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.page.StringPropertyType;
import org.shaolin.bmdp.datamodel.page.ValidatorPropertyType;
import org.shaolin.bmdp.datamodel.page.ValidatorsPropertyType;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.HTMLUtil;
import org.shaolin.uimaster.page.IJSHandlerCollections;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.JSONArray;
import org.shaolin.uimaster.page.security.ComponentPermission;

/**
 * <p>
 * AJAX widget object
 * </p>
 * <p>
 * AJAX update/remove attributes:
 * <ul>
 *  <li>readonly: these methods will be locked while readonly is true. 
 *  <ul><li>addAttribute(Map)</li>
 *      <li>addAttribute(String, Object, boolean)</li>
 *      <li>addClassName(String)</li>
 *      <li>addConstraint(String, Object, boolean)</li>
 *      <li>addConstraint(String, Object, String)</li>
 *      <li>addConstraint(String, Object)</li>
 *      <li>addConstraint(String, Object[], String)</li>
 *      <li>addCustomValidator(String, String[], String)</li>
 *      <li>addEvent(String, String)</li>
 *      <li>addEventListener(Map)</li>
 *      <li>addEventListener(String, String)</li>
 *      <li>addStyle(String, String, boolean)</li>
 *      <li>remove()</li>
 *      <li>removeAllStyles()</li>
 *      <li>removeAttribute(String)</li>
 *      <li>removeClassName(String)</li>
 *      <li>removeConstraint(String)</li>
 *      <li>removeCustomValidators()</li>
 *      <li>removeEventListenter(String)</li>
 *      <li>removeStyle(String)</li>
 *      <li>setWidgetLabel(String)</li>
 *      <li>setWidgetLabelColor(String)</li>
 *      <li>setWidgetLabelFont(String)</li>
 *  </ul>
 *  </li>
 *  <li>editable: these methods are parallel 
 *      <ul>
 *          <li>setReadonly</li>
 *          <li>setVisible</li>
 *          <li>disableValidation</li>
 *      </ul>
 *  </li>
 *  <li>widgetLabel</li>
 *  <li>style</li>
 *  <li>customized attributes(support all attributes in W3 standard)</li>
 * </ul>
 * </p>
 * <p>
 * AJAX update/remove event:
 * <ul>
 *  <li>click</li>
 *  <li>blur</li>
 *  <li>customized event(support all events in W3 standard)</li>
 * </ul>
 * </p>
 * <p>
 * AJAX update/remove css:
 * <ul>
 *  <li>widgetLabelFont</li>
 *  <li>widgetLabelColor</li>
 *  <li>customized styles(support all styles in W3 standard)</li>
 * </ul>
 * </p>
 * 
 * @author swu
 *
 */
abstract public class Widget<T> implements Serializable
{
    protected final Logger logger = Logger.getLogger(this.getClass());

    private static final long serialVersionUID = 6011414203344569865L;

    private String id;

    private Boolean readOnly;

    private String UIEntityName;

    private String frameInfo = "";
    
    private String[] viewPermissions;
    
    private String[] editPermissions;

    private Map<String, Object> attributeMap;

    private Map<String, String> styleMap;

    private Map<String, String> eventListenerMap;

    private Map<String, Object> constraintMap;

    private List<Widget<?>> listeners;
    
    private Layout htmlLayout;

    private String widgetLabel;

    private String widgetLabelColor;

    private String widgetLabelFont;

    protected boolean visible = true;

    // the secure value won't be shown in the client browser, such as object id or any other sensitive attributes.
    private boolean isSecure = false;
    
    private boolean isListened = false;

    private boolean valueMask = false;
    
    private boolean FLSEnabled = false;

    public Widget(String id, Layout layout)
    {
        this.id = id;
        htmlLayout = layout;
        if (layout instanceof CellLayout)
        {
            ((CellLayout)layout).addComponent(this);
        }
        attributeMap = new HashMap<String, Object>();
        
        AjaxContext context = AjaxActionHelper.getAjaxContext();
        if(context != null)
        {
            this.frameInfo = context.getRequestData().getFrameId();
        }
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    /**
     * This method will change the visible,editable,readonly attributes of the component according
     * to the permission that user owns
     * 
     * @param cp
     */
    public void addSecurityControls(ComponentPermission cp)
    {
        String[] viewPermission = HTMLUtil.getViewPermission(cp, getViewPermissions());

        if (viewPermission != null && viewPermission.length > 0)
        {
            //view permission configured
            if (!HTMLUtil.checkViewPermission(viewPermission))
            {
                logger.debug("the user doesn't own the permission to view the component: " + id);
                // no view permission
                setVisible(false, false);
                // also disable all the validation on it
                disableValidation();
            }
            else
            {
                // with view permission
                logger.debug("the user owns the permission to view the component: " + id);
                setVisible(true, false);

                if (this.isEditPermissionEnabled())
                {
                    // check edit permission
                    String[] editPermissions = HTMLUtil.getEditPermission(cp, getEditPermissions());
                    addEditableControl(editPermissions);
                }
            }
        }
        else if (this.isEditPermissionEnabled())
        {
            // no view permission configured
            logger.debug("no view permission configured on the component: " + id);
            // check edit permission
            String[] editPermissions = HTMLUtil.getEditPermission(cp, getEditPermissions());
            addEditableControl(editPermissions);
        }
    }

    /**
     * this method check whether the user owns the edit permission and add the editable control on
     * the component
     * 
     * @param editPermission
     */
    private void addEditableControl(String[] editPermissions)
    {
        if (editPermissions != null && editPermissions.length > 0)
        {
            if (!HTMLUtil.checkEditPermission(editPermissions))
            {
                // no edit permission
                logger.debug("the user doesn't own the permission to edit the component: " + id);
                setEditable(false, false);
                setReadOnly(Boolean.TRUE, false);
            }
            else
            {
                // with edit permission
                logger.debug("the user owns the permission to edit the component: " + id);
                setEditable(true, false);
                setReadOnly(Boolean.FALSE, false);
            }
        }
        else
        {
            // no edit permission configured
            // do not change the editable property of component
            logger.debug("no edit permission configured on the component: " + id);
        }
    }

    /**
     * Whether this component can have editPermission.
     */
    public boolean isEditPermissionEnabled()
    {
        return true;
    }

    public void setReadOnly(Boolean readOnly)
    {
        setReadOnly(readOnly, true);
    }

    /**
     * Readonly attribute related so many behaviors. if update is true, 
     * the whole widget will be re-drawn.
     * 
     * @param readOnly
     * @param update
     */
    protected void setReadOnly(Boolean readOnly, boolean update)
    {
        this.readOnly = readOnly;
        
        if (!update)
        {
            return;
        }
        this._updateReadOnly(isReadOnly());
    }
    
    public boolean isSecure() 
    {
    	return this.isSecure;
    }
    
    public void setIsSecure(boolean v) {
    	this.isSecure = v;
    }

    /**
     * @deprecated 
     * Cause ambiguous issue. please use isReadOnly method.
     * 
     * @return
     */
    public Boolean getReadOnly()
    {
        return readOnly;
    }
    
    public boolean isReadOnly()
    {
        return readOnly != null && readOnly.booleanValue()? true : false ;
    }
    
    public void setViewPermission(String viewPermission)
    {
        setViewPermissions(new String[]{viewPermission});
    }
    
    public String getViewPermission()
    {
        if (viewPermissions != null && viewPermissions.length > 0)
        {
            return viewPermissions[0];
        }
        else
        {
            return null;
        }
    }
    
    public void setViewPermissions(String[] viewPermissions)
    {
        this.viewPermissions = viewPermissions;
    }
    
    public String[] getViewPermissions()
    {
        return this.viewPermissions;
    }
    
    public void setEditPermissions(String[] editPermissions)
    {
        this.editPermissions = editPermissions;
    }
    
    public String[] getEditPermissions()
    {
        return this.editPermissions;
    }

    public void setUIEntityName(String uIEntityName)
    {
        UIEntityName = uIEntityName;
    }

    public String getUIEntityName()
    {
        return UIEntityName;
    }

    public String getFrameInfo()
    {
        return frameInfo;
    }

    public void setFrameInfo(String frameInfo)
    {
        this.frameInfo = frameInfo;
    }

    public void setHtmlLayout(Layout htmlLayout)
    {
        this.htmlLayout = htmlLayout;
    }

    public Layout getHtmlLayout()
    {
        return htmlLayout;
    }
    
    /**
     * same to getHtmlLayout();
     * @return
     */
    public Layout getLayout()
    {
        return htmlLayout;
    }

    /**
     * AJAX update is unnecessary to support this method
     */
    @SuppressWarnings("unchecked")
	public T addAttribute(Map<String, Object> attributeMap)
    {
        if (attributeMap == null)
        {
            return (T) this;
        }
        if(!this.checkAcessible())
        {
            return (T) this;
        }
        
        this.attributeMap.putAll(attributeMap);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addAttribute(String name, int value)
    {
        addAttribute(name, Integer.valueOf(value), true);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T addAttribute(String name, long value)
    {
        addAttribute(name, Long.valueOf(value), true);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T addAttribute(String name, boolean value)
    {
        addAttribute(name, Boolean.valueOf(value), true);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T addAttribute(String name, float value)
    {
        addAttribute(name, Float.valueOf(value), true);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T addAttribute(String name, double value)
    {
        addAttribute(name, Double.valueOf(value), true);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T addAttribute(String name, String value)
    {
        addAttribute(name, value, true);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T addAttribute(String name, Object value)
    {
        addAttribute(name, value, true);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addAttribute(String name, Object value, boolean update)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        attributeMap.put(name, value);
        if (update && value != null)
        {
            if(value instanceof String)
            {
                _updateAttribute(name, (String)value);
            }
            else if(value instanceof List)
            {
                JSONArray array = new JSONArray((List)value);
                _updateAttribute(name, array.toString());
            }
            else if(value instanceof Integer || value instanceof Long
                    || value instanceof Float || value instanceof Double
                    || value instanceof Short || value instanceof Boolean)
            {
                _updateAttribute(name, value.toString());
            }
            else
            {
                logger.warn("Unsupport AJAX object type: "+value.getClass()
                            +" for adding attribute in "+this.getClass()+" widget.");
            }
        }
        return (T) this;
    }

    public boolean hasAttribute(String name)
    {
        return attributeMap.containsKey(name);
    }
    
    public Object getAttribute(String name)
    {
        return attributeMap.get(name);
    }

    public Object removeAttribute(String name)
    {
        if(!this.checkAcessible())
        {
            return null;
        }
        
        if(attributeMap.containsKey(name))
        {
            _removeAttribute(name);
            return attributeMap.remove(name);
        }
        else
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public T addClassName(String name)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if(attributeMap.containsKey("class"))
        {
            String className = (String)attributeMap.get("class");
            String[] names = className.split("\\s");
            for(int i=0; i<names.length; i++)
            {
                if(names[i].equals(name))
                {
                	return (T) this;
                }
            }
            attributeMap.put("class", className+" "+name);
        }
        else
        {
            attributeMap.put("class", name);
        }
        _updateAttribute("class", (String)attributeMap.get("class"));
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T addStyle(String name, String value)
    {
        addStyle(name, value, true);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T addStyle(String name, String value, boolean update)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if (styleMap == null)
        {
            styleMap = new HashMap<String, String>();
        }
        styleMap.put(name, value);

        if (update)
        {
            _updateCSS(name, value);
        }
        return (T) this;
    }

    public String getStyle(String name)
    {
        return styleMap == null ? null : (String)styleMap.get(name);
    }

    @SuppressWarnings("unchecked")
    public T removeStyle(String name)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if(styleMap == null)
        {
        	return (T) this;
        }
        if( styleMap.containsKey(name) )
        {
            styleMap.remove(name);
            _removeCSS(name);
        }
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T removeClassName(String name)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if(attributeMap.containsKey("class"))
        {
            String newClassName = "";
            String className = (String)attributeMap.get("class");
            String[] names = className.split("\\s");
            for(int i=0; i<names.length; i++)
            {
                if(names[i].equals(name))
                {
                    names[i] = null;
                    continue;
                }
                newClassName += names[i]+" ";
            }
            attributeMap.put("class", newClassName);
            _updateAttribute("class", newClassName);
        }
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T removeAllStyles()
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if(styleMap == null)
        {
        	return (T) this;
        }
        styleMap.clear();
        _removeAttribute("style");
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    protected T addConstraint(String name, Object value)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        if (constraintMap == null) {
        	constraintMap = new HashMap<String, Object>();
        }
        constraintMap.put(name, value);
        
        this._updateConstraint();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T addConstraint(String name, Object value, boolean isUpdated)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if (constraintMap == null) {
        	constraintMap = new HashMap<String, Object>();
        }
        constraintMap.put(name, value);
        if(isUpdated)
        {
            this._updateConstraint();
        }
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T addConstraint(String name, Object value, String message)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if (name != null && value != null)
        {
        	if (name.toLowerCase().equals("initvalidation"))
            {
                addConstraint("flag", String.valueOf(value));
            } else {
	            addConstraint(name, value);
	            if (message != null)
	            {
	                addConstraint(name + "Text", packMessageText(message));
	            }
            }
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addConstraint(String name, Object[] value, String message)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if (name != null && value != null)
        {
            addConstraint(name, joinArray(value));
            addConstraint(name + "Real", value);
            if (message != null)
            {
                addConstraint(name + "Text", packMessageText(message));
            }
        }
        return (T) this;
    }

    /**
     * The server side check for the constraint definition 
     * that will prevent the security violation issue.
     * 
     */
    @SuppressWarnings("unchecked")
    public T checkConstraint() {
    	// it's override by the specific widget.
    	return (T) this;
    }
    
    public boolean hasConstraint(String name)
    {
        return constraintMap != null && constraintMap.containsKey(name) && constraintMap.get(name) != null;
    }
    
    public Object getConstraint(String name)
    {
        return constraintMap != null ? constraintMap.get(name) : null;
    }

    public Object removeConstraint(String name)
    {
        if(!this.checkAcessible())
        {
            return null;
        }
        
        if (name.toLowerCase().equals("initvalidation"))
        {
            name = "flag";
        }
        if(constraintMap != null && constraintMap.containsKey(name))
        {
            Object s = constraintMap.remove(name);
            constraintMap.remove(name + "Text");
            constraintMap.remove(name + "Real");
            this._removeConstraint("{"+name+":null}");
            return s;
        }
        else
        {
            Object s = removeAttribute(name);
            if(s != null)
            {
                this._updateConstraint();
            }
            return s;
        }
    }

    @SuppressWarnings("unchecked")
	public T addCustomValidator(String script, String[] param, String msg)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if (constraintMap == null) {
        	constraintMap = new HashMap<String, Object>();
        }
        ValidatorsPropertyType validators = (ValidatorsPropertyType)constraintMap.get("validators");
        if (validators == null)
        {
            validators = new ValidatorsPropertyType();
            constraintMap.put("validators", validators);
        }
        ValidatorPropertyType v = new ValidatorPropertyType();
        v.setErrMsg(msg);
        if (param != null)
        {
            for (int i = 0; i < param.length; i++)
            {
            	StringPropertyType paramList = new StringPropertyType();
                paramList.setValue(param[i]);
                v.getParams().add(paramList);
            }
        }
        v.setFuncCode(script);
        validators.getValidators().add(v);
        
        this._updateConstraint();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
	public T removeCustomValidators()
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if (constraintMap == null) {
        	return (T) this;
        }
        ValidatorsPropertyType validators = (ValidatorsPropertyType)constraintMap.get("validators");
        if (validators != null)
        {
            this._removeValidators();
        }
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
	public T click() {
    	if(!this.checkAcessible())
        {
    		return (T) this;
        }
    	AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if (ajaxContext == null) {
        	return (T) this;
        }
        ajaxContext.executeJavaScript("$('"+this.getId()+"').trigger('click');");
        
        return (T) this;
    }
    
    /**
     * ajax update is unnecessary!
     * 
     * @param eventMap
     */
    @SuppressWarnings("unchecked")
	public T addEventListener(Map<String, String> eventMap)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if (eventMap == null)
        {
        	return (T) this;
        }
        if (eventListenerMap == null)
        {
            eventListenerMap = new HashMap();
        }
        eventListenerMap.putAll(eventMap);
        
        return (T) this;
    }

    /**
     * add an event for a widget
     * 
     * Keep compatible with old update behavior.
     * 
     * @param eventName
     * @param handler
     */
    @SuppressWarnings("unchecked")
	public T addEventListener(String eventName, String handler)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if (eventListenerMap == null)
        {
            eventListenerMap = new HashMap<String, String>();
        }
        eventListenerMap.put(eventName, handler);
        _updateEvent(eventName, handler);
        
        return (T) this;
    }
    
    /**
     * Add event for a widget
     * 
     * <ul>
     *  <li>blur: 
     *  Form Events, Forms Bind an event handler to the "blur" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>click:
     *  Mouse Events Bind an event handler to the "click" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>dblclick:
     *  Mouse Events Bind an event handler to the "dblclick" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>focus:
     *  Form Events, Forms Bind an event handler to the "focus" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>focusin:
     *  Keyboard Events, Mouse Events Bind an event handler to the "focusin" JavaScript event.
     *  </li>
     *  <li>focusout:
     *  Keyboard Events, Mouse Events Bind an event handler to the "focusout" JavaScript event.
     *  </li>
     *  <li>hover:
     *  Mouse Events Bind two handlers to the matched elements, to be executed when the mouse pointer enters and leaves the elements.
     *  </li>
     *  <li>keydown:
     *  Keyboard Events Bind an event handler to the "keydown" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>keypress:
     *  Keyboard Events Bind an event handler to the "keypress" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>keyup:
     *  Keyboard Events Bind an event handler to the "keyup" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>mousedown:
     *  Mouse Events Bind an event handler to the "mousedown" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>mouseenter:
     *  Mouse Events Bind an event handler to be fired when the mouse enters an element, or trigger that handler on an element.
     *  </li>
     *  <li>mouseleave:
     *  </li>
     *  Mouse Events Bind an event handler to be fired when the mouse leaves an element, or trigger that handler on an element.
     *  </li>
     *  <li>mousemove:
     *  Mouse Events Bind an event handler to the "mousemove" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>mouseout:
     *  Mouse Events Bind an event handler to the "mouseout" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>mouseover:
     *  Mouse Events Bind an event handler to the "mouseover" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>mouseup:
     *  Mouse Events Bind an event handler to the "mouseup" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>resize:
     *  Browser Events Bind an event handler to the "resize" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>scroll:
     *  Browser Events Bind an event handler to the "scroll" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>select:
     *  Form Events, Forms Bind an event handler to the "select" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>submit:
     *  Form Events, Forms Bind an event handler to the "submit" JavaScript event, or trigger that event on an element.
     *  </li>
     *  <li>unload:
     *  Document Loading Bind an event handler to the "unload"
     *  </li>
     *  </ul>
     * @param eventName 
     * @param handler only object reference is allowed!
     */
    @SuppressWarnings("unchecked")
	public T addEvent(String eventName, String handler)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        if (eventListenerMap == null)
        {
            eventListenerMap = new HashMap<String, String>();
        }
        eventListenerMap.put(eventName, handler);
        _updateEvent(eventName, handler);
        
        return (T) this;
    }

    public String getEventListener(String eventName)
    {
        return eventListenerMap == null ? null : (String)eventListenerMap.get(eventName);
    }

    public String removeEventListenter(String eventName)
    {
        if(!this.checkAcessible())
        {
            return null;
        }
        
        if(eventListenerMap == null)
        {
            return null;
        }
        if(eventListenerMap.containsKey(eventName))
        {
            _removeEvent(eventName);
            return (String)eventListenerMap.remove(eventName);
        }
        else
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public T setWidgetLabel(String widgetLabel)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        this.widgetLabel = widgetLabel;
        _updateAttribute("widgetLabel",widgetLabel);
        
        return (T) this;
    }

    public String getWidgetLabel()
    {
        return widgetLabel;
    }

    @SuppressWarnings("unchecked")
	public T setWidgetLabelColor(String widgetLabelColor)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        this.widgetLabelColor = widgetLabelColor;
        _updateCSS("widgetLabelColor",widgetLabelColor);
        
        return (T) this;
    }

    public String getWidgetLabelColor()
    {
        return widgetLabelColor;
    }

    @SuppressWarnings("unchecked")
	public T setWidgetLabelFont(String widgetLabelFont)
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        this.widgetLabelFont = widgetLabelFont;
        _updateCSS("widgetLabelFont",widgetLabelFont);
        
        return (T) this;
    }

    public String getWidgetLabelFont()
    {
        return widgetLabelFont;
    }

    /**
     * This makes the component visible in the web page.
     * 
     * @param visible setting this to true makes it visible setting it to false makes it invisible
     * @see setVisible(boolean visible, boolean update)
     */
    @SuppressWarnings("unchecked")
	public T setVisible(boolean visible)
    {
        setVisible(visible, true);
        
        return (T) this;
    }

    /**
     * This makes the component visible in the web page. It also controls whether the component as a
     * whole should be update.
     * 
     * @param visible - setting this to true makes it visible setting it to false makes it invisible
     * @param update - update should only be called ONCE when the component is instanciated.
     */
    @SuppressWarnings("unchecked")
	public T setVisible(boolean visible, boolean update)
    {
        this.visible = visible;
        if (!visible)
        {
            addStyle("display", "none", update);
        }
        else
        {
            addStyle("display", "", update);
        }
        
        return (T) this;
    }

    /**
     * If set true, when the component generate html, it will replace the value with "****"
     * 
     * @param mask - whether to mask the value
     */
    public T setValueMask(boolean mask)
    {
        this.valueMask = mask;
        return (T) this;
    }

    protected boolean isValueMask()
    {
        return this.valueMask;
    }
    
    public T enableFLS()
    {
        FLSEnabled = true;
        return (T) this;
    }
    
    public T disableFLS()
    {
        FLSEnabled = false;
        return (T) this;
    }
    
    public boolean isFLSEnabled()
    {
        return FLSEnabled;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public T setEditable(boolean editable)
    {
        setEditable(editable, true);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
	protected T disableValidation()
    {
        attributeMap.put("flag", "disabled");
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
	protected T setEditable(boolean editable, boolean update)
    {
        attributeMap.put("disabled", String.valueOf(editable));
        if (update)
        {
            if(editable)
            {
                _removeAttribute("disabled");
            }
            else
            {
                _updateAttribute("disabled", "disabled");
            }
        }
        return (T) this;
    }

    public boolean isEditable()
    {
    	if (!attributeMap.containsKey("disabled")) {
    		return true;
    	}
        String editable = (String)attributeMap.get("disabled");
        return "false".equals(editable);
    }

    public String generateHTML()
    {
        return "";
    }

    /**
     * TODO
     * @param validLabel
     * @param is
     * @return
     */
    public String createWidgetLabel(boolean validLabel, boolean is)
    {
        String widgetLabel = "";
        return widgetLabel;
    }

    /**
     * @param sb
     */
    @SuppressWarnings("unchecked")
	public T generateWidget(StringBuilder sb)
    {
        boolean isNeed = false;
        if (!visible)
        {
            sb.append("<span style=\"display:none;\">");
        }

        if (widgetLabel != null && !widgetLabel.equals(""))
        {
            if (widgetLabel.equals(" "))
            {
                isNeed = true;
            }

            sb.append("<label id=\"");
            sb.append(id);
            sb.append("_widgetLabel\"");
            sb.append(" style=\"display:block;");
            if (widgetLabelColor != null)
            {
                sb.append("color:");
                sb.append(widgetLabelColor);
                sb.append(";");
            }
            if (widgetLabelFont != null)
            {
                sb.append("font:");
                sb.append(widgetLabelFont);
                sb.append(";");
            }
            sb.append("\" class=\"uimaster_widgetLabel\">");
            sb.append(widgetLabel);
            sb.append("</label>");
        }

        if (!visible)
        {
            sb.append("</span>");
        }

        if (isNeed)
        {
            sb.append("<br/>");
        }
        
        return (T) this;
    }
    
    public void registerListener(Widget<?> widget) {
    	if (listeners == null) {
    		listeners = new ArrayList<Widget<?>>();
    	}
    	listeners.add(widget);
    }
    
    public void notifyChange() {
    	if (listeners != null) {
    		for (Widget<?> w : listeners) {
    			w.refreshValue();
    		}
    	}
    }
    
    public void refreshValue() {}
    
    @SuppressWarnings("unchecked")
	public T setListened(boolean isListened)
    {
        this.isListened = isListened;
        if (this.getHtmlLayout() != null)
        {
            this.getHtmlLayout().setListened(isListened);
        }
        return (T) this;
    }

    public boolean isListened()
    {
        return isListened;
    }

    @SuppressWarnings("unchecked")
	public T remove()
    {
        if(!this.checkAcessible())
        {
        	return (T) this;
        }
        
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if (ajaxContext == null)
        	return (T) this;

        if (ajaxContext.existElmByAbsoluteId(id, frameInfo))
        {
            String parentID = null;
            if (htmlLayout != null)
            {
                htmlLayout.remove();
                if (htmlLayout.parent != null)
                {
                    parentID = htmlLayout.parent.id;
                }
            }
            IDataItem dataItem = AjaxActionHelper.createRemoveItem(parentID, id);
            dataItem.setFrameInfo(frameInfo);
            ajaxContext.addDataItem(dataItem);
            ajaxContext.removeElement(id, frameInfo);
        }
        return (T) this;
    }

    boolean _addComponent(Widget comp, AjaxContext ajaxContext)
    {
        if(comp == this)
        {
            return false;
        }
        if (comp.getUIEntityName() == null)
        {
            comp.setUIEntityName(ajaxContext.getEntityName());
        }
        comp.setFrameInfo(frameInfo);
        boolean success = ajaxContext.addElement(comp.getId(), comp, frameInfo);
        // This code is only for system level edit control of FLS
        if (!ajaxContext.isPageEditable())
        {
            comp.setEditable(false, false);
            comp.setReadOnly(Boolean.TRUE, false);
        }
        return success;
    }
    
    @SuppressWarnings("unchecked")
	protected T _updateReadOnly(boolean readonly)
    {
        if (!isListened || id == null)
        {
        	return (T) this;
        }
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if (ajaxContext == null || (!ajaxContext.existElement(this) && !(this instanceof Layout)))
        {
        	return (T) this;
        }
        if (htmlLayout != null && htmlLayout != Layout.NULL)
        {
            IDataItem dataItem = AjaxActionHelper.createReadOnlyItem(id);
            dataItem.setJs(generateJS());
            dataItem.setFrameInfo(getFrameInfo());
            dataItem.addItem("readonly", String.valueOf(readonly));
            if(htmlLayout.getParent() != null)
            {
                dataItem.setParent(htmlLayout.getParent().getId());
            }
            dataItem.setData(htmlLayout.generateHTML());
            AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
        }
        return (T) this;
    }

    /**
     * Adding all attributes for a new widget to the client.
     * 
     * @param sb
     */
    @SuppressWarnings("unchecked")
	protected T generateAttributes(StringBuilder sb)
    {
        for (Iterator it = attributeMap.keySet().iterator(); it.hasNext();)
        {
            String attributeName = (String)it.next();
            if (attributeName.equals("type"))
            {
                continue;
            }
            Object attributeValue = attributeMap.get(attributeName);
            generateAttribute(attributeName, attributeValue, sb);
        }

        if (styleMap != null && !styleMap.isEmpty())
        {
            sb.append(" style=\"");
            for (Iterator it = styleMap.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry)it.next();
                String name = (String)entry.getKey();
                String value = (String)entry.getValue();
                sb.append(name);
                sb.append(":");
                sb.append(value);
                sb.append(";");
            }
            sb.append("\"");
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
	protected T generateAttribute(String name, Object value, StringBuilder sb)
    {
        if (value == null)
        {
            value = "";
        }
        sb.append(" ");
        sb.append(name);
        sb.append("=\"");
        sb.append(HTMLUtil.handleEscape(String.valueOf(value)));
        sb.append("\"");
        
        return (T) this;
    }

    /**
     * Adding all events for a new widget to the client.
     * 
     * @param sb
     */
    @SuppressWarnings("unchecked")
	protected T generateEventListeners(StringBuilder sb)
    {
        if (eventListenerMap == null)
        {
        	return (T) this;
        }
        else
        {
            for (Iterator it = eventListenerMap.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry entry = (Map.Entry)it.next();
                String name = (String)entry.getKey();
                String value = (String)entry.getValue();
                sb.append(" ");
                sb.append(name);
                sb.append("=\"");
                sb.append(value);
                sb.append("\"");
            }
        }
        return (T) this;
    }
    
    protected String generateJS()
    {
        return generateConstraint();
    }
    
    protected String generateConstraint()
    {
    	if (constraintMap == null) {
    		return "";
    	}
    	
        StringBuffer js = new StringBuffer(150);
        for (Iterator it = constraintMap.keySet().iterator(); it.hasNext();)
        {
            String constraintName = (String)it.next();
            if (constraintName.equals("validators"))
            {
                ValidatorsPropertyType validators = (ValidatorsPropertyType)constraintMap
                        .get("validators");
                if (validators.getValidators().size() > 0)
                {
                    js.append(",validators:[");
                    
                    List<ValidatorPropertyType> properties = validators.getValidators();
                    for (ValidatorPropertyType property : properties)
                    {
                        ValidatorPropertyType v = property;
                        js.append("{func:function(){");
                        js.append(v.getFuncCode());
                        js.append("},");
                        int size = v.getParams().size();
                        if (size > 0)
                        {
                            js.append("param:[");
                            List<StringPropertyType> sTypes = v.getParams();
                            for (StringPropertyType s : sTypes)
                            {
                                js.append(s.getValue());
                                js.append(",");
                            }
							js.deleteCharAt(js.length() - 1);
                            js.append("],");
                        }
                        js.append("msg:\"");
                        if (v.getErrMsg() != null)
                        {
                            js.append(v.getErrMsg());
                        }
                        js.append("\"}");
                        js.append(",");
                    }
                    js.deleteCharAt(js.length() - 1);
                    js.append("]");
                }
            }
            else
            {
                String attributeValue = (String)constraintMap.get(constraintName);
                js.append(",");
                js.append(constraintName);
                js.append(":");
                js.append(attributeValue);
            }
        }
        return js.toString();
    }

    protected String packMessageText(String message)
    {
        return HTMLUtil.formatHtmlValue(message);
    }

    protected String joinArray(Object[] array)
    {
        StringBuffer arrayBuffer = new StringBuffer(100);
        arrayBuffer.append("[");
        for (int i = 0; i < array.length; i++)
        {
            if (i > 0)
            {
                arrayBuffer.append(',');
            }
            if (array[i] != null)
            {
                arrayBuffer.append("\"");
                arrayBuffer.append(array[i].toString());
                arrayBuffer.append("\"");
            }
        }
        arrayBuffer.append("]");
        return arrayBuffer.toString();
    }
    
    /**
     * to check a method whether allows visitor in the current conditions.
     * 
     * @return
     */
    protected boolean checkAcessible()
    {
        return !this._isReadOnly();// if widget is readonly mode, lock all attributes update.
    }
    
    private boolean isUpdateAllowed()
    {
        if (!isListened)
        {
            return false;
        }
        if(id == null)//Empty Layout
        {
            return false;
        }
        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        if (ajaxContext == null || (!ajaxContext.existElement(this) && !(this instanceof Layout)))
        {
            return false;
        }
        if(this._isReadOnly())
        {
            return false;
        }
        return true;
    }
    
    protected void _setWidgetLabel(String widgetLabel)
    {
        this.widgetLabel = widgetLabel;
    }
    
    protected boolean _isReadOnly()
    {
        return readOnly != null && readOnly.booleanValue()? true : false ;
    }
    
    /**
     * update the changed style/new style for an exiting widget.
     */
    protected void _updateCSS(String name, String value)
    {
        if(!isUpdateAllowed())
        {
            return;
        }
        
        StringBuilder sb = new  StringBuilder();
        sb.append("{'name':'");
        sb.append(name);
        sb.append("','value':'");
        sb.append(HTMLUtil.handleEscape(String.valueOf(value)));
        sb.append("'}");
        IDataItem dataItem = AjaxActionHelper.updateCssItem(id, sb.toString());
        dataItem.setFrameInfo(getFrameInfo());
        if(this instanceof Layout)
        {
            dataItem.setLayout(true);
        }
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
    
    protected void _removeCSS(String name)
    {
        if(!isUpdateAllowed())
        {
            return;
        }
        
        IDataItem dataItem = AjaxActionHelper.removeCssItem(id, name);
        dataItem.setFrameInfo(getFrameInfo());
        if(this instanceof Layout)
        {
            dataItem.setLayout(true);
        }
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
    
    /**
     * update the changed attribute/new attribute for an exiting widget.
     */
    protected void _updateAttribute(String name, String value)
    {
        if(!isUpdateAllowed())
        {
            return;
        }
        
        StringBuilder sb = new  StringBuilder();
        sb.append("{'name':'");
        sb.append(name);
        sb.append("','value':'");
        sb.append(HTMLUtil.handleEscape(String.valueOf(value)));
        sb.append("'}");
        IDataItem dataItem = AjaxActionHelper.updateAttrItem(id, sb.toString());
        dataItem.setFrameInfo(getFrameInfo());
        if(this instanceof Layout)
        {
            dataItem.setLayout(true);
        }
        //dataItem.addItem("visible", String.valueOf(this.isVisible()));
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }

    protected void _removeAttribute(String name)
    {
        if(!isUpdateAllowed())
        {
            return;
        }
        
        IDataItem dataItem = AjaxActionHelper.removeAttrItem(id, name);
        dataItem.setFrameInfo(getFrameInfo());
        if(this instanceof Layout)
        {
            dataItem.setLayout(true);
        }
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
    
    protected void _updateConstraint()
    {
        if(!isUpdateAllowed())
        {
            return;
        }
        if(this instanceof Layout)
        {
            return;
        }
        String script = this.generateConstraint();
        StringBuilder sb = new  StringBuilder();
        if(script.length() > 0)
        {
            script = script.substring(1);//remove comma symbol at the beginning of this string
            sb.append("{").append(script).append("}");// make it as JSON object.
        }
        IDataItem dataItem = AjaxActionHelper.updateConstraintItem(id, sb.toString());
        dataItem.setFrameInfo(getFrameInfo());
        dataItem.addItem("readonly", String.valueOf(this.isReadOnly()));
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
    
    protected void _removeValidators()
    {
        if(!isUpdateAllowed())
        {
            return;
        }
        if(this instanceof Layout)
        {
            return;
        }
        IDataItem dataItem = AjaxActionHelper.updateConstraintItem(id, "{validators:[]}");
        dataItem.setFrameInfo(getFrameInfo());
        dataItem.addItem("readonly", String.valueOf(this.isReadOnly()));
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
    
    protected void _removeConstraint(String data)
    {
        if(!isUpdateAllowed())
        {
            return;
        }
        
        IDataItem dataItem = AjaxActionHelper.updateConstraintItem(id, data);
        dataItem.setFrameInfo(getFrameInfo());
        dataItem.addItem("readonly", String.valueOf(this.isReadOnly()));
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
    
    public T showConstraint(String message) {
    	IDataItem dataItem = AjaxActionHelper.showConstraint(id, message);
        dataItem.setFrameInfo(getFrameInfo());
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
        
        return (T) this;
    }
    
    public T removeConstraint() {
    	IDataItem dataItem = AjaxActionHelper.removeConstraint(id);
        dataItem.setFrameInfo(getFrameInfo());
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
        
        return (T) this;
    }
    
    /**
     * update the changed event/new event for an exiting widget.
     * 
     * @param name
     * @param value
     */
    protected void _updateEvent(String name, String value)
    {
        if(!isUpdateAllowed())
        {
            return;
        }
        
        String str = "{'name':'"+ name +"','value':'"+ HTMLUtil.handleEscape(String.valueOf(value)) +"'}";
        IDataItem dataItem = AjaxActionHelper.updateEventItem(id, str);
        if(this instanceof Layout)
        {
            dataItem.setLayout(true);
        }
        dataItem.setFrameInfo(getFrameInfo());
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
    
    protected void _removeEvent(String name)
    {
        if(!isUpdateAllowed())
        {
            return;
        }
        
        IDataItem dataItem = AjaxActionHelper.removeEventItem(id, name);
        dataItem.setFrameInfo(getFrameInfo());
        if(this instanceof Layout)
        {
            dataItem.setLayout(true);
        }
        AjaxActionHelper.getAjaxContext().addDataItem(dataItem);
    }
}
