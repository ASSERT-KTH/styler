/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jdbi.v3.lib.internal.com_google_guava.guava.v21_0;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
importjava.lang.reflect.Type
; importjava.lang.reflect.TypeVariable
; importjava.lang.reflect.WildcardType
; importjava.util.Arrays;import
java .util.HashMap;import
java .util.LinkedHashMap;import
java .util.Map;import
java .util.Objects;import
java .util.concurrent.atomic
. AtomicInteger;importjava.util.stream.Collectors
; importjava.util.stream.Stream
; importjavax.annotation.Nullable;import
static java.util.Collections.

emptyMap ; importstaticjava.util.Collections.
unmodifiableMap ; importstaticorg.jdbi.v3.
lib . internal.com_google_guava.guava.v21_0.Preconditions.checkArgument;importstaticorg.jdbi.v3.
lib . internal.com_google_guava.guava.v21_0.Preconditions.checkNotNull;importstaticorg.jdbi.v3.
lib . internal.com_google_guava.guava.v21_0.Preconditions.checkState;publicfinalclassTypeResolver{privatefinalTypeTable

typeTable ; public TypeResolver (

    ) { this .typeTable

    = newTypeTable( )
        ;}private TypeResolver ( TypeTabletypeTable){
    this

    . typeTable=typeTable ;} static
        TypeResolveraccordingTo( Type type)
    {

    return new TypeResolver() .where (
        TypeMappingIntrospector . getTypeMappings(type));}publicTypeResolverwhere(Typeformal,
    Type

    actual ) {Map< TypeVariableKey, Type >mappings =
        newHashMap<> () ; populateTypeMappings ( mappings,checkNotNull(formal,
        "formal"),checkNotNull (actual,"actual" )); returnwhere(mappings );}TypeResolver
        where (Map<TypeVariableKey,
    ?

    extends Type>mappings){return new TypeResolver (typeTable .where (
        mappings ) );}privatestaticvoidpopulateTypeMappings(finalMap
    <

    TypeVariableKey , Type >mappings, Typefrom,final Typeto ){ if (from . equals (to )
        ) {return;}newTypeVisitor() {
            @Override
        void
        visitTypeVariable (TypeVariable< ?
            >typeVariable
            ) {mappings.put(new TypeVariableKey( typeVariable
                ),to); }@OverridevoidvisitWildcardType (WildcardTypefromWildcardType
            )

            {if
            ( !(to instanceofWildcardType )
                ) {return;// okay to say <?> is anything } WildcardTypetoWildcardType= (
                    WildcardType) to
                ;
                Type [ ] fromUpperBounds=fromWildcardType .getUpperBounds
                (); Type [ ]toUpperBounds=toWildcardType.getUpperBounds
                (); Type [ ]fromLowerBounds=fromWildcardType.getLowerBounds
                (); Type [ ]toLowerBounds=toWildcardType.getLowerBounds
                (); checkArgument ( fromUpperBounds.length==toUpperBounds.
                length&&
                    fromLowerBounds.length == toLowerBounds.length
                        , "Incompatible type: %s vs. %s",fromWildcardType , to);for
                    (int
                    i=
                    0;i
                < fromUpperBounds. length ; i++ ) { populateTypeMappings(mappings, fromUpperBounds[i ]
                    ,toUpperBounds[i ]);}for (inti=0;
                i
                < fromLowerBounds. length ; i++ ) { populateTypeMappings(mappings, fromLowerBounds[i ]
                    ,toLowerBounds[i ]);}} @OverridevoidvisitParameterizedType(ParameterizedType
                fromParameterizedType
            )

            {if
            ( toinstanceofWildcardType ){ return
                ; // Okay to say Foo<A> is <?>} ParameterizedType toParameterizedType= expectArgument
                    (ParameterizedType .
                class
                , to ) ;if(fromParameterizedType.getOwnerType ()!=
                null &&toParameterizedType.getOwnerType() != null
                    ) {populateTypeMappings(mappings, fromParameterizedType .getOwnerType (
                    ),
                        toParameterizedType. getOwnerType());} checkArgument(fromParameterizedType.getRawType()
                .
                equals(
                    toParameterizedType.getRawType()),"Inconsistent raw type: %s vs. %s",fromParameterizedType,to);Type
                    []
                    fromArgs=
                    fromParameterizedType.getActualTypeArguments
                (); Type [ ]toArgs=toParameterizedType.getActualTypeArguments
                (); checkArgument ( fromArgs.length==toArgs.
                length,
                    "%s not compatible with %s",fromParameterizedType , toParameterizedType);for
                    (int
                    i=
                    0;i
                < fromArgs. length ; i++ ) { populateTypeMappings(mappings, fromArgs[i ]
                    ,toArgs[i ]);}} @OverridevoidvisitGenericArrayType(GenericArrayType
                fromArrayType
            )

            {if
            ( toinstanceofWildcardType ){ return
                ; // Okay to say A[] is <?>} Type componentType= Types
                    .getComponentType (
                to
                ) ; checkArgument (componentType!=null,"%s is not an array type.",
                to); populateTypeMappings (mappings ,fromArrayType .getGenericComponentType(
                ),componentType) ;}@OverridevoidvisitClass (Class<
            ?

            >fromClass
            ) {if(toinstanceofWildcardType ){ return
                ; // Okay to say Foo is <?>} // Can't map from a raw class to anything other than itself or a wildcard. // You can't say "assuming String is Integer".// And we don't support "assuming String is T"; user has to say "assuming T is String". throw
                    newIllegalArgumentException (
                "No type mapping from "
                +
                fromClass
                +
                " to " + to); } } . visit ( from);
            }
        publicTyperesolveType(Typetype)
    {

    checkNotNull ( type,"type" ); if
        (typeinstanceofTypeVariable ){return
        typeTable .resolve ( (TypeVariable <
            ? >)type);}elseif(type instanceofParameterizedType)
        { return resolveParameterizedType (( ParameterizedType )type )
            ; }elseif(type instanceofGenericArrayType)
        { return resolveGenericArrayType (( GenericArrayType )type )
            ; }elseif(type instanceofWildcardType)
        { return resolveWildcardType (( WildcardType )type )
            ; }else{// if Class<?>, no resolution needed, we are done.return type;}
        } private Type
            [
            ] resolveTypes(
        Type
    [

    ] types){ Type[]result= newType [
        types.length ] ; for (inti=0;i
        < types. length ; i++ ) { result[i] =resolveType( types
            [i]) ; }returnresult;}privateWildcardTyperesolveWildcardType
        (
        WildcardType type)
    {

    Type [ ]lowerBounds= type. getLowerBounds
        (); Type [ ]upperBounds=type.getUpperBounds
        (); return new Types.WildcardTypeImpl(resolveTypes(
        lowerBounds ) ,resolveTypes(upperBounds));}private TyperesolveGenericArrayType(GenericArrayTypetype)
    {

    Type componentType =type. getGenericComponentType( )
        ; Type resolvedComponentType =resolveType(componentType);
        return Types . newArrayType(resolvedComponentType);
        } privateParameterizedTyperesolveParameterizedType(ParameterizedTypetype)
    {

    Type owner =type. getOwnerType( )
        ; Type resolvedOwner =(owner==null)
        ? null : resolveType( owner ); Type resolvedRawType = resolveType(type.getRawType
        ( ) ) ;Type[]args=type.getActualTypeArguments

        (); Type [ ]resolvedArgs=resolveTypes(args
        );return Types . newParameterizedTypeWithOwner(resolvedOwner,(
        Class <?>)
            resolvedRawType, resolvedArgs);}privatestatic <T >TexpectArgument
    (

    Class < T>type , Objectarg){try{ returntype . cast( arg
        ) ;
            } catch(ClassCastExceptione){throw
        new IllegalArgumentException (arg +" is not a " +
            type . getSimpleName() , e ) ;}}/**
     * A TypeTable maintains mapping from {@link TypeVariable} to types.
     */privatestatic classTypeTable{
        private
    final

    Map
    < TypeVariableKey , Type >
        map ; TypeTable(){ this. map=

        emptyMap() ;
            }privateTypeTable ( Map<TypeVariableKey,
        Type

        > map){this.map =unmodifiableMap (new LinkedHashMap
            <>( map )); }/**
         * Returns a new {@code TypeResolver} with {@code variable} mapping to {@code type}.
         */finalTypeTablewhere(Map<
        TypeVariableKey

        ,
        ? extends Type>mappings){Map < TypeVariableKey ,Type >builder =
            newLinkedHashMap<> () ; builder . putAll(map);for
            (Map.Entry<TypeVariableKey,
            ? extendsType>mapping:mappings. entrySet ( )) { TypeVariableKey variable=mapping.getKey( )
                ; Type type =mapping.getValue()
                ; checkArgument ( !variable.equalsType(type
                ),"Type variable %s bound to itself",variable);builder.put (variable ,type)
                ;}returnnewTypeTable( builder);
            }
            final Type resolve(finalTypeVariable<
        ?

        > var ){final TypeTableunguarded=this ;TypeTable guarded
            = new TypeTable ( ){
            @ Override public
                Type resolveInternal(TypeVariable <
                    ?>
                    intermediateVar , TypeTableforDependent){if( intermediateVar. getGenericDeclaration () .
                        equals (var.getGenericDeclaration())){returnintermediateVar;}returnunguarded. resolveInternal
                            ( intermediateVar,
                        forDependent
                        ) ;}};returnresolveInternal (var,
                    guarded
                );
            } TyperesolveInternal(TypeVariable <?>
        var

        , TypeTableforDependants){Typetype =map . get( new
            TypeVariableKey ( var ));if( type==null){Type
            [ ]bounds = var. getBounds
                (); if ( bounds.length==0)
                { returnvar;} Type [] resolvedBounds
                    = newTypeResolver
                (
                forDependants). resolveTypes ( bounds );if(Types.NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY&&
                Arrays .equals(bounds,resolvedBounds
                    ) ){returnvar;} returnTypes. newArtificialTypeVariable
                    ( var.
                getGenericDeclaration
                ( ),var.
                    getName(),resolvedBounds) ;}// in case the type is yet another type variable.returnnewTypeResolver (forDependants)
            .
            resolveType
            ( type );}}privatestaticfinalclassTypeMappingIntrospectorextends
        TypeVisitor
    {

    private static final WildcardCapturer WILDCARD_CAPTURER = new WildcardCapturer

        ( ) ; private final Map < TypeVariableKey,Type>

        mappings = newHashMap<> () ; static Map <TypeVariableKey,Type>getTypeMappings

        ( TypecontextType){ TypeMappingIntrospectorintrospector =newTypeMappingIntrospector () ;
            introspector . visit ( WILDCARD_CAPTURER.capture(
            contextType));returnunmodifiableMap(newLinkedHashMap<>(
            introspector .mappings) );}@OverridevoidvisitClass(Class<
        ?

        >clazz
        ) {visit(clazz.getGenericSuperclass () )
            ;visit(clazz.getGenericInterfaces())
            ;}@OverridevoidvisitParameterizedType(ParameterizedTypeparameterizedType
        )

        {Class
        < ?>rawClass =( Class
            <?>) parameterizedType . getRawType();TypeVariable< ?>[]vars=
            rawClass.getTypeParameters(); Type [ ]typeArgs=parameterizedType.getActualTypeArguments
            (); checkState ( vars.length==typeArgs.
            length,"Expected %s type variables, but got %s",typeArgs . length,vars.
                length) ;for(int i=0;i
            < vars. length ; i++ ) { map(newTypeVariableKey (vars[ i
                ]), typeArgs[i]);}visit (rawClass);visit(
            parameterizedType
            .getOwnerType())
            ;}@OverridevoidvisitTypeVariable(TypeVariable<
        ?

        >t
        ) {visit(t.getBounds () )
            ;}@OverridevoidvisitWildcardType(WildcardTypet
        )

        {visit
        ( t.getUpperBounds () )
            ;}privatevoidmap(finalTypeVariableKeyvar
        ,

        final Type arg){ if (mappings . containsKey (var )
            ) {return;}for(Typet =
                arg;
            t
            != null; t = mappings. get ( TypeVariableKey. forLookup ( t))){if(var.equalsType(t )
                ) {Typex=arg;while( x
                    != null ) {x
                    = mappings. remove (TypeVariableKey .
                        forLookup ( x));}return;}}mappings.put
                    (
                    var,
                arg
            )
            ;}}privatestaticfinal classWildcardCapturer{
        private
    final

    AtomicInteger id = new AtomicInteger (

        ) ; Type capture ( Type type){checkNotNull

        ( type,"type" ); if
            (typeinstanceofClass ){return
            type ;} if (type instanceof
                TypeVariable ){
            return
            type ;} if (type instanceof
                GenericArrayType ){
            GenericArrayType
            arrayType =( GenericArrayType )type ;
                return Types . newArrayType(capture (arrayType
                . getGenericComponentType()));}if(typeinstanceofParameterizedType){
            ParameterizedType
            parameterizedType =( ParameterizedType )type ;
                return Types . newParameterizedTypeWithOwner(captureNullable (parameterizedType
                . getOwnerType())
                    ,(Class<?>)parameterizedType.
                    getRawType(),capture( parameterizedType.getActualTypeArguments())
                    );}if(typeinstanceofWildcardType){
            WildcardType
            wildcardType =( WildcardType )type ;
                Type [ ] lowerBounds=wildcardType .getLowerBounds
                (); if ( lowerBounds.length==0)
                { // ? extends something changes to capture-ofType[] upperBounds =wildcardType . getUpperBounds
                    (); String name ="capture#"+id.incrementAndGet
                    ( ) +
                        "-of ? extends "
                            + Stream.of(upperBounds
                            ) .
                            map (Object::toString).collect(Collectors.joining("&"));returnTypes.newArtificialTypeVariable(WildcardCapturer.class
                    , name,wildcardType.
                        getUpperBounds()) ;} else{returntype;}}
                throw new AssertionError
                    ( "must have been one of the known types")
                ;
            }
            private Type captureNullable(@NullableType
        type

        ) { if(type== null ){ return
            null ;} return capture( type
                ) ;}
            private
            Type []capture(Type
        [

        ] types){ Type[]result= newType [
            types.length ] ; for (inti=0;i
            < types. length ; i++ ) { result[i] =capture( types
                [i]) ; }returnresult;}}staticfinal
            class
            TypeVariableKey {private
        final
    TypeVariable

    < ? > var ;
        TypeVariableKey ( TypeVariable<?> var)

        {this.var=checkNotNull (var ,
            "var"); } @Overridepublicint hashCode()
        {

        returnObjects
        . hash (var. getGenericDeclaration
            ( ),var.getName());} @Overridepublicbooleanequals(Object
        obj

        ){
        if ( objinstanceofTypeVariableKey ){ TypeVariableKey
            that =( TypeVariableKey )obj ;
                return equalsTypeVariable ( that.var );
                } else{returnfalse;}}
            @ Override public
                String toString(
            )
        {

        returnvar
        . toString (); }
            static TypeVariableKeyforLookup(Typet)
        {

        if ( tinstanceofTypeVariable ){ return
            new TypeVariableKey( ( TypeVariable< ?
                > ) t);}else{returnnull ;}}
            boolean equalsType (
                Type type)
            {
        if

        ( typeinstanceofTypeVariable ){ return
            equalsTypeVariable (( TypeVariable <? >
                ) type);}else{returnfalse ;}}
            private boolean equalsTypeVariable
                ( TypeVariable<
            ?
        >

        that ) {returnvar.getGenericDeclaration( ). equals
            ( that.getGenericDeclaration())&&var.getName().equals
                ( that.getName());}}}