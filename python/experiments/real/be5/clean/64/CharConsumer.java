package com.developmentontheedge.dbms;

/**
 * An interface which consumes the stream of chars.
 */
public interface CharConsumer
{
    /**
     * Called when new symbol is available from the source
     *
     * @param c the next symbol
     */
    public void symbol(char c);

    /**
     * Called when the input source is finished.
     */
    public void end();
}
