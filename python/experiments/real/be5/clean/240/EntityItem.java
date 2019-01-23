package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeElementWithOriginModule;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyName;

/**
 * Superclass for Operation and Query
 *
 * @author lan
 */
public abstract class EntityItem extends BeVectorCollection<BeModelElement> implements BeElementWithOriginModule
{
    private Icon icon = new Icon(this);
    private RoleSet roles = new RoleSet(this);
    private String originModule;
    private String wellKnownName = "";
    private String notSupported;
    private Long contextID;
    private Long categoryID;
    private boolean secure = false;
    private String layout = "";

    public EntityItem(String name, BeModelCollection<?> origin)
    {
        super(name, BeModelElement.class, origin);
        this.originModule = getModule().getName();
        this.icon.setOriginModuleName(originModule);
    }

    public Entity getEntity()
    {
        return (Entity) (getOrigin().getOrigin());
    }

    public abstract String getEntityItemType();

    @PropertyName("Icon")
    public Icon getIcon()
    {
        return icon;
    }

    @PropertyName("Roles")
    public RoleSet getRoles()
    {
        if (prototype == null || (customizedProperties != null && customizedProperties.contains("roles")))
            return roles;
        roles.clear();
        roles.setPrototype(true, ((EntityItem) prototype).getRoles());
        return roles;
    }

    /**
     * Stub method necessary for bean info
     *
     * @param roles roles to set (ignored)
     */
    public void setRoles(RoleSet roles)
    {
        throw new UnsupportedOperationException();
    }

    @PropertyName("Module")
    @Override
    public String getOriginModuleName()
    {
        return originModule;
    }

    @Override
    public void setOriginModuleName(String name)
    {
        this.originModule = name;
        fireChanged();
    }

    @PropertyName("Well-known name")
    public String getWellKnownName()
    {
        return getValue("wellKnownName", wellKnownName, "", () -> ((EntityItem) prototype).getWellKnownName());
    }

    public void setWellKnownName(String wellKnownName)
    {
        this.wellKnownName = customizeProperty("wellKnownName", this.wellKnownName, Strings2.nullToEmpty(wellKnownName));
        fireChanged();
    }

    @PropertyName("Not supported message")
    public String getNotSupported()
    {
        return getValue("notSupported", notSupported, () -> ((EntityItem) prototype).getNotSupported());
    }

    public void setNotSupported(String notSupported)
    {
        this.notSupported = customizeProperty("notSupported", this.notSupported, notSupported);
        fireChanged();
    }

    @PropertyName("Secure")
    public boolean isSecure()
    {
        return getValue("secure", secure, false, () -> ((EntityItem) prototype).isSecure());
    }

    public void setSecure(boolean secure)
    {
        this.secure = customizeProperty("secure", this.secure, secure);
        fireChanged();
    }

    @PropertyName("Context ID")
    public Long getContextID()
    {
        return getValue("contextID", contextID, () -> ((EntityItem) prototype).getContextID());
    }

    public void setContextID(Long contextID)
    {
        this.contextID = customizeProperty("contextID", this.contextID, contextID);
        fireChanged();
    }

    @PropertyName("Category ID")
    public Long getCategoryID()
    {
        return getValue("categoryID", categoryID, () -> ((EntityItem) prototype).getCategoryID());
    }

    public void setCategoryID(Long categoryID)
    {
        this.categoryID = customizeProperty("categoryID", this.categoryID, categoryID);
        fireChanged();
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
        EntityItem other = (EntityItem) obj;
        if (!getName().equals(other.getName()))
            return debugEquals("name");

        if (getLayout() == null)
        {
            if (other.getLayout() != null)
                return debugEquals("layout");
        }
        else if (!getLayout().equals(other.getLayout()))
            return debugEquals("layout");

        if (getWellKnownName() == null)
        {
            if (other.getWellKnownName() != null)
                return debugEquals("wellKnownName");
        }
        else if (!getWellKnownName().equals(other.getWellKnownName()))
            return debugEquals("wellKnownName");

        if (getNotSupported() == null)
        {
            if (other.getNotSupported() != null)
                return debugEquals("notSupported");
        }
        else if (!getNotSupported().equals(other.getNotSupported()))
            return debugEquals("notSupported");
        if (getCategoryID() == null)
        {
            if (other.getCategoryID() != null)
                return debugEquals("categoryID");
        }
        else if (!getCategoryID().equals(other.getCategoryID()))
            return debugEquals("categoryID");
        if (getContextID() == null)
        {
            if (other.getContextID() != null)
                return debugEquals("contextID");
        }
        else if (!getContextID().equals(other.getContextID()))
            return debugEquals("contextID");
        if (isSecure() != other.isSecure())
            return debugEquals("isSecure");
        if (!getIcon().equals(other.getIcon()))
            return debugEquals("icon");
        if (!getRoles().equals(other.getRoles()))
            return debugEquals("roles");
        if (!DataElementUtils.equals(getCollection(PageCustomization.CUSTOMIZATIONS_COLLECTION, PageCustomization.class),
                other.getCollection(PageCustomization.CUSTOMIZATIONS_COLLECTION, PageCustomization.class)))
            return debugEquals("customizations");
        return true;
    }

    @Override
    public boolean hasErrors()
    {
        return !getErrors().isEmpty();
    }

    @Override
    public boolean isCustomized()
    {
        final Project project = getProject();
        if (project != null && getOriginModuleName() != null && project.getProjectOrigin().equals(getOriginModuleName())
                && !getModule().getName().equals(getOriginModuleName()))
            return true;
        for (BeModelElement element : this)
            if (element.isCustomized())
                return true;
        return false;
    }

    @Override
    public void merge(BeModelCollection<BeModelElement> other, boolean filterItems, boolean inherit)
    {
        super.merge(other, filterItems, inherit);
        if (inherit)
        {
            this.roles.setPrototype(false, ((EntityItem) other).roles);
        }
        else
        {
            this.roles.addInclusionAll(((EntityItem) other).roles.getAllIncludedValues());
            this.roles.addExclusionAll(((EntityItem) other).roles.getAllExcludedValues());
        }
    }

    @Override
    public EntityItem clone(BeModelCollection<?> origin, String name, boolean inherit)
    {
        EntityItem clone = (EntityItem) super.clone(origin, name, inherit);
        clone.icon = new Icon(clone);
        clone.icon.copyFrom(icon);
        if (clone.prototype == null)
        {
            clone.roles = new RoleSet(clone, roles);
        }
        else
        {
            clone.roles = new RoleSet(clone);
            clone.roles.setPrototype(true, ((EntityItem) clone.prototype).roles);
        }
        clone.setOriginModuleName(getOriginModuleName());
        return clone;
    }

    @Override
    protected void internalCustomizeProperty(String propertyName)
    {
        super.internalCustomizeProperty(propertyName);
        moveToApplication();
    }

    @Override
    protected void fireChanged()
    {
        if (!customizing)
            getEntity().fireCodeChanged();
    }

    public void moveToApplication()
    {
        setOriginModuleName(getProject().getProjectOrigin());
        fireChanged();
    }

    @Override
    protected void mergeThis(BeModelElement other, boolean inherit)
    {
        super.mergeThis(other, inherit);
        if (customizedProperties == null || !customizedProperties.contains("icon"))
            icon.copyFrom(((EntityItem) other).icon);
    }

    public String getLayout()
    {
        return getValue("layout", layout, "", () -> ((EntityItem) prototype).getLayout());
    }

    public void setLayout(String layout)
    {
        this.layout = customizeProperty("layout", this.layout, Strings2.nullToEmpty(layout));
        fireChanged();
    }
}
