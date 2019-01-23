/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.processor;

import

java .util.ArrayList;import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mapstruct.ap.

internal .model.Annotation;importorg.mapstruct.ap.
internal .model.Mapper;/**
 * A {@link ModelElementProcessor} which converts the given {@link Mapper}
 * object into a Spring bean in case Spring is configured as the
 * target component model for this mapper.
 *
 * @author Gunnar Morling
 * @author Andreas Gudian
 */publicclassSpringComponentProcessorextendsAnnotationBasedComponentModelProcessor{

@
Override protected String getComponentModelIdentifier ( )
    {return
    "spring" ; }@Override protected
        List <Annotation
    >

    getTypeAnnotations(
    Mapper mapper){List <Annotation> typeAnnotations= new
        ArrayList<>( ) ; typeAnnotations .add(component()
        );if( mapper.getDecorator ()

        != null ){typeAnnotations.add ( qualifierDelegate ( )
            );}return typeAnnotations;} @Override
        protected

        List <Annotation
    >

    getDecoratorAnnotations(
    ) {returnArrays. asList(component (
        ) ,primary()
            );}@
            OverrideprotectedList
        <Annotation
    >

    getMapperReferenceAnnotations(
    ) {returnCollections. singletonList(autowired (
        ) );}@
            OverrideprotectedList
        <Annotation
    >

    getDelegatorReferenceAnnotations(
    Mapper mapper){return Arrays.asList (autowired (
        ) ,qualifierDelegate()
            );}@
            Overrideprotectedboolean
        requiresGenerationOfDecoratorClass(
    )

    {return
    true ; }privateAnnotation autowired
        ( ){
    return

    new Annotation (getTypeFactory( )
        . getType ("org.springframework.beans.factory.annotation.Autowired" ));}privateAnnotation qualifierDelegate ( ){
    return

    new Annotation (getTypeFactory( )
        . getType ("org.springframework.beans.factory.annotation.Qualifier"
            ),Collections.singletonList( "\"delegate\"" ))
            ;}privateAnnotation primary ( ){
    return

    new Annotation (getTypeFactory( )
        . getType ("org.springframework.context.annotation.Primary" ));}privateAnnotation component ( ){
    return

    new Annotation (getTypeFactory( )
        . getType ("org.springframework.stereotype.Component" ));}}