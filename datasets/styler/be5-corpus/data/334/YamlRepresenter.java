package com.developmentontheedge.be5.metadata.serialization.yaml;

import com.developmentontheedge.be5.metadata.serialization.SerializationConstants;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author asko
 */
class YamlRepresenter extends Representer
{
    private final Map<Object, Object> flowStyleElements;
    private static final Set<String> flowStyleListNames = new HashSet<>(Arrays.asList("roles"));
    private static final int MAXIMUM_LEVEL = 20;
    private final Deque<Object> stack = new LinkedList<>();

    private class MyRepresentList extends RepresentList
    {
        @Override
        public Node representData(Object data)
        {
            final Node represented = super.representData(data);
            patch(represented, data);
            return represented;
        }
    }

    public YamlRepresenter(final Object root)
    {
        super();
        flowStyleElements = evalFlowStyleElements(root);
        multiRepresenters.put(List.class, new MyRepresentList());
    }

    private Map<Object, Object> evalFlowStyleElements(final Object root)
    {
        final Map<Object, Object> map = new IdentityHashMap<>();
        collectFlowStyleElements(null, root, map, 0);

        return map;
    }

    private void collectFlowStyleElements(final String name, final Object element, final Map<Object, Object> out, final int level)
    {
        if (name != null)
        {
            stack.push(name);
        }

        if (level > MAXIMUM_LEVEL)
        {
            throw new IllegalStateException();
        }

        if (name != null)
        {
            if (flowStyleListNames.contains(name) && element instanceof List)
            {
                out.put(element, element);
            }
        }

        if (element instanceof List && stack.contains(SerializationConstants.TAG_REFERENCES))
        {
            out.put(element, element);
        }

        if (element instanceof List)
        {
            final List<?> list = (List<?>) element;
            for (final Object child : list)
            {
                collectFlowStyleElements(null, child, out, level + 1);
            }
        }

        if (element instanceof Map)
        {
            final Map<?, ?> map = (Map<?, ?>) element;
            map.forEach((key, child) -> {
                String childName = key instanceof String ? (String) key : null;
                collectFlowStyleElements(childName, child, out, level + 1);
            });
        }

        if (name != null)
        {
            stack.pop();
        }
    }

    @Override
    public Node represent(Object data)
    {
        final Node represented = super.represent(data);
        patch(represented, data); // not sure if it can be ever applied
        return represented;
    }

    private void patch(final Node represented, Object data)
    {
        if (represented instanceof SequenceNode)
        {
            if (getFlowStyle(data))
            {
                ((CollectionNode) represented).setFlowStyle(DumperOptions.FlowStyle.FLOW);
            }
        }
    }

    private boolean getFlowStyle(final Object data)
    {
        return flowStyleElements.containsKey(data);
    }
}