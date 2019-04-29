package com.developmentontheedge.be5.metadata.model.selectors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents hierarchical selector like 'Entity#blahblah Query[type="1D"]'
 *
 * @author lan
 */
public class HierarchySelectorRule implements SelectorRule
{
    private final List<ComplexSelectorRule> rules;

    public HierarchySelectorRule(List<ComplexSelectorRule> rules)
    {
        this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
    }

    public HierarchySelectorRule(ComplexSelectorRule... rules)
    {
        this(Arrays.asList(rules));
    }

    @Override
    public boolean matches(BeModelElement element)
    {
        int ruleNum = rules.size() - 1;
        if (!rules.get(ruleNum).matches(element))
            return false;
        ruleNum--;
        while (ruleNum >= 0)
        {
            element = element.getOrigin();
            if (element == null)
                return false;
            if (rules.get(ruleNum).matches(element))
                ruleNum--;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return StreamEx.of(rules).joining(" ");
    }
}
