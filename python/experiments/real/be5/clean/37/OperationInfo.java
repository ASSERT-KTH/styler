package com.developmentontheedge.be5.operation.model;

import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.RoleSet;


/**
 * Immutable wrapper for {@link com.developmentontheedge.be5.metadata.model.Operation}
 */
public class OperationInfo
{
    private final Operation operationModel;

    public OperationInfo(Operation operationModel)
    {
        this.operationModel = operationModel;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Properties
    //

    /**
     * @PENDING
     */

    //public String getWellKnownName()                { return operationModel.getWellKnownName();   }
    //public String getNotSupported()                 { return operationModel.getNotSupported();  }
    //public Long getContextID()                      { return operationModel.getContextID(); }
    //public boolean isSecure()                       { return operationModel.isSecure(); }
    //public Icon getIcon()                           { return operationModel.getIcon();  }
    //public BeModelCollection<OperationExtender> getExtenders()
    public Operation getModel()
    {
        return operationModel;
    }

    public String getName()
    {
        return operationModel.getName();
    }

    public RoleSet getRoles()
    {
        return operationModel.getRoles();
    }

    public String getType()
    {
        return operationModel.getType();
    }

    public String getCode()
    {
        return operationModel.getCode();
    }

    public int getRecords()
    {
        return operationModel.getRecords();
    }

    public String getVisibleWhen()
    {
        return operationModel.getVisibleWhen();
    }

    public int getExecutionPriority()
    {
        return operationModel.getExecutionPriority();
    }

    public String getLogging()
    {
        return operationModel.getLogging();
    }

    public boolean isConfirm()
    {
        return operationModel.isConfirm();
    }

    public Long getCategoryID()
    {
        return operationModel.getCategoryID();
    }

    public Entity getEntity()
    {
        return operationModel.getEntity();
    }

    public Object getLayout()
    {
        return operationModel.getLayout();
    }

    public String getEntityName()
    {
        return operationModel.getEntity().getName();
    }

    public String getPrimaryKey()
    {
        return operationModel.getEntity().getPrimaryKey();
    }

}
