package com.developmentontheedge.be5.modules.core;

import com.developmentontheedge.be5.base.services.CoreUtils;
import com.developmentontheedge.be5.modules.core.controllers.CategoriesController;
import com.developmentontheedge.be5.modules.core.controllers.UserInfoController;
import com.developmentontheedge.be5.modules.core.services.CategoriesService;
import com.developmentontheedge.be5.modules.core.services.DocumentCategoriesPlugin;
import com.developmentontheedge.be5.modules.core.services.LoginService;
import com.developmentontheedge.be5.modules.core.services.impl.CategoriesHelper;
import com.developmentontheedge.be5.modules.core.services.impl.CategoriesServiceImpl;
import com.developmentontheedge.be5.modules.core.services.impl.CoreUtilsImpl;
import com.developmentontheedge.be5.modules.core.services.impl.LoginServiceImpl;
import com.developmentontheedge.be5.modules.core.services.scheduling.DaemonStarter;
import com.developmentontheedge.be5.modules.core.services.scheduling.impl.DaemonStarterImpl;
import com.developmentontheedge.be5.modules.core.services.scheduling.impl.GuiceJobFactory;
import com.developmentontheedge.be5.server.ServerModule;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;


public class CoreModule extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        install(new ServerModule());
        bind(UserInfoController.class).in(Scopes.SINGLETON);
        bind(CategoriesController.class).in(Scopes.SINGLETON);

        serve("/api/userInfo*").with(UserInfoController.class);
        serve("/api/categories*").with(CategoriesController.class);

        bind(CategoriesHelper.class).in(Scopes.SINGLETON);
        bind(CoreUtils.class).to(CoreUtilsImpl.class).in(Scopes.SINGLETON);
        bind(LoginService.class).to(LoginServiceImpl.class).in(Scopes.SINGLETON);
        bind(CategoriesService.class).to(CategoriesServiceImpl.class).in(Scopes.SINGLETON);
        bind(DocumentCategoriesPlugin.class).asEagerSingleton();

        bind(GuiceJobFactory.class).in(Scopes.SINGLETON);
        bind(DaemonStarter.class).to(DaemonStarterImpl.class).asEagerSingleton();
    }
}
