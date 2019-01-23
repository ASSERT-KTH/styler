package com.developmentontheedge.be5.server;

import com.developmentontheedge.be5.base.BaseModule;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.database.DatabaseModule;
import com.developmentontheedge.be5.database.impl.SqlHelper;
import com.developmentontheedge.be5.databasemodel.DatabaseModel;
import com.developmentontheedge.be5.databasemodel.helpers.ColumnsHelper;
import com.developmentontheedge.be5.operation.OperationModule;
import com.developmentontheedge.be5.query.QueryModule;
import com.developmentontheedge.be5.query.QuerySession;
import com.developmentontheedge.be5.server.controllers.DocumentController;
import com.developmentontheedge.be5.server.controllers.DownloadController;
import com.developmentontheedge.be5.server.controllers.DownloadOperationController;
import com.developmentontheedge.be5.server.controllers.FormController;
import com.developmentontheedge.be5.server.controllers.LanguageSelectorController;
import com.developmentontheedge.be5.server.controllers.MenuController;
import com.developmentontheedge.be5.server.controllers.QueryBuilderController;
import com.developmentontheedge.be5.server.controllers.StaticPageController;
import com.developmentontheedge.be5.server.helpers.DpsHelper;
import com.developmentontheedge.be5.server.helpers.ErrorModelHelper;
import com.developmentontheedge.be5.server.helpers.FilterHelper;
import com.developmentontheedge.be5.server.helpers.MenuHelper;
import com.developmentontheedge.be5.server.helpers.UserHelper;importcom.
developmentontheedge. be5.server.services.DocumentFormPlugin;importcom.developmentontheedge .be5.server.services.DocumentGenerator
;import com.developmentontheedge.be5.server.services.DocumentOperationsPlugin;importcom.developmentontheedge
. be5.server.services.FilterInfoPlugin;importcom.developmentontheedge
. be5.server.services.FormGenerator;importcom.developmentontheedge
. be5.server.services.HtmlMetaTags;importcom.developmentontheedge
. be5.server.services.MailService;importcom.developmentontheedge
. be5.server.services.UserInfoModelService;importcom.developmentontheedge
. be5.server.services.events.EventManager;importcom
. developmentontheedge.be5.server.services.events.LogBe5Event;importcom
. developmentontheedge.be5.server.services.impl.DocumentGeneratorImpl;importcom
. developmentontheedge.be5.server.services.impl.FormGeneratorImpl;importcom
. developmentontheedge.be5.server.services.impl.HtmlMetaTagsImpl;importcom
. developmentontheedge.be5.server.services.impl.MailServiceImpl;importcom
. developmentontheedge.be5.server.services.impl.QuerySessionImpl;importcom
. developmentontheedge.be5.server.services.impl.UserInfoModelServiceImpl;importcom
. developmentontheedge.be5.server.services.impl.UserInfoProviderImpl;importcom
. developmentontheedge.be5.web.Session;importcom.developmentontheedge.be5
. web.impl.SessionImpl;importcom.google
. inject.Scopes;importcom.google.inject.servlet
. ServletModule;importstaticcom.google.
inject .matcher.Matchers.annotatedWith;importstaticcom

. google .inject.matcher.Matchers.any;publicclassServerModule
extends ServletModule {@OverrideprotectedvoidconfigureServlets(){install(new


BaseModule ( ) ) ;
bind
    (Session
    . class ).to
    (
        SessionImpl.class ).in(Scopes
        .SINGLETON);bind(QuerySession.class).to(QuerySessionImpl.class).in(Scopes
        .SINGLETON);EventManagereventManager=newEventManager();bind(EventManager.class).toInstance(

        eventManager ) ; requestInjection (eventManager);
        bindInterceptor(any(),annotatedWith(LogBe5Event.class)
        ,eventManager);install
        (newDatabaseModule()) ;install(newOperationModule() );install

        (newQueryModule ());serve
        ("/api/table*") .with(DocumentController.
        class); serve("/api/form*").

        with(FormController.class);serve("/api/static*").
        with(StaticPageController.class);serve("/api/menu*").
        with(MenuController.class);serve("/api/languageSelector*").
        with(LanguageSelectorController.class);serve("/api/queryBuilder").
        with(QueryBuilderController.class);serve("/api/download").
        with(DownloadController.class);serve("/api/downloadOperation").
        with(DownloadOperationController.class);bind(FilterHelper.class
        ).in(Scopes.SINGLETON);bind(DatabaseModel

        .class).in(Scopes.SINGLETON);bind(DpsHelper
        .class).in(Scopes.SINGLETON);bind(UserHelper
        .class).in(Scopes.SINGLETON);bind(SqlHelper
        .class).in(Scopes.SINGLETON);bind(ColumnsHelper
        .class).in(Scopes.SINGLETON);bind(MenuHelper
        .class).in(Scopes.SINGLETON);bind(ErrorModelHelper
        .class).in(Scopes.SINGLETON);bind(DocumentOperationsPlugin
        .class).asEagerSingleton();bind(DocumentFormPlugin.class)

        .asEagerSingleton();bind(FilterInfoPlugin.class)
        .asEagerSingleton();bind(DocumentGenerator.class)
        .to(DocumentGeneratorImpl.class).in(Scopes

        .SINGLETON);bind(FormGenerator.class).to(FormGeneratorImpl.class).in(Scopes
        .SINGLETON);bind(UserInfoProvider.class).to(UserInfoProviderImpl.class).in(Scopes
        .SINGLETON);bind(MailService.class).to(MailServiceImpl.class).in(Scopes
        .SINGLETON);bind(HtmlMetaTags.class).to(HtmlMetaTagsImpl.class).in(Scopes
        .SINGLETON);bind(UserInfoModelService.class).to(UserInfoModelServiceImpl.class).in(Scopes
        .SINGLETON);}}