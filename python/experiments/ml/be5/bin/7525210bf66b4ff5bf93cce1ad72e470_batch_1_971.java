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
import com.developmentontheedge.be5.metadata.model.Query;importcom.developmentontheedge.be5.metadata
.model .QuerySettings;importcom.google.common.base.Strings;importcom.google.common
. collect.ImmutableList;importjavax.inject.Inject
; importjava.util.List;importjava.

util .Optional;importjava.
util .Set;importjava.
util .regex.Pattern;public
class UserAwareMetaImplimplementsUserAwareMeta{/**
     * The prefix constant for localized message.
     * <br/>This "{{{".
     */private
static finalStringLOC_MSG_PREFIX="{{{";/**
     * The postfix constant for localized message.
     * <br/>This "}}}".
     */private


static final String LOC_MSG_POSTFIX =
"}}}"
    ;
    private static final Pattern MESSAGE_PATTERN = MoreStrings.

    variablePattern
    ( LOC_MSG_PREFIX , LOC_MSG_POSTFIX ) ; privateCompiledLocalizations

    localizations ; private final Meta meta ;privatefinalProjectProviderprojectProvider; privatefinalUserInfoProvider

    userInfoProvider ; @Inject

    public UserAwareMetaImpl ( Metameta
    , ProjectProvider projectProvider ,UserInfoProvider
    userInfoProvider ) { this.

    meta=
    meta ;this. projectProvider= projectProvider ;this . userInfoProvider=
    userInfoProvider
        ;projectProvider. addToReload (this
        ::compileLocalizations) ; compileLocalizations(
        );} //    @Override //    public void configure(String config)//    {

        //        compileLocalizations();//    }@OverridepublicvoidcompileLocalizations()
        {localizations=CompiledLocalizations
    .

from
(
projectProvider
.
get

    ()
    ) ; }//todo localize entity, query, operation names@
    Override
        public String getLocalizedBe5ErrorMessage(Be5Exceptione){returnErrorTitles.formatTitle(
    getLocalizedExceptionMessage

    (
    ErrorTitles.
    getTitle ( e.getCode ()
    )
        ) ,e.getParameters
                ());}@OverridepublicStringgetLocalizedEntityTitle(Entityentity)
                {Optional<String>
        localization=
    localizations

    .getEntityTitle
    ( getLanguage (), entity.
    getName
        ()); if ( !localization.isPresent()){ if(!Strings.isNullOrEmpty(

        entity .getDisplayName())){return
        entity
            . getDisplayName();}returnentity.getName();}
            return
                localization .get();}
            @
            Override publicStringgetLocalizedEntityTitle(Stringentity
        )

        { returngetLocalizedEntityTitle(meta.getEntity
    (

    entity)
    ) ; }@Override publicString
    getLocalizedQueryTitle
        ( Stringentity,Stringquery){returnlocalizations.
    getQueryTitle

    (getLanguage
    ( ) ,entity, query) ; }@
    Override
        public StringgetLocalizedOperationTitle(Operationoperation){return localizations. getOperationTitle(getLanguage
    (

    ),
    operation . getEntity() .getName
    (
        ) ,operation.getName());
                }@OverridepublicStringgetLocalizedOperationTitle(Stringentity, Stringname){returnlocalizations.
    getOperationTitle

    (getLanguage
    ( ) ,entity, name) ; }public
    String
        getLocalizedOperationField (StringentityName,StringoperationName,String name) {returnlocalizations
    .

    getFieldTitle ( getLanguage() ,entityName , operationName, name ).
    orElseGet
        ( ()->getColumnTitle(entityName,name )) ;} @Override
                publicStringgetLocalizedCell(String content ,Stringentity, Stringquery){
    String

    localized=
    MoreStrings . substituteVariables(content ,MESSAGE_PATTERN , (message ) ->localizations
    .
        get ( getLanguage (),entity,query ,message ).orElse (
                message));if(localized. startsWith( "{{{") &&localized.endsWith("}}}")
        ){

        String clearContent=localized.substring(3 , localized.length()-3
        )
            ; return localizations .get(getLanguage() ,entity,query, clearContent ).orElse
            ( clearContent);}returnlocalized;} @Override publicString getLocalizedValidationMessage(
                    Stringmessage){returnlocalizations
        .

        get (getLanguage
    (

    ),
    "messages.l10n" , "validation",message ).
    orElse
        ( message);}@OverridepublicString getLocalizedExceptionMessage( Stringmessage ){returnlocalizations.get(getLanguage
    (

    ),
    "messages.l10n" , "exception",message ).
    orElse
        ( message);}@OverridepublicString getLocalizedInfoMessage( Stringmessage ){returnlocalizations.get(getLanguage
    (

    ),
    "messages.l10n" , "info",message ).
    orElse
        ( message);}@OverridepublicQuerySettings getQuerySettings( Queryquery ){List<String>currentRoles=
    userInfoProvider

    .get
    ( ) .getCurrentRoles( );
    for
        (QuerySettingssettings: query . getQuerySettings()){Set<String>roles
        = settings. getRoles ( ).getFinalRoles();
        for
            (Stringrole: currentRoles ) {if(roles.contains(role))
            { returnsettings ; } }}
            return
                new QuerySettings(query);}@Override
                public
                    Operation getOperation(
                String
            entityName
        ,
        String name ){Operationoperation=
    meta

    .getOperation
    ( entityName ,name) ;if ( !meta
    .
        hasAccess ( operation .getRoles(),userInfoProvider .get(
        ) .getCurrentRoles()))throwBe5Exception.accessDeniedToOperation(entityName ,name);returnoperation;}@Overridepublic
            boolean hasAccessToOperation(StringentityName,String queryName,String

        name ){
    Operation

    operation=
    meta . getOperation(entityName ,queryName , name) ; returnmeta
    .
        hasAccess ( operation .getRoles(),userInfoProvider .get ().
        getCurrentRoles ());}@OverridepublicOperationgetOperation (StringentityName,StringqueryName,Stringname){
    Operation

    operation=
    meta . getOperation(entityName ,queryName , name) ; if(
    !
        meta . hasAccess (operation.getRoles() ,userInfoProvider .get(
        ) .getCurrentRoles()))throwBe5Exception.accessDeniedToOperation(entityName ,name);returnoperation;}@Overridepublic
            Query getQuery(StringentityName,String queryName){

        Query query=
    meta

    .getQuery
    ( entityName ,queryName) ;if ( !meta
    .
        hasAccess ( query .getRoles(),userInfoProvider .get(
        ) .getCurrentRoles()))throwBe5Exception.accessDeniedToQuery(entityName ,queryName);returnquery;}@Overridepublic
            String getColumnTitle(StringentityName,String queryName,String
        columnName ){
    return

    localizations.
    get ( getLanguage() ,entityName , queryName, columnName ).
    orElse
        ( columnName);}publicStringgetColumnTitle( StringentityName ,String columnName){ImmutableList<String>defaultQueries
    =

    ImmutableList . of("All records" ); for (String
    queryName
        :defaultQueries){ Optional < String>columnTitle=localizations.get
        ( getLanguage( ) , entityName,
        queryName
            ,columnName); if ( columnTitle.isPresent())returncolumnTitle .get () ;}return
            columnName ;}@OverridepublicStringgetFieldTitle ( StringentityName,StringoperationName,
        String
        queryName ,String
    name

    ){
    return localizations .getFieldTitle( getLanguage( ) ,entityName , operationName, queryName ,name
    )
        . orElse(name);}publicCompiledLocalizations getLocalizations( ){ returnlocalizations ;}privateStringgetLanguage(){
    return

    meta . getLocale(userInfoProvider
    .
        get ()
    .

    getLocale ( )).
    getLanguage
        ( );}@OverridepublicStringgetStaticPageContent(Stringname){StringpageContent=projectProvider.get
    (

    ).
    getStaticPageContent ( getLanguage() ,name
    )
        ; if ( pageContent==null)throwBe5Exception.notFound("static/"+name );return
        pageContent ;} } 