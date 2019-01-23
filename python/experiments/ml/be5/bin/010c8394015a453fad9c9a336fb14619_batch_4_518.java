package com.developmentontheedge.be5.server.services;

import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.OperationSet;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.util.Collections3;
import com.developmentontheedge.be5.server.model.DocumentPlugin;
import com.developmentontheedge.be5.server.model.TableOperationPresentation;

import com.developmentontheedge.be5.
server .model.jsonapi.ResourceData
; importjavax.inject.Inject
; importjava.util.ArrayList
; importjava.util.Comparator


; import java . util
.
    List ; import java.
    util . Map ;public

    classDocumentOperationsPlugin
    implements DocumentPlugin{private finalUserInfoProvider userInfoProvider ;private
                                    final UserAwareMetauserAwareMeta
    ;
        @Injectpublic DocumentOperationsPlugin (UserInfoProvider
        userInfoProvider,UserAwareMeta userAwareMeta ,DocumentGenerator
        documentGenerator){this.userInfoProvider =userInfoProvider;
    this

    .userAwareMeta
    = userAwareMeta ;documentGenerator. addDocumentPlugin( "documentOperations",this) ;} @Override
    public
        ResourceDataaddData(Query query , Map<String,Object
        > parameters){List<TableOperationPresentation > operations=
        collectOperations
            ( query );if( operations. size()
        >

        0 ){
    return

    new ResourceData("documentOperations", operations,null );
    }
        returnnull;} private List < TableOperationPresentation>collectOperations(Queryquery
        ){List< TableOperationPresentation > operations=newArrayList<>();List

        < String> userRoles = userInfoProvider.get()
        .
            getCurrentRoles ();for( Operationoperation:
            getQueryOperations
                (query)){if(isAllowed (operation,userRoles
            )
        )

        {operations.add(presentOperation(query,operation));}

        } operations.
    sort

    ( Comparator.comparing( TableOperationPresentation::getTitle ))
    ;
        returnoperations;} private List < Operation>getQueryOperations(Queryquery
        ) { List <Operation>queryOperations=new

        ArrayList <> ( ) ;OperationSetoperationNames=query.
        getOperationNames
            ( ) ; for(StringoperationName:operationNames.getFinalValues()){Operationop=
            query .getEntity ( ).
                getOperations().get(operationName
        )

        ; if(
    op

    != null )queryOperations. add( op );
    }
        return queryOperations ; }privateTableOperationPresentationpresentOperation(
        Query query , Operationoperation){StringvisibleWhen=determineWhenVisible(operation);Stringtitle =userAwareMeta.getLocalizedOperationTitle(query.
        getEntity ( ) .getName(),operation
        . getName ( ));booleanrequiresConfirmation=operation.isConfirm();boolean
        isClientSide = Operation .OPERATION_TYPE_JAVASCRIPT
        . equals(operation
        .
            getType ( ));Stringaction=
        null

        ; if (isClientSide){action=operation. getCode( ); }return newTableOperationPresentation (operation.
    getName

    ( ) , title,visibleWhen ,requiresConfirmation
    ,
        isClientSide ,action);}privatestatic
        String
            determineWhenVisible (Operationoperation)
            { switch(operation.
                getRecords ()
            ) {caseOperation.
                VISIBLE_ALWAYS :case
            Operation .VISIBLE_ALL_OR_SELECTED:return
                "always" ;case
            Operation .VISIBLE_WHEN_ONE_SELECTED_RECORD:return
                "oneSelected" ;case
            Operation.
                VISIBLE_WHEN_ANY_SELECTED_RECORDS : return"anySelected";case
        Operation
    .

    VISIBLE_WHEN_HAS_RECORDS : return "hasRecords";default :throw newAssertionError() ;}
    }
        private staticbooleanisAllowed(Operationoperation ,List<String>userRoles){returnCollections3.
    containsAny

(
