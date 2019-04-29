package com.developmentontheedge.be5.metadata.model.selectors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.beans.annot.PropertyName;

/**
 * Rule to match element class like 'Query' or 'Operation'
 *
 * @author lan
 */
public class ElementClassRule implements SelectorRule
{
    private final String classTitle;

    public ElementClassRule(String classTitle)
    {
        assert classTitle != null;
        this.classTitle = classTitle;
    }

    @Override
    public boolean matches(BeModelElement element)
    {
        PropertyName annotation = element.getClass().getAnnotation(PropertyName.class);
        return annotation != null && classTitle.equals(annotation.value());
    }

    @Override
    public String toString()
    {
        return SelectorUtils.escapeIdentifier(classTitle);
    }
}
