package com.developmentontheedge.be5.metadata.serialization.yaml.deserializers;


import com.developmentontheedge.be5.metadata.QueryType;
import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.Icon;
import com.developmentontheedge.be5.metadata.model.base.BeElementWithProperties;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.serialization.Field;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.SerializationConstants;
import com.developmentontheedge.beans.util.Beans;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.developmentontheedge.be5.metadata.serialization.SerializationConstants.TAG_PROPERTIES;

class BaseDeserializer
{
    protected final LoadContext loadContext;
    protected final Path path;

    public BaseDeserializer(LoadContext loadContext, final Path path)
    {
        this.loadContext = loadContext;
        Objects.requireNonNull(path);
        this.path = path;
    }

    public BaseDeserializer(LoadContext loadContext)
    {
        this.loadContext = loadContext;
        this.path = null;
    }

    public void readFields(BeModelElement target, Map<String, Object> content, List<Field> fields)
    {
        Collection<String> customizableProperties = target.getCustomizableProperties();

        for (final Field field : fields)
        {
            if (field.name.equals("name") || (customizableProperties.contains(field.name) && !content.containsKey(field.name)))
            {
                continue;
            }

            try
            {
                Class<?> type = Beans.getBeanPropertyType(target, field.name);
                final Object value = readField(content, field, type);

                if (value != null || !type.isPrimitive())
                    Beans.setBeanPropertyValue(target, field.name, value);

                target.customizeProperty(field.name);
            }
            catch (final Exception e)
            {
                loadContext.addWarning(new ReadException(e, target, path, "Error reading field " + field.name));
            }
        }
    }

    private Object readField(Map<String, Object> content, Field field, Class<?> klass)
    {
        Object fieldValue = content.get(field.name);
        if (fieldValue == null)
        {
            return field.defaultValue;
        }
        if (!(fieldValue instanceof Boolean) && !(fieldValue instanceof Number) && !(fieldValue instanceof String)
                && !(fieldValue instanceof Enum))
        {
            throw new IllegalArgumentException("Invalid value: expected scalar");
        }
        final String value = fieldValue.toString();

        try
        {
            return castValue(klass, value);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new AssertionError();
        }
    }

    private /*static*/ Object castValue(final Class<?> klass, final String value) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        if (klass == Boolean.class || klass == boolean.class)
        {
            return Boolean.parseBoolean(value);
        }

        if (klass == Integer.class || klass == int.class)
        {
            return Integer.parseInt(value);
        }

        if (klass == Long.class || klass == long.class)
        {
            return Long.parseLong(value);
        }

        if (klass == String.class)
        {
            return value;
        }

        if (klass == QueryType.class)
        {
            return QueryType.fromString(value);
        }

        return klass.getConstructor(String.class).newInstance(value);
    }

    protected void readDocumentation(final Map<String, Object> source, final BeModelElement target)
    {
        final String documenation = (String) source.get(SerializationConstants.TAG_COMMENT);
        target.setComment(documenation != null ? documenation : "");
    }

    protected void readUsedExtras(final Map<String, Object> source, final BeModelElement target) throws ReadException
    {
        final Object serializedExtras = source.get(SerializationConstants.TAG_EXTRAS);

        if (serializedExtras != null)
        {
            final List<String> extras = asStrList(serializedExtras);
            if (extras.contains(""))
                loadContext.addWarning(new ReadException(target, path, "Extras tag contains empty string: probably it's incorrectly specified in YAML"));
            target.setUsedInExtras(extras.toArray(new String[extras.size()]));
        }
    }

    protected void readProperties(final Map<String, Object> elementBody, final BeElementWithProperties target) throws ReadException
    {
        final Object serializedProperties = elementBody.get(TAG_PROPERTIES);

        if (serializedProperties == null)
        {
            return;
        }

        for (final Object serializedProperty : asList(serializedProperties))
        {
            final Map<String, Object> serializedProperty0 = asMap(serializedProperty);

            if (serializedProperty0.size() != 1)
            {
                loadContext.addWarning(new ReadException(path, "Property should contain a key-value pair"));
                continue;
            }

            Map.Entry<String, Object> entry = serializedProperty0.entrySet().iterator().next();
            final String name = entry.getKey();
            final String value = asStr(entry.getValue());
            target.setProperty(name, value);
        }
    }

    protected void readIcon(final Map<String, Object> element, final Icon icon)
    {
        try
        {
            if (element.containsKey(SerializationConstants.ATTR_ICON))
            {
                icon.setMetaPath((String) element.get(SerializationConstants.ATTR_ICON));
                icon.load();
                icon.setOriginModuleName(icon.getOwner().getProject().getProjectOrigin());
                ((BeModelCollection<?>) icon.getOwner()).customizeProperty("icon");
            }
        }
        catch (final ReadException e)
        {
            loadContext.addWarning(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected List<String> readList(final Map<String, Object> element, final String attributeName)
    {
        final Object value = element.get(attributeName);

        if (value instanceof List)
            return Collections.unmodifiableList((List<String>) value);

        final String strValue = (String) value;

        if (strValue == null || strValue.trim().isEmpty())
        {
            return Collections.emptyList();
        }

        final List<String> result = new ArrayList<>();

        for (String item : strValue.trim().split(";"))
        {
            item = item.trim();
            if (!item.isEmpty())
            {
                result.add(item);
            }
        }

        return result;
    }

    public List<String> asStrList(Object object) throws ReadException
    {
        if (object == null)
            return Collections.emptyList();
        if (object instanceof String)
            return Collections.singletonList((String) object);
        if (object instanceof List)
        {
            final List<?> list = (List<?>) object;
            final List<String> strings = new ArrayList<>();
            for (Object element : list)
                if (element instanceof String)
                    strings.add((String) element);
                else
                    throw new ReadException(path, "Invalid file format: string expected");
            return strings;
        }

        throw new ReadException(path, "Invalid file format: list or string expected");
    }

    @SuppressWarnings("unchecked")
    protected List<Object> asList(Object object) throws ReadException
    {
        if (object instanceof List)
            return (List<Object>) object;

        throw new ReadException(path, "Invalid file format: list expected");
    }

    protected String asStr(Object object) throws ReadException
    {
        if (object instanceof String)
            return (String) object;

        throw new ReadException(path, "Invalid file format: string expected");
    }

    protected boolean nullableAsBool(Object object) throws ReadException
    {
        if (object == null)
            return false;

        if (object instanceof Boolean)
            return (boolean) object;

        if (object.toString().equals("true"))
            return true;

        if (object.toString().equals("false"))
            return false;

        throw new ReadException(path, "Invalid file format: boolean expected");
    }

    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> asMaps(Object object)
    {
        if (object instanceof List)
        {
            final List<?> list = (List<?>) object;
            final List<Map<String, Object>> maps = new ArrayList<>();
            for (Object element : list)
                if (element instanceof Map)
                    maps.add((Map<String, Object>) element);
            return maps;
        }

        if (object instanceof Map)
        {
            final List<Map<String, Object>> splitted = new ArrayList<>();
            final Map<String, Object> map = (Map<String, Object>) object;
            for (Map.Entry<String, Object> entry : map.entrySet())
            {
                final String name = entry.getKey();
                final Object value = entry.getValue();
                final Map<String, Object> adaptedEntry = new LinkedHashMap<>();
                adaptedEntry.put(name, value);
                splitted.add(adaptedEntry);
            }
            return splitted;
        }

        return Collections.emptyList();
    }

    protected List<Map.Entry<String, Object>> asPairs(Object object) throws ReadException
    {
        if (object instanceof List)
        {
            final List<?> list = (List<?>) object;
            final List<Map.Entry<String, Object>> pairs = new ArrayList<>();
            for (Object listPair : list)
            {
                if (!(listPair instanceof Map))
                    throw new ReadException(path, "Invalid file format: pair expected");

                @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) listPair;

                if (map.size() != 1)
                    throw new ReadException(path, "Invalid file format: pair expected");

                pairs.add(map.entrySet().iterator().next());
            }
            return pairs;
        }

        throw new ReadException(path, "Invalid file format: list of pairs expected");
    }

    // public -> protected
    @SuppressWarnings("unchecked")
    public Map<String, Object> asMap(Object object) throws ReadException
    {
        if (object instanceof Map)
            return (Map<String, Object>) object;
        throw new ReadException(path, "Invalid file format: map expected");
    }

    protected Map<String, Object> asMapOrEmpty(Object object) throws ReadException
    {
        if (object == null)
            return Collections.emptyMap();
        return asMap(object);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getRootMap(Object object, String name) throws ReadException
    {
        try
        {
            if (!(object instanceof Map))
                throw new IllegalArgumentException("Invalid file format: map expected");
            Map<String, Object> topLevelMap = (Map<String, Object>) object;
            if (!topLevelMap.containsKey(name))
                throw new IllegalArgumentException("Invalid file format: top-level element '" + name + "' must be present");
            if (topLevelMap.size() > 1)
                throw new IllegalArgumentException("Invalid file format: there must be only one top-level element '" + name + "'");
            Object rootObject = topLevelMap.get(name);
            if (!(rootObject instanceof Map))
                throw new IllegalArgumentException("Invalid file format: top-level element '" + name + "' must be a map");
            return (Map<String, Object>) rootObject;
        }
        catch (IllegalArgumentException e)
        {
            throw new ReadException(path, e.getMessage());
        }
    }

    protected void checkChildren(BeModelElement context, Map<String, Object> element, Object... allowedFields)
    {
        Set<String> allowed = getAllowedFields(allowedFields);

        for (String name : element.keySet())
        {
            if (!allowed.contains(name))
            {
                String message = "Unknown child element found: " + name + ", possible values: " + allowed;
                loadContext.addWarning(new ReadException(context, path, message));
            }
        }
    }

    protected Set<String> getAllowedFields(Object... allowedFields)
    {
        Set<String> allowed = new HashSet<>();

        for (Object allowedField : allowedFields)
        {
            if (allowedField instanceof Field)
            {
                allowed.add(((Field) allowedField).name);
            }
            else if (allowedField instanceof Collection)
            {
                allowed.addAll(getAllowedFields(((Collection<?>) allowedField).toArray()));
            }
            else
                allowed.add(allowedField.toString());
        }

        return allowed;
    }

    @SuppressWarnings("unchecked")
    public void save(BeModelElement element)
    {
        if (element == null)
            return;
        @SuppressWarnings("rawtypes")
        BeModelCollection origin = element.getOrigin();
        if (origin == null)
            return;
        if (origin.contains(element.getName()))
            loadContext.addWarning(new ReadException(element, path, "Duplicate element"));
        else
            origin.put(element);
    }
}