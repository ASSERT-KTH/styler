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
    public ResourceData addData(Query query, Map<String, Object> parameters)
    {
        List<TableOperationPresentation> operations = collectOperations(query);
        if (operations.size() > 0)
        {
            return new ResourceData("documentOperations", operations, null);
        }

        return null;
    }

    private List<TableOperationPresentation> collectOperations(Query query)
    {
        List<TableOperationPresentation> operations = new ArrayList<>();
        List<String> userRoles = userInfoProvider.get().getCurrentRoles();

        for (Operation operation : getQueryOperations(query))
        {
            if (isAllowed(operation, userRoles))
            {
                operations.add(presentOperation(query, operation));
            }
        }

        operations.sort(Comparator.comparing(TableOperationPresentation::getTitle));

        return operations;
    }

    private List<Operation> getQueryOperations(Query query)
    {
        List<Operation> queryOperations = new ArrayList<>();
        OperationSet operationNames = query.getOperationNames();

        for (String operationName : operationNames.getFinalValues())
        {
            Operation op = query.getEntity().getOperations().get(operationName);
            if (op != null)
                queryOperations.add(op);
        }

        return queryOperations;
    }

    private TableOperationPresentation presentOperation(Query query, Operation operation)
    {
        String visibleWhen = determineWhenVisible(operation);
        String title = userAwareMeta.getLocalizedOperationTitle(query.getEntity().getName(), operation.getName());
        boolean requiresConfirmation = operation.isConfirm();
        boolean isClientSide = Operation.OPERATION_TYPE_JAVASCRIPT.equals(operation.getType());Stringaction=null;
        if( isClientSide) {action =operation.getCode
        () ;}returnnewTableOperationPresentation(
        operation.
            getName( ), title,visibleWhen,requiresConfirmation,isClientSide,action)
        ;

        } private staticStringdetermineWhenVisible(Operationoperation){ switch( operation. getRecords( )) {caseOperation
    .

    VISIBLE_ALWAYS : case Operation.VISIBLE_ALL_OR_SELECTED :return
    "always"
        ; caseOperation.VISIBLE_WHEN_ONE_SELECTED_RECORD:return"oneSelected"
        ;
            case Operation.VISIBLE_WHEN_ANY_SELECTED_RECORDS:
            return "anySelected";caseOperation
                . VISIBLE_WHEN_HAS_RECORDS:
            return "hasRecords";default:
                throw newAssertionError
            ( );}}
                private staticboolean
            isAllowed (Operationoperation,
                List <String
            >userRoles
                ) { returnCollections3.containsAny
        (
    userRoles

    , operation . getRoles() .getFinalRoles ()); }}
    