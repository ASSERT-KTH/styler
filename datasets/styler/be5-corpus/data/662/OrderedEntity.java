package com.developmentontheedge.be5.base.services.impl;

import com.developmentontheedge.be5.metadata.model.Entity;

import java.util.Objects;

/**
 * Note that this class is not a part of the public API.
 */
class OrderedEntity implements Comparable<OrderedEntity>
{

    public final Entity entity;
    public final int order;
    public final String title;

    public OrderedEntity(Entity entity, String title)
    {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(title);
        this.entity = entity;
        this.order = softParseInt(entity.getOrder(), Integer.MAX_VALUE);
        this.title = title;
    }

    @Override
    public int compareTo(OrderedEntity other)
    {
        Objects.requireNonNull(other);

        if (order != other.order)
            return Integer.compare(order, other.order);

        return title.compareTo(other.title);
    }

    private static int softParseInt(String order, int defaultValue)
    {
        if (order == null || order.trim().length() == 0)
        {
            return defaultValue;
        }

        try
        {
            return Integer.parseInt(order);
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

}