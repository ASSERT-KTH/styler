package com.developmentontheedge.be5.server.servlet.support;

import com.developmentontheedge.be5.server.model.jsonapi.ErrorModel;
import com.developmentontheedge.be5.server.model.jsonapi.JsonApiModel;
import com.developmentontheedge.be5.server.model.jsonapi.ResourceData;
import com.developmentontheedge.be5.web.Request;
import com.developmentontheedge.be5.web.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static com.developmentontheedge.be5.server.RestApiConstants.TIMESTAMP_PARAM;


public abstract class JsonApiModelController extends ApiControllerSupport
{
    @Override
    protected final void generate(Request req, Response res, String subUrl)
    {
        JsonApiModel jsonApiModel = generate(req, subUrl);
        if (jsonApiModel != null)
        {
            if (jsonApiModel.getMeta() == null)
            {
                jsonApiModel.setMeta(getDefaultMeta(req));
            }
            res.sendAsJson(jsonApiModel);
        }
        else
        {
            res.sendErrorAsJson("Unknown action", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected abstract JsonApiModel generate(Request req, String subUrl);

//    protected JsonApiModel data(ResourceData data, Object meta)
//    {
//        return JsonApiModel.data(data, meta);
//    }

    protected JsonApiModel data(ResourceData data)
    {
        return JsonApiModel.data(data, null);
    }

    protected JsonApiModel data(ResourceData data, ResourceData[] included)
    {
        return JsonApiModel.data(data, included, null);
    }

    protected JsonApiModel data(ResourceData data, ErrorModel[] errorModels, ResourceData[] included)
    {
        return JsonApiModel.data(data, errorModels, included, null);
    }

//
//    protected JsonApiModel error(ErrorModel error, Object meta)
//    {
//        return JsonApiModel.error(error, meta);
//    }

    protected JsonApiModel error(ErrorModel error)
    {
        return JsonApiModel.error(error, null);
    }

    private Object getDefaultMeta(Request request)
    {
        return Collections.singletonMap(TIMESTAMP_PARAM, request.get(TIMESTAMP_PARAM));
    }
}
