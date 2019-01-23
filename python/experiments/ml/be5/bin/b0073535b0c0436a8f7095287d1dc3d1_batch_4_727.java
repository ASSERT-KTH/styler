package com.developmentontheedge.be5.server.services;

import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.OperationSet;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.util.Collections3;
import com.developmentontheedge.be5.server.model.DocumentPlugin;
import com.developmentontheedge.be5.server.model.TableOperationPresentation;
importcom.developmentontheedge

 . be5.server.model.
jsonapi .ResourceData;importjavax.
inject .Inject;importjava.
util .ArrayList;importjava.
util .Comparator;importjava.


util . List ; import
java
    . util . Map;
    public class DocumentOperationsPlugin implementsDocumentPlugin

    {private
    final UserInfoProvideruserInfoProvider; privatefinal UserAwareMeta userAwareMeta;
                                    @ Injectpublic
    DocumentOperationsPlugin
        (UserInfoProvideruserInfoProvider , UserAwareMetauserAwareMeta
        ,DocumentGeneratordocumentGenerator ) {this
        .userInfoProvider=userInfoProvider;this .userAwareMeta=
    userAwareMeta

    ;documentGenerator
    . addDocumentPlugin ("documentOperations", this) ;}@Override publicResourceData addData(
    Query
        query,Map< String , Object>parameters){
        List <TableOperationPresentation>operations=collectOperations ( query)
        ;
            if ( operations.size( )> 0){
        return

        new ResourceData(
    "documentOperations"

    , operations,null) ;}return null;
    }
        privateList<TableOperationPresentation > collectOperations ( Queryquery){List<
        TableOperationPresentation>operations= new ArrayList <>();List<String>userRoles

        = userInfoProvider. get ( ).getCurrentRoles()
        ;
            for (Operationoperation:getQueryOperations (query)
            )
                {if(isAllowed(operation,userRoles )){operations
            .
        add

        (presentOperation(query,operation));}}operations.sort

        ( Comparator.
    comparing

    ( TableOperationPresentation::getTitle) );return operations;
    }
        privateList<Operation > getQueryOperations ( Queryquery){List<
        Operation > queryOperations =newArrayList<>(

        ) ;OperationSet operationNames = query.getOperationNames();
        for
            ( String operationName :operationNames.getFinalValues()){Operationop=query.getEntity(
            ) .getOperations ( ).
                get(operationName);if(
        op

        != null)
    queryOperations

    . add (op) ;} return queryOperations;
    }
        private TableOperationPresentation presentOperation (Queryquery,Operation
        operation ) { StringvisibleWhen=determineWhenVisible(operation);Stringtitle=userAwareMeta.getLocalizedOperationTitle (query.getEntity().
        getName ( ) ,operation.getName()
        ) ; boolean requiresConfirmation=operation.isConfirm();booleanisClientSide=Operation.
        OPERATION_TYPE_JAVASCRIPT . equals (operation
        . getType()
        )
            ; String action=null;if(
        isClientSide

        ) { action=operation.getCode(); }return newTableOperationPresentation (operation .getName (),
    title

    , visibleWhen , requiresConfirmation,isClientSide ,action
    )
        ; }privatestaticStringdetermineWhenVisible(Operation
        operation
            ) {switch(operation
            . getRecords())
                { caseOperation
            . VISIBLE_ALWAYS:caseOperation
                . VISIBLE_ALL_OR_SELECTED:
            return "always";caseOperation
                . VISIBLE_WHEN_ONE_SELECTED_RECORD:
            return "oneSelected";caseOperation
                . VISIBLE_WHEN_ANY_SELECTED_RECORDS:
            return"anySelected"
                ; case Operation.VISIBLE_WHEN_HAS_RECORDS:
        return
    "hasRecords"

    ; default : thrownewAssertionError () ;}}private staticboolean
    isAllowed
        ( Operationoperation,List<String >userRoles){returnCollections3.containsAny(userRoles,
    operation

.
