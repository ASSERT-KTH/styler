package com.developmentontheedge.be5.server.services;

import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.OperationSet;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.util.Collections3;
import com.developmentontheedge.be5.server.model.DocumentPlugin;
import com.developmentontheedge.be5.server.model.TableOperationPresentation;
import com.developmentontheedge.be5.server.model.
jsonapi.ResourceData

; importjavax.inject.Inject
; importjava.util.ArrayList
; importjava.util.Comparator
; importjava.util.List
; importjava.util.Map


; public class DocumentOperationsPlugin implements
DocumentPlugin
    { private final UserInfoProvideruserInfoProvider
    ; private final UserAwareMetauserAwareMeta

    ;@
    Inject publicDocumentOperationsPlugin( UserInfoProvideruserInfoProvider , UserAwareMetauserAwareMeta
                                    , DocumentGeneratordocumentGenerator
    )
        {this. userInfoProvider =userInfoProvider
        ;this. userAwareMeta =userAwareMeta
        ;documentGenerator.addDocumentPlugin("documentOperations" ,this)
    ;

    }@
    Override public ResourceDataaddData( Queryquery ,Map<String ,Object >parameters
    )
        {List<TableOperationPresentation > operations =collectOperations(query)
        ; if(operations.size( ) >0
        )
            { return newResourceData("documentOperations" ,operations ,null)
        ;

        } returnnull
    ;

    } privateList<TableOperationPresentation >collectOperations( Queryquery
    )
        {List<TableOperationPresentation > operations = newArrayList<>()
        ;List<String > userRoles =userInfoProvider.get().getCurrentRoles()

        ; for( Operation operation :getQueryOperations(query)
        )
            { if(isAllowed(operation ,userRoles)
            )
                {operations.add(presentOperation(query ,operation))
            ;
        }

        }operations.sort(Comparator.comparing(TableOperationPresentation::getTitle))

        ; returnoperations
    ;

    } privateList<Operation >getQueryOperations( Queryquery
    )
        {List<Operation > queryOperations = newArrayList<>()
        ; OperationSet operationNames =query.getOperationNames()

        ; for( String operationName :operationNames.getFinalValues()
        )
            { Operation op =query.getEntity().getOperations().get(operationName)
            ; if( op !=null
                )queryOperations.add(op)
        ;

        } returnqueryOperations
    ;

    } private TableOperationPresentationpresentOperation( Queryquery , Operationoperation
    )
        { String visibleWhen =determineWhenVisible(operation)
        ; String title =userAwareMeta.getLocalizedOperationTitle(query.getEntity().getName() ,operation.getName())
        ; boolean requiresConfirmation =operation.isConfirm()
        ; boolean isClientSide =Operation.OPERATION_TYPE_JAVASCRIPT.equals(operation.getType())
        ; String action =null
        ; if(isClientSide
        )
            { action =operation.getCode()
        ;

        } return newTableOperationPresentation(operation.getName() ,title ,visibleWhen ,requiresConfirmation ,isClientSide ,action)
    ;

    } private static StringdetermineWhenVisible( Operationoperation
    )
        { switch(operation.getRecords()
        )
            { caseOperation.VISIBLE_ALWAYS
            : caseOperation.VISIBLE_ALL_OR_SELECTED
                : return"always"
            ; caseOperation.VISIBLE_WHEN_ONE_SELECTED_RECORD
                : return"oneSelected"
            ; caseOperation.VISIBLE_WHEN_ANY_SELECTED_RECORDS
                : return"anySelected"
            ; caseOperation.VISIBLE_WHEN_HAS_RECORDS
                : return"hasRecords"
            ;default
                : throw newAssertionError()
        ;
    }

    } private static booleanisAllowed( Operationoperation ,List<String >userRoles
    )
        { returnCollections3.containsAny(userRoles ,operation.getRoles().getFinalRoles())
    ;

}
