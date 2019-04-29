package com.developmentontheedge.be5.metadata.model.selectors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.be5.metadata.model.selectors.parser.ParseException;
import com.developmentontheedge.be5.metadata.model.selectors.parser.Parser;
import one.util.streamex.StreamEx;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents comma-separated several hierarchy rules
 *
 * @author lan
 */
public class UnionSelectorRule implements SelectorRule
{
    private final List<HierarchySelectorRule> rules;

    public UnionSelectorRule(List<HierarchySelectorRule> rules)
    {
        this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
    }

    public UnionSelectorRule(HierarchySelectorRule... rules)
    {
        this(Arrays.asList(rules));
    }

    @Override
    public boolean matches(BeModelElement element)
    {
        for (HierarchySelectorRule rule : rules)
        {
            if (rule.matches(element))
                return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return StreamEx.of(rules).joining(", ");
    }

    public static UnionSelectorRule create(String input) throws ParseException
    {
        return (UnionSelectorRule) new Parser(new StringReader(input)).parse();
    }
}
