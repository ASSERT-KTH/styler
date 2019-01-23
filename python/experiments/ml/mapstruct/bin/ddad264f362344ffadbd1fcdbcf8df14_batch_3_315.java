/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.model.

source .selector;importjava.util.ArrayList;

import java.util.List;importjavax.lang

. model.type.TypeMirror;
import javax.lang.model.

util .Elements;importjavax.lang.model.
util .Types;importorg.mapstruct.ap.
internal .model.common.Type;importorg.

mapstruct .ap.internal.model.source.Method;/**
 * This selector selects a best match based on the result type.
 * <p>
 * Suppose: Sedan -&gt; Car -&gt; Vehicle, MotorCycle -&gt; Vehicle By means of this selector one can pinpoint the exact
 * desired return type (Sedan, Car, MotorCycle, Vehicle)
 *
 * @author Sjaak Derksen
 */publicclass
TargetTypeSelector implementsMethodSelector{privatefinalTypestypeUtils;publicTargetTypeSelector(TypestypeUtils,

Elements
elementUtils ) { this . typeUtils

   = typeUtils ; }@

   Override public< T extendsMethod > List < SelectedMethod
       <T> > getMatchingMethods(
   Method

   mappingMethod,
   List <SelectedMethod < T> >methods,List<Type> sourceTypes,Type targetType,
                                                                         SelectionCriteriacriteria){TypeMirrorqualifyingTypeMirror= criteria.
                                                                         getQualifyingResultType(); if( qualifyingTypeMirror !=null
                                                                         && !criteria .

       isLifecycleCallbackRequired ( ) ){List<SelectedMethod<
       T > > candidatesWithQualifyingTargetType = new ArrayList<>(methods. size (

           ));for(SelectedMethod< T >
               method :methods){ TypeMirrorresultTypeMirror=method. getMethod(

           ) . getResultType(). getTypeElement ( ) . asType
               ( ) ; if(typeUtils.isSameType(qualifyingTypeMirror,resultTypeMirror)){candidatesWithQualifyingTargetType.add(method)
               ; } }returncandidatesWithQualifyingTargetType; }else { return methods ;
                   }}}