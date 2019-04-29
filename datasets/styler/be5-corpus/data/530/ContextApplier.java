package com.developmentontheedge.sql.format;

import com.developmentontheedge.sql.model.AstBeCondition;
import com.developmentontheedge.sql.model.AstBeConditionChain;
import com.developmentontheedge.sql.model.AstBeDictionary;
import com.developmentontheedge.sql.model.AstBeElse;
import com.developmentontheedge.sql.model.AstBeIf;
import com.developmentontheedge.sql.model.AstBeListPlaceHolder;
import com.developmentontheedge.sql.model.AstBeParameterTag;
import com.developmentontheedge.sql.model.AstBePlaceHolder;
import com.developmentontheedge.sql.model.AstBeSessionTag;
import com.developmentontheedge.sql.model.AstBeSql;
import com.developmentontheedge.sql.model.AstBeSqlAuto;
import com.developmentontheedge.sql.model.AstBeSqlSubQuery;
import com.developmentontheedge.sql.model.AstBeSqlVar;
import com.developmentontheedge.sql.model.AstBeThen;
import com.developmentontheedge.sql.model.AstBeUnless;
import com.developmentontheedge.sql.model.AstIdentifierConstant;
import com.developmentontheedge.sql.model.AstInPredicate;
import com.developmentontheedge.sql.model.AstInValueList;
import com.developmentontheedge.sql.model.AstNumericConstant;
import com.developmentontheedge.sql.model.AstOrderingElement;
import com.developmentontheedge.sql.model.AstParenthesis;
import com.developmentontheedge.sql.model.AstStart;
import com.developmentontheedge.sql.model.AstStringConstant;
import com.developmentontheedge.sql.model.AstStringPart;
import com.developmentontheedge.sql.model.AstTableRef;
import com.developmentontheedge.sql.model.ExprEvaluator;
import com.developmentontheedge.sql.model.SimpleNode;
import com.developmentontheedge.sql.model.SqlParserTreeConstants;
import com.developmentontheedge.sql.model.SqlQuery;
import one.util.streamex.StreamEx;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class ContextApplier
{
    private static final Logger log = Logger.getLogger(ContextApplier.class.getName());

    private final QueryContext context;

    public ContextApplier(QueryContext context)
    {
        this.context = Objects.requireNonNull(context);
    }

    public void applyContext(AstStart tree)
    {
        checkExpression(tree, null, (x) -> null);
    }

    public StreamEx<String> subQueryKeys()
    {
        return StreamEx.ofKeys(context.getSubQueries());
    }

    public Map<String, String> getSubQueryPropertyMap(String key, Function<String, String> varResolver)
    {
        Map<String, String> propertyMap = new HashMap<>();

        AstBeSqlSubQuery subQuery = context.getSubQueries().get(key);
        if (subQuery == null)
            return propertyMap;

        String keyStr = subQuery.getFilterKeys();
        String valPropStr = subQuery.getFilterValProperties();
        if (keyStr != null && valPropStr != null)
        {
            String[] keys = keyStr.split(",");
            String[] valProps = valPropStr.split(",");

            for (int i = 0; i < keys.length; i++)
            {
                propertyMap.put(keys[i], varResolver.apply(valProps[i]));
            }
        }
        return propertyMap;
    }

    /**
     * todo rename getSubQuery
     */
    public AstBeSqlSubQuery applyVars(String key, Function<String, String> varResolver)
    {
        AstBeSqlSubQuery subQuery = context.getSubQueries().get(key);
        if (subQuery == null)
            return null;
        AstBeSqlSubQuery result = (AstBeSqlSubQuery) subQuery.clone();
        if (result.getLimit() != null)
            new LimitsApplier(0, result.getLimit()).transformQuery(result.getQuery());

        applyFirstLevelVars(1, result, (AstBeSqlVar node) -> applyVar(key, node, varResolver));

//        result.tree().select( AstBeSqlVar.class ).forEach( varNode -> {
//            String value = varResolver.apply( subQuery.translateVar( varNode.getName() ) );
//            if( value == null )
//                value = varNode.getDefault();
//            SimpleNode constant;
//
//            if(varNode.jjtGetParent() instanceof AstBeSqlSubQuery && value != null && !"".equals(value.trim()))
//            {
//                constant = SqlQuery.parse(value).getQuery();
//            }
//            else
//            {
//                if(value == null)
//                {
//                    value = "null";
//                }
//                constant = varNode.jjtGetParent() instanceof AstStringConstant ? new AstStringPart(value)
//                        : new AstIdentifierConstant(value);
//            }
//
//            varNode.replaceWith( constant );
//        } );

        checkExpression(result, key, varResolver);

        return result;
    }

    private void applyFirstLevelVars(int level, SimpleNode node, Consumer<AstBeSqlVar> valueSetter)
    {
        for (SimpleNode child : node.children())
        {
            if (child instanceof AstBeSqlVar)
            {
                valueSetter.accept((AstBeSqlVar) child);
            }
            else if (level == 1 || !(child instanceof AstBeSqlSubQuery))
            {
                applyFirstLevelVars(level + 1, child, valueSetter);
            }
        }
    }

    private void applyVar(String key, AstBeSqlVar varNode, Function<String, String> varResolver)
    {
        String value = varResolver.apply(context.getSubQueries().get(key).translateVar(varNode.getName()));
        if (value == null)
            value = varNode.getDefault();
        SimpleNode constant;

        if (varNode.jjtGetParent() instanceof AstBeSqlSubQuery && value != null && !"".equals(value.trim()))
        {
            constant = SqlQuery.parse(value).getQuery();
        }
        else
        {
            if (value == null)
            {
                value = "null";
            }
            constant = varNode.jjtGetParent() instanceof AstStringConstant ? new AstStringPart(value)
                    : new AstIdentifierConstant(value);
        }

        varNode.replaceWith(constant);
    }

    private void checkExpression(SimpleNode newTree, String key, Function<String, String> varResolver)
    {
        for (int i = 0; i < newTree.jjtGetNumChildren(); i++)
        {
            SimpleNode child = newTree.child(i);
            checkExpression(child, key, varResolver);

            if (child instanceof AstBeCondition)
                i = applyConditions(newTree, i);

            if (child instanceof AstBeParameterTag)
                applyParameterTag(newTree, i, key, varResolver);

            else if (child instanceof AstBePlaceHolder)
                applyPlaceHolder((AstBePlaceHolder) child);

            else if (child instanceof AstBeSessionTag)
                applySessionTag((AstBeSessionTag) child);

            else if (child instanceof AstBeSqlSubQuery)
                applySubQuery((AstBeSqlSubQuery) child);

            else if (child instanceof AstBeSqlAuto)
                applyAutoQuery((AstBeSqlAuto) child);

            else if (child instanceof AstBeConditionChain)
            {
                applyConditionChain((AstBeConditionChain) child);
                i--;
            }
            else if (child instanceof AstBeSql)
                applySqlTag((AstBeSql) child);

            else if (child instanceof AstBeDictionary)
                applyDictionary((AstBeDictionary) child);
        }
    }

    private void applySqlTag(AstBeSql sql)
    {
        if (sql.getExec().equals("include"))
        {
            String entityName = sql.getEntityName();
            String queryName = sql.getQueryName();

            String subQuery = context.resolveQuery(entityName, queryName);
            sql.replaceWith(SqlQuery.parse(subQuery).getQuery());
            return;
        }

        List<String> beautifiers = Arrays.asList("com.beanexplorer.web.html.HtmlLineGlueBeautifier");
        if (sql.jjtGetParent() instanceof AstInValueList)
        {
            beautifiers = Arrays.asList("com.beanexplorer.web.html.SqlInClauseQuotedBeautifier",
                    "com.beanexplorer.web.html.SqlInClauseBeautifier");
        }
        else
        {
            List<String> inValueAttr = Arrays.asList("entity", "queryName", "filterKeyProperty", "filterKey", "filterValProperty", "filterVal", "outColumns");
            for (String attr : inValueAttr)
                if (sql.getParameter(attr) != null)
                    throw new IllegalArgumentException("Unsupported attribute: " + attr);
        }
        if (!sql.getExec().equals("pre") || !beautifiers.contains(sql.getBeautifier()))
            throw new IllegalArgumentException("Unsupported attributes: exec=\"" + sql.getExec() + "\" beautifier=\"" + sql.getBeautifier() + "\"");
        if (sql.getDistinct().equalsIgnoreCase("yes"))
            new DistinctApplier().transformQuery(sql.getQuery());
        if (sql.getLimit() != null)
            new LimitsApplier(0, sql.getLimit()).transformQuery(sql.getQuery());
        sql.replaceWith(new AstParenthesis(sql.getQuery()));
    }

    private void applyConditionChain(AstBeConditionChain chain)
    {
        if (chain.jjtGetNumChildren() == 0)
        {
            chain.remove();
        }
        else
        {
            if (chain.jjtGetNumChildren() == 0)
                throw new InternalError("Unexpected number of chldren inside AstBeConditionChain: " + chain.jjtGetNumChildren() + "\nNode: " + chain.format());
            chain.children().findFirst(child -> child instanceof AstBeCondition)
                    .ifPresent(child -> {
                        throw new InternalError("Internal condition is not replaced: " + child.format());
                    });
            chain.replaceWith(chain.children().toArray(SimpleNode[]::new));
        }
    }

    private void applyAutoQuery(AstBeSqlAuto child)
    {
        throw new UnsupportedOperationException("Auto-query is not supported yet :(");
    }

    private void applyDictionary(AstBeDictionary child)
    {
        String value = context.getDictionaryValue(child.getTagName(), child.getName(), child.getParameters());
        child.replaceWith(new AstIdentifierConstant(value));
    }

    private void applySubQuery(AstBeSqlSubQuery subQuery)
    {
        if (subQuery.getExec() != null || subQuery.getQueryName() != null)
        {
            String name = subQuery.getQueryName();
            if (name == null)
            {
                throw new IllegalStateException("Empty subQuery without queryName parameter: " + subQuery.format());
            }
            String entity = subQuery.getEntityName();
            String subQueryText = context.resolveQuery(entity, name);
            if (subQueryText == null)
            {
                throw new IllegalStateException("Unable to resolve subquery: " + (entity == null ? "" : entity + ".") + name);
            }
            AstStart start = SqlQuery.parse(subQueryText);
            subQuery.addChild(start.getQuery());
        }

        if (subQuery.getQuery() != null)
        {
            if (subQuery.getOutColumns() != null)
                new ColumnsApplier().keepOnlyOutColumns(subQuery);

            if (subQuery.getLimit() != null)
                new LimitsApplier(0, subQuery.getLimit()).transformQuery(subQuery.getQuery());

            String keyStr = subQuery.getFilterKeys();
            String valPropStr = subQuery.getFilterValProperties();
            if (keyStr != null && valPropStr != null)
            {
                String[] keys = keyStr.split(",");
                String[] valProps = valPropStr.split(",");
                Map<ColumnRef, String> conditions = new HashMap<>();
                for (int i = 0; i < keys.length; i++)
                {
                    //ColumnRef.resolve(subQuery.getQuery(), keys[i])
                    conditions.put(new ColumnRef(null, keys[i]), "<var:" + valProps[i] + "/>");
                    //subQuery.addParameter(keys[i], valProps[i]);
                }
                //new FilterApplier().addFilter(subQuery.getQuery(), conditions);
            }
        }
        //AstBeSqlVar beSqlVar = subQuery.getAstBeSqlVar();

        //AstStart start = SqlQuery.parse(context.getParameter(beSqlVar.getName()));
        //subQuery.addChild(start.getQuery());
        //subQuery.replaceWith(beSqlVar);
        //subQuery.removeChildren();


        String key = "<sql> SubQuery# " + (context.getSubQueries().size() + 1) + "</sql>";
        context.getSubQueries().put(key, subQuery);
        subQuery.replaceWith(new AstStringPart(key));
    }

    private void applySessionTag(AstBeSessionTag child)
    {
        String name = child.getName();
        Object value = context.getSessionVariable(name);
        boolean multiple = "true".equals(child.getMultiple());

        if (!multiple)
        {
            if (value == null)
            {
                if (child.getDefault() != null)
                {
                    value = SqlTypeUtils.parseValue(child.getDefault(), child.getType());
                }
                else
                {
                    value = "";
                }
            }

            child.replaceWith(applySessionParameters(child, value));
        }
        else
        {
            if (child.jjtGetParent() instanceof AstInValueList)
            {
                SimpleNode[] objects;

                if (value instanceof List)
                {
                    List list = (List) value;
                    objects = new SimpleNode[list.size()];
                    for (int i = 0; i < list.size(); i++)
                    {
                        objects[i] = applySessionParameters(child, list.get(i));
                    }
                }
                else
                {
                    objects = (Arrays.stream((Object[]) value))
                            .map(val -> applySessionParameters(child, val))
                            .toArray(SimpleNode[]::new);
                }

                child.replaceWith(objects);
                return;
            }
            if (!(child.jjtGetParent() instanceof AstInPredicate))
                throw new IllegalArgumentException("Parameter Multiple can only be put inside InPredicate");

            AstInValueList list = new AstInValueList(SqlParserTreeConstants.JJTINVALUELIST);
            List<String> values = context.getListParameter(child.getName());

            if (values != null)
            {
                for (String val : values)
                {
                    list.addChild(applySessionParameters(child, val));
                }
            }
            list.inheritFrom(child);
        }
    }

    private SimpleNode applySessionParameters(AstBeSessionTag node, Object value)
    {
        if (node.jjtGetParent() instanceof AstStringConstant)
        {
            return new AstStringPart(value.toString());
        }
        else
        {
            if (SqlTypeUtils.isNumber(value.getClass()))
            {
                return AstNumericConstant.of((Number) value);
            }
            else
            {
                return new AstStringConstant(value.toString());
            }
        }
    }

    private void applyPlaceHolder(AstBePlaceHolder placeholderNode)
    {
        String ph = placeholderNode.getPlaceHolder();
        SimpleNode replacement;
        if (placeholderNode instanceof AstBeListPlaceHolder)
        {
            AstInValueList list = new AstInValueList(SqlParserTreeConstants.JJTINVALUELIST);
            context.roles().map(AstStringConstant::new).forEach(list::addChild);
            replacement = list;
        }
        else
        {
            String rawResult;
            switch (ph)
            {
                case "username":
                    rawResult = context.getUserName();
                    break;
                case "timestamp":
                    rawResult = String.valueOf(System.currentTimeMillis());
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported placeholder: " + ph);
            }
            replacement = new AstStringConstant(rawResult);
        }
        replacement.inheritFrom(placeholderNode);
        placeholderNode.replaceWith(replacement);
    }

    private int applyConditions(SimpleNode newTree, int i)
    {
        AstBeCondition condNode = (AstBeCondition) newTree.child(i);
        boolean correct = checkConditions(condNode);

        if (condNode instanceof AstBeUnless)
            i = setUnlessResult(newTree, i, condNode, correct);

        else if (condNode instanceof AstBeIf)
            i = setIfResult(newTree, i, condNode, correct);
        return i;
    }

    private void applyParameterTag(SimpleNode newTree, int i, String key, Function<String, String> varResolver)
    {
        String value;
        SimpleNode replacement;
        AstBeParameterTag paramNode = (AstBeParameterTag) newTree.child(i);
        String multiple = paramNode.getMultiple();
        boolean tableRefAddend = newTree instanceof AstTableRef && i == 1;

        if (multiple == null)
        {
            value = context.getParameter(paramNode.getName());

            Map<String, String> propertyMap = getSubQueryPropertyMap(key, varResolver);
            if (value == null && propertyMap.containsKey(paramNode.getName()))
            {
                value = propertyMap.get(paramNode.getName());

            }

            replacement = applyParameters(paramNode, value, tableRefAddend);
        }
        else
        {
            if (newTree instanceof AstInValueList)
            {
                List<String> values = context.getListParameter(paramNode.getName());
                paramNode.replaceWith(StreamEx.of(values).map(val -> applyParameters(paramNode, val, tableRefAddend)).toArray(SimpleNode[]::new));
                return;
            }
            if (!(newTree instanceof AstInPredicate))
                throw new IllegalArgumentException("Parameter Multiple can only be put inside InPredicate");

            AstInValueList list = new AstInValueList(SqlParserTreeConstants.JJTINVALUELIST);
            List<String> values = context.getListParameter(paramNode.getName());

            if (values != null)
            {
                for (String val : values)
                {
                    list.addChild(applyParameters(paramNode, val, tableRefAddend));
                }
            }
            replacement = list;
        }

        replacement.inheritFrom(paramNode);

        if (tableRefAddend)
        {
            AstTableRef tableRef = (AstTableRef) newTree;
            tableRef.setTable(tableRef.getTable() + replacement.format());
            tableRef.removeChild(i);
        }
        else
        {
            newTree.jjtAddChild(replacement, i);
        }
    }

    private SimpleNode applyParameters(AstBeParameterTag paramNode, String value, boolean tableRefAddend)
    {
        String defValue = paramNode.getDefValue() != null ? paramNode.getDefValue() : "";
        String prefix = paramNode.getPrefix() != null ? paramNode.getPrefix() : "";
        String postfix = paramNode.getPostfix() != null ? paramNode.getPostfix() : "";
        String regex = paramNode.getRegex();
        String repl = paramNode.getReplacement();
        String changeCase = paramNode.getCase();
        String type = paramNode.getType();
        boolean safeStr = "yes".equals(paramNode.getSafeStr()) && !tableRefAddend;

        if (value == null)
            value = "";

        if (value.equals(""))
            value = defValue;

        if (regex != null && repl != null)
            value = value.replaceAll(regex, repl);

        if ("upper".equals(changeCase))
            value = value.toUpperCase();

        if ("lower".equals(changeCase))
            value = value.toLowerCase();

        if ("capitalize".equals(changeCase))
        {
            value = value.toLowerCase();
            if (value.length() > 0)
                value = value.substring(0, 1).toUpperCase() + value.substring(1);
        }

        value = prefix + value + postfix;

        SimpleNode constant;
        if (paramNode.jjtGetParent() instanceof AstStringConstant)
        {
            constant = new AstStringPart(value);
        }
        else if (type != null)
        {
            if (SqlTypeUtils.isNumber(type))
            {
                if (!value.equals(""))
                {
                    constant = AstNumericConstant.of((Number) SqlTypeUtils.parseValue(value, type));
                }
                else
                {
                    constant = new AstIdentifierConstant(value);
                }
            }
            else
            {
                constant = new AstStringConstant(value);
            }
        }
        else if (!safeStr)
        {
            constant = new AstIdentifierConstant(value);
        }
        else if (paramNode.jjtGetParent() instanceof AstOrderingElement)
        {
            constant = new AstIdentifierConstant(value, false);
        }
        else
        {
            constant = new AstStringConstant(value);
        }
        return constant;
    }

    private int setIfResult(SimpleNode newTree, int i, SimpleNode condNode, boolean correct)
    {
        SimpleNode child;
        for (int j = condNode.jjtGetNumChildren() - 1; j >= 0; j--)
        {
            child = condNode.child(j);
            if (child instanceof AstBeThen && correct || child instanceof AstBeElse && !correct)
            {
                if (child.jjtGetNumChildren() != 0)
                    setChildren(newTree, i, child);
                else
                {
                    newTree.removeChild(condNode);
                    i--;
                }
                break;
            }
            else if (child instanceof AstBeThen && !correct)
            {
                newTree.removeChild(condNode);
                i--;
            }
        }
        return i;
    }

    private int setUnlessResult(SimpleNode newTree, int i, SimpleNode condNode, boolean correct)
    {
        if (!correct)
        {
            if (condNode.jjtGetNumChildren() != 0)
                setChildren(newTree, i, condNode);
            else
            {
                newTree.removeChild(condNode);
                i--;
            }
        }
        else if (correct)
        {
            newTree.removeChild(condNode);
            i--;
        }
        return i;
    }

    private boolean checkConditions(AstBeCondition node)
    {
        String expression = node.getExpression();
        if (expression != null)
        {
            return new ExprEvaluator(context.asMap(), expression).evaluate();
        }
        String session = node.getSession();
        if (session != null)
        {
            String value = node.getValue();
            return value != null ? context.getSessionVariable(session).equals(value) : context.getSessionVariable(session) != null;
        }
        else
        {
            String parameter = node.getKey();
            String value = node.getValue();
            return context.getListParameter(parameter) != null && (value != null ? context.getListParameter(parameter).contains(value) : context.getListParameter(parameter).size() > 0);
        }
    }

    private void setChildren(SimpleNode tree, int i, SimpleNode node)
    {
        for (int j = tree.jjtGetNumChildren() - 1; j > i; j--)
            tree.jjtAddChild((tree.child(j)), j + node.jjtGetNumChildren() - 1);

        for (int k = 0; k < node.jjtGetNumChildren(); k++)
            tree.jjtAddChild((node.child(k)), i + k);
    }
//
//    public static boolean isNumeric(String value)
//    {
//        return value.matches( "[-+]?\\d*\\.?\\d+" );
//    }
}
