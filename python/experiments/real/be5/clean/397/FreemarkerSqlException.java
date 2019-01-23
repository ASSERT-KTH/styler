package com.developmentontheedge.be5.metadata.exception;

import com.developmentontheedge.be5.metadata.freemarker.Position;
import com.developmentontheedge.dbms.ExtendedSqlException;

public class FreemarkerSqlException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private final Position[] positions;

    public FreemarkerSqlException(ExtendedSqlException ex, Position[] pos)
    {
        super(ex);
        this.positions = pos;
    }

    @Override
    public String getMessage()
    {
        StringBuilder sb = new StringBuilder(getCause().getMessage());
        for (Position pos : positions)
        {
            sb.append("\nAt: " + pos);
        }
        return sb.toString();
    }

    public Position[] getPositions()
    {
        return positions;
    }

    @Override
    public ExtendedSqlException getCause()
    {
        return (ExtendedSqlException) super.getCause();
    }
}
