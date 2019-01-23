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
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;


public class UserAwareMetaImpl implements UserAwareMeta
{
    /**
     * The prefix constant for localized message.
     * <br/>This "{{{".
     */
    private static final String LOC_MSG_PREFIX = "{{{";

    /**
     * The postfix constant for localized message.
     * <br/>This "}}}".
     */
    private static final String LOC_MSG_POSTFIX = "}}}";

    private static final Pattern MESSAGE_PATTERN = MoreStrings.variablePattern(LOC_MSG_PREFIX, LOC_MSG_POSTFIX);

    private CompiledLocalizations localizations;

    private final Meta meta;
    private final ProjectProvider projectProvider;
    private final UserInfoProvider userInfoProvider;

    @Inject
    public UserAwareMetaImpl(Meta meta, ProjectProvider projectProvider, UserInfoProvider userInfoProvider)
    {
        this.meta = meta;
        this.projectProvider = projectProvider;
        this.userInfoProvider = userInfoProvider;

        projectProvider.addToReload(this::compileLocalizations);
        compileLocalizations();
    }

//    @Override
//    public void configure(String config)
//    {
//        compileLocalizations();
//    }

    @Override
    public void compileLocalizations()
    {
        localizations = CompiledLocalizations.from(projectProvider.get());
    }

    //todo localize entity, query, operation names
    @Override
    public String getLocalizedBe5ErrorMessage(Be5Exception e)
    {
        return ErrorTitles.formatTitle(
                getLocalizedExceptionMessage(ErrorTitles.getTitle(e.getCode())),
                e.getParameters()
        );
    }

    @Override
    public String getLocalizedEntityTitle(Entity entity)
    {
        Optional<String> localization = localizations.getEntityTitle(getLanguage(), entity.getName());

        if (!localization.isPresent())
        {
            if (!Strings.isNullOrEmpty(entity.getDisplayName())){
            return
                entity .getDisplayName();}
            return
            entity .getName();}
        return

        localization .get();}
    @

    Overridepublic
    String getLocalizedEntityTitle (Stringentity ){
    return
        getLocalizedEntityTitle (meta.getEntity(entity));}
    @

    Overridepublic
    String getLocalizedQueryTitle (Stringentity ,String query ){
    return
        localizations .getQueryTitle(getLanguage(),entity ,query );}
    @

    Overridepublic
    String getLocalizedOperationTitle (Operationoperation ){
    return
        localizations .getOperationTitle(getLanguage(),operation
                .getEntity().getName(),operation .getName());}
    @

    Overridepublic
    String getLocalizedOperationTitle (Stringentity ,String name ){
    return
        localizations .getOperationTitle(getLanguage(),entity ,name );}
    public

    String getLocalizedOperationField (StringentityName ,String operationName ,String name ){
    return
        localizations .getFieldTitle(getLanguage(),entityName ,operationName ,name ).
                orElseGet(()-> getColumnTitle (entityName,name ));}
    @

    Overridepublic
    String getLocalizedCell (Stringcontent ,String entity ,String query ){
    String
        localized = MoreStrings .substituteVariables(content,MESSAGE_PATTERN ,( message)-> localizations
                .get(getLanguage(),entity ,query ,message ).orElse(message))
        ;if

        ( localized.startsWith("{{{")&& localized .endsWith("}}}")){
        String
            clearContent = localized .substring(3,localized .length()- 3 );return
            localizations .get(getLanguage(),entity ,query ,clearContent ).
                    orElse(clearContent);}
        return

        localized ;}
    @

    Overridepublic
    String getLocalizedValidationMessage (Stringmessage ){
    return
        localizations .get(getLanguage(),"messages.l10n" ,"validation" ,message ).orElse(message);}
    @

    Overridepublic
    String getLocalizedExceptionMessage (Stringmessage ){
    return
        localizations .get(getLanguage(),"messages.l10n" ,"exception" ,message ).orElse(message);}
    @

    Overridepublic
    String getLocalizedInfoMessage (Stringmessage ){
    return
        localizations .get(getLanguage(),"messages.l10n" ,"info" ,message ).orElse(message);}
    @

    Overridepublic
    QuerySettings getQuerySettings (Queryquery ){
    List
        <String>currentRoles = userInfoProvider .get().getCurrentRoles();for
        ( QuerySettingssettings : query .getQuerySettings()){
        Set
            <String>roles = settings .getRoles().getFinalRoles();for
            ( Stringrole : currentRoles ){
            if
                ( roles.contains(role)){
                return
                    settings ;}
                }
            }
        return
        new QuerySettings (query);}
    @

    Overridepublic
    Operation getOperation (StringentityName ,String name ){
    Operation
        operation = meta .getOperation(entityName,name );if
        ( !meta.hasAccess(operation.getRoles(),userInfoProvider .get().getCurrentRoles()))throw
            Be5Exception .accessDeniedToOperation(entityName,name );return

        operation ;}
    @

    Overridepublic
    boolean hasAccessToOperation (StringentityName ,String queryName ,String name ){
    Operation
        operation = meta .getOperation(entityName,queryName ,name );return
        meta .hasAccess(operation.getRoles(),userInfoProvider .get().getCurrentRoles());}
    @

    Overridepublic
    Operation getOperation (StringentityName ,String queryName ,String name ){
    Operation
        operation = meta .getOperation(entityName,queryName ,name );if
        ( !meta.hasAccess(operation.getRoles(),userInfoProvider .get().getCurrentRoles()))throw
            Be5Exception .accessDeniedToOperation(entityName,name );return

        operation ;}
    @

    Overridepublic
    Query getQuery (StringentityName ,String queryName ){
    Query
        query = meta .getQuery(entityName,queryName );if
        ( !meta.hasAccess(query.getRoles(),userInfoProvider .get().getCurrentRoles()))throw
            Be5Exception .accessDeniedToQuery(entityName,queryName );return
        query ;}
    @

    Overridepublic
    String getColumnTitle (StringentityName ,String queryName ,String columnName ){
    return
        localizations .get(getLanguage(),entityName ,queryName ,columnName ).orElse(columnName);}
    public

    String getColumnTitle (StringentityName ,String columnName ){
    ImmutableList
        <String>defaultQueries = ImmutableList .of("All records");for
        ( StringqueryName : defaultQueries ){
        Optional
            <String>columnTitle = localizations .get(getLanguage(),entityName ,queryName ,columnName );if
            ( columnTitle.isPresent())return columnTitle .get();}
        return
        columnName ;}
    @

    Overridepublic
    String getFieldTitle (StringentityName ,String operationName ,String queryName ,String name ){
    return
        localizations .getFieldTitle(getLanguage(),entityName ,operationName ,queryName ,name ).orElse(name);}
    public

    CompiledLocalizations getLocalizations (){
    return
        localizations ;}
    private

    String getLanguage (){
    return
        meta .getLocale(userInfoProvider.get().getLocale()).getLanguage();}
    @

    Overridepublic
    String getStaticPageContent (Stringname ){
    String
        pageContent = projectProvider .get().getStaticPageContent(getLanguage(),name );if
        ( pageContent== null )throw
            Be5Exception .notFound("static/"+ name );return

        pageContent ;}
    }
