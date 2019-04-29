package com.developmentontheedge.be5.web;


public interface RequestPreprocessor
{
    void preprocessUrl(Request req, Response res);
}
