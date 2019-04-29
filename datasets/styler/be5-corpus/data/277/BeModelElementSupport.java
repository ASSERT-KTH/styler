package com.developmentontheedge.be5.metadata.model.base;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.util.Beans;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;


public abstract class BeModelElementSupport implements BeModelElement
{
    protected String comment = "";
    private long lastModified = 0;
    private String[] usedInExtras;
    protected BeModelElement prototype;
    protected Set<String> customizedProperties;
    protected boolean customizing = false;
    private String name;
    private BeModelCollection<?> origin;

    public BeModelElementSupport(String name, BeModelCollection<?> origin)
    {
        this.name = name;
        this.origin = origin;
    }

    /**
     * Return name of the data element.
     *
     * @return Name of the data element.
     * @todo final specifier needed.
     */
    @Override
    @PropertyName("Name")
    @PropertyDescription("Name of the element")
    public String getName()
    {
        return name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        return Collections.emptyList();
    }

    @Override
    public boolean hasErrors()
    {
        return !getErrors().isEmpty();
    }

    @Override
    public boolean isCustomized()
    {
        return prototype != null && customizedProperties != null && !customizedProperties.isEmpty();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public BeModelCollection getOrigin()
    {
        return origin;
    }

    @Override
    public Project getProject()
    {
        BeModelCollection<?> origin = getOrigin();
        return origin == null ? null : origin.getProject();
    }

    @Override
    public Module getModule()
    {
        BeModelCollection<?> origin = getOrigin();
        return origin == null ? null : origin.getModule();
    }

    @Override
    public String getComment()
    {
        return comment;
    }

    @Override
    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    @Override
    public long getLastModified()
    {
        return lastModified;
    }

    public void updateLastModification()
    {
        lastModified = Math.max(lastModified + 1, System.currentTimeMillis());
        BeModelCollection<?> origin = getOrigin();
        if (origin != null)
        {
            origin.updateLastModification();
        }
    }

    @Override
    public DataElementPath getCompletePath()
    {
        return DataElementPath.create(this);
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + ":" + getCompletePath();
    }

    protected boolean debugEquals(String property)
    {
        PrintStream debugStream = getProject().getDebugStream();
        if (debugStream != null)
        {
            debugStream.println(getCompletePath() + ": difference: " + property);
        }
        return false;
    }

    /**
     * Default implementation: always returns an empty collection.
     */
    @Override
    public Collection<BeModelElement> getDependentElements()
    {
        return Collections.emptyList();
    }

    @Override
    public final Collection<BeModelElement> getTransitiveDependentElements()
    {
        Set<BeModelElement> result = new LinkedHashSet<>();
        result.add(this);
        Set<BeModelElement> nextStepGenerators = new HashSet<>();
        nextStepGenerators.add(this);

        while (!nextStepGenerators.isEmpty())
        {
            final Set<BeModelElement> newGeneration = new HashSet<>();

            for (final BeModelElement nextStepGenerator : nextStepGenerators)
            {
                newGeneration.addAll(nextStepGenerator.getDependentElements());
                if (nextStepGenerator instanceof BeModelCollection)
                {
                    ((BeModelCollection<?>) nextStepGenerator).stream().forEach(newGeneration::add);
                }
            }

            newGeneration.removeAll(result);
            result.addAll(newGeneration);
            nextStepGenerators = newGeneration;
        }

        result = reduce(result);
        result.remove(this);

        return result;
    }

    /**
     * Removes child elements from the set.
     *
     * @param elements
     * @return
     */
    private static Set<BeModelElement> reduce(final Set<BeModelElement> elements)
    {
        final Set<BeModelElement> reduced = new LinkedHashSet<>();

        for (BeModelElement element : elements)
            if (!hasAncestorIn(element, elements))
                reduced.add(element);

        return reduced;
    }

    private static boolean hasAncestorIn(final BeModelElement element, final Set<BeModelElement> elements)
    {
        for (final BeModelElement element2 : elements)
            if (element2 != element && element2.getCompletePath().isAncestorOf(element.getCompletePath()))
                return true;

        return false;
    }

    @PropertyName("Extras where this element is defined")
    @Override
    public String[] getUsedInExtras()
    {
        return usedInExtras;
    }

    @Override
    public void setUsedInExtras(String[] usedInExtras)
    {
        this.usedInExtras = usedInExtras;
    }

    @PropertyName("Available in current project")
    @Override
    public boolean isAvailable()
    {
        if (usedInExtras == null || usedInExtras.length == 0)
            return true;
        Module module = getModule();
        String[] extras = module == null ? Strings2.EMPTY : module.getExtras();
        Project project = getProject();
        for (String usedInExtra : usedInExtras)
        {
            if (!hasExtra(extras, project, usedInExtra))
                return false;
        }
        return true;
    }

    private boolean hasExtra(String[] extras, Project project, String usedInExtra)
    {
        if (project.hasCapability(usedInExtra))
            return true;
        for (String extra : extras)
        {
            if (usedInExtra.equals(extra))
                return true;
        }
        return false;
    }

    public BeModelElement getPrototype()
    {
        return prototype;
    }

    @Override
    public Collection<String> getCustomizedProperties()
    {
        if (customizedProperties == null)
        {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(customizedProperties);
    }

    @Override
    public void inheritProperty(String propertyName)
    {
        if (customizedProperties != null)
            customizedProperties.remove(propertyName);
        fireChanged();
    }

    protected void fireChanged()
    {
        /* intended to be overridden */
    }

    @Override
    public void customizeProperty(String propertyName)
    {
        if (!getCustomizableProperties().contains(propertyName)
                || (customizedProperties != null && customizedProperties.contains(propertyName)))
            return;
        internalCustomizeProperty(propertyName);
        fireChanged();
    }

    protected void internalCustomizeProperty(String propertyName)
    {
        if (customizedProperties == null)
        {
            customizedProperties = new HashSet<>();
        }
        customizedProperties.add(propertyName);
    }

//    protected <V> V getValueWithReflection( String propertyName, V value, V defaultValue )
//    {
//        if ( customizedProperties != null && customizedProperties.contains( propertyName ) )
//            return value;
//        if ( prototype == null )
//            return defaultValue;
//        try
//        {
//            @SuppressWarnings( "unchecked" )
//            V result = ( V ) Beans.getBeanPropertyValue( prototype, propertyName );
//            return result;
//        }
//        catch ( Exception e )
//        {
//            throw new RuntimeException( "Unexpected exception when retrieving property '" + propertyName + "' of " + getCompletePath() + ": "
//                + e, e );
//        }
//    }

    protected <V> V getValue(String propertyName, V value, V defaultValue, Supplier<V> getPrototypeValue)
    {
        if (customizedProperties != null && customizedProperties.contains(propertyName))
            return value;
        if (prototype == null)
            return defaultValue;

        return getPrototypeValue.get();
    }

    protected <V> V getValue(String propertyName, V value, Supplier<V> getPrototypeValue)
    {
        return getValue(propertyName, value, null, getPrototypeValue);
    }

    protected <V> V customizeProperty(String propertyName, V oldValue, V newValue)
    {
        this.customizing = true;
        try
        {
            if (!Objects.equals(oldValue, newValue))
            {
                internalCustomizeProperty(propertyName);
            }
        }
        finally
        {
            this.customizing = false;
        }
        return newValue;
    }

    @Override
    public Collection<String> getCustomizableProperties()
    {
        return Collections.emptySet();
    }

    protected void mergeThis(BeModelElement other, boolean inherit)
    {
        if (inherit)
        {
            this.prototype = other;
        }
        else
        {
            this.prototype = null;
            Set<String> customizableProperties = new HashSet<>(getCustomizableProperties());
            customizableProperties.removeAll(getCustomizedProperties());
            for (String customizableProperty : customizableProperties)
            {
                try
                {
                    Object value = Beans.getBeanPropertyValue(other, customizableProperty);
                    ComponentModel info = ComponentFactory.getModel(this, ComponentFactory.Policy.DEFAULT);
                    Property property = info.findProperty(customizableProperty);
                    if (property != null)
                    {
                        if (!customizableProperty.equals("roles"))
                        {
                            property.setValue(value);
                        }
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public BeModelElementSupport clone(BeModelCollection<?> origin, String name)
    {
        return clone(origin, name, true);
    }

    protected BeModelElementSupport clone(BeModelCollection<?> origin, String name, boolean inherit)
    {
        BeModelElementSupport clone;
        try
        {
            clone = (BeModelElementSupport) super.clone();
            clone.name = name;
            clone.origin = origin;
        }
        catch (CloneNotSupportedException e)
        {
            throw new AssertionError("Unexpected exception", e);
        }
        if (inherit)
        {
            if (clone.customizedProperties != null && !clone.customizedProperties.isEmpty())
            {
                clone.prototype = this;
                clone.customizedProperties = null;
            }
        }
        return clone;
    }

    @Override
    public boolean isFromApplication()
    {
        return getModule() == getProject().getApplication();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        BeModelElementSupport that = (BeModelElementSupport) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}
