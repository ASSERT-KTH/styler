package com.developmentontheedge.be5.modules.core.services.impl;

import com.developmentontheedge.be5.base.util.Utils;
import com.developmentontheedge.be5.database.DbService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class CategoriesHelper
{
    private final DbService db;

    @Inject
    public CategoriesHelper(DbService db)
    {
        this.db = db;
    }

    public List<Long> getParentCategories(Long categoryID)
    {
        List<Long> categories = new ArrayList<>();
        Long cat = categoryID;

        while (cat != null)
        {
            categories.add(cat);

            cat = db.oneLong("SELECT c1.parentID FROM categories c1 WHERE c1.ID = ?", cat);
        }

        return categories;
    }

    public List<Long> getChildCategories(Long categoryID)
    {
        List<Long> categories = new ArrayList<>();
        categories.add(categoryID);

        //bfs
        int i = 0;
        do
        {
            categories.addAll(db.scalarList("SELECT id FROM categories c WHERE c.parentID = ?", categories.get(i)));
        }
        while (++i < categories.size());

        return categories;
    }

    public void removeWithChildCategories(Long categoryID, String entityName, Object primaryKey)
    {
        List<Long> categories = getChildCategories(categoryID);

        db.update("DELETE FROM classifications WHERE recordID = CONCAT('" + entityName + ".', " + primaryKey + ")" +
                "AND categoryID IN " + Utils.inClause(categories.size()), categories.toArray());
    }

    public void addWithParentCategories(Long categoryID, String entityName, Object primaryKey)
    {
        List<Long> categories = getParentCategories(categoryID);

        db.update("DELETE FROM classifications WHERE recordID = CONCAT('" + entityName + ".', " + primaryKey + ")" +
                "AND categoryID IN " + Utils.inClause(categories.size()), categories.toArray());

        db.insert("INSERT INTO classifications (recordID, categoryID)" +
                "SELECT CONCAT('" + entityName + ".', " + primaryKey + "), c.ID FROM categories c " +
                "WHERE id IN " + Utils.inClause(categories.size()), categories.toArray());
    }
}
