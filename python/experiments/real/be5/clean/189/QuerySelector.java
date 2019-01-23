package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.QuickFilter;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.editors.StringTagEditor;

import java.util.ArrayList;
import java.util.List;

public class QuerySelector extends StringTagEditor
{
    @Override
    public String[] getTags()
    {
        try
        {
            List<String> queries = new ArrayList<>();
            Query query;
            if (getBean() instanceof Query)
            {
                query = (Query) getBean();
                queries.add("");
            }
            else
            {
                query = ((QuickFilter) getBean()).getQuery();
            }
            queries.addAll(query.getEntity().getQueries().getNameList());
            return queries.toArray(new String[queries.size()]);
        }
        catch (Exception e)
        {
            return Strings2.EMPTY;
        }
    }
}