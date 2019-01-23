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
import com.developmentontheedge.be5.metadata.model.QuerySettings;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;importjava


. util . Set ;
import
    java
    . util . regex . Pattern ;public

    class
    UserAwareMetaImpl implements UserAwareMeta { /**
     * The prefix constant for localized message.
     * <br/>This "{{{".
     */ private staticfinal

    String LOC_MSG_PREFIX = "{{{" ; /**
     * The postfix constant for localized message.
     * <br/>This "}}}".
     */ privatestaticfinalStringLOC_MSG_POSTFIX= "}}}";private

    static final PatternMESSAGE_PATTERN

    = MoreStrings . variablePattern(
    LOC_MSG_PREFIX , LOC_MSG_POSTFIX );
    private CompiledLocalizations localizations ;private

    finalMeta
    meta ;privatefinal ProjectProviderprojectProvider ; privatefinal UserInfoProvider userInfoProvider;
    @
        InjectpublicUserAwareMetaImpl ( Metameta
        ,ProjectProviderprojectProvider , UserInfoProvideruserInfoProvider
        ){this . meta=

        meta;this.projectProvider=projectProvider;this
        .userInfoProvider=userInfoProvider
    ;

projectProvider
.
addToReload
(
this

    ::compileLocalizations
    ) ; compileLocalizations()
    ;
        } //    @Override //    public void configure(String config)//    {//        compileLocalizations();//    }@OverridepublicvoidcompileLocalizations()
    {

    localizations
    =CompiledLocalizations
    . from (projectProvider. get(
    )
        ) ;}//todo localize entity, query, operation names@
                OverridepublicStringgetLocalizedBe5ErrorMessage(Be5Exceptione){returnErrorTitles.formatTitle(
                getLocalizedExceptionMessage(ErrorTitles.getTitle
        (e
    .

    getCode(
    ) ) ),e .getParameters
    (
        ));} @ Override publicStringgetLocalizedEntityTitle(Entityentity){ Optional<String>localization=localizations

        . getEntityTitle(getLanguage(),entity.
        getName
            ( ));if(!localization.isPresent()){
            if
                ( !Strings.isNullOrEmpty(entity
            .
            getDisplayName ())){return
        entity

        . getDisplayName();}return
    entity

    .getName
    ( ) ;}return localization.
    get
        ( );}@OverridepublicStringgetLocalizedEntityTitle(String
    entity

    ){
    return getLocalizedEntityTitle (meta. getEntity( entity ))
    ;
        } @OverridepublicStringgetLocalizedQueryTitle(Stringentity ,String query){
    return

    localizations.
    getQueryTitle ( getLanguage() ,entity
    ,
        query );}@OverridepublicStringgetLocalizedOperationTitle
                (Operationoperation){returnlocalizations.getOperationTitle( getLanguage(),operation.getEntity
    (

    ).
    getName ( ),operation .getName ( ))
    ;
        } @OverridepublicStringgetLocalizedOperationTitle(Stringentity ,String name){
    return

    localizations . getOperationTitle(getLanguage () , entity, name );
    }
        public StringgetLocalizedOperationField(StringentityName,StringoperationName ,String name) {return
                localizations.getFieldTitle(getLanguage ( ),entityName, operationName,name)
    .

    orElseGet(
    ( ) ->getColumnTitle( entityName, name )) ; }@
    Override
        public String getLocalizedCell (Stringcontent,Stringentity ,String query){ String
                localized=MoreStrings.substituteVariables(content, MESSAGE_PATTERN, (message )->localizations.get(getLanguage
        ()

        , entity,query,message). orElse (message));if(
        localized
            . startsWith ( "{{{")&&localized.endsWith ("}}}")){ String clearContent=localized
            . substring(3,localized.length( )- 3) ;return
                    localizations.get(getLanguage(
        )

        , entity,
    query

    ,clearContent
    ) . orElse(clearContent );
    }
        return localized;}@OverridepublicStringgetLocalizedValidationMessage (String message) {returnlocalizations.get(getLanguage(
    )

    ,"messages.l10n"
    , "validation" ,message) .orElse
    (
        message );}@OverridepublicStringgetLocalizedExceptionMessage (String message) {returnlocalizations.get(getLanguage(
    )

    ,"messages.l10n"
    , "exception" ,message) .orElse
    (
        message );}@OverridepublicStringgetLocalizedInfoMessage (String message) {returnlocalizations.get(getLanguage(
    )

    ,"messages.l10n"
    , "info" ,message) .orElse
    (
        message);} @ Override publicQuerySettingsgetQuerySettings(Queryquery){List<
        String >currentRoles = userInfoProvider .get().getCurrentRoles
        (
            );for( QuerySettings settings :query.getQuerySettings()){Set<
            String >roles = settings .getRoles
            (
                ) .getFinalRoles();for(String
                role
                    : currentRoles)
                {
            if
        (
        roles . contains(role))
    {

    returnsettings
    ; } }}return newQuerySettings ( query)
    ;
        } @ Override publicOperationgetOperation(StringentityName ,Stringname
        ) {Operationoperation=meta.getOperation(entityName,name) ;if(!meta.hasAccess(operation.getRoles
            ( ),userInfoProvider.get( ).getCurrentRoles

        ( ))
    )

    throwBe5Exception
    . accessDeniedToOperation (entityName, name) ; returnoperation ; }@
    Override
        public boolean hasAccessToOperation (StringentityName,StringqueryName ,String name){
        Operation operation=meta.getOperation(entityName,queryName, name);returnmeta.hasAccess(operation.getRoles
    (

    ),
    userInfoProvider . get() .getCurrentRoles ( )) ; }@
    Override
        public Operation getOperation (StringentityName,StringqueryName ,String name){
        Operation operation=meta.getOperation(entityName,queryName,name) ;if(!meta.hasAccess(operation.getRoles
            ( ),userInfoProvider.get( ).getCurrentRoles

        ( ))
    )

    throwBe5Exception
    . accessDeniedToOperation (entityName, name) ; returnoperation
    ;
        } @ Override publicQuerygetQuery(StringentityName ,StringqueryName
        ) {Queryquery=meta.getQuery(entityName,queryName) ;if(!meta.hasAccess(query.getRoles
            ( ),userInfoProvider.get( ).getCurrentRoles
        ( ))
    )

    throwBe5Exception
    . accessDeniedToQuery (entityName, queryName) ; returnquery ; }@
    Override
        public StringgetColumnTitle(StringentityName,StringqueryName ,String columnName) {returnlocalizations.get(getLanguage(
    )

    , entityName ,queryName, columnName) . orElse(
    columnName
        );}public String getColumnTitle (StringentityName,StringcolumnName)
        { ImmutableList< String > defaultQueries=
        ImmutableList
            .of("All records" ) ; for(StringqueryName:defaultQueries){ Optional< String> columnTitle=localizations
            . get(getLanguage(),entityName , queryName,columnName);if
        (
        columnTitle .isPresent
    (

    ))
    return columnTitle .get( ); } returncolumnName ; }@ Override publicString
    getFieldTitle
        ( StringentityName,StringoperationName,StringqueryName ,String name) {return localizations.getFieldTitle(getLanguage(),
    entityName

    , operationName ,queryName,
    name
        ) .orElse
    (

    name ) ;}public
    CompiledLocalizations
        getLocalizations (){returnlocalizations;}privateStringgetLanguage(){returnmeta.getLocale(userInfoProvider
    .

    get(
    ) . getLocale() ).
    getLanguage
        ( ) ; }@OverridepublicStringgetStaticPageContent(Stringname){String pageContent=projectProvider
        . get( ) .getStaticPageContent
            ( getLanguage(),name ) ;if(

        pageContent ==null
    )
throw
