package com.developmentontheedge.be5.metadata.model.selectors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Several selector rules like Element[attr="value"]
 *
 * @author lan
 */
public class ComplexSelectorRule implements SelectorRule
{
    private final List<SelectorRule> rules;

    public ComplexSelectorRule(List<SelectorRule> rules)
    {
        this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
    }

    public ComplexSelectorRule(SelectorRule... rules)
    {
        this(Arrays.asList(rules));
    }

    @Override
    public boolean matches(BeModelElement element)
    {
        for (SelectorRule rule : rules)
        {
            if (!rule.matches(element))
                return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (SelectorRule rule : rules)
        {
            sb.append(rule.toString());
        }
        return sb.toString();
    }
}
