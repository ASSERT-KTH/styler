package com.developmentontheedge.be5.server.controllers;

import com.developmentontheedge.be5.base.FrontendConstants;
import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.services.ProjectProvider;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.metadata.QueryType;
import com.developmentontheedge.be5.metadata.RoleType;
import com.developmentontheedge.be5.metadata.model.DataElementUtils;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.EntityType;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.services.QueryService;
import com.developmentontheedge.be5.server.RestApiConstants;
import com.developmentontheedge.be5.server.helpers.ErrorModelHelper;
import com.developmentontheedge.be5.server.model.StaticPagePresentation;
import com.developmentontheedge.be5.server.model.jsonapi.ErrorModel;
import com.developmentontheedge.be5.server.model.jsonapi.JsonApiModel;
import com.developmentontheedge.be5.server.model.jsonapi.ResourceData;
import com.developmentontheedge.be5.server.services.DocumentGenerator;
import com.developmentontheedge.be5.server.servlet.support.JsonApiModelController;
import com.developmentontheedge.be5.server.util.ParseRequestUtils;
import com.developmentontheedge.be5.web.Request;
import com.developmentontheedge.sql.model.AstDelete;
import com.developmentontheedge.sql.model.AstInsert;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.AstUpdate;
import com.developmentontheedge.sql.model.SqlQuery;
import com.google.inject.Stage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.developmentontheedge.be5.server.RestApiConstants.SELF_LINK;
import static com.developmentontheedge.be5.server.SessionConstants.QUERY_BUILDER_HISTORY;


public class QueryBuilderController extends JsonApiModelController
{
    private static final String entityName = "queryBuilderComponent";

    private List<ResourceData> includedData;
    private List<ErrorModel> errorModelList;

    private final DbService db;
    private final DocumentGenerator documentGenerator;
    private final ProjectProvider projectProvider;
    private final QueryService queryService;
    private final ErrorModelHelper errorModelHelper;
    private final UserInfoProvider userInfoProvider;
    private final Stage stage;

    @Inject
    public QueryBuilderController(DbService db, DocumentGenerator documentGenerator, ProjectProvider projectProvider,
                                  QueryService queryService, ErrorModelHelper errorModelHelper,
                                  UserInfoProvider userInfoProvider, Stage stage)
    {
        this.db = db;
        this.documentGenerator = documentGenerator;
        this.projectProvider = projectProvider;
        this.queryService = queryService;
        this.errorModelHelper = errorModelHelper;
        this.userInfoProvider = userInfoProvider;
        this.stage = stage;
    }

    @Override
    public JsonApiModel generate(Request req, String requestSubUrl)
    {
        includedData = new ArrayList<>();
        errorModelList = new ArrayList<>();

        if (userInfoProvider.isSystemDeveloper())
        {
            String sql = req.get("sql");
            boolean execute = sql != null;

            List<String> history;
            if (req.getSession().get(QUERY_BUILDER_HISTORY) != null)
            {
                history = (List<String>) req.getSession().get(QUERY_BUILDER_HISTORY);
            }
            else
            {
                history = new ArrayList<String>() {{
                    add("select * from users");
                }};
            }

            if (sql == null)
            {
                sql = history.get(history.size() - 1);
            }
            else
            {
                if (!history.get(history.size() - 1).equals(sql))
                {
                    history.add(sql);
                    req.getSession().set(QUERY_BUILDER_HISTORY, history);
                }
            }

            Data data;
            try
            {
                SqlType type = getSqlType(sql);

                if (type == SqlType.SELECT)
                {
                    data = new Data(sql, history);
                    select(sql, req);
                }
                else
                {
                    data = new Data("", history);
                    if (execute)
                    {
                        switch (type)
                        {
                            case INSERT:
                                insert(sql);
                                break;
                            case UPDATE:
                                update(sql);
                                break;
                            case DELETE:
                                update(sql);
                                break;
                            default:
                                return null;
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                data = new Data(sql, history);
                errorModelList.add(errorModelHelper.getErrorModel(Be5Exception.internal(e)));
            }

            ResourceData resourceData = new ResourceData(
                    "queryBuilder",
                    data,
                    Collections.singletonMap(SELF_LINK, "queryBuilder")
            );

            return data(
                    resourceData,
                    errorModelList.toArray(new ErrorModel[0]),
                    includedData.toArray(new ResourceData[0])
            );
        }
        else
        {
            return error(errorModelHelper.getErrorModel(Be5Exception.accessDenied("Role " + RoleType.ROLE_SYSTEM_DEVELOPER + " required."),
                    Collections.singletonMap(SELF_LINK, "queryBuilder")));
        }
    }

    private void insert(String sql)
    {
        Object id = db.insert(sql);

        includedData.add(new ResourceData(
                "result",
                FrontendConstants.STATIC_ACTION,
                new StaticPagePresentation(
                        "Insert was successful",
                        "New primaryKey: " + id
                ),
                null
        ));
    }

    private void update(String sql)
    {
        Object id = db.update(sql);

        includedData.add(new ResourceData(
                "result",
                FrontendConstants.STATIC_ACTION,
                new StaticPagePresentation(
                        "Update was successful",
                        id + " row(s) affected"
                ),
                null
        ));
    }

    private void select(String sql, Request req)
    {
        String userQBuilderQueryName = userInfoProvider.get().getUserName() + "Query";

        Map<String, Object> parameters = ParseRequestUtils.getValuesFromJson(req.get(RestApiConstants.VALUES));

        Entity entity = new Entity(entityName, projectProvider.get().getApplication(), EntityType.TABLE);
        DataElementUtils.save(entity);

        Query query = new Query(userQBuilderQueryName, entity);
        query.setType(QueryType.D1_UNKNOWN);

        if (sql != null)
        {
            query.setQuery(sql);
        }
        DataElementUtils.save(query);

        try
        {
            includedData.add(new ResourceData(
                    "finalSql",
                    FrontendConstants.STATIC_ACTION,
                    new StaticPagePresentation(
                            "Final sql",
                            queryService.build(query, parameters).getFinalSql()
                    ),
                    null
            ));
        }
        catch (Be5Exception e)
        {
            if (stage == Stage.DEVELOPMENT) log.log(Level.SEVERE, "Error in queryBuilder", e);
            errorModelList.add(errorModelHelper.getErrorModel(e));
        }

        try
        {
            JsonApiModel document = documentGenerator.getJsonApiModel(query, parameters);

            //todo refactor documentGenerator
            document.getData().setId("result");
            includedData.add(document.getData());
            includedData.addAll(Arrays.asList(document.getIncluded()));
        }
        catch (Be5Exception e)
        {
            if (stage == Stage.DEVELOPMENT) log.log(Level.SEVERE, "Error in queryBuilder", e);
            errorModelList.add(errorModelHelper.getErrorModel(e));
        }

        entity.getOrigin().remove(entityName);
    }

    private static SqlType getSqlType(String sql)
    {
        if (sql == null || sql.trim().length() == 0) return SqlType.SELECT;

        AstStart parse = SqlQuery.parse(sql);
        if (parse.getQuery().children().select(AstUpdate.class).findAny().isPresent())
        {
            return SqlType.UPDATE;
        }
        if (parse.getQuery().children().select(AstInsert.class).findAny().isPresent())
        {
            return SqlType.INSERT;
        }
        if (parse.getQuery().children().select(AstDelete.class).findAny().isPresent())
        {
            return SqlType.DELETE;
        }

        return SqlType.SELECT;
    }

    enum SqlType
    {
        INSERT, SELECT, UPDATE, DELETE
    }

    public class Data
    {
        String sql;
        List<String> history;

        public Data(String sql, List<String> history)
        {
            this.sql = sql;
            this.history = history;
        }

        public String getSql()
        {
            return sql;
        }

        public List<String> getHistory()
        {
            return history;
        }
    }
}
