package com.developmentontheedge.be5.metadata.model;

import com.developmentontheedge.be5.metadata.exception.ProjectElementException;
import com.developmentontheedge.be5.metadata.exception.ReadException;
import com.developmentontheedge.be5.metadata.model.base.BeModelCollection;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.base.BeModelElementSupport;
import com.developmentontheedge.be5.metadata.model.selectors.SelectorRule;
import com.developmentontheedge.be5.metadata.model.selectors.SelectorUtils;
import com.developmentontheedge.be5.metadata.model.selectors.UnionSelectorRule;
import com.developmentontheedge.be5.metadata.model.selectors.parser.ParseException;
import com.developmentontheedge.be5.metadata.model.selectors.parser.TokenMgrError;
import com.developmentontheedge.be5.metadata.serialization.LoadContext;
import com.developmentontheedge.be5.metadata.serialization.SerializationConstants;
import com.developmentontheedge.be5.metadata.serialization.yaml.deserializers.YamlDeserializer;
import com.developmentontheedge.beans.annot.PropertyName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * name = selector rule
 *
 * @author lan
 */
public class MassChange extends BeModelElementSupport
{
    private final Map<String, Object> data;
    private final SelectorRule rule;
    private final Throwable ex;

    public MassChange(String name, BeModelCollection<? extends MassChange> origin, Map<String, Object> data)
    {
        super(name, origin);
        SelectorRule rule = null;
        Throwable ex = null;
        try
        {
            rule = UnionSelectorRule.create(name);
        }
        catch (ParseException | TokenMgrError e)
        {
            ex = e;
        }
        this.rule = rule;
        this.ex = ex;
        this.data = new LinkedHashMap<>(data);
        this.data.remove("select");
        this.data.remove(SerializationConstants.TAG_COMMENT);
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public SelectorRule getRule()
    {
        return rule;
    }

    @PropertyName("Validated selector")
    public String getSelectorString()
    {
        return rule == null ? ex.getMessage() : rule.toString();
    }

    @PropertyName("Changed properties")
    public String getPropertiesString()
    {
        return String.join(", ", data.keySet());
    }

    @Override
    public List<ProjectElementException> getErrors()
    {
        if (ex != null)
        {
            return Collections.singletonList(new ProjectElementException(getCompletePath(), "name", ex));
        }
        return Collections.emptyList();
    }

    public List<BeModelElement> apply(LoadContext loadContext, Project project)
    {
        List<BeModelElement> elements = SelectorUtils.select(project, getRule());
        List<BeModelElement> changedElements = new ArrayList<>();
        for (BeModelElement element : elements)
        {
            if (element instanceof Query)
            {
                Query oldQuery = (Query) element;
                Query newQuery = YamlDeserializer.readQuery(loadContext, oldQuery.getName(), data, oldQuery.getEntity());
                DataElementUtils.saveQuiet(newQuery);
                newQuery.merge(oldQuery, false, true);
                newQuery.setOriginModuleName(oldQuery.getOriginModuleName());
            }
            else if (element instanceof Operation)
            {
                Operation oldOperation = (Operation) element;
                Map<String, Object> realData = data;
                // Set type, because it cannot be inherited yet
                if (!data.containsKey("type") && !Operation.OPERATION_TYPE_JAVA.equals(oldOperation.getType()))
                {
                    realData = new HashMap<>(data);
                    realData.put("type", oldOperation.getType());
                }
                Operation newOperation = YamlDeserializer.readOperation(loadContext, oldOperation.getName(), realData, oldOperation.getEntity());
                DataElementUtils.saveQuiet(newOperation);
                newOperation.merge(oldOperation, false, true);
                newOperation.setOriginModuleName(oldOperation.getOriginModuleName());
            }
            else if (element instanceof Entity)
            {
                Entity oldEntity = (Entity) element;
                Map<String, Object> realData = data;
                // Set type, because it cannot be inherited yet
                if (!data.containsKey("type"))
                {
                    realData = new HashMap<>(data);
                    realData.put("type", oldEntity.getType().getSqlName());
                }
                Entity newEntity = YamlDeserializer.readEntity(loadContext, oldEntity.getName(), realData, oldEntity.getModule());
                for (EntityItem q : newEntity.getQueries())
                    q.setOriginModuleName(oldEntity.getModule().getName());
                for (EntityItem o : newEntity.getOperations())
                    o.setOriginModuleName(oldEntity.getModule().getName());
                DataElementUtils.saveQuiet(newEntity);
                newEntity.merge(oldEntity, false, true);
            }
            else
            {
                loadContext.addWarning(new ReadException(element, null, "Mass change is not supported for type " + element.getClass().getSimpleName()));
                continue;
            }
            changedElements.add(element);
        }
        return changedElements;
    }
}
