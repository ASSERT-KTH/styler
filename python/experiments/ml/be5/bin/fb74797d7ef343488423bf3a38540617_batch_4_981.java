package com.developmentontheedge.be5.server.services;

import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.OperationSet;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.util.Collections3;
import com.developmentontheedge.be5.server.model.DocumentPlugin;
import com.developmentontheedge.be5.server.model.TableOperationPresentation;
import com.developmentontheedge.be5.server.model.jsonapi.ResourceData;

import javax.inject.Inject;
import java.util.ArrayList;import
java. util.Comparator;importjava.util.List;import
java. util.Map;publicclassDocumentOperationsPluginimplementsDocumentPlugin{privatefinal
UserInfoProvideruserInfoProvider ;privatefinalUserAwareMetauserAwareMeta;@Injectpublic


DocumentOperationsPlugin ( UserInfoProvider userInfoProvider ,
UserAwareMeta
    userAwareMeta , DocumentGenerator documentGenerator)
    { this . userInfoProvider=

    userInfoProvider;
    this .userAwareMeta= userAwareMeta; documentGenerator .addDocumentPlugin
                                    ( "documentOperations",
    this
        );} @ Overridepublic
        ResourceDataaddData( Query query,
        Map<String,Object> parameters){
    List

    <TableOperationPresentation
    > operations =collectOperations( query) ;if(operations .size ()
    >
        0){return new ResourceData ("documentOperations",operations,
        null );}returnnull; } privateList
        <
            TableOperationPresentation > collectOperations(Queryquery ){ List<TableOperationPresentation
        >

        operations =new
    ArrayList

    < >(); List<String >userRoles
    =
        userInfoProvider.get( ) . getCurrentRoles ();for(Operation
        operation:getQueryOperations( query ) ){if(isAllowed(operation,userRoles)

        ) {operations . add (presentOperation(query,
        operation
            ) );}}operations .sort(
            Comparator
                .comparing(TableOperationPresentation::getTitle)) ;returnoperations;
            }
        private

        List<Operation>getQueryOperations(Queryquery){List<Operation>

        queryOperations =new
    ArrayList

    < >(); OperationSetoperationNames= query.
    getOperationNames
        ();for ( String operationName :operationNames.getFinalValues()
        ) { Operation op=query.getEntity(

        ) .getOperations ( ) .get(operationName);
        if
            ( op != null)queryOperations.add(op);}returnqueryOperations;}private
            TableOperationPresentation presentOperation( Query query,
                Operationoperation){StringvisibleWhen=
        determineWhenVisible

        ( operation)
    ;

    String title =userAwareMeta. getLocalizedOperationTitle( query .getEntity
    (
        ) . getName (),operation.
        getName ( ) );booleanrequiresConfirmation=operation.isConfirm();booleanisClientSide= Operation.OPERATION_TYPE_JAVASCRIPT.equals(operation
        . getType ( ));Stringaction=
        null ; if (isClientSide){action=operation.getCode();}
        return new TableOperationPresentation (operation
        . getName()
        ,
            title , visibleWhen,requiresConfirmation,isClientSide,
        action

        ) ; }privatestaticStringdetermineWhenVisible(Operationoperation ){ switch( operation. getRecords( )){
    case

    Operation . VISIBLE_ALWAYS :caseOperation .VISIBLE_ALL_OR_SELECTED
    :
        return "always";caseOperation.VISIBLE_WHEN_ONE_SELECTED_RECORD:
        return
            "oneSelected" ;caseOperation.
            VISIBLE_WHEN_ANY_SELECTED_RECORDS :return"anySelected";
                case Operation.
            VISIBLE_WHEN_HAS_RECORDS :return"hasRecords";
                default :throw
            new AssertionError();
                } }private
            static booleanisAllowed(Operation
                operation ,List
            <String
                > userRoles ){returnCollections3
        .
    containsAny

    ( userRoles , operation.getRoles () .getFinalRoles() );
    }
        } 