package com.developmentontheedge.be5.metadata.model;


public class OperationSet extends InheritableStringSet
{
    private final Query owner;

    public OperationSet(Query owner)
    {
        super(owner);
        this.owner = owner;
    }

    public OperationSet(Query owner, OperationSet source)
    {
        super(owner, source);
        this.owner = owner;
    }

    @Override
    protected void customizeAndFireChanged()
    {
        if (this.owner != null) // can be called by superclass constructor, thus null is possible
        {
            this.owner.customizeProperty("operationNames");
            this.owner.fireChanged();
        }
    }

    public Query getOwner()
    {
        return owner;
    }
}
