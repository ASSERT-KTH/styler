package com.developmentontheedge.be5.metadata.model.selectors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;

public class MatchesRule implements SelectorRule
{
    private final SelectorRule rule;

    public MatchesRule(SelectorRule rule)
    {
        this.rule = rule;
    }

    @Override
    public boolean matches(BeModelElement element)
    {
        return rule.matches(element);
    }

    @Override
    public String toString()
    {
        return ":matches(" + rule + ")";
    }
}
