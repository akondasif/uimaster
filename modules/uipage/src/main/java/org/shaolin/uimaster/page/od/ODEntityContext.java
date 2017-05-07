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
package org.shaolin.uimaster.page.od;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.shaolin.bmdp.datamodel.page.ODMappingType;
import org.shaolin.bmdp.exceptions.BusinessOperationException;
import org.shaolin.bmdp.i18n.ExceptionConstants;
import org.shaolin.bmdp.runtime.entity.EntityNotFoundException;
import org.shaolin.javacc.context.DefaultEvaluationContext;
import org.shaolin.javacc.context.DefaultParsingContext;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.exception.EvaluationException;
import org.shaolin.javacc.exception.ParsingException;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.UserRequestContext;
import org.shaolin.uimaster.page.cache.ODFormObject;
import org.shaolin.uimaster.page.cache.ODObject;
import org.shaolin.uimaster.page.cache.PageCacheManager;
import org.shaolin.uimaster.page.cache.UIFormObject;
import org.shaolin.uimaster.page.exception.AjaxException;
import org.shaolin.uimaster.page.exception.ODException;
import org.shaolin.uimaster.page.exception.ODProcessException;
import org.shaolin.uimaster.page.exception.UIPageException;
import org.shaolin.uimaster.page.od.mappings.ComponentMapping;
import org.shaolin.uimaster.page.widgets.HTMLReferenceEntityType;

public class ODEntityContext extends ODContext 
{
	private static Logger logger = Logger.getLogger(ODEntityContext.class);
	
	/**
     * OD component meta-info.
     */
    private ODMappingType odDescriptor;
    
    private ODFormObject odEntityObject;
    
    private UIFormObject uiFromObject;
    
    public ODEntityContext( String odEntityName, UserRequestContext htmlContext )
    {
        super( htmlContext, false );
        this.odEntityName = odEntityName;
    }
    
    /**
     * 
     * @throws EvaluationException
     * @throws ParsingException
     * @throws BusinessOperationException
     * @throws RepositoryException
     * @throws ClassNotFoundException
     * @throws AjaxException 
     * @throws UIPageException 
     * @throws EntityNotFoundException 
     */
    public void initContext() throws EvaluationException, ParsingException, 
    	BusinessOperationException, ClassNotFoundException, AjaxException, EntityNotFoundException, UIPageException
    {
    	if(logger.isInfoEnabled())
    		logger.info("Initialize UI Form: " + odEntityName + 
    				",htmlPrefix: " + requestContext.getHTMLPrefix());	
		
    	this.odEntityObject = PageCacheManager.getODFormObject(odEntityName);
    	this.uiFromObject = PageCacheManager.getUIFormObject(odEntityName);
		this.uiEntityName = odEntityObject.getUIEntityName();
		this.isODBaseRule = odEntityObject.isODBaseRule();
		this.isODBaseType = odEntityObject.isODBaseType();
		this.odDescriptor = odEntityObject.getODEntity();
    	
    	int debugCount = 0;
    	DefaultEvaluationContext localEContext = odEntityObject.getLocalEContext();
    	if( !inputParamValues.isEmpty() )
    	{
    		if(logger.isDebugEnabled())
    			logger.info("----->The 'page in' is receiving values from web flow parameters:");
    		String[] keys = odEntityObject.getParamKeys();
    		for (int i = 0; i < keys.length; i++)
    		{
    			String keyName = keys[i];
    			if( inputParamValues.containsKey(keyName) && inputParamValues.get(keyName) != null )
    			{
    				localEContext.setVariableValue(keyName, inputParamValues.get(keyName));
    				if(logger.isDebugEnabled())
    				{
    					Object obj = inputParamValues.get(keyName);
    					StringBuffer sb = new StringBuffer();
    					sb.append("Input parameter[");
    					sb.append(debugCount);
    					sb.append("]: ");
    					sb.append(keyName);
    					sb.append(", value: ");
    					sb.append(obj);
    					sb.append(", class: ");
    					sb.append(obj.getClass());
    					logger.debug(sb.toString());
    					debugCount++;
    				}
    			}
    		}
    	}
    	
		Set keySet = inputParamValues.keySet();
		for (Object key: keySet) {
			Object value = inputParamValues.get(key);
			if (value instanceof HTMLReferenceEntityType) {
				uiEntity = (HTMLReferenceEntityType)value;
				break;
			}
		}
		if (uiEntity == null || !UserRequestContext.isInstance(uiEntityName, uiEntity))
			throw new ODProcessException(ExceptionConstants.EBOS_ODMAPPER_049,
					new Object[]{uiEntity.getUIEntityName(), uiEntityName});
    	if(logger.isDebugEnabled())
            logger.debug("UI Reference Entity uiid: "+this.uiEntity.getId());
    	
    	DefaultEvaluationContext defaultEContext = new DefaultEvaluationContext();
    	defaultEContext.setVariableValue("context", requestContext);
    	defaultEContext.setVariableValue("odContext", this);
    	this.setEvaluationContextObject(GLOBAL_TAG, defaultEContext);
    	
    	this.setDefaultEvaluationContext(localEContext);
    	this.setEvaluationContextObject(LOCAL_TAG, localEContext);
    	this.setExternalEvaluationContext(this);
    	if( !isDataToUI )
    	{
    		DefaultEvaluationContext globalContext = new DefaultEvaluationContext();
    		globalContext.setVariableValue("context", requestContext);
    		globalContext.setVariableValue("odContext", this);
    		String uiid = requestContext.getHTMLPrefix() + uiEntity.getUIID();
    		globalContext.setVariableValue(AJAX_UICOMP_NAME, 
    				AjaxActionHelper.createUI2DataAjaxContext(uiid, 
					uiEntityName, requestContext.getRequest()));
    		this.setEvaluationContextObject(GLOBAL_TAG, globalContext);
    	}
    	if(logger.isInfoEnabled())
		{
			String[] keys = odEntityObject.getParamKeys();
			if(keys != null)
			{
				EvaluationContext dEContext = this.getEvaluationContextObject(ODContext.LOCAL_TAG);
				for (int i = 0; i < keys.length; i++ )
				{
					try
					{
						Object variableValue = dEContext.getVariableValue(keys[i]);
						if(variableValue == null) {
							if (logger.isDebugEnabled()) {
								logger.debug("Local variable["+keys[i]+"] value is null.");
							}
						}
					}catch(Exception e){}
				}
			}
		}
    }
	
	public String evalDataLocale() throws EvaluationException
	{
		//TODO:
//	    return ODContextHelper.evalDataLocale(getPageInDescritpor().getDataLocale(),
//	            this, ODContext.GLOBAL_TAG, odPageEntityObject.getPageInLocaleConfigs());
		return "";
	}
	
	public void executeAllMappings() throws ODException {
		List<ComponentMapping> mappings = odEntityObject.getAllMappings();
		for (ComponentMapping mapping: mappings) {
			mapping.execute(this);
		}
	}
	
	public void executeMapping(String name) throws ODException {
		List<ComponentMapping> mappings = odEntityObject.getAllMappings();
		for (ComponentMapping mapping : mappings) {
			if (mapping.getMappingName().equals(name)) {
				mapping.execute(this);
			}
		}
	}
	
	public String getUIParamName() {
		return odEntityObject.getUiParamName();
	}
	
	public void executeDataToUI() throws EvaluationException
	{
		odDescriptor.getDataToUIMappingOperation().evaluate(this);
	}
	
	public void executeUITOData() throws EvaluationException
	{
		odDescriptor.getUIToDataMappingOperation().evaluate(this);
	}
	
	public DefaultParsingContext getLocalPContext() 
    {
    	return odEntityObject.getLocalPContext();
    }
    
	public ODObject getODObject() 
	{
		return odEntityObject;
	}
	
	public UIFormObject getUIFormObject() {
		return uiFromObject;
	}
}
