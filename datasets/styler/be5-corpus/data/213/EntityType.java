package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.util.EnumWithHumanReadableName;

public enum EntityType implements EnumWithHumanReadableName
{
    DICTIONARY
            {
                @Override
                public String getSqlName()
                {
                    return "dictionary";
                }

                @Override
                public String getHumanReadableName()
                {
                    return "Dictionary";
                }
            },
    TABLE
            {
                @Override
                public String getSqlName()
                {
                    return "table";
                }

                @Override
                public String getHumanReadableName()
                {
                    return "Table";
                }
            },
    METADATA
            {
                @Override
                public String getSqlName()
                {
                    return "metadata";
                }

                @Override
                public String getHumanReadableName()
                {
                    return "Metadata table";
                }
            },
    COLLECTION
            {
                @Override
                public String getSqlName()
                {
                    return "collection";
                }

                @Override
                public String getHumanReadableName()
                {
                    return "Collection";
                }
            },
    GENERIC_COLLECTION
            {
                @Override
                public String getSqlName()
                {
                    return "genericCollection";
                }

                @Override
                public String getHumanReadableName()
                {
                    return "Generic Collection";
                }
            };

    public abstract String getSqlName();

    @Override
    public abstract String getHumanReadableName();

    public static EntityType forSqlName(String name)
    {
        for (EntityType type : values())
        {
            if (type.getSqlName().equals(name))
                return type;
        }
        return null;
    }

}
