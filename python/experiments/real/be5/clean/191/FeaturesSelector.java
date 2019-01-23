package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.beans.editors.GenericMultiSelectEditor;

public class FeaturesSelector extends GenericMultiSelectEditor
{
    private static String[] features;

    @Override
    protected Object[] getAvailableValues()
    {
        return getFeatures();
    }

    public static String[] getFeatures()
    {
        // TODO - be5 features are undefined yet
        /**
         if(features == null)
         {
         Set<String> featuresSet = ModuleUtils.getAvailableFeatures();
         features = featuresSet.toArray( new String[featuresSet.size()] );
         }
         return features.clone();
         */

        return new String[]{};
    }
}
