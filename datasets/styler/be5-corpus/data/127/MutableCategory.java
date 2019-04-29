package com.developmentontheedge.be5.modules.core.services.impl.model;

import com.developmentontheedge.be5.modules.core.services.model.Category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MutableCategory
{
    public static MutableCategory fromResultSet(ResultSet rs) throws SQLException
    {
        return new MutableCategory(rs.getInt("ID"), rs.getInt("parentID"), rs.getString("name"));
    }

    public final int id;
    public final String name;
    public final Integer parentId;
    public final List<MutableCategory> children;

    private MutableCategory(int id, Integer parentId, String name, List<MutableCategory> children)
    {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.children = children;
    }

    private MutableCategory(int id, int parentId, String name)
    {
        this(id, parentId, name, new ArrayList<>());
    }

    public MutableCategory withChildren(List<MutableCategory> children)
    {
        return new MutableCategory(id, parentId, name, children);
    }

    private Category toCategory()
    {
        return new Category(id, name, MutableCategory.toCategories(children));
    }

    public static List<Category> toCategories(List<MutableCategory> categories)
    {
        return categories.stream().map(MutableCategory::toCategory).collect(Collectors.toList());
    }

}
