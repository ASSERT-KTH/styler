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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class DocumentOperationsPlugin implements DocumentPlugin
{
    private final UserInfoProvider userInfoProvider;
    private final UserAwareMeta userAwareMeta;

    @Inject
    public DocumentOperationsPlugin(UserInfoProvider userInfoProvider, UserAwareMeta userAwareMeta,
                                    DocumentGenerator documentGenerator)
    {
        this.userInfoProvider = userInfoProvider;
        this.userAwareMeta = userAwareMeta;
        documentGenerator.addDocumentPlugin("documentOperations", this);
    }

    @Override
    public ResourceData
    addData
        (Queryquery, Map < String,Object>parameters
        ) {List<TableOperationPresentation>operations = collectOperations(
        query
            ) ; if(operations. size( )>0
        )

        { returnnew
    ResourceData

    ( "documentOperations",operations, null); }return
    null
        ;}privateList < TableOperationPresentation > collectOperations(Queryquery){
        List<TableOperationPresentation> operations = newArrayList<>();List<String

        > userRoles= userInfoProvider . get().getCurrentRoles
        (
            ) ;for(Operationoperation :getQueryOperations(
            query
                )){if(isAllowed(operation ,userRoles))
            {
        operations

        .add(presentOperation(query,operation));}}operations

        . sort(
    Comparator

    . comparing(TableOperationPresentation:: getTitle)) ;return
    operations
        ;}privateList < Operation > getQueryOperations(Queryquery){
        List < Operation >queryOperations=newArrayList<

        > () ; OperationSet operationNames=query.getOperationNames(
        )
            ; for ( StringoperationName:operationNames.getFinalValues()){Operationop=query.
            getEntity () . getOperations(
                ).get(operationName);
        if

        ( op!=
    null

    ) queryOperations .add( op) ; }return
    queryOperations
        ; } private TableOperationPresentationpresentOperation(Queryquery
        , Operation operation ){StringvisibleWhen=determineWhenVisible(operation);Stringtitle=userAwareMeta .getLocalizedOperationTitle(query.getEntity(
        ) . getName (),operation.getName
        ( ) ) ;booleanrequiresConfirmation=operation.isConfirm();booleanisClientSide=
        Operation . OPERATION_TYPE_JAVASCRIPT .equals
        ( operation.getType
        (
            ) ) ;Stringaction=null;
        if

        ( isClientSide ){action=operation.getCode( ); }return newTableOperationPresentation (operation .getName(
    )

    , title , visibleWhen,requiresConfirmation ,isClientSide
    ,
        action );}privatestaticStringdetermineWhenVisible
        (
            Operation operation){switch
            ( operation.getRecords(
                ) ){
            case Operation.VISIBLE_ALWAYS:
                case Operation.
            VISIBLE_ALL_OR_SELECTED :return"always";
                case Operation.
            VISIBLE_WHEN_ONE_SELECTED_RECORD :return"oneSelected";
                case Operation.
            VISIBLE_WHEN_ANY_SELECTED_RECORDS:
                return "anySelected" ;caseOperation.
        VISIBLE_WHEN_HAS_RECORDS
    :

    return "hasRecords" ; default:throw newAssertionError ();} }private
    static
        boolean isAllowed(Operationoperation,List <String>userRoles){returnCollections3.containsAny(
    userRoles

,
