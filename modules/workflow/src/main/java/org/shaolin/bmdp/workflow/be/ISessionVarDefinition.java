/*
 *
 * This file is automatically generated on Fri Mar 11 20:52:26 CST 2016
 */

    
package org.shaolin.bmdp.workflow.be;
        
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.shaolin.bmdp.datamodel.bediagram.*;
import org.shaolin.bmdp.datamodel.common.*;
import org.shaolin.bmdp.runtime.be.IBusinessEntity;
import org.shaolin.bmdp.runtime.be.IExtensibleEntity;
import org.shaolin.bmdp.runtime.be.IPersistentEntity;
import org.shaolin.bmdp.runtime.be.IHistoryEntity;
import org.shaolin.bmdp.runtime.be.ITaskEntity;
import org.shaolin.bmdp.runtime.be.BEExtensionInfo;

import org.shaolin.bmdp.runtime.spi.IConstantService;

import org.shaolin.bmdp.runtime.AppContext;

import org.shaolin.bmdp.runtime.ce.CEUtil;



/**
 * null
 * 
 * This code is generated automatically, any change will be replaced after rebuild.
 * 
 *
 */

public interface ISessionVarDefinition 
    extends IBusinessEntity, IExtensibleEntity
{
    public final static String ENTITY_NAME = "org.shaolin.bmdp.workflow.be.SessionVarDefinition";
    
 
    /**
     *  get name
     *
     *  @return name
     */
    public java.lang.String getName();

    /**
     *  get sessionServiceClass
     *
     *  @return sessionServiceClass
     */
    public java.lang.String getSessionServiceClass();

    /**
     *  get sessionClass
     *
     *  @return sessionClass
     */
    public java.lang.String getSessionClass();

    /**
     *  set name
     */
    public void setName(java.lang.String name);

    /**
     *  set sessionServiceClass
     */
    public void setSessionServiceClass(java.lang.String sessionServiceClass);

    /**
     *  set sessionClass
     */
    public void setSessionClass(java.lang.String sessionClass);


}

        