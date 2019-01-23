package com.developmentontheedge.be5.modules.core.controllers;

import com.developmentontheedge.be5.server.RestApiConstants;
import com.developmentontheedge.be5.modules.core.services.CategoriesService;
import com.developmentontheedge.be5.server.servlet.support.JsonApiController;
import com.developmentontheedge.be5.web.Request;

import javax.inject.Inject;


public class CategoriesController extends JsonApiController
{
    private final CategoriesService categoriesService;

    @Inject
    public CategoriesController(CategoriesService categoriesService)
    {
        this.categoriesService = categoriesService;
    }

    @Override
    public Object generate(Request req, String requestSubUrl)
    {
        switch (requestSubUrl)
        {
            case "forest":
                return categoriesService.getCategoriesForest(
                        req.getNonEmpty(RestApiConstants.ENTITY),
                        req.getBoolean("hideEmpty", false)
                );
            default:
                return null;
        }
    }

}
