/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap .internal.processor;import
java .util.ArrayList;import
java .util.Arrays;import
java .util.Collections;import

java .util.List;importorg.mapstruct.ap.
internal .model.Annotation;importorg.mapstruct.ap.

internal
. model . Mapper ; /**
 * A {@link ModelElementProcessor} which converts the given {@link Mapper}
 * object into a Spring bean in case Spring is configured as the
 * target component model for this mapper.
 *
 * @author Gunnar Morling
 * @author Andreas Gudian
 */
    publicclass
    SpringComponentProcessor extends AnnotationBasedComponentModelProcessor{@ Override
        protected StringgetComponentModelIdentifier
    (

    ){
    return "spring";}@ OverrideprotectedList <Annotation >
        getTypeAnnotations(Mappermapper ) { List <Annotation>typeAnnotations=new
        ArrayList<>( );typeAnnotations .add

        ( component ());if ( mapper . getDecorator
            ()!=null ){typeAnnotations .add
        (

        qualifierDelegate ()
    )

    ;}
    return typeAnnotations;}@ OverrideprotectedList <
        Annotation >getDecoratorAnnotations()
            {returnArrays.
            asList(component
        ()
    ,

    primary(
    ) );}@ OverrideprotectedList <
        Annotation >getMapperReferenceAnnotations()
            {returnCollections
        .singletonList
    (

    autowired(
    ) );}@ OverrideprotectedList <Annotation >
        getDelegatorReferenceAnnotations (Mappermapper)
            {returnArrays.
            asList(autowired
        ()
    ,

    qualifierDelegate(
    ) ) ;}@ Override
        protected booleanrequiresGenerationOfDecoratorClass
    (

    ) { returntrue; }
        private Annotation autowired( ){returnnewAnnotation( getTypeFactory ( ).
    getType

    ( "org.springframework.beans.factory.annotation.Autowired" )); }
        private Annotation qualifierDelegate(
            ){returnnewAnnotation( getTypeFactory ()
            .getType("org.springframework.beans.factory.annotation.Qualifier" ) , Collections.
    singletonList

    ( "\"delegate\"" )); }
        private Annotation primary( ){returnnewAnnotation( getTypeFactory ( ).
    getType

    ( "org.springframework.context.annotation.Primary" )); }
        private Annotation component( ){returnnewAnnotation( getTypeFactory ( ).
    getType
(
