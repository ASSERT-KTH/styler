package com.developmentontheedge.be5.modules.core.services.model;

import java.util.List;
import java.util.Objects;


public class Category
{
    public final int id;
    public final String name;
    public final List<Category> children;

    public Category(int id, String name, List<Category> children)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(children);

        this.id = id;
        this.name = name;
        this.children = children; //ImmutableList.copyOf(children);
    }

}