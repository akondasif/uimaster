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

public class VariableDefinitionImpl  implements org.shaolin.bmdp.workflow.be.IVariableDefinition
{
    private static final long serialVersionUID = 0x90B1123CE87B50FFL;

    private final transient IConstantService ceService = AppContext.get().getConstantService();

    protected String getBusinessEntityName()
    {
        return "org.shaolin.bmdp.workflow.be.VariableDefinition";
    }

    public VariableDefinitionImpl()
    {
        
        _extField = new BEExtensionInfo();
        
    }
    
    
        /**
     * Create Date
     */
    private java.util.Date createDate = null;


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
    protected java.lang.String name;
    
   /**
     *  help is not available
     */    
    protected java.lang.String varType;
    
   /**
     *  help is not available
     */    
    protected java.lang.String classType;
    
        /**
     *  Get createDate
     *
     *  @return java.util.Date
     */
    public java.util.Date getCreateDate() {
        return createDate;
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
     *  get name
     *
     *  @return name
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     *  get varType
     *
     *  @return varType
     */
    public java.lang.String getVarType() {
        return varType;
    }

    /**
     *  get classType
     *
     *  @return classType
     */
    public java.lang.String getClassType() {
        return classType;
    }

        /**
     *  set createDate
     *  @parameter true or false.
     */
    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

    /**
     *  set _extType
     *  @param _extType which is the extension type of be object.
     */
    public void set_extType(java.lang.String _extType) {
        this._extType = _extType;
    }
        
    /**
     *  set name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     *  set varType
     */
    public void setVarType(java.lang.String varType) {
        this.varType = varType;
    }

    /**
     *  set classType
     */
    public void setClassType(java.lang.String classType) {
        this.classType = classType;
    }

    
    /**
     * Check different according to primary key.
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof org.shaolin.bmdp.workflow.be.VariableDefinitionImpl))
            return false;
        org.shaolin.bmdp.workflow.be.VariableDefinitionImpl o = (org.shaolin.bmdp.workflow.be.VariableDefinitionImpl)obj;
        
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
        aBuf.append("org.shaolin.bmdp.workflow.be.VariableDefinition");
    
        aBuf.append(" : ");
        
        aBuf.append("createDate=").append(createDate).append(", ");
        
        aBuf.append("name");
        aBuf.append("=");
        aBuf.append(name);
        aBuf.append(", ");
        
        aBuf.append("varType");
        aBuf.append("=");
        aBuf.append(varType);
        aBuf.append(", ");
        
        aBuf.append("classType");
        aBuf.append("=");
        aBuf.append(classType);
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
        
        org.shaolin.bmdp.datamodel.bediagram.StringType nameBEType = new org.shaolin.bmdp.datamodel.bediagram.StringType();
    
        //MemberType Define for name
        member = new MemberType();
        member.setName("name");
        member.setDescription("null");
        member.setType(nameBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.StringType varTypeBEType = new org.shaolin.bmdp.datamodel.bediagram.StringType();
    
        //MemberType Define for varType
        member = new MemberType();
        member.setName("varType");
        member.setDescription("null");
        member.setType(varTypeBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.StringType classTypeBEType = new org.shaolin.bmdp.datamodel.bediagram.StringType();
    
        //MemberType Define for classType
        member = new MemberType();
        member.setName("classType");
        member.setDescription("null");
        member.setType(classTypeBEType);
        memberTypeList.add(member);
            
        return memberTypeList;
    }
    
    public IVariableDefinition createEntity ()
    {
        return new VariableDefinitionImpl();
    }
    
}

        

