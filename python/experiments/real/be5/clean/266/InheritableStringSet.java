package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class InheritableStringSet
{
    Set<String> includedValues = Collections.emptySet();
    Set<String> excludedValues = Collections.emptySet();

    final Project project;
    boolean usePrototype = false;
    InheritableStringSet prototype = null;

    public InheritableStringSet(BeModelElement owner)
    {
        this.project = owner.getProject();
    }

    public InheritableStringSet(BeModelElement owner, InheritableStringSet source)
    {
        this(owner);
        this.prototype = source.prototype;
        this.usePrototype = source.usePrototype;
        addInclusionAll(source.getIncludedValues());
        addExclusionAll(source.getExcludedValues());
    }

    public Project getProject()
    {
        return project;
    }

    public Set<String> getIncludedValues()
    {
        return Collections.unmodifiableSet(includedValues);
    }

    public Set<String> getExcludedValues()
    {
        return Collections.unmodifiableSet(excludedValues);
    }

    /**
     * Splits a collection of role and role groups to
     * included and excluded parts to change the state
     * of the set.
     *
     * @param values
     */
    public void parseValues(Collection<String> values)
    {
        TreeSet<String> included = new TreeSet<>();
        TreeSet<String> excluded = new TreeSet<>();

        usePrototype = !values.isEmpty();

        for (String value : values)
        {
            if (value.startsWith("-"))
            {
                excluded.add(value.substring(1));
            }
            else if (value.startsWith("+") && !value.startsWith("+/")) // exception for inputs like "+/- Category"
            {
                included.add(value.substring(1));
            }
            else
            {
                included.add(value);
                usePrototype = false;
            }
        }

        setInternal(included);
        setExcludedInternal(excluded);
    }

    /**
     * Joins included and excluded roles to list.
     *
     * @return list that describes this role set
     */
    public List<String> printValues()
    {
        List<String> result = new ArrayList<>();
        for (String value : includedValues)
        {
            result.add(usePrototype ? '+' + value : value);
        }
        for (String value : excludedValues)
        {
            result.add('-' + value);
        }
        return result;
    }

    /**
     * Roles and role groups. Each role group name should begin with the '@' sign.
     *
     * @param values
     */
    public void setValues(Collection<String> values)
    {
        if (values == null || values.isEmpty())
            setInternal(new TreeSet<String>());
        else
            setInternal(new TreeSet<>(values));
    }

    public void setExcludedValues(Collection<String> excludedValues)
    {
        if (excludedValues == null || excludedValues.isEmpty())
            setExcludedInternal(new TreeSet<String>());
        else
            setExcludedInternal(new TreeSet<>(excludedValues));
    }

    /**
     * @return roles and role groups
     */
    @PropertyName("Included values")
    public String[] getValuesArray()
    {
        return includedValues.toArray(new String[0]);
    }

    public void setValuesArray(String[] values)
    {
        if (values == null || values.length == 0)
            setInternal(new TreeSet<String>());
        else
            setInternal(new TreeSet<>(Arrays.asList(values)));
    }

    @PropertyName("Excluded values")
    public String[] getExcludedValuesArray()
    {
        return excludedValues.toArray(new String[0]);
    }

    public void setExcludedValuesArray(String[] excludedValues)
    {
        if (excludedValues == null || excludedValues.length == 0)
            setExcludedInternal(new TreeSet<String>());
        else
            setExcludedInternal(new TreeSet<>(Arrays.asList(excludedValues)));
    }

    @PropertyName("List of prototype values")
    public String getPrototypeValues()
    {
        if (prototype == null)
            return "";
        return String.join(", ", prototype.getFinalValues());
    }

    @PropertyName("List of computed values")
    public String getFinalValuesString()
    {
        return String.join(", ", getFinalValues());
    }

    public boolean isPrototypeHidden()
    {
        return prototype == null;
    }

    public Set<String> getAllIncludedValues()
    {
        HashSet<String> result = new HashSet<>();
        if (usePrototype && prototype != null)
            result.addAll(prototype.getAllIncludedValues());
        result.addAll(includedValues);
        return result;
    }

    public Set<String> getAllExcludedValues()
    {
        HashSet<String> result = new HashSet<>();
        if (usePrototype && prototype != null)
            result.addAll(prototype.getAllExcludedValues());
        result.addAll(excludedValues);
        return result;
    }

    public Set<String> getFinalValues()
    {
        TreeSet<String> values = new TreeSet<>();
        if (usePrototype && prototype != null)
            values.addAll(prototype.getFinalValues());
        values.addAll(getFinalIncludedValues());
        values.removeAll(getFinalExcludedValues());

        return Collections.unmodifiableSet(values);
    }

    /**
     * Substitutes all role groups as sets of groups and combines all these roles together.
     *
     * @return set of all roles, that was explicitly or implicitly (using groups) selected
     */
    public Set<String> getFinalIncludedValues()
    {
        return getAllIncludedValues();
    }

    public Set<String> getFinalExcludedValues()
    {
        return getAllExcludedValues();
    }

    @PropertyName("Inherit values from module")
    public boolean isUsePrototype()
    {
        return usePrototype;
    }

    public void setUsePrototype(boolean usePrototype)
    {
        if (usePrototype != this.usePrototype)
        {
            this.usePrototype = usePrototype;
            customizeAndFireChanged();
        }
    }

    public void setPrototype(boolean use, InheritableStringSet prototype)
    {
        this.usePrototype |= use;
        this.prototype = prototype;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(includedValues) * 31 + Objects.hashCode(excludedValues);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        InheritableStringSet other = (InheritableStringSet) obj;
        return getFinalValues().equals(other.getFinalValues());
    }

    public boolean includesExplicitly(Object role)
    {
        return includedValues.contains(role);
    }

    public boolean excludesExplicitly(String role)
    {
        return excludedValues.contains(role);
    }

    /**
     * @param value value to include
     */
    public boolean add(String value)
    {
        TreeSet<String> set = new TreeSet<>(includedValues);
        removeExclusion(value);
        boolean result = set.add(value);
        setInternal(set);
        return result;
    }

    public boolean addExclusion(String value)
    {
        TreeSet<String> set = new TreeSet<>(excludedValues);
        remove(value);
        boolean result = set.add(value);
        setExcludedInternal(set);
        return result;
    }

    public boolean remove(String value)
    {
        TreeSet<String> set = new TreeSet<>(includedValues);
        boolean result = set.remove(value);
        setInternal(set);
        return result;
    }

    public boolean removeExclusion(String value)
    {
        TreeSet<String> set = new TreeSet<>(excludedValues);
        boolean result = set.remove(value);
        setExcludedInternal(set);
        return result;
    }

    public void clear()
    {
        includedValues = Collections.emptySet();
        excludedValues = Collections.emptySet();
    }

    public boolean addInclusionAll(Collection<? extends String> c)
    {
        TreeSet<String> set = new TreeSet<>(includedValues);
        boolean result = set.addAll(c);
        setInternal(set);
        return result;
    }

    public boolean addExclusionAll(Collection<? extends String> c)
    {
        TreeSet<String> set = new TreeSet<>(excludedValues);
        boolean result = set.addAll(c);
        setExcludedInternal(set);
        return result;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    private void setInternal(TreeSet<String> values)
    {
        if (this.includedValues.equals(values))
            return;
        if (values.isEmpty())
            this.includedValues = Collections.emptySet();
        else if (values.size() == 1)
            this.includedValues = Collections.singleton(values.first());
        else
            this.includedValues = values;

        customizeAndFireChanged();
    }

    private void setExcludedInternal(TreeSet<String> excludedValues)
    {
        if (this.excludedValues.equals(excludedValues))
            return;
        if (excludedValues.isEmpty())
            this.excludedValues = Collections.emptySet();
        else if (excludedValues.size() == 1)
            this.excludedValues = Collections.singleton(excludedValues.first());
        else
            this.excludedValues = excludedValues;

        customizeAndFireChanged();
    }

    protected void customizeAndFireChanged()
    {
    }

    public boolean isEmpty()
    {
        return getAllIncludedValues().isEmpty() && getAllExcludedValues().isEmpty();
    }
}
