package com.developmentontheedge.be5.server.services.impl;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.util.HashUrl;
import com.developmentontheedge.be5.base.util.LayoutUtils;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.model.ColumnModel;
import com.developmentontheedge.be5.query.model.InitialRow;
import com.developmentontheedge.be5.query.model.InitialRowsBuilder;
import com.developmentontheedge.be5.query.model.MoreRows;
import com.developmentontheedge.be5.query.model.MoreRowsBuilder;
import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.query.services.TableModelService;
import com.developmentontheedge.be5.server.helpers.ErrorModelHelper;
import com.developmentontheedge.be5.server.model.DocumentPlugin;
import com.developmentontheedge.be5.server.model.StaticPagePresentation;
import com.developmentontheedge.be5.server.model.TablePresentation;
import com.developmentontheedge.be5.server.model.jsonapi.JsonApiModel;
import com.developmentontheedge.be5.server.model.jsonapi.ResourceData;
import com.developmentontheedge.be5.server.services.DocumentFormPlugin;
import com.developmentontheedge.be5.server.services.DocumentGenerator;
import com.developmentontheedge.be5.server.services.DocumentOperationsPlugin;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.developmentontheedge.be5.base.FrontendConstants.STATIC_ACTION;
import static com.developmentontheedge.be5.base.FrontendConstants.TABLE_ACTION;
import static com.developmentontheedge.be5.base.FrontendConstants.TABLE_MORE_ACTION;
import static com.developmentontheedge.be5.server.RestApiConstants.SELF_LINK;


public class DocumentGeneratorImpl implements DocumentGenerator
{
    private static final Logger log = Logger.getLogger(DocumentGeneratorImpl.class.getName());

    private final UserAwareMeta userAwareMeta;
    private final TableModelService tableModelService;
    private final ErrorModelHelper errorModelHelper;

    private final List<DocumentPlugin> documentPlugins = new ArrayList<>();

    @Inject
    public DocumentGeneratorImpl(
            UserAwareMeta userAwareMeta,
            TableModelService tableModelService,
            DocumentFormPlugin documentFormPlugin,
            DocumentOperationsPlugin documentOperationsPlugin,
            ErrorModelHelper errorModelHelper)
    {
        this.userAwareMeta = userAwareMeta;
        this.tableModelService = tableModelService;
        this.errorModelHelper = errorModelHelper;

        addDocumentPlugin(documentFormPlugin);
        addDocumentPlugin(documentOperationsPlugin);
    }

    @Override
    public JsonApiModel getStaticPage(String name)
    {
        String url = new HashUrl(STATIC_ACTION, name).toString();

        try
        {
            return JsonApiModel.data(new ResourceData(STATIC_ACTION, new StaticPagePresentation(
                    null,
                    userAwareMeta.getStaticPageContent(name)),
                    Collections.singletonMap(SELF_LINK, url)), null);
        }
        catch (Be5Exception e)
        {
            log.log(e.getLogLevel(), "Error in static page: " + url, e);
            return JsonApiModel.error(errorModelHelper.getErrorModel(e, Collections.singletonMap(SELF_LINK, url)), null);
        }
    }

    @Override
    public JsonApiModel createStaticPage(String title, String content, String url)
    {
        return JsonApiModel.data(
                new ResourceData(
                        STATIC_ACTION,
                        new StaticPagePresentation(title, content),
                        Collections.singletonMap(SELF_LINK, url)
                ),
                null
        );
    }

    public TablePresentation getTablePresentation(Query query, Map<String, Object> parameters)
    {
        return getTablePresentation(query, parameters, tableModelService.getTableModel(query, parameters));
    }

    public TablePresentation getTablePresentation(Query query, Map<String, Object> parameters, TableModel tableModel)
    {
        List<Object> columns = tableModel.getColumns().stream().map(ColumnModel::getTitle).collect(Collectors.toList());
        List<InitialRow> rows = new InitialRowsBuilder(tableModel).build();
        Long totalNumberOfRows = tableModel.getTotalNumberOfRows();

        String entityName = query.getEntity().getName();
        String queryName = query.getName();
        String localizedEntityTitle = userAwareMeta.getLocalizedEntityTitle(query.getEntity());
        String localizedQueryTitle = userAwareMeta.getLocalizedQueryTitle(entityName, queryName);
        String title = localizedEntityTitle + ": " + localizedQueryTitle;

        return new TablePresentation(title, entityName, queryName, tableModel.isSelectable(), columns, rows,
                tableModel.orderColumn, tableModel.orderDir, tableModel.offset, tableModel.getRows().size(),
                parameters, totalNumberOfRows, tableModel.isHasAggregate(), LayoutUtils.getLayoutObject(query));
    }

    @Override
    public JsonApiModel getJsonApiModel(Query query, Map<String, Object> parameters)
    {
        return getJsonApiModel(query, parameters, tableModelService.getTableModel(query, parameters));
    }

    private JsonApiModel getJsonApiModel(Query query, Map<String, Object> parameters, TableModel tableModel)
    {
        TablePresentation data = getTablePresentation(query, parameters, tableModel);
        HashUrl url = new HashUrl(TABLE_ACTION, query.getEntity().getName(), query.getName()).named(parameters);

        List<ResourceData> included = new ArrayList<>();
        documentPlugins.forEach(d -> {
            ResourceData resourceData = d.addData(query, parameters);
            if (resourceData != null) included.add(resourceData);
        });

        return JsonApiModel.data(
                new ResourceData(TABLE_ACTION, data, Collections.singletonMap(SELF_LINK, url.toString())),
                included.toArray(new ResourceData[0]),
                null
        );
    }

    @Override
    public JsonApiModel queryJsonApiFor(String entityName, String queryName, Map<String, Object> parameters)
    {
        try
        {
            Query query = userAwareMeta.getQuery(entityName, queryName);
            return getJsonApiModel(query, parameters);
        }
        catch (Be5Exception e)
        {
            HashUrl url = new HashUrl(TABLE_ACTION, entityName, queryName).named(parameters);
            log.log(e.getLogLevel(), "Error in table: " + url.toString(), e);
            return JsonApiModel.error(errorModelHelper.
                    getErrorModel(e, Collections.singletonMap(SELF_LINK, url.toString())), null);
        }
    }

    @Override
    public JsonApiModel updateQueryJsonApi(String entityName, String queryName, Map<String, Object> parameters)
    {
        String url = new HashUrl(TABLE_ACTION, entityName, queryName).named(parameters).toString();
        Map<String, String> links = Collections.singletonMap(SELF_LINK, url);

        try
        {
            Query query = userAwareMeta.getQuery(entityName, queryName);
            TableModel tableModel = tableModelService.getTableModel(query, parameters);

            return JsonApiModel.data(new ResourceData(TABLE_MORE_ACTION, new MoreRows(
                    tableModel.getTotalNumberOfRows().intValue(),
                    tableModel.getTotalNumberOfRows().intValue(),
                    new MoreRowsBuilder(tableModel).build()
            ), links), null);
        }
        catch (Be5Exception e)
        {
            log.log(e.getLogLevel(), "Error in table: " + url, e);
            return JsonApiModel.error(errorModelHelper.
                    getErrorModel(e, links), null);
        }
    }

    @Override
    public void addDocumentPlugin(DocumentPlugin documentPlugin)
    {
        documentPlugins.add(documentPlugin);
    }

}
