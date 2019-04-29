package com.developmentontheedge.be5.server.controllers;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.util.HashUrl;
import com.developmentontheedge.be5.server.RestApiConstants;
import com.developmentontheedge.be5.server.helpers.ErrorModelHelper;
import com.developmentontheedge.be5.server.model.jsonapi.JsonApiModel;
import com.developmentontheedge.be5.server.services.DocumentGenerator;
import com.developmentontheedge.be5.server.servlet.support.JsonApiModelController;
import com.developmentontheedge.be5.server.util.ParseRequestUtils;
import com.developmentontheedge.be5.web.Request;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

import static com.developmentontheedge.be5.base.FrontendConstants.TABLE_ACTION;
import static com.developmentontheedge.be5.server.RestApiConstants.SELF_LINK;


public class DocumentController extends JsonApiModelController
{
    private final DocumentGenerator documentGenerator;
    private final ErrorModelHelper errorModelHelper;

    @Inject
    public DocumentController(DocumentGenerator documentGenerator, ErrorModelHelper errorModelHelper)
    {
        this.documentGenerator = documentGenerator;
        this.errorModelHelper = errorModelHelper;
    }

    @Override
    public JsonApiModel generate(Request req, String requestSubUrl)
    {
        String entityName = req.getNonEmpty(RestApiConstants.ENTITY);
        String queryName = req.getNonEmpty(RestApiConstants.QUERY);

        Map<String, Object> parameters = ParseRequestUtils.getValuesFromJson(req.get(RestApiConstants.VALUES));

        try
        {
            switch (requestSubUrl)
            {
                case "":
                    return documentGenerator.queryJsonApiFor(entityName, queryName, parameters);
                case "update":
                    return documentGenerator.updateQueryJsonApi(entityName, queryName, parameters);
                default:
                    return null;
            }
        }
        catch (Be5Exception e)
        {
            String url = new HashUrl(TABLE_ACTION, entityName, queryName).named(parameters).toString();
            log.log(e.getLogLevel(), "Error in table: " + url + ", on requestSubUrl = '" + requestSubUrl + "'", e);
            return error(errorModelHelper.getErrorModel(e, Collections.singletonMap(SELF_LINK, url)));
        }
    }

}
