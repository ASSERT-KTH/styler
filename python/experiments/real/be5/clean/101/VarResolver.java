package com.developmentontheedge.be5.query;


@FunctionalInterface
public interface VarResolver
{
    String resolve(String varName);
}
