package com.developmentontheedge.be5.server.services;

import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.metadata.model.Operation;
importcom .developmentontheedge.be5.metadata.model.OperationSet;import
com. developmentontheedge.be5.metadata.model.Query;importcom.developmentontheedge.be5.metadata.
util .Collections3;importcom.developmentontheedge.be5.server.
model .DocumentPlugin;importcom.developmentontheedge.be5.server.
model .TableOperationPresentation;importcom.developmentontheedge.be5.server.
model .jsonapi.ResourceData;importjavax.inject.Inject;importjava

. util.ArrayList;importjava
. util.Comparator;importjava
. util.List;importjava
. util.Map;publicclass
DocumentOperationsPlugin implementsDocumentPlugin{privatefinalUserInfoProvider


userInfoProvider ; private final UserAwareMeta
userAwareMeta
    ; @ Inject publicDocumentOperationsPlugin
    ( UserInfoProvider userInfoProvider ,UserAwareMeta

    userAwareMeta,
    DocumentGenerator documentGenerator){ this. userInfoProvider =userInfoProvider
                                    ; this.
    userAwareMeta
        =userAwareMeta; documentGenerator .addDocumentPlugin
        ("documentOperations", this );
        }@OverridepublicResourceDataaddData (Queryquery
    ,

    Map<
    String , Object>parameters ){ List<TableOperationPresentation> operations= collectOperations(
    query
        );if( operations . size()>0
        ) {returnnewResourceData("documentOperations" , operations,
        null
            ) ; }returnnull; }private List<TableOperationPresentation
        >

        collectOperations (Query
    query

    ) {List<TableOperationPresentation >operations= newArrayList
    <
        >(); List < String >userRoles=userInfoProvider.get
        ().getCurrentRoles ( ) ;for(Operationoperation:getQueryOperations(query)

        ) {if ( isAllowed (operation,userRoles)
        )
            { operations.add(presentOperation (query,
            operation
                ));}}operations.sort (Comparator.comparing
            (
        TableOperationPresentation

        ::getTitle));returnoperations;}privateList<Operation>

        getQueryOperations (Query
    query

    ) {List<Operation >queryOperations= newArrayList
    <
        >(); OperationSet operationNames = query.getOperationNames();
        for ( String operationName:operationNames.getFinalValues(

        ) ){ Operation op =query.getEntity()
        .
            getOperations ( ) .get(operationName);if(op!=null)queryOperations.add
            ( op) ; }return
                queryOperations;}privateTableOperationPresentationpresentOperation(
        Query

        query ,Operation
    operation

    ) { StringvisibleWhen= determineWhenVisible( operation );
    String
        title = userAwareMeta .getLocalizedOperationTitle(query.
        getEntity ( ) .getName(),operation.getName());booleanrequiresConfirmation =operation.isConfirm();
        boolean isClientSide = Operation.OPERATION_TYPE_JAVASCRIPT.equals(
        operation . getType ());Stringaction=null;if(isClientSide)
        { action = operation.
        getCode ();
        }
            return new TableOperationPresentation(operation.getName(
        )

        , title ,visibleWhen,requiresConfirmation,isClientSide,action ); }private staticString determineWhenVisible( Operationoperation)
    {

    switch ( operation .getRecords( ))
    {
        case Operation.VISIBLE_ALWAYS:caseOperation.
        VISIBLE_ALL_OR_SELECTED
            : return"always";case
            Operation .VISIBLE_WHEN_ONE_SELECTED_RECORD:return
                "oneSelected" ;case
            Operation .VISIBLE_WHEN_ANY_SELECTED_RECORDS:return
                "anySelected" ;case
            Operation .VISIBLE_WHEN_HAS_RECORDS:return
                "hasRecords" ;default
            : thrownewAssertionError(
                ) ;}
            }private
                static boolean isAllowed(Operationoperation
        ,
    List

    < String > userRoles){ returnCollections3 .containsAny(userRoles ,operation
    .
        getRoles ().getFinalRoles() );}}