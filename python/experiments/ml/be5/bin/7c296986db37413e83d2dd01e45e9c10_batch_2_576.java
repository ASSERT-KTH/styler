package com.developmentontheedge.be5.base.services.impl;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.exceptions.ErrorTitles;
import com.developmentontheedge.be5.base.
services.Meta;importcom
. developmentontheedge.be5.base.services.ProjectProvider;importcom
. developmentontheedge.be5.base.services.UserAwareMeta;importcom
. developmentontheedge.be5.base.services.UserInfoProvider;importcom
. developmentontheedge.be5.base.util.MoreStrings;importcom
. developmentontheedge.be5.metadata.model.Entity;importcom
. developmentontheedge.be5.metadata.model.Operation;importcom
. developmentontheedge.be5.metadata.model.Query;importcom
. developmentontheedge.be5.metadata.model.QuerySettings;importcom
. google.common.base.Strings;importcom
. google.common.collect.ImmutableList;importjavax

. inject.Inject;importjava
. util.List;importjava
. util.Optional;importjava
. util.Set;importjava
. util.regex.Pattern;publicclass


UserAwareMetaImpl implements UserAwareMeta { /**
     * The prefix constant for localized message.
     * <br/>This "{{{".
     */
private
    static
    final String LOC_MSG_PREFIX = "{{{" ; /**
     * The postfix constant for localized message.
     * <br/>This "}}}".
     */private

    static
    final String LOC_MSG_POSTFIX = "}}}" ; privatestatic

    final Pattern MESSAGE_PATTERN = MoreStrings . variablePattern(LOC_MSG_PREFIX,LOC_MSG_POSTFIX) ;privateCompiledLocalizations

    localizations ; privatefinal

    Meta meta ; privatefinal
    ProjectProvider projectProvider ; privatefinal
    UserInfoProvider userInfoProvider ; @Inject

    publicUserAwareMetaImpl
    ( Metameta, ProjectProviderprojectProvider , UserInfoProvideruserInfoProvider ) {this
    .
        meta=meta ; this.
        projectProvider=projectProvider ; this.
        userInfoProvider=userInfoProvider ; projectProvider.

        addToReload(this::compileLocalizations);compileLocalizations(
        );}//    @Override
    //    public void configure(String config)

//    {
//        compileLocalizations();
//    }
@
Override

    publicvoid
    compileLocalizations ( ){localizations
    =
        CompiledLocalizations . from(projectProvider.get());}//todo localize entity, query, operation names
    @

    Override
    publicString
    getLocalizedBe5ErrorMessage ( Be5Exceptione) {return
    ErrorTitles
        . formatTitle(getLocalizedExceptionMessage(
                ErrorTitles.getTitle(e.getCode())),e.
                getParameters());
        }@
    Override

    publicString
    getLocalizedEntityTitle ( Entityentity) {Optional
    <
        String>localization= localizations . getEntityTitle(getLanguage(),entity. getName());if(

        ! localization.isPresent()){if
        (
            ! Strings.isNullOrEmpty(entity.getDisplayName())){return
            entity
                . getDisplayName();}return
            entity
            . getName();}return
        localization

        . get();}@
    Override

    publicString
    getLocalizedEntityTitle ( Stringentity) {return
    getLocalizedEntityTitle
        ( meta.getEntity(entity));}@
    Override

    publicString
    getLocalizedQueryTitle ( Stringentity, Stringquery ) {return
    localizations
        . getQueryTitle(getLanguage(),entity, query) ;}@
    Override

    publicString
    getLocalizedOperationTitle ( Operationoperation) {return
    localizations
        . getOperationTitle(getLanguage(),operation.
                getEntity().getName(),operation. getName());}@
    Override

    publicString
    getLocalizedOperationTitle ( Stringentity, Stringname ) {return
    localizations
        . getOperationTitle(getLanguage(),entity, name) ;}public
    String

    getLocalizedOperationField ( StringentityName, StringoperationName , Stringname ) {return
    localizations
        . getFieldTitle(getLanguage(),entityName, operationName, name) .orElseGet
                (()->getColumnTitle ( entityName,name) );}@
    Override

    publicString
    getLocalizedCell ( Stringcontent, Stringentity , Stringquery ) {String
    localized
        = MoreStrings . substituteVariables(content,MESSAGE_PATTERN, (message )->localizations .
                get(getLanguage(),entity, query, message) .orElse(message));
        if(

        localized .startsWith("{{{")&&localized . endsWith("}}}")){String
        clearContent
            = localized . substring(3,localized. length()-3 ) ;returnlocalizations
            . get(getLanguage(),entity, query, clearContent) .orElse
                    (clearContent);}return
        localized

        ; }@
    Override

    publicString
    getLocalizedValidationMessage ( Stringmessage) {return
    localizations
        . get(getLanguage(),"messages.l10n", "validation", message) .orElse(message);}@
    Override

    publicString
    getLocalizedExceptionMessage ( Stringmessage) {return
    localizations
        . get(getLanguage(),"messages.l10n", "exception", message) .orElse(message);}@
    Override

    publicString
    getLocalizedInfoMessage ( Stringmessage) {return
    localizations
        . get(getLanguage(),"messages.l10n", "info", message) .orElse(message);}@
    Override

    publicQuerySettings
    getQuerySettings ( Queryquery) {List
    <
        String>currentRoles= userInfoProvider . get().getCurrentRoles();for(
        QuerySettings settings: query . getQuerySettings()){Set
        <
            String>roles= settings . getRoles().getFinalRoles();for(
            String role: currentRoles ) {if
            (
                roles .contains(role)){return
                settings
                    ; }}
                }
            return
        new
        QuerySettings ( query);}@
    Override

    publicOperation
    getOperation ( StringentityName, Stringname ) {Operation
    operation
        = meta . getOperation(entityName,name) ;if(
        ! meta.hasAccess(operation.getRoles(),userInfoProvider. get().getCurrentRoles()))throwBe5Exception
            . accessDeniedToOperation(entityName,name) ;returnoperation

        ; }@
    Override

    publicboolean
    hasAccessToOperation ( StringentityName, StringqueryName , Stringname ) {Operation
    operation
        = meta . getOperation(entityName,queryName, name) ;returnmeta
        . hasAccess(operation.getRoles(),userInfoProvider. get().getCurrentRoles());}@
    Override

    publicOperation
    getOperation ( StringentityName, StringqueryName , Stringname ) {Operation
    operation
        = meta . getOperation(entityName,queryName, name) ;if(
        ! meta.hasAccess(operation.getRoles(),userInfoProvider. get().getCurrentRoles()))throwBe5Exception
            . accessDeniedToOperation(entityName,name) ;returnoperation

        ; }@
    Override

    publicQuery
    getQuery ( StringentityName, StringqueryName ) {Query
    query
        = meta . getQuery(entityName,queryName) ;if(
        ! meta.hasAccess(query.getRoles(),userInfoProvider. get().getCurrentRoles()))throwBe5Exception
            . accessDeniedToQuery(entityName,queryName) ;returnquery
        ; }@
    Override

    publicString
    getColumnTitle ( StringentityName, StringqueryName , StringcolumnName ) {return
    localizations
        . get(getLanguage(),entityName, queryName, columnName) .orElse(columnName);}public
    String

    getColumnTitle ( StringentityName, StringcolumnName ) {ImmutableList
    <
        String>defaultQueries= ImmutableList . of("All records");for(
        String queryName: defaultQueries ) {Optional
        <
            String>columnTitle= localizations . get(getLanguage(),entityName, queryName, columnName) ;if(
            columnTitle .isPresent())returncolumnTitle . get();}return
        columnName
        ; }@
    Override

    publicString
    getFieldTitle ( StringentityName, StringoperationName , StringqueryName , Stringname ) {return
    localizations
        . getFieldTitle(getLanguage(),entityName, operationName, queryName, name) .orElse(name);}public
    CompiledLocalizations

    getLocalizations ( ){return
    localizations
        ; }private
    String

    getLanguage ( ){return
    meta
        . getLocale(userInfoProvider.get().getLocale()).getLanguage();}@
    Override

    publicString
    getStaticPageContent ( Stringname) {String
    pageContent
        = projectProvider . get().getStaticPageContent(getLanguage(),name) ;if(
        pageContent ==null ) throwBe5Exception
            . notFound("static/"+name ) ;returnpageContent

        ; }}
    