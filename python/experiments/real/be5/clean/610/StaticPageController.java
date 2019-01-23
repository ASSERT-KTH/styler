package com.developmentontheedge.be5.server.controllers;

import com.developmentontheedge.be5.server.model.jsonapi.JsonApiModel;
import com.developmentontheedge.be5.server.services.DocumentGenerator;
import com.developmentontheedge.be5.server.servlet.support.JsonApiModelController;
import com.developmentontheedge.be5.web.Request;

import javax.inject.Inject;


public class StaticPageController extends JsonApiModelController
{
    private final DocumentGenerator documentGenerator;

    @Inject
    public StaticPageController(DocumentGenerator documentGenerator)
    {
        this.documentGenerator = documentGenerator;
    }

    @Override
    public JsonApiModel generate(Request req, String requestSubUrl)
    {
        return documentGenerator.getStaticPage(requestSubUrl);
    }

}
