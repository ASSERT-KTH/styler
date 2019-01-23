package com.developmentontheedge.be5.server.services;

import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
importcom
.developmentontheedge .be5.metadata.model.Operation;importcom.developmentontheedge.be5.metadata.model.OperationSet;importcom
.developmentontheedge .be5.metadata.model.Query;importcom.developmentontheedge.be5.metadata.util
. Collections3;importcom.developmentontheedge.be5.server.model
. DocumentPlugin;importcom.developmentontheedge.be5.server.model
. TableOperationPresentation;importcom.developmentontheedge.be5.server.model
. jsonapi.ResourceData;importjavax.inject.Inject;import
java .util.ArrayList;importjava.util.Comparator;importjava

. util.List;importjava
. util.Map;publicclass
DocumentOperationsPlugin implementsDocumentPlugin{privatefinalUserInfoProvider
userInfoProvider ;privatefinalUserAwareMetauserAwareMeta;
@ InjectpublicDocumentOperationsPlugin(UserInfoProvideruserInfoProvider


, UserAwareMeta userAwareMeta , DocumentGenerator
documentGenerator
    ) { this .userInfoProvider
    = userInfoProvider ; this.

    userAwareMeta=
    userAwareMeta ;documentGenerator. addDocumentPlugin( "documentOperations" ,this
                                    ) ;}
    @
        OverridepublicResourceData addData (Query
        query,Map < String,
        Object>parameters){List <TableOperationPresentation>
    operations

    =collectOperations
    ( query );if (operations .size() >0 ){
    return
        newResourceData("documentOperations" , operations ,null);}
        return null;}privateList< TableOperationPresentation >collectOperations
        (
            Query query ){List< TableOperationPresentation> operations=new
        ArrayList

        < >(
    )

    ; List<String> userRoles=userInfoProvider .get
    (
        ).getCurrentRoles( ) ; for (Operationoperation:getQueryOperations(
        query)){ if ( isAllowed(operation,userRoles)){operations.

        add (presentOperation ( query ,operation));
        }
            } operations.sort(Comparator .comparing(
            TableOperationPresentation
                ::getTitle));returnoperations; }privateList<
            Operation
        >

        getQueryOperations(Queryquery){List<Operation>queryOperations=newArrayList

        < >(
    )

    ; OperationSetoperationNames=query .getOperationNames( );
    for
        (StringoperationName: operationNames . getFinalValues ()){Operationop
        = query . getEntity().getOperations(

        ) .get ( operationName );if(op!=
        null
            ) queryOperations . add(op);}returnqueryOperations;}privateTableOperationPresentationpresentOperation(Query
            query ,Operation operation ){
                StringvisibleWhen=determineWhenVisible(operation)
        ;

        String title=
    userAwareMeta

    . getLocalizedOperationTitle (query. getEntity( ) .getName
    (
        ) , operation .getName())
        ; boolean requiresConfirmation =operation.isConfirm();booleanisClientSide=Operation.OPERATION_TYPE_JAVASCRIPT. equals(operation.getType()
        ) ; String action=null;if(
        isClientSide ) { action=operation.getCode();}returnnewTableOperationPresentation(
        operation . getName ()
        , title,visibleWhen
        ,
            requiresConfirmation , isClientSide,action);}
        private

        static String determineWhenVisible(Operationoperation){switch( operation. getRecords( )) {case Operation.VISIBLE_ALWAYS
    :

    case Operation . VISIBLE_ALL_OR_SELECTED:return "always";
    case
        Operation .VISIBLE_WHEN_ONE_SELECTED_RECORD:return"oneSelected";case
        Operation
            . VISIBLE_WHEN_ANY_SELECTED_RECORDS:return"anySelected"
            ; caseOperation.VISIBLE_WHEN_HAS_RECORDS
                : return"hasRecords"
            ; default:thrownew
                AssertionError ()
            ; }}privatestatic
                boolean isAllowed(
            Operation operation,List<
                String >userRoles
            ){
                return Collections3 .containsAny(userRoles
        ,
    operation

    . getRoles ( ).getFinalRoles () );}} 