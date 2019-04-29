package com.developmentontheedge.be5.databasemodel;


public interface MethodProvider
{

    Object invoke();

    Object invoke(Object... args);

}
