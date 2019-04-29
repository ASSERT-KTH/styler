package com.developmentontheedge.be5.metadata.model.editors;

import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.TableDef;
import com.developmentontheedge.be5.metadata.util.Strings2;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class ColumnSelector extends StringTagEditor
{

    @Override
    public String[] getTags()
    {
        final Entity entity = (Entity) getBean();
        final TableDef tableDefinition = entity.findTableDefinition();

        if (tableDefinition == null)
            return Strings2.EMPTY;

        return tableDefinition.getColumns().names().toArray(String[]::new);
    }

}
