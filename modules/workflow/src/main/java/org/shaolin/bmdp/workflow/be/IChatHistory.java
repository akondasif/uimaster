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

public interface IChatHistory 
    extends IPersistentEntity, IExtensibleEntity
{
    public final static String ENTITY_NAME = "org.shaolin.bmdp.workflow.be.ChatHistory";
    
 
    /**
     *  get orgId
     *
     *  @return orgId
     */
    public long getOrgId();

    /**
     *  set orgId
     */
    public void setOrgId(long orgId);

    /**
     *  get id
     *
     *  @return id
     */
    public long getId();

    /**
     *  get taskId
     *
     *  @return taskId
     */
    public long getTaskId();

    /**
     *  get sentPartyId
     *
     *  @return sentPartyId
     */
    public long getSentPartyId();

    /**
     *  get receivedPartyId
     *
     *  @return receivedPartyId
     */
    public long getReceivedPartyId();

    /**
     *  get message
     *
     *  @return message
     */
    public java.lang.String getMessage();

    /**
     *  get read
     *
     *  @return read
     */
    public boolean getRead();

    /**
     *  set id
     */
    public void setId(long id);

    /**
     *  set taskId
     */
    public void setTaskId(long taskId);

    /**
     *  set sentPartyId
     */
    public void setSentPartyId(long sentPartyId);

    /**
     *  set receivedPartyId
     */
    public void setReceivedPartyId(long receivedPartyId);

    /**
     *  set message
     */
    public void setMessage(java.lang.String message);

    /**
     *  set read
     */
    public void setRead(boolean read);


}

        