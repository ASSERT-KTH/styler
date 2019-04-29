package com.developmentontheedge.be5.modules.core.operations.users;

import com.developmentontheedge.be5.databasemodel.EntityModel;
import com.developmentontheedge.be5.operation.model.TransactionalOperation;
import com.developmentontheedge.be5.server.operations.support.OperationSupport;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetSupport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.developmentontheedge.beans.BeanInfoConstants.MULTIPLE_SELECTION_LIST;
import static com.developmentontheedge.beans.BeanInfoConstants.TAG_LIST_ATTR;


public class EditRoles extends OperationSupport implements TransactionalOperation
{
    private DynamicPropertySet params;
    private String user_name;

    @Override
    public Object getParameters(Map<String, Object> presetValues) throws Exception
    {
        user_name = context.getRecord();
        Object[] currentRoles = db.scalarList("SELECT role_name FROM user_roles WHERE user_name = ?", user_name).toArray();
        params = new DynamicPropertySetSupport();

        DynamicProperty prop = new DynamicProperty("roles", "Roles", String.class);
        prop.setCanBeNull(false);
        prop.setAttribute(TAG_LIST_ATTR, meta.getProjectRoles().toArray());
        prop.setAttribute(MULTIPLE_SELECTION_LIST, true);
        prop.setValue(presetValues.getOrDefault("roles", currentRoles));
        params.add(prop);

        return params;
    }

    @Override
    public void invoke(Object parameters) throws Exception
    {
        EntityModel<String> user_roles = database.getEntity("user_roles");
        database.getEntity("user_roles").removeBy(Collections.singletonMap("user_name", user_name));

        for (Object role_name : (Object[]) params.getProperty("roles").getValue())
        {
            user_roles.add(new HashMap<String, String>() {{
                put("user_name", user_name);
                put("role_name", (String) role_name);
            }});
        }
    }
}
