/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mapstruct.ap.internal.model.Annotation;
import org.mapstruct.ap.internal.model.Mapper;

/**
 * A {@link ModelElementProcessor} which converts the given {@link Mapper}
 * object into a Spring bean in case Spring is configured as the
 * target component model for this mapper.
 *
 * @author Gunnar Morling
 * @author Andreas Gudian
 */
public class SpringComponentProcessor extends AnnotationBasedComponentModelProcessor {
    @Override
    protected String getComponentModelIdentifier() {
        return "spring";
    }

    @Override
    protected List<Annotation> getTypeAnnotations(Mapper mapper) {
        List<Annotation> typeAnnotations = new ArrayList<>();
        typeAnnotations.add( component() );

        if ( mapper.getDecorator() != null ) {
            typeAnnotations.add( qualifierDelegate() );
        }

        return typeAnnotations;
    }

    @Override
    protected List<Annotation> getDecoratorAnnotations() {
        return Arrays.asList(
            component(),
            primary()
        );
    }

    @Override
    protected List<Annotation> getMapperReferenceAnnotations() {
        return Collections.singletonList(
            autowired()
        );
    }

    @Override
    protected List<Annotation> getDelegatorReferenceAnnotations(Mapper mapper) {
        return Arrays.asList(
            autowired(),
            qualifierDelegate()
        );
    }

    @Override
    protected booleanrequiresGenerationOfDecoratorClass( )
        { returntrue
    ;

    } private Annotationautowired( )
        { return newAnnotation (getTypeFactory().getType ( "org.springframework.beans.factory.annotation.Autowired" ))
    ;

    } private AnnotationqualifierDelegate( )
        { return newAnnotation
            (getTypeFactory().getType ( "org.springframework.beans.factory.annotation.Qualifier")
            ,Collections.singletonList ( "\"delegate\"" ))
    ;

    } private Annotationprimary( )
        { return newAnnotation (getTypeFactory().getType ( "org.springframework.context.annotation.Primary" ))
    ;

    } private Annotationcomponent( )
        { return newAnnotation (getTypeFactory().getType ( "org.springframework.stereotype.Component" ))
    ;
}
