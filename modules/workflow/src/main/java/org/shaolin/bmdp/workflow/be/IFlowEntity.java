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

public interface IFlowEntity 
    extends IPersistentEntity, IExtensibleEntity
{
    public final static String ENTITY_NAME = "org.shaolin.bmdp.workflow.be.FlowEntity";
    
 
    /**
     *  get id
     *
     *  @return id
     */
    public long getId();

    /**
     *  get entityName
     *
     *  @return entityName
     */
    public java.lang.String getEntityName();

    /**
     *  get type
     *
     *  @return type
     */
    public java.lang.String getType();

    /**
     *  get content
     *
     *  @return content
     */
    public java.lang.String getContent();

    /**
     *  set id
     */
    public void setId(long id);

    /**
     *  set entityName
     */
    public void setEntityName(java.lang.String entityName);

    /**
     *  set type
     */
    public void setType(java.lang.String type);

    /**
     *  set content
     */
    public void setContent(java.lang.String content);


}

        