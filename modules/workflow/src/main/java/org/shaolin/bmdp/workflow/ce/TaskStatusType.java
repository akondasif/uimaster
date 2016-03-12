/*
 * This code is generated automatically, any change will be replaced after rebuild.
 * Generated on Fri Mar 11 20:52:26 CST 2016
 */

package org.shaolin.bmdp.workflow.ce;
import java.util.*;
import org.shaolin.bmdp.runtime.ce.IConstantEntity;
import org.shaolin.bmdp.runtime.ce.AbstractConstant;

/**
 * 
 * entityName: org.shaolin.bmdp.workflow.ce.TaskStatusType
 *
 */
public final class TaskStatusType extends AbstractConstant
{
    public static final String ENTITY_NAME = "org.shaolin.bmdp.workflow.ce.TaskStatusType";
    
    protected static final long serialVersionUID = 0x811b9115811b9115L;
    private static String i18nBundle = "org_shaolin_bmdp_workflow_i18n";

    //User-defined constant define

    public static final TaskStatusType NOT_SPECIFIED = new TaskStatusType(CONSTANT_DEFAULT_VALUE, -1, null, null, null, null, false);

    public static final TaskStatusType NOTSTARTED = new TaskStatusType("NotStarted", 0, "org.shaolin.bmdp.workflow.ce.TaskStatusType.NOTSTARTED", "Not Started", null, null,false);

    public static final TaskStatusType INPROGRESS = new TaskStatusType("InProgress", 1, "org.shaolin.bmdp.workflow.ce.TaskStatusType.INPROGRESS", "In Progress", null, null,false);

    public static final TaskStatusType COMPLETED = new TaskStatusType("Completed", 2, "org.shaolin.bmdp.workflow.ce.TaskStatusType.COMPLETED", "Completed", null, null,false);

    public static final TaskStatusType EXPIRED = new TaskStatusType("Expired", 3, "org.shaolin.bmdp.workflow.ce.TaskStatusType.EXPIRED", "Expired", null, null,false);

    public static final TaskStatusType CANCELLED = new TaskStatusType("Cancelled", 4, "org.shaolin.bmdp.workflow.ce.TaskStatusType.CANCELLED", "Cancelled", null, null,false);

    //End of constant define

    //Common constant define
    public TaskStatusType()
    {
        constantList.add(NOT_SPECIFIED);
        constantList.add(NOTSTARTED);
        constantList.add(INPROGRESS);
        constantList.add(COMPLETED);
        constantList.add(EXPIRED);
        constantList.add(CANCELLED);
    }

    private TaskStatusType(long id, String value, int intValue, String i18nKey, String description)
    {
        super(id, value, intValue, i18nKey, description);
    }
    
    private TaskStatusType(String value, int intValue, String i18nKey,
        String description, Date effTime, Date expTime)
    {
        super(value, intValue, i18nKey, description, effTime, expTime);
    }

    private TaskStatusType(String value, int intValue, String i18nKey,
            String description, Date effTime, Date expTime, boolean isPassivated)
    {
        super(value, intValue, i18nKey, description, effTime, expTime, isPassivated);
    }
    
    public String getI18nBundle()
    {
        return i18nBundle;
    }

    public void setI18nBundle(String bundle)
    {
        i18nBundle = bundle;
    }

    protected AbstractConstant __create(String value, int intValue, String i18nKey,
        String description, Date effTime, Date expTime)
    {
        return new TaskStatusType(value, intValue, i18nKey,
            description, effTime, expTime);
    }

    protected String getTypeEntityName()
    {
        return ENTITY_NAME;
    }

}

