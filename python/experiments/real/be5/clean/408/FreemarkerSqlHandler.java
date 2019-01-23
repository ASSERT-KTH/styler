package com.developmentontheedge.be5.metadata.freemarker;

import com.developmentontheedge.be5.metadata.exception.FreemarkerSqlException;
import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.model.FreemarkerScript;
import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.base.DataElementPath;
import com.developmentontheedge.be5.metadata.util.ProcessController;
import com.developmentontheedge.dbms.CharConsumer;
import com.developmentontheedge.dbms.ExtendedSqlException;
import com.developmentontheedge.dbms.MultiSqlConsumer;
import com.developmentontheedge.dbms.SqlExecutor;
import com.developmentontheedge.dbms.SqlHandler;
import freemarker.core.Environment;
import freemarker.core.TemplateElement;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FreemarkerSqlHandler implements SqlHandler
{
    private final SqlExecutor sqlExecutor;
    private final boolean debug;
    private TemplateElement[] startPosition;
    private int startLineOffset, startColumnOffset;
    private int lineOffset, columnOffset;
    private List<String> includeChain = Collections.emptyList();
    private final ProcessController log;

    public FreemarkerSqlHandler(SqlExecutor sqlExecutor, boolean debug, ProcessController log)
    {
        this.sqlExecutor = sqlExecutor;
        this.debug = debug;
        this.log = log;
    }

    @Override
    public void startStatement()
    {
        startPosition = Environment.getCurrentEnvironment().getInstructionStackSnapshot();
        startLineOffset = lineOffset;
        startColumnOffset = columnOffset;
    }

    @Override
    public void endStatement(String statement)
    {
        List<Position> positions = calculatePosition();
        if (debug)
        {
            for (Position position : positions)
            {
                sqlExecutor.comment("At " + position, false);
            }
        }
        List<String> newIncludeChain = getIncludeChain(positions);
        if (!newIncludeChain.equals(includeChain))
        {
            int i = 0;
            while (newIncludeChain.size() > i && includeChain.size() > i && newIncludeChain.get(i).equals(includeChain.get(i)))
            {
                i++;
            }
            for (int j = includeChain.size() - 1; j >= i; j--)
            {
                sqlExecutor.comment("End of included " + includeChain.get(j), false);
            }
            for (int j = i; j < newIncludeChain.size(); j++)
            {
                if (log != null)
                {
                    StringBuilder sb = new StringBuilder("[>]   ");
                    for (int k = 0; k < j; k++)
                        sb.append("  ");
                    sb.append(newIncludeChain.get(j));
                    log.setOperationName(sb.toString());
                }
                sqlExecutor.comment("Start of included " + newIncludeChain.get(j));
            }
        }
        includeChain = newIncludeChain;
        try
        {
            sqlExecutor.executeSingle(statement);
        }
        catch (ExtendedSqlException e)
        {
            if (!debug)
            {
                for (Position position : positions)
                {
                    sqlExecutor.comment("At " + position, false);
                }
            }
            throw new FreemarkerSqlException(e, positions.toArray(new Position[positions.size()]));
        }
    }

    private List<String> getIncludeChain(List<Position> positions)
    {
        List<String> chain = new ArrayList<>();
        for (int i = 0; i < positions.size() - 1; i++)
        {
            if (positions.get(i).getElementType().equals("Include"))
                chain.add(positions.get(i + 1).getPath());
        }
        return chain;
    }

    private List<Position> calculatePosition()
    {
        Environment env = Environment.getCurrentEnvironment();
        List<Position> positions = new ArrayList<>();
        if (env == null)
            return positions;
        TemplateElement[] endPosition = env.getInstructionStackSnapshot();
        for (int i = 0; i < Math.min(startPosition.length, endPosition.length); i++)
        {
            TemplateElement startElement = startPosition[startPosition.length - i - 1];
            TemplateElement endElement = endPosition[endPosition.length - i - 1];
            if (startElement.getTemplate() != endElement.getTemplate())
                break;
            String path = startElement.getTemplate().getName();
            int fromLine = startElement.getBeginLine();
            int fromColumn = startElement.getBeginColumn();
            if (startElement.getNodeName().equals("TextBlock"))
            {
                fromLine += startLineOffset;
                fromColumn = (startLineOffset == 0 ? fromColumn : 1) + startColumnOffset;
            }
            int toLine = endElement.getEndLine();
            int toColumn = endElement.getEndColumn();
            if (endElement.getNodeName().equals("TextBlock"))
            {
                toLine = endElement.getBeginLine() + lineOffset;
                toColumn = (lineOffset == 0 ? endElement.getBeginColumn() : 1) + columnOffset;
            }
            positions.add(new Position(path, startElement == endElement ? startElement.getNodeName() : "", fromLine, fromColumn, toLine, toColumn));
            if (startElement != endElement)
                break;
        }
        for (int i = 0; i < positions.size(); i++)
        {
            if (i < positions.size() - 1 && positions.get(i + 1).inside(positions.get(i)))
            {
                positions.remove(i);
                i--;
            }
        }
        return positions;
    }

    public void execute(FreemarkerScript freemarkerScript) throws ProjectElementException, FreemarkerSqlException
    {
        DataElementPath path = freemarkerScript.getCompletePath();
        if (log != null)
        {
            log.setOperationName("[>] " + path);
        }
        sqlExecutor.comment("Execute " + path);
        Project project = freemarkerScript.getProject();
        ResultToConsumerWriter out = new ResultToConsumerWriter(new MultiSqlConsumer(project.getDatabaseSystem().getType(), this));
        FreemarkerUtils.mergeTemplateByPath(path.toString(), project.getContext(freemarkerScript), project.getConfiguration(), out);
        for (int j = includeChain.size() - 1; j >= 0; j--)
        {
            sqlExecutor.comment("End of included " + includeChain.get(j), false);
        }
    }

    private class ResultToConsumerWriter extends Writer
    {
        CharConsumer consumer;

        public ResultToConsumerWriter(CharConsumer consumer)
        {
            this.consumer = consumer;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException
        {
            lineOffset = 0;
            columnOffset = 0;
            for (int i = 0; i < len; i++)
            {
                if (cbuf[i] != '\r')
                    consumer.symbol(cbuf[i + off]);
                if (cbuf[i] == '\n')
                {
                    lineOffset++;
                    columnOffset = 0;
                }
                else if (cbuf[i] != '\r')
                    columnOffset++;
            }
        }

        @Override
        public void flush() throws IOException
        {
        }

        @Override
        public void close() throws IOException
        {
            consumer.end();
        }
    }
}