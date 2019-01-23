package com.developmentontheedge.be5.metadata.model.base;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * Represent paths to DataElement in the repository
 * This object is read-only; any changes will generate new object
 * Note that DataElement represented by path may not exist
 * To construct the path object use DataElementPath.create method
 * If element name contains /, it will be replaced in path with \s
 * If element name contains \, it will be replaced in path with \\
 * Use escapeName/unescapeName static methods for these transformations
 *
 * @author lan
 */
public class DataElementPath implements Comparable<DataElementPath>, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final String PATH_SEPARATOR = "/";
    public static final DataElementPath EMPTY_PATH = new DataElementPath("", null);
    protected final String path;
    protected transient String name;
    protected transient DataElementPath parentPath;
    /** Path delimiter for complete names of data collections. */

    /**
     * Construct from path string
     * Use DataElementPath.create to create paths
     *
     * @param path - complete path to DataElement
     * @throws InvalidParameterException if path is incorrect
     */
    private DataElementPath(String path, DataElementPath parentPath)
    {
        this.path = path;
        this.parentPath = parentPath;
        validatePath();
    }

    public boolean isEmpty()
    {
        return path.length() == 0;
    }

    /**
     * Tests whether passed DataElementPath is an ancestor for current one
     *
     * @param ancestor DataElementPath to test
     * @return true if ancestor is actually an ancestor or elements are equal; false otherwise
     */
    public boolean isDescendantOf(DataElementPath ancestor)
    {
        if (ancestor.equals(EMPTY_PATH))
            return true;
        String[] fields1 = getPathComponents();
        String[] fields2 = ancestor.getPathComponents();
        if (fields2.length > fields1.length) return false;
        for (int i = 0; i < fields2.length; i++)
        {
            if (!fields1[i].equals(fields2[i])) return false;
        }
        return true;
    }

    /**
     * Tests whether passed DataElementPath is a child for current one (not necessarily immediate child)
     *
     * @param descendant DataElementPath to test
     * @return true if descendant is actually a descendant or elements are equal; false otherwise
     */
    public boolean isAncestorOf(DataElementPath descendant)
    {
        return descendant.isDescendantOf(this);
    }

    /**
     * Tests whether supplied path is the sibling to current one
     *
     * @param sibling - path to test
     * @return true if sibling is sibling for current path or equals to current path; false otherwise
     */
    public boolean isSibling(DataElementPath sibling)
    {
        String[] fields1 = getPathComponents();
        String[] fields2 = sibling.getPathComponents();
        if (fields1.length != fields2.length) return false;
        for (int i = 0; i < fields1.length - 1; i++)
        {
            if (!fields1[i].equals(fields2[i])) return false;
        }
        return true;
    }

    /**
     * Creates relative path string so that ancestor.getRelativePath(path.getPathDifference(ancestor)) equals to path
     *
     * @param ancestor path to some ancestor element
     * @return relative path string
     */
    public String getPathDifference(DataElementPath ancestor)
    {
        String[] myComponents = getPathComponents();
        int depth = ancestor.getDepth();
        StringBuilder result = new StringBuilder();
        for (int i = depth; i < myComponents.length; i++)
        {
            if (result.length() > 0) result.append(PATH_SEPARATOR);
            result.append(escapeName(myComponents[i]));
        }
        return result.toString();
    }

    public DataElementPath getCommonPrefix(DataElementPath other)
    {
        if (this.equals(EMPTY_PATH) || other.equals(EMPTY_PATH) || this.equals(other))
            return this;
        String[] myComponents = getPathComponents();
        String[] otherComponents = other.getPathComponents();
        DataElementPath result = EMPTY_PATH;
        for (int i = 0; i < Math.min(myComponents.length, otherComponents.length); i++)
        {
            if (!myComponents[i].equals(otherComponents[i]))
                break;
            result = result.getChildPath(myComponents[i]);
        }
        return result;
    }

    /**
     * Converts path relative to current to absolute path and returns it. Handy replacement for series of getChildPath/getSiblingPath/getParentPath
     *
     * @param relativePath - relative path. May contain ".." to go up or "." to stay.
     * @return created path
     */
    public DataElementPath getRelativePath(String relativePath)
    {
        //if(!relativePath.startsWith("./") && !relativePath.startsWith("../")) return new DataElementPath(relativePath);
        if (relativePath.isEmpty())
            return this;
        String[] elements = relativePath.split(PATH_SEPARATOR);
        DataElementPath path = this;
        for (String element : elements)
        {
            if (element.equals("."))
                continue;
            if (element.equals(".."))
            {
                path = path.getParentPath();
                continue;
            }
            path = path.getChildPath(unescapeName(element));
        }
        return path;
    }

    /**
     * Creates path for child item to current path
     *
     * @param name - name of child item (may not exist). Null name is considered as empty name
     * @return created DataElementPath
     */
    public DataElementPath getChildPath(String... names)
    {
        DataElementPath result = this;
        for (String name : names)
        {
            if (name == null)
                name = "";
            if (result.path.isEmpty())
            {
                if (name.isEmpty())
                    result = EMPTY_PATH;
                result = new DataElementPath(escapeName(name), EMPTY_PATH);
            }
            else
                result = new DataElementPath(result.path + PATH_SEPARATOR + escapeName(name), result);
        }
        return result;
    }

    /**
     * @return array of Strings containing path components ("data/Example/element" -> {"data", "Example", "element"})
     */
    public String[] getPathComponents()
    {
        if (equals(EMPTY_PATH)) return new String[0];
        String[] result = path.split(PATH_SEPARATOR, -1);
        for (int i = 0; i < result.length; i++) result[i] = unescapeName(result[i]);
        return result;
    }

    /**
     * @return number of path components
     */
    public int getDepth()
    {
        if (equals(EMPTY_PATH)) return 0;
        int depth = 1;
        for (int i = 0; i < path.length(); i++)
        {
            if (path.charAt(i) == '/') depth++;
        }
        return depth;
    }

    /**
     * Creates path for sibling item to current path
     *
     * @param name - name of sibling item (may not exist)
     * @return created DataElementPath
     */
    public DataElementPath getSiblingPath(String name)
    {
        return getParentPath().getChildPath(name);
    }

    /**
     * Creates path for parent item to current path
     * Note that neither current path nor parent path should actually exist
     *
     * @return created DataElementPath
     */
    public DataElementPath getParentPath()
    {
        DataElementPath _parentPath = parentPath;
        if (_parentPath == null)
        {
            int pos = path.lastIndexOf(PATH_SEPARATOR);
            _parentPath = pos <= -1 ? EMPTY_PATH : new DataElementPath(path.substring(0, pos), null);
            parentPath = _parentPath;
        }
        return _parentPath;
    }

    /**
     * Returns string representation of the path
     */
    @Override
    public String toString()
    {
        return path;
    }

    /**
     * Validates path
     */
    protected void validatePath()
    {
        // TODO: check whether path is valid
    }

    @Override
    public int hashCode()
    {
        return (path == null) ? 0 : path.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        DataElementPath other = (DataElementPath) obj;
        return Objects.equals(path, other.path);
    }

    /**
     * Returns last name component of current path
     */
    public String getName()
    {
        if (name == null)
        {
            int pos = path.lastIndexOf(PATH_SEPARATOR);
            if (pos != -1)
            {
                name = unescapeName(path.substring(pos + 1));
            }
            else
                name = unescapeName(path);
        }
        return name;
    }

    @Override
    public int compareTo(DataElementPath elem)
    {
        return path.compareTo(elem.path);
    }

    /**
     * Create and return DataElementPath if argument is not null (otherwise return null)
     */
    public static DataElementPath create(String path)
    {
        if (path == null) return null;
        if (path.isEmpty()) return EMPTY_PATH;
        return new DataElementPath(path, null);
    }

    /**
     * The same as create. Special for TextUtil.fromString
     */
    public static DataElementPath createInstance(String path)
    {
        return create(path);
    }

    /**
     * Returns DataElementPath constructed by existing DataElement if argument is not null (otherwise return null)
     *
     * @param element - element to construct from
     */
    public static DataElementPath create(BeModelElement element)
    {
        if (element == null)
            return null;
        if (element instanceof BeModelCollection)
        {
            return ((BeModelCollection<?>) element).getCompletePath();
        }
        if (element.getName() == null)
            throw new InvalidParameterException("Cannot obtain element path: element has no name");
        if (element.getOrigin() == null)
        {
            return create(escapeName(element.getName()));
        }
        return element.getOrigin().getCompletePath().getChildPath(element.getName());
    }

    /**
     * Returns DataElementPath constructed by existing DataCollection and its child name if argument is not null (otherwise return null)
     *
     * @param dc        - parent DataCollection
     * @param childName - name of the child item (child may not exist)
     */
    public static DataElementPath create(BeModelCollection<?> dc, String childName)
    {
        if (dc == null || childName == null) return null;
        // TODO simplify
        return dc.getCompletePath().getChildPath(childName);
    }

    /**
     * Construct from array of paths. Equivalent to new DataElementPath(basePath).getRelativePath(path[0]).getRelativePath(path[1])...
     *
     * @param basePath - first slice. If null, then null will be returned
     * @param path     - list of path slices. Note that it's path slices, not names, thus they should be escaped even if they contain only one path component
     */
    public static DataElementPath create(String basePath, String... path)
    {
        if (basePath == null) return null;
        DataElementPath result = create(basePath);
        for (String pathComponent : path)
            result = result.getRelativePath(pathComponent);
        return result;
    }

    /**
     * Escapes special chars in element name
     *
     * @param name unescaped name
     * @return escaped name
     */
    public static String escapeName(String name)
    {
        char[] result = null;
        int j = 0;
        for (int i = 0; i < name.length(); i++)
        {
            char curChar = name.charAt(i);
            if (curChar == '\\' || curChar == '/')
            {
                if (result == null)
                {
                    result = new char[name.length() * 2 - i];
                    for (j = 0; j < i; j++) result[j] = name.charAt(j);
                }
                result[j++] = '\\';
                result[j++] = curChar == '/' ? 's' : '\\';
            }
            else if (result != null) result[j++] = curChar;
        }
        return result == null ? name : new String(result, 0, j);
    }

    /**
     * Unescapes special chars in element name
     *
     * @param escapedName escaped name
     * @return unescaped name
     */
    public static String unescapeName(String escapedName)
    {
        char[] result = null;
        int j = 0;
        for (int i = 0; i < escapedName.length(); i++)
        {
            char curChar = escapedName.charAt(i);
            if (curChar == '\\')
            {
                if (result == null)
                {
                    result = new char[escapedName.length() - 1];
                    for (j = 0; j < i; j++) result[j] = escapedName.charAt(j);
                }
                i++;
                if (i < escapedName.length())
                {
                    char nextChar = escapedName.charAt(i);
                    result[j++] = nextChar == 's' ? '/' : nextChar;
                }
            }
            else if (result != null) result[j++] = curChar;
        }
        return result == null ? escapedName : new String(result, 0, j);
    }
}
