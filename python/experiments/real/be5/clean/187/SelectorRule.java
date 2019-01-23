package com.developmentontheedge.be5.metadata.model.selectors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;

/**
 * Interface representing selector rule like .1D or [attr="value"]
 *
 * @author lan
 */
public interface SelectorRule
{
    /**
     * @param element
     * @return true if rule matches the element
     */
    public boolean matches(BeModelElement element);
}
