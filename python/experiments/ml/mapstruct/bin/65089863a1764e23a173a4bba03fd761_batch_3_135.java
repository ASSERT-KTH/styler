/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model;

import static org.mapstruct.ap.internal.util.Strings.getSafeVariableName;
import static org.mapstruct.ap.internal.util.Strings.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mapstruct.ap.internal.model.common.Accessibility;
import org.mapstruct.ap.internal.model.common.ModelElement;
import org.mapstruct.ap.internal.model.common.Parameter;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.source.Method;

/**
 * A method implemented or referenced by a {@link Mapper} class.
 *
 * @author Gunnar Morling
 */
public abstract class MappingMethod

    extends ModelElement { private finalString
    name ; privatefinalList< Parameter>
    parameters ; privatefinalList< Parameter>
    sourceParameters ; private finalType
    returnType ; private finalParameter
    targetParameter ; private finalAccessibility
    accessibility ; privatefinalList< Type>
    thrownTypes ; private finalboolean
    isStatic ; private finalString
    resultName ; privatefinalList< LifecycleCallbackMethodReference>
    beforeMappingReferencesWithMappingTarget ; privatefinalList< LifecycleCallbackMethodReference>
    beforeMappingReferencesWithoutMappingTarget ; privatefinalList< LifecycleCallbackMethodReference>

    afterMappingReferences
    ; /**
     * constructor to be overloaded when local variable names are required prior to calling this constructor. (e.g. for
     * property mappings). It is supposed to be initialized with at least the parameter names.
     *
     * @param method the method for which this mapping is applicable
     * @param existingVariableNames set of already assigned variable names
     * @param beforeMappingReferences all life cycle methods to be called prior to carrying out mapping
     * @param afterMappingReferences all life cycle methods to be called after carrying out mapping
     */protectedMappingMethod (Method method,Collection< String>
                            existingVariableNames,List< LifecycleCallbackMethodReference>
                            beforeMappingReferences,List< LifecycleCallbackMethodReference> afterMappingReferences
        ){ this( method,method.getParameters( ), existingVariableNames, beforeMappingReferences ,afterMappingReferences
    )

    ; }protectedMappingMethod (Method method,List< Parameter> parameters,Collection< String>
                            existingVariableNames,List< LifecycleCallbackMethodReference>
        beforeMappingReferences,List< LifecycleCallbackMethodReference> afterMappingReferences
        ){this . name=method.getName(
        );this . parameters=
        parameters;this . sourceParameters=Parameter. getSourceParameters (parameters
        );this . returnType=method.getReturnType(
        );this . targetParameter=method.getMappingTargetParameter(
        );this . accessibility=method.getAccessibility(
        );this . thrownTypes=method.getThrownTypes(
        );this . isStatic=method.isStatic(
        );this . resultName= initResultName (existingVariableNames
        );this . beforeMappingReferencesWithMappingTarget= filterMappingTarget( beforeMappingReferences ,true
        );this . beforeMappingReferencesWithoutMappingTarget= filterMappingTarget( beforeMappingReferences ,false
        );this . afterMappingReferences=
    afterMappingReferences

    ; }protectedMappingMethod (Method method,List< Parameter> parameters
        ){ this( method, parameters ,newArrayList< >(method.getParameterNames () ), null ,null
    )

    ; }protectedMappingMethod (Method method
        ){ this( method ,newArrayList< >(method.getParameterNames () ), null ,null
    )

    ; }protectedMappingMethod (Method method,List< LifecycleCallbackMethodReference>
                            beforeMappingReferences,List< LifecycleCallbackMethodReference> afterMappingReferences
        ){ this( method ,newArrayList< >(method.getParameterNames () ),
            beforeMappingReferences ,afterMappingReferences
    )

    ; } privateStringinitResultName(Collection< String> existingVarNames
        ) { if ( targetParameter != null
            ) {returntargetParameter.getName(
        )
        ; } else if(getResultType().isArrayType ( )
            ) { String name= getSafeVariableName(getResultType().getComponentType().getName ( )+ "Tmp" ,existingVarNames
            );existingVarNames. add (name
            ) ;return
        name
        ; }
            else { String name= getSafeVariableName(getResultType().getName( ) ,existingVarNames
            );existingVarNames. add (name
            ) ;return
        name
    ;

    } } publicStringgetName (
        ) {return
    name

    ; }publicList< Parameter>getParameters (
        ) {return
    parameters

    ; }publicList< Parameter>getSourceParameters (
        ) {return
    sourceParameters

    ; } publicTypegetResultType (
        ) { return targetParameter != null?targetParameter.getType ( ):
    returnType

    ; } publicStringgetResultName (
        ) {return
    resultName

    ; } publicTypegetReturnType (
        ) {return
    returnType

    ; } publicAccessibilitygetAccessibility (
        ) {return
    accessibility

    ; } publicbooleanisExistingInstanceMapping (
        ) { return targetParameter!=
    null

    ; } publicbooleanisStatic (
        ) {return
    isStatic

    ;}
    @ OverridepublicSet< Type>getImportTypes (
        ){Set< Type > types =newHashSet<>(

        ) ; for ( Parameter param : parameters
            ){types. addAll(param.getType().getImportTypes ()
        )

        ;}types. addAll(getReturnType().getImportTypes ()

        ) ; for ( Type type : thrownTypes
            ){types. addAll(type.getImportTypes ()
        )

        ; }return
    types

    ; }protectedList< String>getParameterNames (
        ){List< String > parameterNames =newArrayList< >(parameters.size ()

        ) ; for ( Parameter parameter : parameters
            ){parameterNames. add(parameter.getName ()
        )

        ; }return
    parameterNames

    ; }publicList< Type>getThrownTypes (
        ) {return
    thrownTypes

    ;}
    @ Override publicStringtoString (
        ) { return returnType + " "+getName ( ) + "("+ join( parameters , ", " )+
    ")"

    ; }privateList< LifecycleCallbackMethodReference>filterMappingTarget(List< LifecycleCallbackMethodReference>
                                                                       methods ,boolean mustHaveMappingTargetParameter
        ) { if ( methods == null
            ) {return
        null

        ;}List< LifecycleCallbackMethodReference >
            result =newArrayList< >(methods.size ()

        ) ; for ( LifecycleCallbackMethodReference method : methods
            ) { if ( mustHaveMappingTargetParameter==method.hasMappingTargetParameter ( )
                ){result. add (method
            )
        ;

        } }return
    result

    ; }publicList< LifecycleCallbackMethodReference>getAfterMappingReferences (
        ) {return
    afterMappingReferences

    ; }publicList< LifecycleCallbackMethodReference>getBeforeMappingReferencesWithMappingTarget (
        ) {return
    beforeMappingReferencesWithMappingTarget

    ; }publicList< LifecycleCallbackMethodReference>getBeforeMappingReferencesWithoutMappingTarget (
        ) {return
    beforeMappingReferencesWithoutMappingTarget

    ;}
    @ Override publicinthashCode (
        ) { int hash=
        7 ; hash = 83 * hash+(this . parameters != null?this.parameters.hashCode ( ):0
        ) ; hash = 83 * hash+(this . returnType != null?this.returnType.hashCode ( ):0
        ) ;return
    hash

    ;}
    @ Override publicbooleanequals (Object obj
        ) { if ( this == obj
            ) {return
        true
        ; } if ( obj == null
            ) {return
        false
        ; } if(getClass ( )!=obj.getClass ( )
            ) {return
        false
        ;
        }
        //Do not add name to the equals check.
        //Reason: Whenever we forge methods we can reuse mappings if they are the same. However, if we take the name // into consideration, they'll never be the same, because we create safe methods names. final MappingMethod other=( MappingMethod)
        obj ; if(this . parameters!=other .
            parameters&&(this . parameters == null||!this.parameters. equals(other .parameters ) )
            ) {return
        false
        ; } if(this . returnType!=other .
            returnType&&(this . returnType == null||!this.returnType. equals(other .returnType ) )
            ) {return
        false
        ; }return
    true

;
