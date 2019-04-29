package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.be5.metadata.model.base.TemplateElement;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PageCustomization extends BeModelElementSupport implements TemplateElement
{
    public static final String DOMAIN_OPERATION = "operation";
    public static final String DOMAIN_OPERATION_FORM = "operation.form";
    public static final String DOMAIN_OPERATION_BUTTONS_HEADER = "operation.buttons.header";
    public static final String DOMAIN_OPERATION_BUTTONS_FOOTER = "operation.buttons.footer";
    public static final String DOMAIN_PRINT = "print";
    public static final String DOMAIN_PRINT_HEADER = "print.header";
    public static final String DOMAIN_PRINT_FOOTER = "print.footer";
    public static final String DOMAIN_QUERY = "query";
    public static final String DOMAIN_QUERY_HEADER = "query.header";
    public static final String DOMAIN_QUERY_TOP = "query.top";
    public static final String DOMAIN_QUERY_FOOTER = "query.footer";
    public static final String DOMAIN_INDEX_HEADER = "index.header";
    public static final String DOMAIN_INDEX_FOOTER = "index.footer";
    public static final String DOMAIN_INDEX = "index";
    public static final String DOMAIN_STATIC_PAGE_HEADER = "static_page.header";
    public static final String DOMAIN_STATIC_PAGE_FOOTER = "static_page.footer";

    private static final String[] MODULE_DOMAIN_LIST = new String[]{DOMAIN_OPERATION, DOMAIN_OPERATION_FORM, DOMAIN_OPERATION_BUTTONS_HEADER, DOMAIN_OPERATION_BUTTONS_FOOTER, DOMAIN_QUERY, DOMAIN_QUERY_TOP, DOMAIN_QUERY_HEADER, DOMAIN_QUERY_FOOTER, DOMAIN_PRINT, DOMAIN_PRINT_HEADER, DOMAIN_PRINT_FOOTER, DOMAIN_INDEX_HEADER, DOMAIN_INDEX_FOOTER, DOMAIN_INDEX};
    private static final String[] ENTITY_DOMAIN_LIST = new String[]{DOMAIN_OPERATION, DOMAIN_OPERATION_FORM, DOMAIN_OPERATION_BUTTONS_HEADER, DOMAIN_OPERATION_BUTTONS_FOOTER, DOMAIN_QUERY, DOMAIN_QUERY_TOP, DOMAIN_QUERY_HEADER, DOMAIN_QUERY_FOOTER};
    private static final String[] QUERY_DOMAIN_LIST = new String[]{DOMAIN_QUERY, DOMAIN_QUERY_TOP, DOMAIN_QUERY_HEADER, DOMAIN_QUERY_FOOTER, DOMAIN_PRINT, DOMAIN_PRINT_HEADER, DOMAIN_PRINT_FOOTER};
    private static final String[] OPERATION_DOMAIN_LIST = new String[]{DOMAIN_OPERATION, DOMAIN_OPERATION_FORM, DOMAIN_OPERATION_BUTTONS_HEADER, DOMAIN_OPERATION_BUTTONS_FOOTER};
    private static final String[] STATIC_PAGE_DOMAIN_LIST = new String[]{DOMAIN_STATIC_PAGE_HEADER, DOMAIN_STATIC_PAGE_FOOTER};

    public static final String TYPE_CSS = "css";
    public static final String TYPE_JS = "js";
    public static final String TYPE_HTML = "html";
    public static final String TYPE_JSP = "jsp";
    public static final String TYPE_DBOM = "dbom";
    public static final String TYPE_CLASS = "class";

    private static final String[] TYPES = new String[]{TYPE_CLASS, TYPE_CSS, TYPE_DBOM, TYPE_HTML, TYPE_JS, TYPE_JSP};

    private Set<String> roles = Collections.emptySet();
    private final String type;
    private String code;
    private final String domain;
    private String originModule;
    public static final String CUSTOMIZATIONS_COLLECTION = "Customizations";

    public PageCustomization(String type, String domain, BeVectorCollection<?> origin)
    {
        super(generateName(type, domain, origin), origin);
        this.type = type;
        this.domain = domain;
        Entity entity = getEntity();
        if (entity != null)
        {
            setOriginModuleName(entity.getModule().getName());
        }
        else
        {
            setOriginModuleName(getProject().getProjectOrigin());
        }
    }

    public static String generateName(String type, String domain, BeModelCollection<?> origin)
    {
        String location = type;
        BeModelCollection<?> owner = origin.getOrigin();
        if (owner instanceof EntityItem)
        {
            location = ((EntityItem) owner).getEntity().getName() + "." + owner.getName() + "." + type;
        }
        else if (owner instanceof Entity || owner instanceof StaticPage)
        {
            location = owner.getName() + "." + type;
        }
        return domain + "." + location;
    }

    public Entity getEntity()
    {
        BeModelElement owner = getOwner();
        if (owner instanceof EntityItem)
        {
            return ((EntityItem) owner).getEntity();
        }
        if (owner instanceof Entity)
        {
            return (Entity) owner;
        }
        return null;
    }

    public BeModelElement getOwner()
    {
        return getOrigin().getOrigin();
    }

    public String[] getDomains()
    {
        BeModelElement owner = getOwner();
        return getDomains(owner);
    }

    public static String[] getDomains(BeModelElement owner)
    {
        if (owner instanceof Operation)
        {
            return OPERATION_DOMAIN_LIST.clone();
        }
        if (owner instanceof Query)
        {
            return QUERY_DOMAIN_LIST.clone();
        }
        if (owner instanceof Entity)
        {
            return ENTITY_DOMAIN_LIST.clone();
        }
        if (owner instanceof Module)
        {
            return MODULE_DOMAIN_LIST.clone();
        }
        if (owner instanceof StaticPage)
        {
            return STATIC_PAGE_DOMAIN_LIST.clone();
        }
        return Strings2.EMPTY;
    }

    public static String[] getTypes()
    {
        return TYPES.clone();
    }

    @PropertyName("Type")
    public String getType()
    {
        return type;
    }

    @PropertyName("Code")
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
        updateLastModification();
        fireChanged();
    }

    @PropertyName("Result")
    public ParseResult getResult()
    {
        return getProject().mergeTemplate(this);
    }

    @PropertyName("Roles")
    public String[] getRolesArray()
    {
        return roles.toArray(new String[roles.size()]);
    }

    public void setRolesArray(String[] roles)
    {
        setRoles(Arrays.asList(roles));
    }

    public Set<String> getRoles()
    {
        return Collections.unmodifiableSet(roles);
    }

    public void setRoles(java.util.Collection<String> roles)
    {
        if (roles == null)
            this.roles = Collections.emptySet();
        else
        {
            this.roles = new TreeSet<>(roles);
        }
        fireChanged();
    }

    @PropertyName("Domain")
    public String getDomain()
    {
        return domain;
    }

    public boolean merge(PageCustomization other)
    {
        if (code == null)
        {
            if (other.code != null)
                return false;
        }
        else if (!code.equals(other.code))
            return false;
        if (type == null)
        {
            if (other.type != null)
                return false;
        }
        else if (!type.equals(other.type))
            return false;
        if (!domain.equals(other.domain))
            return false;
        this.roles.addAll(other.roles);
        return true;
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
        PageCustomization other = (PageCustomization) obj;
        if (type == null)
        {
            if (other.type != null)
                return debugEquals("type");
        }
        else if (!type.equals(other.type))
            return debugEquals("type");
        if (!getName().equals(other.getName()))
            return debugEquals("name");
        if (!roles.equals(other.roles))
            return debugEquals("roles");
        if (!getResult().equals(other.getResult()))
            return debugEquals("result");
        return true;
    }

    @Override
    public PageCustomization clone(BeModelCollection<?> origin, String name)
    {
        PageCustomization clone = (PageCustomization) super.clone(origin, name);
        clone.roles = new TreeSet<>(roles);
        return clone;
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
        Entity entity = getEntity();
        if (entity == null)
        {
            return false;
        }
        Module module = getModule();
        return !getOriginModuleName().equals(module.getName()) && getOriginModuleName().equals(module.getProject().getProjectOrigin());
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> result = new ArrayList<>();
        try
        {
            ModelValidationUtils.checkValueInSet(this, "domain", domain, getDomains());
        }
        catch (ProjectElementException e)
        {
            result.add(e);
        }
        try
        {
            ModelValidationUtils.checkValueInSet(this, "type", type, TYPES);
        }
        catch (ProjectElementException e)
        {
            result.add(e);
        }
        ProjectElementException error = getResult().getError();
        if (error != null && !error.isNoError())
        {
            DataElementPath path = getCompletePath();
            if (error.getPath().equals(path.toString()))
                result.add(error);
            else
                result.add(new ProjectElementException(path, "query", error));
        }
        return result;
    }

    @Override
    public String getTemplateCode()
    {
        return getCode();
    }

    @Override
    public BeVectorCollection<?> getOrigin()
    {
        return (BeVectorCollection<?>) super.getOrigin();
    }

    @Override
    protected void fireChanged()
    {
        final BeVectorCollection<?> origin = getOrigin();

        if (origin == null || origin.get(getName()) != this)
            return;

        if (origin instanceof PageCustomizations && origin.getOrigin() instanceof Module)
        {
            origin.fireCodeChanged();
            return;
        }

        if (origin.getOrigin() instanceof EntityItem && origin.getOrigin().get(PageCustomization.CUSTOMIZATIONS_COLLECTION) == origin)
        {
            ((EntityItem) origin.getOrigin()).fireChanged();
            return;
        }
    }
}
