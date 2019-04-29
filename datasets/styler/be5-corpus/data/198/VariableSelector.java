package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Project;
import com.developmentontheedge.be5.metadata.model.base.BeModelElement;
import com.developmentontheedge.beans.editors.StringTagEditor;

import java.util.Map;
import java.util.Map.Entry;

public class VariableSelector extends StringTagEditor
{
    @Override
    public String[] getTags()
    {
        Project project = ((BeModelElement) getBean()).getProject();
        Map<String, String> variableNames = project.getVariables();
        String[] result = new String[variableNames.size() + 1];
        result[0] = "(none)";
        int i = 1;
        for (Entry<String, String> entry : variableNames.entrySet())
        {
            result[i++] = entry.getKey() + " (" + entry.getValue() + ")";
        }
        return result;
    }

    @Override
    public String getAsText()
    {
        String name = super.getAsText();
        if (name.isEmpty())
            return "(none)";
        Project project = ((BeModelElement) getBean()).getProject();
        String value = project.getVariableValue(name);
        return name + " (" + value + ")";
    }

    @Override
    public void setAsText(String text)
    {
        int pos = text.indexOf('(');
        String value;
        if (pos >= 0)
        {
            value = text.substring(0, pos).trim();
        }
        else
        {
            value = text.trim();
        }
        super.setAsText(value);
    }
}
