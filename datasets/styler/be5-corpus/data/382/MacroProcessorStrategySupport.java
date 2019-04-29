package com.developmentontheedge.be5.metadata.sql.macro;


public abstract class MacroProcessorStrategySupport implements IMacroProcessorStrategy
{
    protected String quoteIdentifier(String id)
    {
        return '"' + id + '"';
    }

    protected String genericRefCommon(String entity, String id)
    {
        String[] pair = id.split("\\.", 2);
        if (!"ID".equalsIgnoreCase(pair[pair.length - 1]))
            return null;
        String prefix = pair.length == 2 ? pair[0] + "." : "";
        return "<if columnExists=\"" + entity + ".___ownerID\">" + prefix + quoteIdentifier("___ownerID") + "</if><unless columnExists=\""
                + entity + ".___ownerID\">" + genericRefLowLevel(entity, id) + "</unless>";
    }

    @Override
    public String trim(String str)
    {
        return "TRIM(" + str + ")";
    }

    @Override
    public String genericRef(String entity, String id)
    {
        String def = genericRefCommon(entity, id);
        if (def != null)
        {
            return def;
        }
        return genericRefLowLevel(entity, id);
    }

}
