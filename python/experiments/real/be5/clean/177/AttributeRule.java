package com.developmentontheedge.be5.metadata.model.selectors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.beans.util.Beans;

public class AttributeRule implements SelectorRule
{
    public static final String OP_EQUALS = "";
    public static final String OP_CONTAINS = "*";
    public static final String OP_STARTS = "^";
    public static final String OP_ENDS = "$";

    private final String attribute;
    private final String value;
    private final String valueOriginal;
    private final String operator;

    public AttributeRule(String attribute, String value, String operator)
    {
        assert attribute != null;
        assert value != null;
        assert OP_EQUALS.equals(operator) || OP_STARTS.equals(operator) || OP_ENDS.equals(operator) || OP_CONTAINS.equals(operator);
        this.attribute = attribute;
        this.operator = operator;
        this.value = value.toLowerCase();
        this.valueOriginal = value;
    }

    public AttributeRule(String attribute, String value)
    {
        this(attribute, value, OP_EQUALS);
    }

    private boolean test(String actual)
    {
        switch (operator)
        {
            case "":
                return value.equalsIgnoreCase(actual);
            case "^":
                return actual.toLowerCase().startsWith(value);
            case "$":
                return actual.toLowerCase().endsWith(value);
            case "*":
                return actual.toLowerCase().contains(value);
            default:
                return false;
        }
    }

    @Override
    public boolean matches(BeModelElement element)
    {
        try
        {
            Object value = Beans.getBeanPropertyValue(element, attribute);
            if (value == null)
                return false;
            if (test(value.toString()))
                return true;
            if (attribute.equals("type"))
            {
                String val = value.toString().toLowerCase().replace("/", "");
                if (test(val))
                    return true;
                val = val.replace("javascript", "js");
                if (test(val))
                    return true;
            }
        }
        catch (Exception e)
        {
        }
        return false;
    }

    @Override
    public String toString()
    {
        boolean identifier = SelectorUtils.isIdentifier(valueOriginal);
        if (OP_EQUALS.equals(operator) && attribute.equals("name") && identifier)
            return "#" + valueOriginal;
        if (OP_EQUALS.equals(operator) && attribute.equals("type") && identifier)
            return "." + valueOriginal;
        return '[' + SelectorUtils.escapeIdentifier(attribute) + operator + '=' + (identifier ? valueOriginal : SelectorUtils.escapeString(valueOriginal)) + ']';
    }
}
