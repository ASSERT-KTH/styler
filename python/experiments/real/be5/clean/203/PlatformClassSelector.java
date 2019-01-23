package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.base.BeModelElement;

public interface PlatformClassSelector
{
    void selectClass(BeModelElement element, String title, String startValue, PlatformClassSelectorCallback callback);
}
