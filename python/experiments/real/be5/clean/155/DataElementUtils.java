package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.base.BeElementWithOriginModule;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeVectorCollection;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import one.util.streamex.StreamEx;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataElementUtils
{
    public static <T extends BeModelElement> List<T> toList(final BeModelCollection<T> collection, final Class<T> klass)
    {
        return collection.stream().collect(Collectors.toList());
    }

    /**
     * Saves the element to its parent collection.
     *
     * @param element
     * @throws NullPointerException     if the given element is null
     * @throws IllegalArgumentException if the given element has no parent
     * @throws RuntimeException         if saving of the element thrown an exception
     */
    public static void save(final BeModelElement element)
    {
        Objects.requireNonNull(element);
        Objects.requireNonNull(element.getOrigin());

        final BeModelCollection<BeModelElement> collection = element.getOrigin();

        try
        {
            collection.put(element);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes the element from its parent collection.
     *
     * @param element
     * @throws NullPointerException     if the given element is null
     * @throws IllegalArgumentException if the given element has no parent
     * @throws RuntimeException         if saving of the element thrown an exception
     */
    public static void remove(final BeModelElement element)
    {
        Objects.requireNonNull(element);
        Objects.requireNonNull(element.getOrigin());

        final BeModelCollection<BeModelElement> collection = element.getOrigin();

        try
        {
            collection.remove(element.getName());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the element without any notifications.
     *
     * @param element
     */
    @SuppressWarnings("unchecked")
    public static void saveQuiet(final BeModelElement element)
    {
        addQuiet(element.getOrigin(), element);
    }

    public static <T extends BeModelElement> void addQuiet(final BeModelCollection<T> collection, final T element)
    {
        if (collection == null)
            return;
        try
        {
            collection.put(element);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void removeQuiet(BeModelCollection<?> collection, final String element)
    {
        if (collection == null)
            return;
        try
        {
            collection.remove(element);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static <T extends BeModelElement> void putQuiet(final BeModelCollection<T> collection, final T element)
    {
        try
        {
            collection.put(element);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String toPath(final BeModelElement element)
    {
        return element.getCompletePath().toString();
    }

    public static BeModelElement fromPath(final Project root, final String path)
    {
        return fromPath(root, DataElementPath.create(path));
    }

    /**
     * Tries to select an element by the given path.
     *
     * @return an element or null
     */
    public static BeModelElement fromPath(final Project root, final DataElementPath path)
    {
        final BeModelCollection<BeModelElement> wrapper = new BeVectorCollection<>("wrapper", BeModelElement.class, null);
        putQuiet(wrapper, root);
        final Iterable<String> pathParts = Arrays.asList(path.getPathComponents());
        final Iterator<String> pathIterator = pathParts.iterator();

        return traversePath(pathIterator, wrapper);
    }

    // pathIterator is mutable, but it's OK as we can see each its element (pathPart)
    private static BeModelElement traversePath(Iterator<String> pathIterator, BeModelElement node)
    {
        assert pathIterator != null;
        assert node != null;

        if (!pathIterator.hasNext())
            return node; // found

        final String pathPart = pathIterator.next();

        if (!(node instanceof BeModelCollection))
            return null; // need but can't go further

        @SuppressWarnings("unchecked") final BeModelCollection<BeModelElement> collection = (BeModelCollection<BeModelElement>) node;

        if (!collection.contains(pathPart))
            return null; // not found

        final BeModelElement nextNode = collection.get(pathPart);

        return traversePath(pathIterator, nextNode);
    }

    public static boolean equals(BeModelCollection<? extends BeModelElement> c1, BeModelCollection<? extends BeModelElement> c2)
    {
        if (c1 == null && c2 == null)
            return true;
        if (c1 == null)
            return c2.getSize() == 0;
        if (c2 == null)
            return c1.getSize() == 0;
        if (c1.getSize() != c2.getSize())
            return false;
        for (BeModelElement e1 : c1)
        {
            BeModelElement e2 = c2.get(e1.getName());
            if (e2 == null)
                return false;
            if (!e1.equals(e2))
                return false;
        }
        return true;
    }

    public static boolean pathsEqual(BeModelElement lhs, BeModelElement rhs)
    {
        Objects.requireNonNull(lhs);
        Objects.requireNonNull(rhs);

        return toPath(lhs).equals(toPath(rhs));
    }

    /**
     * @param e element
     * @return Iterable over passed element e and all its parents
     */
    public static StreamEx<BeModelElement> parents(final BeModelElement e)
    {
        return StreamEx.iterate(e, BeModelElement::getOrigin).takeWhile(Objects::nonNull);
    }

    public static boolean hasPredecessor(final BeModelElement e, final BeModelElement predecessor)
    {
        return parents(e).has(predecessor);
    }

    public static <T extends BeModelElement> T parentOfType(final BeModelElement e, Class<T> clazz)
    {
        return parents(e).select(clazz).findFirst().orElse(null);
    }

    /**
     * Moves the model element back to the its module if the element is an entity item.
     *
     * @param modelElement
     */
    public static void moveBackToItsModule(final BeModelElement modelElement)
    {
        if (modelElement instanceof BeElementWithOriginModule)
        {
            BeElementWithOriginModule r = (BeElementWithOriginModule) modelElement;
            r.setOriginModuleName(r.getModule().getName());
        }
    }

    /**
     * Moves the model element to the application if the element is an entity item.
     *
     * @param modelElement
     */
    public static void moveToApplication(final BeModelElement modelElement)
    {
        if (modelElement instanceof BeElementWithOriginModule)
        {
            BeElementWithOriginModule r = (BeElementWithOriginModule) modelElement;
            r.setOriginModuleName(r.getProject().getProjectOrigin());
        }
    }
}
