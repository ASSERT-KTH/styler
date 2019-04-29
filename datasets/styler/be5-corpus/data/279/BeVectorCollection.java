package com.developmentontheedge.be5.metadata.model.base;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Main DataCollection implementation for BE4
 *
 * @param <T> element type
 * @author lan
 */
public class BeVectorCollection<T extends BeModelElement> extends BeModelElementSupport implements BeModelCollection<T>
{
    private Map<String, T> elements;
    private final boolean saveOrder;
    private final Class<? extends T> elementClass;
    private DataElementPath completeName = null;
    private boolean propagateCodeChange = false;

    public BeVectorCollection(String name, Class<? extends T> elementClass, BeModelCollection<?> parent, boolean saveOrder)
    {
        super(name, parent);
        this.elementClass = elementClass;
        this.saveOrder = saveOrder;
    }

    public BeVectorCollection(String name, Class<? extends T> elementClass, BeModelCollection<?> parent)
    {
        this(name, elementClass, parent, false);
    }

    public BeVectorCollection<T> propagateCodeChange()
    {
        this.propagateCodeChange = true;
        return this;
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        List<ProjectElementException> errors = new ArrayList<>();
        for (BeModelElement element : this)
            errors.addAll(element.getErrors());
        return errors;
    }

    @Override
    public boolean hasErrors()
    {
        for (BeModelElement element : this)
            if (element.hasErrors())
                return true;
        return false;
    }

    @Override
    public boolean isCustomized()
    {
        if (super.isCustomized())
            return true;
        for (BeModelElement element : this)
            if (element.isCustomized())
                return true;
        return false;
    }

    @Override
    public synchronized T get(String name)
    {
        if (elements == null)
            return null;
        return elements.get(name);
    }

    @SuppressWarnings("unchecked")
    public <S extends BeModelElement> BeModelCollection<S> getCollection(String name, Class<S> clazz)
    {
        // TODO: check class and throw reasonable exception
        return (BeModelCollection<S>) get(name);
    }

    public <S extends BeModelElement> BeVectorCollection<S> getOrCreateCollection(String name, Class<S> clazz)
    {
        @SuppressWarnings("unchecked")
        BeVectorCollection<S> element = (BeVectorCollection<S>) get(name);
        if (element == null)
        {
            element = new BeVectorCollection<>(name, clazz, this);
            DataElementUtils.saveQuiet(element);
        }
        return element;
    }

    @Override
    public BeVectorCollection<T> clone(BeModelCollection<?> origin, String name)
    {
        return clone(origin, name, true);
    }

    protected void beforeCloningElements()
    {

    }

    @Override
    protected BeVectorCollection<T> clone(BeModelCollection<?> origin, String name, boolean inherit)
    {
        @SuppressWarnings("unchecked")
        BeVectorCollection<T> clone = (BeVectorCollection<T>) super.clone(origin, name, inherit);
        clone.completeName = null;
        clone.elements = null;
        clone.beforeCloningElements();
        for (T element : this)
        {
            clone.saveClone(element, inherit);
        }
        return clone;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void merge(BeModelCollection<T> other, boolean ignoreMyItems, boolean inherit)
    {
        if (other == null)
        {
            return;
        }
        mergeThis(other, inherit);
        for (T otherElement : other)
        {
            T element = this.get(otherElement.getName());
            if (element instanceof BeModelCollection && otherElement instanceof BeModelCollection)
            {
                ((BeModelCollection<?>) element).merge((BeModelCollection) otherElement, ignoreMyItems, inherit);
            }
            else if (element instanceof BeModelElementSupport && otherElement instanceof BeModelElementSupport)
            {
                ((BeModelElementSupport) element).mergeThis(otherElement, inherit);
            }
            else if (element == null)
            {
                // Do not merge EntityItems with the same origin as our project:
                // Probably it's some element we deleted from project, but
                // didn't synchronized with db yet
                if (ignoreMyItems && otherElement instanceof BeElementWithOriginModule
                        && ((BeElementWithOriginModule) otherElement).getOriginModuleName().equals(getProject().getProjectOrigin()))
                    continue;
                saveClone(otherElement, inherit);
            }
        }
    }

    void saveClone(T element, boolean inherit) throws InternalError
    {
        BeModelElement clone;
        if (element instanceof BeModelElementSupport)
        {
            clone = ((BeModelElementSupport) element).clone(this, element.getName(), inherit);
        }
        else
        {
            clone = element.clone(this, element.getName());
        }
        DataElementUtils.saveQuiet(clone);
    }

    @Override
    public T put(T element)
    {
        if (element == null)
            throw new IllegalArgumentException("dataElement cannot be null: " + getCompletePath());

        T prev = null;
        String dataElementName = element.getName();
        if (dataElementName == null)
            throw new IllegalArgumentException("dataElement name cannot be null: " + getCompletePath());
        synchronized (this)
        {
            if (elements == null)
            {
                elements = this.saveOrder ? new LinkedHashMap<>() : new TreeMap<>();
            }
            prev = elements.put(dataElementName, element);
        }
        if (prev != null)
            fireElementChanged(this, this, dataElementName, prev);
        else
            fireElementAdded(this, dataElementName);
        updateLastModification();
        return prev;
    }

    @Override
    public void remove(String name)
    {
        if (elements == null)
            return;
        T prev;
        synchronized (this)
        {
            prev = elements.remove(name);
        }
        if (prev != null)
            fireElementRemoved(this, name, prev);
        updateLastModification();
    }

    protected void clear()
    {
        elements = null;
    }

    @Override
    public List<String> getAvailableNames()
    {
        List<String> result = new ArrayList<>();
        for (T element : this)
        {
            if (element.isAvailable())
            {
                result.add(element.getName());
            }
        }
        return result;
    }

    @Override
    public Collection<T> getAvailableElements()
    {
        final BeVectorCollection<T> beVectorCollection = this;
        return getAvailableElements(beVectorCollection);
    }

    public T getAvailableElement(String name)
    {
        final BeVectorCollection<T> beVectorCollection = this;
        return getAvailableElement(name, beVectorCollection);
    }

    private <T2 extends BeModelElement> T2 getAvailableElement(String name, final Iterable<T2> iterable)
    {
        for (T2 element : iterable)
        {
            if (element.isAvailable() && name.equals(element.getName()))
            {
                return element;
            }
        }
        return null;
    }

    private static <T extends BeModelElement> Collection<T> getAvailableElements(final Iterable<T> iterable)
    {
        Collection<T> result = new ArrayList<>();
        for (T element : iterable)
        {
            if (element.isAvailable())
            {
                result.add(element);
            }
        }
        return result;
    }

    @Override
    public int getSize()
    {
        if (elements == null)
            return 0;
        return elements.size();
    }

    @Override
    public Class<? extends BeModelElement> getDataElementType()
    {
        return elementClass;
    }

    @Override
    public boolean contains(String name)
    {
        if (elements == null)
            return false;
        synchronized (this)
        {
            return elements.containsKey(name);
        }
    }

    @Override
    public Iterator<T> iterator()
    {
        return elements == null ? Collections.emptyIterator() : elements.values().iterator();
    }

    @Override
    public List<String> getNameList()
    {
        if (elements == null)
            return Collections.emptyList();
        synchronized (this)
        {
            return new ArrayList<>(elements.keySet());
        }
    }

    @Override
    public void fireCodeChanged()
    {
        if (propagateCodeChange)
        {
            final BeModelCollection<?> origin = getOrigin();
            if (origin != null && origin.get(getName()) == this)
                origin.fireCodeChanged();
        }
    }

    @Override
    public DataElementPath getCompletePath()
    {
        if (completeName == null)
        {
            BeModelCollection<?> origin = getOrigin();
            completeName = (origin == null ? DataElementPath.EMPTY_PATH : origin.getCompletePath()).getChildPath(getName());
        }
        return completeName;
    }

    public void replace(T oldElement, T newElement)
    {
        if (!saveOrder)
        {
            remove(oldElement.getName());
            put(newElement);
            return;
        }
        Map<String, T> oldElements = elements;
        clear();
        for (Entry<String, T> entry : oldElements.entrySet())
        {
            if (entry.getValue() == oldElement)
                put(newElement);
            else
                put(entry.getValue());
        }
    }

    /**
     * @param source
     * @param dataElementName
     * @throws Exception
     */
    protected void fireElementAdded(Object source, String dataElementName)
    {
        fireCodeChanged();
    }

    /**
     * @param source
     * @param owner
     * @param dataElementName
     * @param oldElement
     * @param primaryEvent
     * @throws Exception
     */
    protected void fireElementChanged(Object source, BeModelCollection<?> owner, String dataElementName, BeModelElement oldElement)
    {
        fireCodeChanged();
    }

    /**
     * @param source
     * @param dataElementName
     * @param oldElement
     * @throws Exception
     */
    protected void fireElementRemoved(Object source, String dataElementName, BeModelElement oldElement)
    {
        fireCodeChanged();
    }

    @Override
    public boolean isEmpty()
    {
        return elements == null || elements.isEmpty();
    }

    @Override
    public StreamEx<T> stream()
    {
        return elements == null ? StreamEx.empty() : StreamEx.ofValues(elements);
    }

    @Override
    public StreamEx<String> names()
    {
        return elements == null ? StreamEx.empty() : StreamEx.ofKeys(elements);
    }
}
