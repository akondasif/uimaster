/*
 *
 * This file is automatically generated on Sat Mar 12 13:14:24 CST 2016
 */

    
package org.shaolin.bmdp.analyzer.be;
        
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

import org.shaolin.bmdp.analyzer.ce.*;


/**
 * null
 * 
 * This code is generated automatically, any change will be replaced after rebuild.
 * 
 *
 */

public class JavaCCJobImpl  implements org.shaolin.bmdp.analyzer.be.IJavaCCJob
{
    private static final long serialVersionUID = 0x90B1123CE87B50FFL;

    private final transient IConstantService ceService = AppContext.get().getConstantService();

    protected String getBusinessEntityName()
    {
        return "org.shaolin.bmdp.analyzer.be.JavaCCJob";
    }

    public JavaCCJobImpl()
    {
        
        _extField = new BEExtensionInfo();
        
    }
    
    
        /**
     * Create Date
     */
    private java.util.Date createDate = null;

    /**
     * Enable record
     */
    private boolean _enable = true;


    /**
     *  BEExtension _extType
     */
    protected String _extType;

    /**
     *  BEExtension _extField
     */
    protected BEExtensionInfo _extField;
        
   /**
     *  help is not available
     */    
    protected long id;
    
   /**
     *  help is not available
     */    
    protected java.lang.String script;
    
   /**
     *  help is not available
     */    
    protected int executeDays;
    
   /**
     *  help is not available
     */    
    protected int executeTime;
    
   /**
     *  help is not available
     */    
    protected int count;
    
   /**
     *  help is not available
     */    
    protected java.util.Date realExecutedTime;
    
   /**
     *  help is not available
     */    
    protected JavaCCJobStatusType status = JavaCCJobStatusType.NOT_SPECIFIED;
    
    protected int statusInt = JavaCCJobStatusType.NOT_SPECIFIED.getIntValue();
    
        /**
     *  Get createDate
     *
     *  @return java.util.Date
     */
    public java.util.Date getCreateDate() {
        return createDate;
    }
            /**
     *  Is enable
     *
     *  @return boolean
     */
    public boolean isEnabled() {
        return _enable;
    }
            /**
     *  Is enable
     *
     *  @return boolean
     */
    private boolean get_enable() {
        return _enable;
    }
        
    /**
     *  get _extType
     *
     *  @return _extType
     */
    public String get_extType() {
        return _extType;
    }

    /**
     *  get _extField
     *
     *  @return _extField
     */
    public BEExtensionInfo get_extField() {
        return _extField;
    }
        
    /**
     *  get id
     *
     *  @return id
     */
    public long getId() {
        return id;
    }

    /**
     *  get script
     *
     *  @return script
     */
    public java.lang.String getScript() {
        return script;
    }

    /**
     *  get executeDays
     *
     *  @return executeDays
     */
    public int getExecuteDays() {
        return executeDays;
    }

    /**
     *  get executeTime
     *
     *  @return executeTime
     */
    public int getExecuteTime() {
        return executeTime;
    }

    /**
     *  get count
     *
     *  @return count
     */
    public int getCount() {
        return count;
    }

    /**
     *  get realExecutedTime
     *
     *  @return realExecutedTime
     */
    public java.util.Date getRealExecutedTime() {
        return realExecutedTime;
    }

    /**
     *  get status
     *
     *  @return status
     */
    public JavaCCJobStatusType getStatus() {
        return status;
    }

    /**
     *  get statusInt
     *
     *  @return statusInt
     */
    private int getStatusInt() {
        return statusInt;
    }

        /**
     *  set createDate
     *  @parameter true or false.
     */
    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

        /**
     *  set enable
     *  @parameter true or false.
     */
    public void setEnabled(boolean enable) {
        _enable = enable;
    }

        /**
     *  set enable
     *  @parameter true or false.
     */
    private void set_enable(boolean enable) {
        _enable = enable;
    }

    /**
     *  set _extType
     *  @param _extType which is the extension type of be object.
     */
    public void set_extType(java.lang.String _extType) {
        this._extType = _extType;
    }
        
    /**
     *  set id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *  set script
     */
    public void setScript(java.lang.String script) {
        this.script = script;
    }

    /**
     *  set executeDays
     */
    public void setExecuteDays(int executeDays) {
        this.executeDays = executeDays;
    }

    /**
     *  set executeTime
     */
    public void setExecuteTime(int executeTime) {
        this.executeTime = executeTime;
    }

    /**
     *  set count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     *  set realExecutedTime
     */
    public void setRealExecutedTime(java.util.Date realExecutedTime) {
        this.realExecutedTime = realExecutedTime;
    }

    /**
     *  set status
     */
    public void setStatus(JavaCCJobStatusType status) {
        this.status = status;
    if (statusInt != status.getIntValue()) {
            statusInt = status.getIntValue();
        }
    }

    /**
     *  set int status
     */
    private void setStatusInt(int intValue) {
        this.statusInt = intValue;
        if (statusInt != status.getIntValue()) {
            status = (JavaCCJobStatusType)ceService.getConstantEntity(JavaCCJobStatusType.ENTITY_NAME).getByIntValue(statusInt);
        }
    }

    
    /**
     * Check different according to primary key.
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof org.shaolin.bmdp.analyzer.be.JavaCCJobImpl))
            return false;
        org.shaolin.bmdp.analyzer.be.JavaCCJobImpl o = (org.shaolin.bmdp.analyzer.be.JavaCCJobImpl)obj;
        
        boolean result = super.equals(obj);

        boolean eq = true;
        
        return result;
    }

    /**
     * Generate hashCode according to primary key.
     */
    public int hashCode() {
        
        int result = super.hashCode();

        return result;
    }
        
    
     /**
     * Gets the String format of the business entity.
     *
     * @return String the business entity in String format.
     */
    public  String  toString() {
        StringBuffer aBuf = new StringBuffer();
        aBuf.append("org.shaolin.bmdp.analyzer.be.JavaCCJob");
    
        aBuf.append(" : ");
        
        aBuf.append("createDate=").append(createDate).append(", ");
        
        aBuf.append("enable=").append(_enable).append(", ");
        
        aBuf.append("id");
        aBuf.append("=");
        aBuf.append(id);
        aBuf.append(", ");
        
        aBuf.append("script");
        aBuf.append("=");
        aBuf.append(script);
        aBuf.append(", ");
        
        aBuf.append("executeDays");
        aBuf.append("=");
        aBuf.append(executeDays);
        aBuf.append(", ");
        
        aBuf.append("executeTime");
        aBuf.append("=");
        aBuf.append(executeTime);
        aBuf.append(", ");
        
        aBuf.append("count");
        aBuf.append("=");
        aBuf.append(count);
        aBuf.append(", ");
        
        aBuf.append("realExecutedTime");
        aBuf.append("=");
        aBuf.append(realExecutedTime);
        aBuf.append(", ");
        
        aBuf.append("status");
        aBuf.append("=");
        aBuf.append(status);
        aBuf.append(", ");
        
        return aBuf.toString();
    }
    
    
     /**
     * Gets list of MemberType.
     *
     * @return List     the list of MemberType.
     */
    public List<MemberType> getMemberList() {
        List<MemberType> memberTypeList = new ArrayList<MemberType>();
        
        MemberType member = null;
        
        org.shaolin.bmdp.datamodel.bediagram.LongType idBEType = new org.shaolin.bmdp.datamodel.bediagram.LongType();
    
        //MemberType Define for id
        member = new MemberType();
        member.setName("id");
        member.setDescription("null");
        member.setType(idBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.StringType scriptBEType = new org.shaolin.bmdp.datamodel.bediagram.StringType();
    
        //MemberType Define for script
        member = new MemberType();
        member.setName("script");
        member.setDescription("null");
        member.setType(scriptBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.IntType executeDaysBEType = new org.shaolin.bmdp.datamodel.bediagram.IntType();
    
        //MemberType Define for executeDays
        member = new MemberType();
        member.setName("executeDays");
        member.setDescription("null");
        member.setType(executeDaysBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.IntType executeTimeBEType = new org.shaolin.bmdp.datamodel.bediagram.IntType();
    
        //MemberType Define for executeTime
        member = new MemberType();
        member.setName("executeTime");
        member.setDescription("null");
        member.setType(executeTimeBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.IntType countBEType = new org.shaolin.bmdp.datamodel.bediagram.IntType();
    
        //MemberType Define for count
        member = new MemberType();
        member.setName("count");
        member.setDescription("null");
        member.setType(countBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.DateTimeType realExecutedTimeBEType = new org.shaolin.bmdp.datamodel.bediagram.DateTimeType();
    
        //MemberType Define for realExecutedTime
        member = new MemberType();
        member.setName("realExecutedTime");
        member.setDescription("null");
        member.setType(realExecutedTimeBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.CEObjRefType statusBEType = new org.shaolin.bmdp.datamodel.bediagram.CEObjRefType();
    
        TargetEntityType statusTargetEntity = new TargetEntityType();
        statusBEType.setTargetEntity(statusTargetEntity);
        statusTargetEntity.setEntityName("org.shaolin.bmdp.analyzer.ce.JavaCCJobStatusType");
            
        //MemberType Define for status
        member = new MemberType();
        member.setName("status");
        member.setDescription("null");
        member.setType(statusBEType);
        memberTypeList.add(member);
            
        return memberTypeList;
    }
    
    public IJavaCCJob createEntity ()
    {
        return new JavaCCJobImpl();
    }
    
}

        

