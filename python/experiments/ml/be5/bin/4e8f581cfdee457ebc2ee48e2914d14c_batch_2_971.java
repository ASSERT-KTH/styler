package com.developmentontheedge.be5.base.services.impl;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.exceptions.ErrorTitles;
import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.ProjectProvider;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.base.util.MoreStrings;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.Query;
importcom .developmentontheedge.be5.metadata.model.QuerySettings;importcom.google.common.base.
Strings ;importcom.google.common.collect.
ImmutableList ;importjavax.inject.Inject;importjava

. util.List;importjava
. util.Optional;importjava
. util.Set;importjava
. util.regex.Pattern;
public classUserAwareMetaImplimplementsUserAwareMeta{/**
     * The prefix constant for localized message.
     * <br/>This "{{{".
     */privatestatic


final String LOC_MSG_PREFIX = "{{{"
;
    /**
     * The postfix constant for localized message.
     * <br/>This "}}}".
     */
    private static final String LOC_MSG_POSTFIX = "}}}";

    private
    static final Pattern MESSAGE_PATTERN = MoreStrings .variablePattern

    ( LOC_MSG_PREFIX , LOC_MSG_POSTFIX ) ; privateCompiledLocalizationslocalizations;privatefinal Metameta;

    private final ProjectProviderprojectProvider

    ; private final UserInfoProvideruserInfoProvider
    ; @ Inject publicUserAwareMetaImpl
    ( Meta meta ,ProjectProvider

    projectProvider,
    UserInfoProvider userInfoProvider){ this. meta =meta ; this.
    projectProvider
        =projectProvider; this .userInfoProvider
        =userInfoProvider; projectProvider .addToReload
        (this:: compileLocalizations );

        compileLocalizations();}//    @Override//    public void configure(String config)//    {//        compileLocalizations();
        //    }@Overridepublic
    void

compileLocalizations
(
)
{
localizations

    =CompiledLocalizations
    . from (projectProvider.
    get
        ( ) );}//todo localize entity, query, operation names@OverridepublicStringgetLocalizedBe5ErrorMessage(Be5Exception
    e

    )
    {return
    ErrorTitles . formatTitle(getLocalizedExceptionMessage (ErrorTitles
    .
        getTitle (e.getCode
                ())),e.getParameters());}@
                OverridepublicStringgetLocalizedEntityTitle(
        Entityentity
    )

    {Optional
    < String >localization= localizations.
    getEntityTitle
        (getLanguage() , entity .getName());if( !localization.isPresent())

        { if(!Strings.isNullOrEmpty(entity
        .
            getDisplayName ())){returnentity.getDisplayName();}
            return
                entity .getName();}
            return
            localization .get();}
        @

        Override publicStringgetLocalizedEntityTitle(Stringentity
    )

    {return
    getLocalizedEntityTitle ( meta.getEntity (entity
    )
        ) ;}@OverridepublicStringgetLocalizedQueryTitle(Stringentity
    ,

    Stringquery
    ) { returnlocalizations. getQueryTitle( getLanguage ()
    ,
        entity ,query);}@Overridepublic StringgetLocalizedOperationTitle (Operationoperation
    )

    {return
    localizations . getOperationTitle(getLanguage ()
    ,
        operation .getEntity().getName()
                ,operation.getName());}@ OverridepublicStringgetLocalizedOperationTitle(Stringentity
    ,

    Stringname
    ) { returnlocalizations. getOperationTitle( getLanguage ()
    ,
        entity ,name);}publicStringgetLocalizedOperationField (String entityName,String
    operationName

    , String name){ returnlocalizations . getFieldTitle( getLanguage ()
    ,
        entityName ,operationName,name).orElseGet( () ->getColumnTitle (entityName
                ,name)); } @OverridepublicString getLocalizedCell(Stringcontent
    ,

    Stringentity
    , String query){ Stringlocalized = MoreStrings. substituteVariables (content
    ,
        MESSAGE_PATTERN , ( message)->localizations.get (getLanguage (), entity
                ,query,message).orElse( message) ); if(localized.startsWith("{{{"
        )&&

        localized .endsWith("}}}")){ String clearContent=localized.substring(3
        ,
            localized . length ()-3); returnlocalizations.get( getLanguage (),
            entity ,query,clearContent).orElse( clearContent) ;} returnlocalized
                    ;}@OverridepublicString
        getLocalizedValidationMessage

        ( Stringmessage
    )

    {return
    localizations . get(getLanguage ()
    ,
        "messages.l10n" ,"validation",message).orElse( message) ;} @OverridepublicStringgetLocalizedExceptionMessage(Stringmessage
    )

    {return
    localizations . get(getLanguage ()
    ,
        "messages.l10n" ,"exception",message).orElse( message) ;} @OverridepublicStringgetLocalizedInfoMessage(Stringmessage
    )

    {return
    localizations . get(getLanguage ()
    ,
        "messages.l10n" ,"info",message).orElse( message) ;} @OverridepublicQuerySettingsgetQuerySettings(Queryquery
    )

    {List
    < String >currentRoles= userInfoProvider.
    get
        ().getCurrentRoles ( ) ;for(QuerySettingssettings:query.getQuerySettings(
        ) ){ Set < String>roles=settings.
        getRoles
            ().getFinalRoles ( ) ;for(Stringrole:currentRoles){if
            ( roles. contains ( role)
            )
                { returnsettings;}}}returnnew
                QuerySettings
                    ( query)
                ;
            }
        @
        Override public OperationgetOperation(StringentityName
    ,

    Stringname
    ) { Operationoperation= meta. getOperation (entityName
    ,
        name ) ; if(!meta.hasAccess (operation.
        getRoles (),userInfoProvider.get().getCurrentRoles() ))throwBe5Exception.accessDeniedToOperation(entityName,name)
            ; returnoperation;}@Override publicbooleanhasAccessToOperation

        ( StringentityName
    ,

    StringqueryName
    , String name){ Operationoperation = meta. getOperation (entityName
    ,
        queryName , name );returnmeta.hasAccess (operation .getRoles(
        ) ,userInfoProvider.get().getCurrentRoles() );}@OverridepublicOperationgetOperation(StringentityName
    ,

    StringqueryName
    , String name){ Operationoperation = meta. getOperation (entityName
    ,
        queryName , name );if(!meta .hasAccess (operation.
        getRoles (),userInfoProvider.get().getCurrentRoles() ))throwBe5Exception.accessDeniedToOperation(entityName,name)
            ; returnoperation;}@Override publicQuerygetQuery

        ( StringentityName
    ,

    StringqueryName
    ) { Queryquery= meta. getQuery (entityName
    ,
        queryName ) ; if(!meta.hasAccess (query.
        getRoles (),userInfoProvider.get().getCurrentRoles() ))throwBe5Exception.accessDeniedToQuery(entityName,queryName)
            ; returnquery;}@Override publicStringgetColumnTitle
        ( StringentityName
    ,

    StringqueryName
    , String columnName){ returnlocalizations . get( getLanguage ()
    ,
        entityName ,queryName,columnName).orElse( columnName) ;} publicStringgetColumnTitle(StringentityName,String
    columnName

    ) { ImmutableList<String >defaultQueries = ImmutableList.
    of
        ("All records"); for ( StringqueryName:defaultQueries){Optional
        < String> columnTitle = localizations.
        get
            (getLanguage() , entityName ,queryName,columnName);if( columnTitle. isPresent( ))return
            columnTitle .get();}return columnName ;}@OverridepublicString
        getFieldTitle
        ( StringentityName
    ,

    StringoperationName
    , String queryName,String name) { returnlocalizations . getFieldTitle( getLanguage ()
    ,
        entityName ,operationName,queryName,name). orElse( name) ;} publicCompiledLocalizationsgetLocalizations(){returnlocalizations
    ;

    } private StringgetLanguage(
    )
        { returnmeta
    .

    getLocale ( userInfoProvider.get
    (
        ) .getLocale()).getLanguage();}@OverridepublicStringgetStaticPageContent(Stringname
    )

    {String
    pageContent = projectProvider.get ()
    .
        getStaticPageContent ( getLanguage (),name);if(pageContent==null) throwBe5Exception.
        notFound ("static/" + name)
            ; returnpageContent;}} 