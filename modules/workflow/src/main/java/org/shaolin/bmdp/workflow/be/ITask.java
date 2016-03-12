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

import org.shaolin.bmdp.workflow.ce.*;


/**
 * null
 * 
 * This code is generated automatically, any change will be replaced after rebuild.
 * 
 *
 */

public interface ITask 
    extends IPersistentEntity, IExtensibleEntity
{
    public final static String ENTITY_NAME = "org.shaolin.bmdp.workflow.be.Task";
    
 
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
     *  get partyId
     *
     *  @return partyId
     */
    public long getPartyId();

    /**
     *  get partyType
     *
     *  @return partyType
     */
    public java.lang.String getPartyType();

    /**
     *  get sessionId
     *
     *  @return sessionId
     */
    public java.lang.String getSessionId();

    /**
     *  get flowState
     *
     *  @return flowState
     */
    public byte[] getFlowState();

    /**
     *  get subject
     *
     *  @return subject
     */
    public java.lang.String getSubject();

    /**
     *  get description
     *
     *  @return description
     */
    public java.lang.String getDescription();

    /**
     *  get comments
     *
     *  @return comments
     */
    public java.lang.String getComments();

    /**
     *  get createTime
     *
     *  @return createTime
     */
    public java.util.Date getCreateTime();

    /**
     *  get expiredTime
     *
     *  @return expiredTime
     */
    public java.util.Date getExpiredTime();

    /**
     *  get expiredTimeStart
     *
     *  @return expiredTimeStart
     */
    public java.util.Date getExpiredTimeStart();

    /**
     *  get expiredTimeEnd
     *
     *  @return expiredTimeEnd
     */
    public java.util.Date getExpiredTimeEnd();

    /**
     *  get sendSMS
     *
     *  @return sendSMS
     */
    public boolean getSendSMS();

    /**
     *  get sendEmail
     *
     *  @return sendEmail
     */
    public boolean getSendEmail();

    /**
     *  get periodicJob
     *
     *  @return periodicJob
     */
    public java.lang.Runnable getPeriodicJob();

    /**
     *  get periodicValue
     *
     *  @return periodicValue
     */
    public long getPeriodicValue();

    /**
     *  get periodicType
     *
     *  @return periodicType
     */
    public PeriodicType getPeriodicType();

    /**
     *  get status
     *
     *  @return status
     */
    public TaskStatusType getStatus();

    /**
     *  get completeRate
     *
     *  @return completeRate
     */
    public int getCompleteRate();

    /**
     *  get priority
     *
     *  @return priority
     */
    public TaskPriorityType getPriority();

    /**
     *  get listener
     *
     *  @return listener
     */
    public java.lang.Object getListener();

    /**
     *  set id
     */
    public void setId(long id);

    /**
     *  set partyId
     */
    public void setPartyId(long partyId);

    /**
     *  set partyType
     */
    public void setPartyType(java.lang.String partyType);

    /**
     *  set sessionId
     */
    public void setSessionId(java.lang.String sessionId);

    /**
     *  set flowState
     */
    public void setFlowState(byte[] flowState);

    /**
     *  set subject
     */
    public void setSubject(java.lang.String subject);

    /**
     *  set description
     */
    public void setDescription(java.lang.String description);

    /**
     *  set comments
     */
    public void setComments(java.lang.String comments);

    /**
     *  set createTime
     */
    public void setCreateTime(java.util.Date createTime);

    /**
     *  set expiredTime
     */
    public void setExpiredTime(java.util.Date expiredTime);

    /**
     *  set expiredTimeStart
     */
    public void setExpiredTimeStart(java.util.Date expiredTimeStart);

    /**
     *  set expiredTimeEnd
     */
    public void setExpiredTimeEnd(java.util.Date expiredTimeEnd);

    /**
     *  set sendSMS
     */
    public void setSendSMS(boolean sendSMS);

    /**
     *  set sendEmail
     */
    public void setSendEmail(boolean sendEmail);

    /**
     *  set periodicJob
     */
    public void setPeriodicJob(java.lang.Runnable periodicJob);

    /**
     *  set periodicValue
     */
    public void setPeriodicValue(long periodicValue);

    /**
     *  set periodicType
     */
    public void setPeriodicType(PeriodicType periodicType);

    /**
     *  set status
     */
    public void setStatus(TaskStatusType status);

    /**
     *  set completeRate
     */
    public void setCompleteRate(int completeRate);

    /**
     *  set priority
     */
    public void setPriority(TaskPriorityType priority);

    /**
     *  set listener
     */
    public void setListener(java.lang.Object listener);


}

        