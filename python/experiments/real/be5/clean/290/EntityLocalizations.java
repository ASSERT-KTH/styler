package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.model.LocalizationElement.SpecialTopic;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class EntityLocalizations extends BeModelElementSupport
{
    public static final String DISPLAY_NAME_TOPIC = "displayName";

    Set<LocalizationElement> elements = new TreeSet<>();

    public EntityLocalizations(String name, LanguageLocalizations origin)
    {
        super(name, origin);
    }

    private LanguageLocalizations getLanguageLocalizations()
    {
        return (LanguageLocalizations) getOrigin();
    }

    public Set<LocalizationElement> elements()
    {
        return Collections.unmodifiableSet(elements);
    }

    /**
     * @param topics
     * @param key
     * @param value
     * @return true if there was no such localization before
     */
    public boolean add(java.util.Collection<String> topics, String key, String value)
    {
        final boolean added = elements.add(new LocalizationElement(topics, key, value));
        fireChanged();

        return added;
    }

    public boolean remove(String key, Set<String> topics)
    {
        Set<LocalizationElement> newElements = new TreeSet<>();
        boolean changed = false;
        Iterator<LocalizationElement> iterator = elements.iterator();
        while (iterator.hasNext())
        {
            LocalizationElement el = iterator.next();
            if (el.getKey().equals(key))
            {
                List<String> oldTopics = new ArrayList<>(el.getTopics());
                oldTopics.removeAll(topics);
                if (oldTopics.size() <= el.getTopics().size())
                {
                    iterator.remove();
                    changed = true;
                    if (!oldTopics.isEmpty())
                    {
                        newElements.add(new LocalizationElement(oldTopics, el.getKey(), el.getValue()));
                    }
                }
            }
        }
        if (changed)
        {
            elements.addAll(newElements);
            fireChanged();
            return true;
        }
        return false;
    }

    public void set(final String key, final String value, final Set<String> topics)
    {
        remove(key, topics);
        elements.add(new LocalizationElement(topics, key, value));
        fireChanged();
    }

    public void change(String topic, String key, String value)
    {
        Iterator<LocalizationElement> iterator = elements.iterator();
        while (iterator.hasNext())
        {
            LocalizationElement el = iterator.next();
            if (el.key.equals(key) && el.topics.contains(topic))
            {
                iterator.remove();
                elements.add(new LocalizationElement(el.topics, el.key, value));
                fireChanged();
                return;
            }
        }

        // not found: add
        elements.add(new LocalizationElement(Collections.singletonList(topic), key, value));
        fireChanged();
    }

    @PropertyName("Localization messages")
    public String[] getPairs()
    {
        Set<String> result = new TreeSet<>();
        for (LocalizationElement element : elements)
        {
            result.add(element.getKey() + " -> " + element.getValue());
        }
        return result.toArray(new String[result.size()]);
    }

    public Set<LocalizationRow> getRows()
    {
        Entity entity = getProject().getEntity(getName());
        Set<LocalizationRow> rows = new LinkedHashSet<>();
        for (LocalizationElement element : elements)
        {
            List<String> topics = element.getTopics();
            if (topics.size() == 1 && topics.get(0).equals(DISPLAY_NAME_TOPIC))
            {
                String key = element.getKey();
                if (entity != null)
                {
                    key = entity.getSqlDisplayName();
                }
                rows.add(new LocalizationRow(topics.get(0), key, element.getValue()));
                continue;
            }
            collectRows(element, entity, rows);
        }
        return rows;
    }

    public Set<LocalizationRow> getRawRows()
    {
        Entity entity = getProject().getEntity(getName());
        Set<LocalizationRow> rows = new HashSet<>();

        for (LocalizationElement element : elements)
            collectRows(element, entity, rows);

        return rows;
    }

    private void collectRows(LocalizationElement element, Entity entity, Set<LocalizationRow> out)
    {
        List<String> topics = element.getTopics();
        for (String topic : topics)
        {
            SpecialTopic specialTopic = LocalizationElement.SPECIAL_TOPICS.get(topic);
            if (specialTopic != null)
            {
                for (String subTopic : specialTopic.getTopics(getName(), getProject()))
                {
                    out.add(new LocalizationRow(subTopic, element.getKey(), element.getValue()));
                }
            }
            else if (topic.startsWith("@Op:"))
            { // Starts with "@Op:": add only if such operation exists
                String operationName = topic.substring(4);
                if (entity != null && entity.getOperations().contains(operationName))
                {
                    out.add(new LocalizationRow(operationName, element.getKey(), element.getValue()));
                }
            }
// for default localization
//            else if(topic.equals( "operationName" ))
//            {
//                Operation operation = entity == null ? null : entity.getOperations().get( element.getKey() );
//                if(operation != null && operation.isAvailable())
//                {
//                    out.add( new LocalizationRow( topic, element.getKey(), element.getValue() ) );
//                }
//            }
            // TODO: viewName is not checked in old BE, thus for consistency it's not checked here as well (for a while)
            /*else if(topic.equals( "viewName" ))
            {
                Query query = entity == null ? null : entity.getQueries().get( element.getKey() );
                if(query != null && query.isAvailable())
                {
                    rows.add( new LocalizationRow( topic, element.getKey(), element.getValue() ) );
                }
            }*/
            else
            {
                out.add(new LocalizationRow(topic, element.getKey(), element.getValue()));
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        EntityLocalizations other = (EntityLocalizations) obj;
        return getRows().equals(other.getRows());
    }

    public static class LocalizationRow
    {
        final String topic;
        final String key;
        final String value;

        public LocalizationRow(String topic, String key, String value)
        {
            super();
            this.topic = topic;
            this.key = key;
            this.value = value;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(key, topic, value);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            LocalizationRow other = (LocalizationRow) obj;
            return key.equals(other.key) && topic.equals(other.topic) && value.equals(other.value);
        }

        public String getTopic()
        {
            return topic;
        }

        public String getKey()
        {
            return key;
        }

        public String getValue()
        {
            return value;
        }
    }

    @Override
    protected void fireChanged()
    {
        getLanguageLocalizations().fireCodeChanged();
    }

}
