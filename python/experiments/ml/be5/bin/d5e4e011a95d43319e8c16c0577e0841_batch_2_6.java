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
importcom .developmentontheedge.be5.metadata.model.Query;importcom.developmentontheedge.be5.metadata.model.QuerySettings;
importcom .google.common.base.Strings;importcom.google.common.collect
. ImmutableList;importjavax.inject.Inject;import
java .util.List;importjava.util.

Optional ;importjava.util.
Set ;importjava.util.
regex .Pattern;publicclassUserAwareMetaImpl
implements UserAwareMeta{/**
     * The prefix constant for localized message.
     * <br/>This "{{{".
     */privatestaticfinal
String LOC_MSG_PREFIX="{{{";/**
     * The postfix constant for localized message.
     * <br/>This "}}}".
     */privatestaticfinal


String LOC_MSG_POSTFIX = "}}}" ;
private
    static
    final Pattern MESSAGE_PATTERN = MoreStrings . variablePattern(

    LOC_MSG_PREFIX
    , LOC_MSG_POSTFIX ) ; private CompiledLocalizations localizations;

    private final Meta meta ; private finalProjectProviderprojectProvider;privatefinal UserInfoProvideruserInfoProvider;

    @ Inject publicUserAwareMetaImpl

    ( Meta meta ,ProjectProvider
    projectProvider , UserInfoProvider userInfoProvider)
    { this . meta=

    meta;
    this .projectProvider= projectProvider; this .userInfoProvider = userInfoProvider;
    projectProvider
        .addToReload( this ::compileLocalizations
        );compileLocalizations ( );
        }//    @Override//    public void configure(String config) //    { //        compileLocalizations();//    }

        @OverridepublicvoidcompileLocalizations(){localizations
        =CompiledLocalizations.from
    (

projectProvider
.
get
(
)

    );
    } //todo localize entity, query, operation names @Overridepublic
    String
        getLocalizedBe5ErrorMessage ( Be5Exceptione){returnErrorTitles.formatTitle(getLocalizedExceptionMessage(
    ErrorTitles

    .
    getTitle(
    e . getCode() ))
    ,
        e .getParameters()
                );}@OverridepublicStringgetLocalizedEntityTitle(Entityentity){Optional
                <String>localization=
        localizations.
    getEntityTitle

    (getLanguage
    ( ) ,entity. getName(
    )
        );if( ! localization .isPresent()){if( !Strings.isNullOrEmpty(entity.

        getDisplayName ())){returnentity.
        getDisplayName
            ( );}returnentity.getName();}returnlocalization
            .
                get ();}@Override
            public
            String getLocalizedEntityTitle(Stringentity){
        return

        getLocalizedEntityTitle (meta.getEntity(entity
    )

    );
    } @ OverridepublicString getLocalizedQueryTitle(
    String
        entity ,Stringquery){returnlocalizations.getQueryTitle(
    getLanguage

    ()
    , entity ,query) ;} @ Overridepublic
    String
        getLocalizedOperationTitle (Operationoperation){returnlocalizations. getOperationTitle( getLanguage()
    ,

    operation.
    getEntity ( ).getName ()
    ,
        operation .getName());}@
                OverridepublicStringgetLocalizedOperationTitle(Stringentity,Stringname ){returnlocalizations.getOperationTitle(
    getLanguage

    ()
    , entity ,name) ;} public StringgetLocalizedOperationField
    (
        String entityName,StringoperationName,Stringname) {return localizations.getFieldTitle
    (

    getLanguage ( ),entityName ,operationName , name) . orElseGet(
    (
        ) ->getColumnTitle(entityName,name)) ;} @Override publicString
                getLocalizedCell(Stringcontent, String entity,Stringquery ){Stringlocalized
    =

    MoreStrings.
    substituteVariables ( content,MESSAGE_PATTERN ,( message )-> localizations .get
    (
        getLanguage ( ) ,entity,query,message ). orElse(message )
                );if(localized.startsWith( "{{{") &&localized .endsWith("}}}")){
        StringclearContent

        = localized.substring(3,localized . length()-3);
        return
            localizations . get (getLanguage(),entity ,query,clearContent) . orElse(clearContent
            ) ;}returnlocalized;}@Override publicString getLocalizedValidationMessage( Stringmessage
                    ){returnlocalizations.get
        (

        getLanguage ()
    ,

    "messages.l10n",
    "validation" , message). orElse(
    message
        ) ;}@OverridepublicStringgetLocalizedExceptionMessage( Stringmessage ){ returnlocalizations.get(getLanguage()
    ,

    "messages.l10n",
    "exception" , message). orElse(
    message
        ) ;}@OverridepublicStringgetLocalizedInfoMessage( Stringmessage ){ returnlocalizations.get(getLanguage()
    ,

    "messages.l10n",
    "info" , message). orElse(
    message
        ) ;}@OverridepublicQuerySettingsgetQuerySettings( Queryquery ){ List<String>currentRoles=userInfoProvider.
    get

    ()
    . getCurrentRoles (); for(
    QuerySettings
        settings:query. getQuerySettings ( )){Set<String>roles=settings
        . getRoles( ) . getFinalRoles();for(
        String
            role:currentRoles) { if (roles.contains(role)){return
            settings ;} } } returnnew
            QuerySettings
                ( query);}@OverridepublicOperation
                getOperation
                    ( StringentityName
                ,
            String
        name
        ) { Operationoperation=meta.
    getOperation

    (entityName
    , name );if (! meta .hasAccess
    (
        operation . getRoles (),userInfoProvider.get ().
        getCurrentRoles ()))throwBe5Exception.accessDeniedToOperation(entityName,name );returnoperation;}@OverridepublicbooleanhasAccessToOperation
            ( StringentityName,StringqueryName, Stringname)

        { Operationoperation
    =

    meta.
    getOperation ( entityName,queryName ,name ) ;return meta .hasAccess
    (
        operation . getRoles (),userInfoProvider.get () .getCurrentRoles(
        ) );}@OverridepublicOperationgetOperation(String entityName,StringqueryName,Stringname){Operationoperation
    =

    meta.
    getOperation ( entityName,queryName ,name ) ;if ( !meta
    .
        hasAccess ( operation .getRoles(),userInfoProvider .get ().
        getCurrentRoles ()))throwBe5Exception.accessDeniedToOperation(entityName,name );returnoperation;}@OverridepublicQuerygetQuery
            ( StringentityName,StringqueryName) {Queryquery

        = meta.
    getQuery

    (entityName
    , queryName );if (! meta .hasAccess
    (
        query . getRoles (),userInfoProvider.get ().
        getCurrentRoles ()))throwBe5Exception.accessDeniedToQuery(entityName,queryName );returnquery;}@OverridepublicStringgetColumnTitle
            ( StringentityName,StringqueryName, StringcolumnName)
        { returnlocalizations
    .

    get(
    getLanguage ( ),entityName ,queryName , columnName) . orElse(
    columnName
        ) ;}publicStringgetColumnTitle(StringentityName ,String columnName) {ImmutableList<String>defaultQueries=ImmutableList
    .

    of ( "All records"); for( String queryName:
    defaultQueries
        ){Optional< String > columnTitle=localizations.get(getLanguage
        ( ), entityName , queryName,
        columnName
            );if( columnTitle . isPresent())returncolumnTitle.get () ;} returncolumnName;
            } @OverridepublicStringgetFieldTitle(String entityName ,StringoperationName,StringqueryName
        ,
        String name)
    {

    returnlocalizations
    . getFieldTitle (getLanguage( ), entityName ,operationName , queryName, name ).
    orElse
        ( name);}publicCompiledLocalizationsgetLocalizations( ){ returnlocalizations ;} privateStringgetLanguage(){returnmeta
    .

    getLocale ( userInfoProvider.get
    (
        ) .getLocale
    (

    ) ) .getLanguage(
    )
        ; }@OverridepublicStringgetStaticPageContent(Stringname){StringpageContent=projectProvider.get()
    .

    getStaticPageContent(
    getLanguage ( ),name );
    if
        ( pageContent == null)throwBe5Exception.notFound("static/"+name); returnpageContent;
        } }