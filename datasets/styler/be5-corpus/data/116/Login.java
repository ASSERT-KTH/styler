package com.developmentontheedge.be5.modules.core.operations.users;

import com.developmentontheedge.be5.base.services.CoreUtils;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.modules.core.api.CoreFrontendActions;
import com.developmentontheedge.be5.modules.core.services.LoginService;
import com.developmentontheedge.be5.operation.model.OperationResult;
import com.developmentontheedge.be5.server.operations.support.GOperationSupport;
import com.developmentontheedge.beans.DynamicProperty;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;

import static com.developmentontheedge.beans.BeanInfoConstants.CAN_BE_NULL;
import static com.developmentontheedge.beans.BeanInfoConstants.PASSWORD_FIELD;


public class Login extends GOperationSupport
{
    @Inject
    protected LoginService loginService;
    @Inject
    protected CoreUtils coreUtils;
    @Inject
    protected UserAwareMeta userAwareMeta;

    @Override
    public Object getParameters(Map<String, Object> presetValues) throws Exception
    {
        dpsHelper.addDpForColumns(params, getInfo().getEntity(), Arrays.asList("user_name", "user_pass"), context.getOperationParams(), presetValues);
        DynamicProperty user_pass = params.getProperty("user_pass");
        user_pass.setAttribute(CAN_BE_NULL, false);
        user_pass.setAttribute(PASSWORD_FIELD, true);
        return params;
    }

    @Override
    public void invoke(Object parameters) throws Exception
    {
        String username = params.getValueAsString("user_name");
        if (loginService.loginCheck(username, params.getValueAsString("user_pass")))
        {
            loginService.saveUser(username, request);
            postLogin(parameters);
            if (context.getOperationParams().get("withoutUpdateUserInfo") == null)
            {
                setResult(OperationResult.finished(null,
                        CoreFrontendActions.updateUserAndOpenDefaultRoute(loginService.getUserInfoModel())));
            }
            else
            {
                setResult(OperationResult.finished());
            }
        }
        else
        {
            setResult(OperationResult.error(userAwareMeta
                    .getLocalizedExceptionMessage("Incorrect username or password.")));
        }
    }

    public void postLogin(Object parameters)
    {

    }
}
