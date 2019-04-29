package com.developmentontheedge.be5.server.services;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.server.model.DocumentPlugin;
import com.developmentontheedge.be5.server.model.TablePresentation;
import com.developmentontheedge.be5.server.model.jsonapi.JsonApiModel;

import java.util.Map;


public interface DocumentGenerator
{
    //todo move from DocumentGenerator
    JsonApiModel getStaticPage(String name);

    JsonApiModel createStaticPage(String title, String content, String url);

    TablePresentation getTablePresentation(Query query, Map<String, Object> parameters);

    TablePresentation getTablePresentation(Query query, Map<String, Object> parameters, TableModel tableModel);

    JsonApiModel getJsonApiModel(Query query, Map<String, Object> parameters);

    JsonApiModel queryJsonApiFor(String entityName, String queryName, Map<String, Object> parameters);

    JsonApiModel updateQueryJsonApi(String entityName, String queryName, Map<String, Object> parameters);

    void addDocumentPlugin(DocumentPlugin documentPlugin);
}
