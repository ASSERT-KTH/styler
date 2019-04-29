package com.developmentontheedge.be5.metadata.freemarker;

public class Position
{
    private final String path;
    private final String elementType;
    private final int fromLine, fromColumn, toLine, toColumn;

    public Position(String path, String elementType, int fromLine, int fromColumn, int toLine, int toColumn)
    {
        super();
        this.path = path;
        this.elementType = elementType;
        this.fromLine = fromLine;
        this.fromColumn = fromColumn;
        this.toLine = toLine;
        this.toColumn = toColumn;
    }

    public String getPath()
    {
        return path;
    }

    public String getElementType()
    {
        return elementType;
    }

    public int getFromLine()
    {
        return fromLine;
    }

    public int getFromColumn()
    {
        return fromColumn;
    }

    public int getToLine()
    {
        return toLine;
    }

    public int getToColumn()
    {
        return toColumn;
    }

    public String toString()
    {
        return path + "[" + fromLine + "," + fromColumn + "]-[" + toLine + "," + toColumn + "]";
    }

    public boolean inside(Position position)
    {
        return position.path.equals(path)
                && (fromLine > position.fromLine || fromLine == position.fromLine && fromColumn >= position.fromColumn)
                && (toLine < position.toLine || toLine == position.toLine && toColumn <= position.toColumn);
    }
}