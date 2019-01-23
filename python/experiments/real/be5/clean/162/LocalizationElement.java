package com.developmentontheedge.be5.metadata.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LocalizationElement implements Comparable<LocalizationElement>
{
    public static interface SpecialTopic
    {
        public Collection<String> getTopics(String entity, Project project);
    }

    @SuppressWarnings("serial")
    public static final Map<String, SpecialTopic> SPECIAL_TOPICS = Collections.unmodifiableMap(new HashMap<String, SpecialTopic>()
    {
        {
            put("@AllQueries", (entityName, project) -> {
                Entity entity = project.getEntity(entityName);
                if (entity != null)
                {
                    return entity.getQueries().names().without(Query.SPECIAL_TABLE_DEFINITION).without(Query.SPECIAL_LOST_RECORDS)
                            .toSet();
                }
                return Collections.emptySet();
            });
        }
    });

    final List<String> topics;
    final String key;
    final String value;

    public LocalizationElement(java.util.Collection<String> topics, String key, String value)
    {
        this.topics = new ArrayList<>(topics);
        Collections.sort(this.topics);
        this.key = key;
        this.value = value;
        if (key == null)
            throw new IllegalArgumentException("Key is null");
        if (value == null)
            throw new IllegalArgumentException("Value is null");
    }

    /**
     * Returns sorted by name topics.
     */
    public List<String> getTopics()
    {
        return Collections.unmodifiableList(topics);
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(key, topics);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        LocalizationElement other = (LocalizationElement) obj;
        return key.equals(other.key) && topics.equals(other.topics);
    }

    @Override
    public int compareTo(LocalizationElement o)
    {
        if (!key.equals(o.key))
            return key.compareTo(o.key);
        List<String> oTopics = o.topics;
        for (int i = 0; i < topics.size(); i++)
        {
            if (oTopics.size() <= i)
                return 1;
            int compare = topics.get(i).compareTo(oTopics.get(i));
            if (compare != 0)
                return compare;
        }
        if (oTopics.size() > topics.size())
            return -1;
        return 0;
    }
}
