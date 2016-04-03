/*
 *
 * This file is automatically generated on Sun Apr 03 21:42:15 CST 2016
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



/**
 * null
 * 
 * This code is generated automatically, any change will be replaced after rebuild.
 * 
 *
 */

public class PageHelperImpl  implements org.shaolin.bmdp.analyzer.be.IPageHelper
{
    private static final long serialVersionUID = 0x90B1123CE87B50FFL;

    private final transient IConstantService ceService = AppContext.get().getConstantService();

    protected String getBusinessEntityName()
    {
        return "org.shaolin.bmdp.analyzer.be.PageHelper";
    }

    public PageHelperImpl()
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
    protected java.lang.String htmlFile;
    
   /**
     *  help is not available
     */    
    protected boolean isMobileView;
    
   /**
     *  help is not available
     */    
    protected java.lang.String partyType;
    
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
     *  get htmlFile
     *
     *  @return htmlFile
     */
    public java.lang.String getHtmlFile() {
        return htmlFile;
    }

    /**
     *  get isMobileView
     *
     *  @return isMobileView
     */
    public boolean getIsMobileView() {
        return isMobileView;
    }

    /**
     *  get partyType
     *
     *  @return partyType
     */
    public java.lang.String getPartyType() {
        return partyType;
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
     *  set htmlFile
     */
    public void setHtmlFile(java.lang.String htmlFile) {
        this.htmlFile = htmlFile;
    }

    /**
     *  set isMobileView
     */
    public void setIsMobileView(boolean isMobileView) {
        this.isMobileView = isMobileView;
    }

    /**
     *  set partyType
     */
    public void setPartyType(java.lang.String partyType) {
        this.partyType = partyType;
    }

    
    /**
     * Check different according to primary key.
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof org.shaolin.bmdp.analyzer.be.PageHelperImpl))
            return false;
        org.shaolin.bmdp.analyzer.be.PageHelperImpl o = (org.shaolin.bmdp.analyzer.be.PageHelperImpl)obj;
        
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
        aBuf.append("org.shaolin.bmdp.analyzer.be.PageHelper");
    
        aBuf.append(" : ");
        
        aBuf.append("createDate=").append(createDate).append(", ");
        
        aBuf.append("enable=").append(_enable).append(", ");
        
        aBuf.append("id");
        aBuf.append("=");
        aBuf.append(id);
        aBuf.append(", ");
        
        aBuf.append("htmlFile");
        aBuf.append("=");
        aBuf.append(htmlFile);
        aBuf.append(", ");
        
        aBuf.append("isMobileView");
        aBuf.append("=");
        aBuf.append(isMobileView);
        aBuf.append(", ");
        
        aBuf.append("partyType");
        aBuf.append("=");
        aBuf.append(partyType);
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
            
        org.shaolin.bmdp.datamodel.bediagram.StringType htmlFileBEType = new org.shaolin.bmdp.datamodel.bediagram.StringType();
    
        //MemberType Define for htmlFile
        member = new MemberType();
        member.setName("htmlFile");
        member.setDescription("null");
        member.setType(htmlFileBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.BooleanType isMobileViewBEType = new org.shaolin.bmdp.datamodel.bediagram.BooleanType();
    
        //MemberType Define for isMobileView
        member = new MemberType();
        member.setName("isMobileView");
        member.setDescription("null");
        member.setType(isMobileViewBEType);
        memberTypeList.add(member);
            
        org.shaolin.bmdp.datamodel.bediagram.StringType partyTypeBEType = new org.shaolin.bmdp.datamodel.bediagram.StringType();
    
        //MemberType Define for partyType
        member = new MemberType();
        member.setName("partyType");
        member.setDescription("null");
        member.setType(partyTypeBEType);
        memberTypeList.add(member);
            
        return memberTypeList;
    }
    
    public IPageHelper createEntity ()
    {
        return new PageHelperImpl();
    }
    
}

        

