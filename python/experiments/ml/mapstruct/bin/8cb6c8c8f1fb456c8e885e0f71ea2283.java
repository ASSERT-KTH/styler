/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.

assignment ;importjava.util.
ArrayList ;importjava.util.
HashSet ;importjava.util.
List ;importjava.util.

Set ;importorg.mapstruct.ap.internal.model.common.
Assignment ;importorg.mapstruct.ap.internal.model.common.

Type
; /**
 * Decorates an assignment as local variable.
 *
 * @author Sjaak Derksen
 */ public class LocalVarWrapper extends

   AssignmentWrapper { privatefinalList< Type>
   thrownTypesToExclude ; private finalType

   targetType ;publicLocalVarWrapper (Assignment decoratedAssignment,List< Type> thrownTypesToExclude ,Type
                          targetType ,boolean fieldAssignment
       ){ super( decoratedAssignment ,fieldAssignment
       );this . thrownTypesToExclude=
       thrownTypesToExclude;this . targetType=
   targetType

   ;}
   @ OverridepublicList< Type>getThrownTypes (
       ){List< Type > parentThrownTypes=super.getThrownTypes(
       );List< Type > result =newArrayList< > (parentThrownTypes
       ) ; for ( Type thrownTypeToExclude : thrownTypesToExclude
           ) { for ( Type parentThrownType : parentThrownTypes
               ) { if(parentThrownType. isAssignableTo ( thrownTypeToExclude )
                   ){result. remove (parentThrownType
               )
           ;
       }
       } }return
   result

   ;}
   @ OverridepublicSet< Type>getImportTypes (
       ){Set< Type > imported =newHashSet< >(getAssignment().getImportTypes ()
       );imported. add (targetType
       );imported. addAll(targetType.getTypeParameters ()
       ) ;return
   imported

;
