package com.developmentontheedge.be5.metadata.model.base;

import com.developmentontheedge.be5.metadata.model.DataElementUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class BeCaseInsensitiveCollection<T extends BeModelElement> extends BeVectorCollection<T>
{

    public BeCaseInsensitiveCollection(String name, Class<? extends T> elementClass, BeModelCollection<?> parent, boolean saveOrder)
    {
        super(name, elementClass, parent, saveOrder);
    }

    public BeCaseInsensitiveCollection(String name, Class<? extends T> elementClass, BeModelCollection<?> parent)
    {
        super(name, elementClass, parent);
    }

    public T getCaseInsensitive(String name)
    {
        for (T element : this)
        {
            if (element.getName().compareToIgnoreCase(name) == 0)
                return element;
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void merge(BeModelCollection<T> other, boolean ignoreMyItems, boolean inherit)
    {
        mergeThis(other, inherit);
        Map<String, T> myElements = new LinkedHashMap<>();
        for (T element : this)
        {
            myElements.put(element.getName().toLowerCase(), element);
        }
        clear();
        for (T otherElement : other)
        {
            T element = myElements.remove(otherElement.getName().toLowerCase());
            if (element instanceof BeModelCollection && otherElement instanceof BeModelCollection)
            {
                ((BeModelCollection<?>) element).merge((BeModelCollection) otherElement, ignoreMyItems, inherit);
            }
            else if (element instanceof BeModelElementSupport && otherElement instanceof BeModelElementSupport)
            {
                ((BeModelElementSupport) element).mergeThis(otherElement, inherit);
            }
            if (element == null)
            {
                // Do not merge EntityItems with the same origin as our project:
                // Probably it's some element we deleted from project, but didn't synchronized with db yet
                if (ignoreMyItems && otherElement instanceof BeElementWithOriginModule
                        && ((BeElementWithOriginModule) otherElement).getOriginModuleName().equals(getProject().getProjectOrigin()))
                    continue;
                saveClone(otherElement, inherit);
            }
            else
            {
                DataElementUtils.saveQuiet(element);
            }
        }
        for (T element : myElements.values())
        {
            DataElementUtils.saveQuiet(element);
        }
    }

}
