/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
packageorg.mapstruct.ap.internal.processor

; importjava.util.ArrayList
; importjava.util.Arrays
; importjava.util.Collections
; importjava.util.List

; importorg.mapstruct.ap.internal.model.Annotation
; importorg.mapstruct.ap.internal.model.Mapper

;
/**
 * A {@link ModelElementProcessor} which converts the given {@link Mapper}
 * object into a Spring bean in case Spring is configured as the
 * target component model for this mapper.
 *
 * @author Gunnar Morling
 * @author Andreas Gudian
 */ public class SpringComponentProcessor extends AnnotationBasedComponentModelProcessor
    {@
    Override protected StringgetComponentModelIdentifier( )
        { return"spring"
    ;

    }@
    Override protectedList<Annotation >getTypeAnnotations( Mappermapper )
        {List<Annotation > typeAnnotations = newArrayList<>()
        ;typeAnnotations.add (component( ))

        ; if (mapper.getDecorator( ) != null )
            {typeAnnotations.add (qualifierDelegate( ))
        ;

        } returntypeAnnotations
    ;

    }@
    Override protectedList<Annotation >getDecoratorAnnotations( )
        { returnArrays.asList
            (component()
            ,primary(
        ))
    ;

    }@
    Override protectedList<Annotation >getMapperReferenceAnnotations( )
        { returnCollections.singletonList
            (autowired(
        ))
    ;

    }@
    Override protectedList<Annotation >getDelegatorReferenceAnnotations( Mappermapper )
        { returnArrays.asList
            (autowired()
            ,qualifierDelegate(
        ))
    ;

    }@
    Override protected booleanrequiresGenerationOfDecoratorClass( )
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
