package com.developmentontheedge.sql.format.dbms;

import com.developmentontheedge.sql.model.AstQuery;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.ParserContext;

public class Formatter
{
    public String format(AstStart start, Context context, ParserContext parserContext)
    {
        DbmsTransformer dbmsTransformer = context.getDbmsTransformer();
        dbmsTransformer.setParserContext(parserContext);
        AstStart clone = start.clone();
        dbmsTransformer.transformAst(clone);
        return clone.format();
    }

    public String format(AstQuery start, Context context, ParserContext parserContext)
    {
        DbmsTransformer dbmsTransformer = context.getDbmsTransformer();
        dbmsTransformer.setParserContext(parserContext);
        AstQuery clone = start.clone();
        dbmsTransformer.transformQuery(clone);
        return clone.format();
    }
}
