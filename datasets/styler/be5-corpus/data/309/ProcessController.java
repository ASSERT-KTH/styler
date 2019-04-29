package com.developmentontheedge.be5.metadata.util;

import com.developmentontheedge.be5.metadata.exception.ProcessInterruptedException;

public interface ProcessController
{
    void setOperationName(String name);

    void setProgress(double progress) throws ProcessInterruptedException;

    void info(String msg);

    void error(String msg);
}
