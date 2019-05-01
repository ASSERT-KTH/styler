package com.developmentontheedge.be5.query.impl;

import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.metadata.QueryType;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.query.QuerySession;
import com.developmentontheedge.sql.format.ContextApplier;
import com.developmentontheedge.sql.format.LimitsApplier;
import com.developmentontheedge.sql.format.MacroExpander;
import com.developmentontheedge.sql.format.QueryContext;
import com.developmentontheedge.sql.format.Simplifier;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.SqlQuery;

import javax.inject.Inject;
import java.util.Map;
import java.util.logging.Logger;

import static com.developmentontheedge.be5.base.FrontendConstants.CATEGORY_ID_PARAM;
import static com.developmentontheedge.be5.query.QueryConstants.LIMIT;
import static com.developmentontheedge.be5.query.QueryConstants.OFFSET;
import static com.developmentontheedge.be5.query.QueryConstants.ORDER_COLUMN;
import static com.developmentontheedge.be5.query.QueryConstants.ORDER_DIR;

public class QuerySqlGenerator
{
    private static final Logger log = Logger.getLogger(QuerySqlGenerator.class.getName());

    private final QuerySession querySession;
    private final UserInfoProvider userInfoProvider;
    private final Meta meta;
    private final QueryMetaHelper queryMetaHelper;

    @Inject
    public QuerySqlGenerator(QuerySession querySession, UserInfoProvider userInfoProvider, Meta meta,
                             QueryMetaHelper queryMetaHelper)
    {
        this.querySession = querySession;
        this.userInfoProvider = userInfoProvider;
        this.meta = meta;
        this.queryMetaHelper = queryMetaHelper;
    }

    public AstStart getSql(Query query, Map<String, ?> parameters)
    {
        QueryContext queryContext = new Be5QueryContext(query, parameters, querySession, userInfoProvider.get(), meta);
        return getSql(query, queryContext, Be5SqlQueryExecutor.ExecuteType.DEFAULT);
    }

    public AstStart getSql(Query query, QueryContext queryContext, Be5SqlQueryExecutor.ExecuteType executeType)
    {
        ContextApplier contextApplier = new ContextApplier(queryContext);
        String queryText = query.getFinalQuery();
        if (queryText.isEmpty()) return null;

        AstStart ast = SqlQuery.parse(queryText);
        new MacroExpander().expandMacros(ast);

        queryMetaHelper.resolveTypeOfRefColumn(ast);
        queryMetaHelper.applyFilters(ast, queryContext.getParameters());
        QueryMetaHelper.applyCategory(query, ast, contextApplier.getContext().getParameter(CATEGORY_ID_PARAM));

        contextApplier.applyContext(ast);

        if (query.getType() == QueryType.D1) QueryMetaHelper.addIDColumnLabel(ast, query);

        if (executeType == Be5SqlQueryExecutor.ExecuteType.COUNT)
        {
            QueryMetaHelper.countFromQuery(ast.getQuery());
        }

        if (executeType == Be5SqlQueryExecutor.ExecuteType.DEFAULT)
        {
            int orderColumn = Integer.parseInt(getOrDefault(queryContext, ORDER_COLUMN, "-1"));
            String orderDir = getOrDefault(queryContext, ORDER_DIR, "asc");
            int offset = Integer.parseInt( getOrDefault(queryContext, OFFSET, "0"));
            int limit = Integer.parseInt( getOrDefault(queryContext, LIMIT, Integer.toString(Integer.MAX_VALUE)));
            QueryMetaHelper.applySort(ast, orderColumn, orderDir);
            new LimitsApplier(offset, limit).transform(ast);
        }

        Simplifier.simplify(ast);
        return ast;
    }

    private String getOrDefault(QueryContext queryContext, String name, String defaultValue)
    {
        String value = queryContext.getParameter(name);
        return value != null ? value : defaultValue;
    }
}
