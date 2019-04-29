package com.developmentontheedge.be5.metadata.model.base;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.Module;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.Collection;
import java.util.List;

public interface BeModelElement extends Cloneable
{
    /**
     * Returns a unique name of the data element.
     *
     * @return A unique name of the data element.
     */
    String getName();

    /**
     * @return complete path to this element
     */
    public DataElementPath getCompletePath();

    /**
     * @return list of errors associated with this element and its children
     */
    public List<ProjectElementException> getErrors();

    /**
     * @return true if element or its children has errors
     */
    @PropertyName("Element has errors")
    public boolean hasErrors();

    /**
     * @return true if module element is customized in application
     */
    public boolean isCustomized();

    /**
     * Origin of BeModelElement is always BeModelCollection
     */
    @SuppressWarnings("rawtypes")
    public BeModelCollection getOrigin();

    /**
     * @return project
     */
    public Project getProject();

    /**
     * @return module
     */
    public Module getModule();

    /**
     * @return the documentation of this element.
     */
    public String getComment();

    /**
     * Changes the documentation of this element.
     *
     * @return
     */
    public void setComment(String comment);

    /**
     * Timestamp of last modification in this element (including children)
     *
     * @return
     */
    public long getLastModified();

    /**
     * @return all elements that have direct references to this element.
     */
    public Collection<BeModelElement> getDependentElements();

    /**
     * If some element is a descendant of some other element, then the first one will be omitted.
     *
     * @return reduced list of elements that have direct and indirect references to this element.
     */
    public Collection<BeModelElement> getTransitiveDependentElements();

    /**
     * @return used extras
     */
    public String[] getUsedInExtras();

    /**
     * Sets used extras
     *
     * @param usedInExtras
     */
    public void setUsedInExtras(String[] usedInExtras);

    /**
     * @return true if element is available according to specified used extras
     */
    public boolean isAvailable();

    /**
     * Returns list of customized properties
     *
     * @return
     */
    public Collection<String> getCustomizedProperties();

    /**
     * Decustomize property
     *
     * @param propertyName
     */
    public void inheritProperty(String propertyName);

    /**
     * Customize property
     *
     * @param propertyName
     */
    public void customizeProperty(String propertyName);

    /**
     * Returns list of properties which can be customized
     *
     * @return
     */
    public Collection<String> getCustomizableProperties();

    /**
     * Tells whether this element is from application.
     *
     * @return
     */
    public boolean isFromApplication();

    public BeModelElement clone(BeModelCollection<?> newOrigin, String newName);
}
