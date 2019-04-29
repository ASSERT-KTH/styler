package com.developmentontheedge.be5.metadata.model;


public class EntitiesFactory
{
    private EntitiesFactory()
    {
    }

    /**
     * This code doesn't add the new entity to the model.
     *
     * @param sqlName
     * @param name
     * @param module
     * @return
     */
    public static Entity createBySqlName(final String sqlName, final String name, final Module module)
    {
        EntityType type = EntityType.forSqlName(sqlName);
        if (type == null)
        {
            throw new IllegalArgumentException("Entity '" + name + "' has unknown type: " + sqlName);
        }
        try
        {
            return new Entity(name, module, type);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Can't create " + name, e);
        }
    }

    public static void addToModule(final Entity entity, final Module module)
    {
        DataElementUtils.addQuiet(module.getOrCreateEntityCollection(), entity);
    }
}
