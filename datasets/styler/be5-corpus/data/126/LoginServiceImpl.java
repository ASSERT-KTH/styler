package com.developmentontheedge.be5.modules.core.services.impl;

import com.developmentontheedge.be5.base.model.UserInfo;
import com.developmentontheedge.be5.base.services.CoreUtils;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.database.DbService;
import com.developmentontheedge.be5.metadata.DatabaseConstants;
import com.developmentontheedge.be5.metadata.MetadataUtils;
import com.developmentontheedge.be5.metadata.RoleType;
import com.developmentontheedge.be5.modules.core.model.UserInfoModel;
import com.developmentontheedge.be5.modules.core.services.LoginService;
import com.developmentontheedge.be5.server.helpers.MenuHelper;
import com.developmentontheedge.be5.server.helpers.UserHelper;
import com.developmentontheedge.be5.server.model.Action;
import com.developmentontheedge.be5.web.Request;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class LoginServiceImpl implements LoginService
{
    public static final Logger log = Logger.getLogger(LoginServiceImpl.class.getName());

    private final DbService db;
    private final UserHelper userHelper;
    private final CoreUtils coreUtils;
    private final MenuHelper menuHelper;
    private final UserInfoProvider userInfoProvider;

    @Inject
    public LoginServiceImpl(DbService db, UserHelper userHelper, CoreUtils coreUtils, MenuHelper menuHelper,
                            UserInfoProvider userInfoProvider)
    {
        this.db = db;
        this.userHelper = userHelper;
        this.coreUtils = coreUtils;
        this.menuHelper = menuHelper;
        this.userInfoProvider = userInfoProvider;
    }

    @Override
    public UserInfoModel getUserInfoModel()
    {
        Action defaultAction = menuHelper.getDefaultAction();
        String defaultRouteCall = "";

        if (defaultAction == null)
        {
            log.severe("Default Action must not be null");
        }
        else
        {
            if (defaultAction.getName().equals("call"))
            {
                defaultRouteCall = defaultAction.getArg();
            }
            else
            {
                log.severe("Default Action type must be 'call'");
            }
        }

        UserInfo userInfo = userInfoProvider.get();

        return new UserInfoModel(
                !RoleType.ROLE_GUEST.equals(userInfo.getUserName()),
                userInfo.getUserName(),
                userInfo.getAvailableRoles(),
                userInfo.getCurrentRoles(),
                userInfo.getCreationTime().toInstant(),
                defaultRouteCall
        );
    }

    public boolean loginCheck(String username, String password)
    {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        String sql = "SELECT COUNT(user_name) FROM users WHERE user_name = ? AND user_pass = ?";

        return db.oneLong(sql, username, password) == 1L;
    }

    private List<String> selectAvailableRoles(String username)
    {
        return db.scalarList("SELECT role_name FROM user_roles WHERE user_name = ?", username);
    }

    @Override
    public void saveUser(String username, Request req)
    {
        List<String> availableRoles = selectAvailableRoles(username);

        String savedRoles = coreUtils.getUserSetting(username, DatabaseConstants.CURRENT_ROLE_LIST);

        List<String> currentRoles;
        if (savedRoles != null)
        {
            currentRoles = getAvailableCurrentRoles(parseRoles(savedRoles), availableRoles);
        }
        else
        {
            currentRoles = availableRoles;
        }

        userHelper.saveUser(username, availableRoles, currentRoles,
                req.getLocale(), req.getRemoteAddr());

        log.fine("Login user: " + username);
    }

    @Override
    public void setCurrentRoles(List<String> roles)
    {
        Objects.requireNonNull(roles.get(0), "There must be at least one role.");

        coreUtils.setUserSetting(userInfoProvider.get().getUserName(), DatabaseConstants.CURRENT_ROLE_LIST,
                MetadataUtils.toInClause(roles));

        userInfoProvider.get().setCurrentRoles(roles);
    }

    @Override
    public List<String> getAvailableCurrentRoles(List<String> roles, List<String> availableRoles)
    {
        return roles.stream()
                .filter(availableRoles::contains)
                .collect(Collectors.toList());
    }

    protected List<String> parseRoles(String roles)
    {
        TreeSet<String> rolesList = new TreeSet<>();
        if (roles == null || "()".equals(roles))
        {
            return Collections.emptyList();
        }
        roles = roles.substring(1, roles.length() - 1); // drop starting and trailing '(' ')'
        StringTokenizer st = new StringTokenizer(roles, ",");
        while (st.hasMoreTokens())
        {
            rolesList.add(st.nextToken().trim().replaceAll("'", ""));
        }
        return new ArrayList<>(rolesList);
    }

}
