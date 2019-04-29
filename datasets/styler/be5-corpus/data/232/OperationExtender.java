package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.beans.annot.PropertyName;

public class OperationExtender extends BeModelElementSupport
{
    private String className;
    private int invokeOrder = 0;
    private String originModule;

    private static String generateName(Operation owner, String module)
    {
        BeModelCollection<OperationExtender> parent = owner.getOrCreateExtenders();
        int i = 0;
        while (true)
        {
            String name = String.format("%s - %04d", module == null ? owner.getOriginModuleName() : module, ++i);
            if (!parent.contains(name))
                return name;
        }
    }

    public OperationExtender(Operation owner, String module)
    {
        super(generateName(owner, module), owner.getOrCreateExtenders());
        this.originModule = module == null ? owner.getOriginModuleName() : module;
    }

    /**
     * Copy constructor
     *
     * @param owner
     * @param orig
     */
    public OperationExtender(Operation owner, OperationExtender orig)
    {
        this(owner, owner.getOriginModuleName());
        setInvokeOrder(orig.getInvokeOrder());
        setClassName(orig.getClassName());
    }

    public Operation getOperation()
    {
        return (Operation) (getOrigin().getOrigin());
    }

    @PropertyName("Java class for extender")
    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
        fireChanged();
    }

    @PropertyName("Invokation order")
    public int getInvokeOrder()
    {
        return invokeOrder;
    }

    public void setInvokeOrder(int invokeOrder)
    {
        this.invokeOrder = invokeOrder;
        fireChanged();
    }

    @PropertyName("Module")
    public String getOriginModuleName()
    {
        return originModule;
    }

    public void setOriginModuleName(String name)
    {
        this.originModule = name;
        fireChanged();
    }

    @Override
    public boolean isCustomized()
    {
        return !getOriginModuleName().equals(getModule().getName()) && getOriginModuleName().equals(getProject().getProjectOrigin());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return debugEquals("null");
        if (getClass() != obj.getClass())
            return debugEquals("class");
        OperationExtender other = (OperationExtender) obj;
        if (className == null)
        {
            if (other.className != null)
                return debugEquals("className");
        }
        else if (!className.equals(other.className))
            return debugEquals("className");
        if (invokeOrder != other.invokeOrder)
            return debugEquals("invokeOrder");
        return true;
    }

    protected void fireChanged()
    {
        final BeModelCollection<OperationExtender> extenders = getOperation().getExtenders();
        if (extenders != null && extenders.contains(getName()))
            getOperation().fireCodeChanged();
    }

    public OperationExtender copyFor(Operation operation)
    {
        return new OperationExtender(operation, this);
    }
}
