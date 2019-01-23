package com.developmentontheedge.be5.query.impl.utils;

import com.developmentontheedge.sql.model.AstStart;
import one.util.streamex.MoreCollectors;
import one.util.streamex.StreamEx;

import java.util.logging.Logger;


public class DebugQueryLogger
{
    private static final Logger log = Logger.getLogger(DebugQueryLogger.class.getName());
    private String lastQuery;

    public void log(String name, AstStart ast)
    {
        log(name, ast.format());
    }

    public void log(String name, String query)
    {
        if (!query.equals(lastQuery))
        {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(": ");
            if (lastQuery == null)
            {
                sb.append(query);
            }
            else
            {
                String prefix = StreamEx.of(query, lastQuery).collect(MoreCollectors.commonPrefix());
                String suffix = StreamEx.of(query, lastQuery).collect(MoreCollectors.commonSuffix());
                int startPos = prefix.length();
                int endPos = query.length() - suffix.length();
                startPos = startPos > 10 ? query.lastIndexOf('\n', startPos - 10) : 0;
                endPos = suffix.length() > 10 ? query.indexOf('\n', endPos + 10) : query.length();
                if (startPos < 0)
                    startPos = 0;
                if (endPos < 0)
                    endPos = query.length();
                String substring = query.substring(startPos, endPos);
                if (startPos > 0)
                    substring = "..." + substring.substring(1);
                if (endPos < query.length())
                    substring += "...";
                sb.append(substring);
            }
            log.finer(sb.toString());
            lastQuery = query;
        }
    }
}
