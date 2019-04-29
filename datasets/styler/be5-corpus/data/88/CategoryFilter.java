package com.developmentontheedge.be5.query.impl.utils;

import com.developmentontheedge.sql.model.AstConcatExpression;
import com.developmentontheedge.sql.model.AstFieldReference;
import com.developmentontheedge.sql.model.AstFunNode;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.AstJoin;
import com.developmentontheedge.sql.model.AstNumericConstant;
import com.developmentontheedge.sql.model.AstQuery;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.AstStringConstant;
import com.developmentontheedge.sql.model.AstTableRef;
import com.developmentontheedge.sql.model.DefaultParserContext;
import com.developmentontheedge.sql.model.JoinType;

import java.util.Optional;


public class CategoryFilter
{
    private final long categoryId;
    private final String entity;
    private final String primaryKeyColumn;

    public CategoryFilter(String entity, String primaryKeyColumn, long categoryId)
    {
        this.categoryId = categoryId;
        this.entity = entity;
        this.primaryKeyColumn = primaryKeyColumn;
    }

    public void apply(AstStart start)
    {
        apply(start.getQuery());
    }

    public void apply(AstQuery query)
    {
        query.tree().select(AstTableRef.class)
                .filter(tableRef -> entity.equals(tableRef.getTable()))
                .toList()
                .forEach(this::processTableRef);
    }

    private void processTableRef(AstTableRef ref)
    {
        String alias = Optional.ofNullable(ref.getAlias()).orElse(entity);
        AstFunNode categoryCondition = DefaultParserContext.FUNC_EQ.node(new AstFieldReference(identifier("classifications"),
                identifier("categoryID")), AstNumericConstant.of(categoryId));
        AstFunNode recordCondition = DefaultParserContext.FUNC_EQ.node(
                new AstFieldReference(identifier("classifications"), identifier("recordID")),
                new AstConcatExpression(new AstStringConstant(entity + "."), new AstFieldReference(identifier(alias), identifier(primaryKeyColumn)))
        );
        AstJoin join = JoinType.INNER.node("classifications", DefaultParserContext.FUNC_AND.node(categoryCondition, recordCondition));
        ref.appendSibling(join);
    }

    private AstIdentifierConstant identifier(String identifier)
    {
        return new AstIdentifierConstant(identifier, false);
    }
}
