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

import java.lang.reflect.GenericArrayType;importjava.lang
. reflect.ParameterizedType;importjava.lang
. reflect.Type;importjava.lang
. reflect.TypeVariable;importjava.lang
. reflect.WildcardType;importjava
. util.Arrays;importjava
. util.HashMap;importjava
. util.LinkedHashMap;importjava
. util.Map;importjava
. util.Objects;importjava.util.concurrent
. atomic.AtomicInteger;importjava.util
. stream.Collectors;importjava.util
. stream.Stream;importjavax

. annotation .Nullable;importstaticjava.util
. Collections .emptyMap;importstaticjava.util
. Collections .unmodifiableMap;importstaticorg.jdbi.v3.lib.internal.com_google_guava.guava.v21_0
. Preconditions .checkArgument;importstaticorg.jdbi.v3.lib.internal.com_google_guava.guava.v21_0
. Preconditions .checkNotNull;importstaticorg.jdbi.v3.lib.internal.com_google_guava.guava.v21_0

. Preconditions . checkState ;

    public final class TypeResolver{

    private finalTypeTabletypeTable ;
        publicTypeResolver( ) { this.typeTable=
    new

    TypeTable (); }private TypeResolver
        (TypeTabletypeTable ) {this
    .

    typeTable = typeTable;} staticTypeResolver accordingTo
        ( Type type){returnnewTypeResolver().where(TypeMappingIntrospector.getTypeMappings
    (

    type ) );} publicTypeResolver where (Type formal
        ,Typeactual) {Map < TypeVariableKey , Type>mappings=newHashMap
        <>() ;populateTypeMappings(mappings ,checkNotNull( formal,"formal") ,checkNotNull(actual
        , "actual"));return
    where

    ( mappings);}TypeResolverwhere ( Map <TypeVariableKey ,? extends
        Type > mappings){returnnewTypeResolver(typeTable.where
    (

    mappings ) ) ;}private staticvoidpopulateTypeMappings( finalMap <TypeVariableKey , Type> mappings , Typefrom ,
        final Typeto){if(from. equals
            (to
        )
        ) {return; }
            newTypeVisitor
            ( ){@OverridevoidvisitTypeVariable (TypeVariable <
                ?>typeVariable){ mappings.put(new TypeVariableKey(typeVariable
            )

            ,to
            ) ;}@ Overridevoid visitWildcardType
                ( WildcardTypefromWildcardType){ if (!( to
                    instanceofWildcardType )
                )
                { return ; // okay to say <?> is anything}WildcardType toWildcardType=
                (WildcardType) to ; Type[]fromUpperBounds=fromWildcardType
                .getUpperBounds( ) ; Type[]toUpperBounds=toWildcardType
                .getUpperBounds( ) ; Type[]fromLowerBounds=fromWildcardType
                .getLowerBounds( ) ; Type[]toLowerBounds=toWildcardType
                .getLowerBounds
                    (); checkArgument (fromUpperBounds.
                        length ==toUpperBounds. length &&fromLowerBounds.length
                    ==toLowerBounds
                    .length
                    ,"Incompatible type: %s vs. %s",
                fromWildcardType ,to ) ; for( int i =0;i <fromUpperBounds. length
                    ;i++) {populateTypeMappings(mappings, fromUpperBounds[i],toUpperBounds
                [
                i ]) ; } for( int i =0;i <fromLowerBounds. length
                    ;i++) {populateTypeMappings(mappings, fromLowerBounds[i],toLowerBounds
                [
            i

            ])
            ; }}@ Overridevoid visitParameterizedType
                ( ParameterizedTypefromParameterizedType ) {if (
                    toinstanceof WildcardType
                )
                { return ; // Okay to say Foo<A> is <?>}ParameterizedTypetoParameterizedType=expectArgument (ParameterizedType.
                class ,to);if( fromParameterizedType .
                    getOwnerType ()!=null&& toParameterizedType .getOwnerType (
                    )!=
                        null) {populateTypeMappings(mappings,fromParameterizedType .getOwnerType(),toParameterizedType.
                getOwnerType
                ()
                    );}checkArgument(fromParameterizedType.getRawType().equals(toParameterizedType.
                    getRawType(
                    ))
                    ,"Inconsistent raw type: %s vs. %s",
                fromParameterizedType,to ) ; Type[]fromArgs=fromParameterizedType
                .getActualTypeArguments( ) ; Type[]toArgs=toParameterizedType
                .getActualTypeArguments
                    (); checkArgument (fromArgs.length
                    ==toArgs
                    .length
                    ,"%s not compatible with %s",
                fromParameterizedType ,toParameterizedType ) ; for( int i =0;i <fromArgs. length
                    ;i++) {populateTypeMappings(mappings, fromArgs[i],toArgs
                [
            i

            ])
            ; }}@ Overridevoid visitGenericArrayType
                ( GenericArrayTypefromArrayType ) {if (
                    toinstanceof WildcardType
                )
                { return ; // Okay to say A[] is <?>}TypecomponentType=Types.
                getComponentType(to ) ;checkArgument (componentType !=null,
                "%s is not an array type.",to) ;populateTypeMappings(mappings,fromArrayType .getGenericComponentType(
            )

            ,componentType
            ) ;}@OverridevoidvisitClass (Class <
                ? >fromClass ) {if (
                    toinstanceof WildcardType
                )
                {
                return
                ;
                // Okay to say Foo is <?> } // Can't map from a raw class to anything other than itself or a wildcard.// You can't say "assuming String is Integer".// And we don't support "assuming String is T"; user has to say "assuming T is String". throw new IllegalArgumentException ( "No type mapping from " +fromClass+
            " to "
        +to);}}.
    visit

    ( from );} publicType resolveType
        (Typetype) {checkNotNull(
        type ,"type" ) ;if (
            type instanceofTypeVariable){returntypeTable.resolve(( TypeVariable<?
        > ) type ); } elseif (
            type instanceofParameterizedType){return resolveParameterizedType((
        ParameterizedType ) type ); } elseif (
            type instanceofGenericArrayType){return resolveGenericArrayType((
        GenericArrayType ) type ); } elseif (
            type instanceofWildcardType){return resolveWildcardType((
        WildcardType ) type
            )
            ; }else
        {
    // if Class<?>, no resolution needed, we are done.

    return type;} }privateType[] resolveTypes( Type
        []types ) { Type []result=newType[
        types .length ] ; for( int i =0;i <types. length
            ;i++) { result[i]=resolveType(types
        [
        i ])
    ;

    } return result;} privateWildcardType resolveWildcardType
        (WildcardTypetype ) { Type[]lowerBounds=type
        .getLowerBounds( ) ; Type[]upperBounds=type
        . getUpperBounds ();returnnewTypes.WildcardTypeImpl( resolveTypes(lowerBounds),resolveTypes
    (

    upperBounds ) );} privateType resolveGenericArrayType
        ( GenericArrayType type ){TypecomponentType=type
        . getGenericComponentType ( );TyperesolvedComponentType=
        resolveType (componentType);returnTypes.
    newArrayType

    ( resolvedComponentType );} privateParameterizedType resolveParameterizedType
        ( ParameterizedType type ){Typeowner=type
        . getOwnerType ( ); Type resolvedOwner= ( owner == null)?null:
        resolveType ( owner );TyperesolvedRawType=resolveType(type.

        getRawType() ) ; Type[]args=type
        .getActualTypeArguments( ) ; Type[]resolvedArgs=
        resolveTypes (args);
            returnTypes .newParameterizedTypeWithOwner(resolvedOwner,( Class< ?>)
    resolvedRawType

    , resolvedArgs );} private static<T>TexpectArgument (Class < T> type
        , Object
            arg ){try{returntype.
        cast ( arg) ;} catch
            ( ClassCastException e){ throw new IllegalArgumentException (arg+" is not a "+type .getSimpleName(
        )
    ,

    e
    ) ; } } /**
     * A TypeTable maintains mapping from {@link TypeVariable} to types.
     */
        private static classTypeTable{private finalMap <TypeVariableKey

        ,Type> map
            ;TypeTable( ) {this.map
        =

        emptyMap ();}privateTypeTable (Map <TypeVariableKey ,
            Type>map ) {this. map=unmodifiableMap(newLinkedHashMap<>
        (

        map
        ) ) ;}/**
         * Returns a new {@code TypeResolver} with {@code variable} mapping to {@code type}.
         */finalTypeTablewhere ( Map <TypeVariableKey ,? extends
            Type>mappings) {Map < TypeVariableKey , Type>builder=newLinkedHashMap
            <>();builder.
            putAll (map);for(Map . Entry <TypeVariableKey , ? extendsType>mapping:mappings .
                entrySet ( ) ){TypeVariableKeyvariable=mapping
                . getKey ( );Typetype=mapping
                .getValue();checkArgument(!variable. equalsType( type),
                "Type variable %s bound to itself",variable);builder .put(
            variable
            , type );}returnnew
        TypeTable

        ( builder );} finalTyperesolve( finalTypeVariable <
            ? > var ) {final
            TypeTable unguarded =
                this ;TypeTableguarded =
                    newTypeTable
                    ( ) {@OverridepublicTyperesolveInternal (TypeVariable < ?> intermediateVar
                        , TypeTableforDependent){if(intermediateVar.getGenericDeclaration().equals(var. getGenericDeclaration
                            ( ))
                        )
                        { returnintermediateVar;}returnunguarded .resolveInternal(
                    intermediateVar
                ,forDependent
            ) ;}}; returnresolveInternal(
        var

        , guarded);}TyperesolveInternal (TypeVariable < ?> var
            , TypeTable forDependants ){Typetype= map.get(newTypeVariableKey
            ( var) ) ;if (
                type==null ) { Type[]bounds=var
                . getBounds(); if (bounds .
                    length ==0
                )
                {returnvar ; } Type []resolvedBounds=newTypeResolver(forDependants).
                resolveTypes (bounds);if(
                    Types .NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY&&Arrays .equals( bounds
                    , resolvedBounds)
                )
                { returnvar;}
                    returnTypes.newArtificialTypeVariable(var .getGenericDeclaration(),var .getName(
            )
            ,
            resolvedBounds ) ;}// in case the type is yet another type variable.returnnewTypeResolver(forDependants).
        resolveType
    (

    type ) ; } } private static final

        class TypeMappingIntrospector extends TypeVisitor { private static finalWildcardCapturerWILDCARD_CAPTURER=

        new WildcardCapturer ();private finalMap < TypeVariableKey , Type>mappings=newHashMap

        < >(); staticMap <TypeVariableKey, Type> getTypeMappings
            ( Type contextType ) {TypeMappingIntrospectorintrospector=
            newTypeMappingIntrospector();introspector.visit(WILDCARD_CAPTURER.capture
            ( contextType)) ;returnunmodifiableMap(newLinkedHashMap<>(introspector
        .

        mappings)
        ) ;}@OverridevoidvisitClass (Class <
            ?>clazz){visit(clazz.
            getGenericSuperclass());visit(clazz.
        getGenericInterfaces

        ()
        ) ;}@ Overridevoid visitParameterizedType
            (ParameterizedTypeparameterizedType) { Class <?>rawClass=( Class<?>)parameterizedType
            .getRawType();TypeVariable < ? >[]vars=rawClass
            .getTypeParameters( ) ; Type[]typeArgs=parameterizedType
            .getActualTypeArguments(); checkState (vars.length
                ==typeArgs .length,"Expected %s type variables, but got %s" ,typeArgs.length,
            vars .length ) ; for( int i =0;i <vars. length
                ;i++ ){map(newTypeVariableKey(vars [i]),typeArgs
            [
            i]);}
            visit(rawClass);visit(parameterizedType.
        getOwnerType

        ()
        ) ;}@OverridevoidvisitTypeVariable (TypeVariable <
            ?>t){visit(t.
        getBounds

        ()
        ) ;}@ Overridevoid visitWildcardType
            (WildcardTypet){visit(t.
        getUpperBounds

        ( ) );} private voidmap ( final TypeVariableKeyvar ,
            final Typearg){if(mappings. containsKey
                (var
            )
            ) {return ; } for( Type t =arg ; t !=null;t=mappings.get(TypeVariableKey.forLookup (
                t ))){if(var. equalsType
                    ( t ) ){
                    Type x= arg ;while (
                        x != null){x=mappings.remove(TypeVariableKey.forLookup
                    (
                    x)
                )
            ;
            }return;}}mappings .put(
        var
    ,

    arg ) ; } } private

        static final class WildcardCapturer { private finalAtomicIntegerid=

        new AtomicInteger() ;Type capture
            (Typetype) {checkNotNull(
            type ,"type" ) ;if (
                type instanceofClass
            )
            { returntype ; }if (
                type instanceofTypeVariable
            )
            { returntype ; }if (
                type instanceof GenericArrayType ){GenericArrayType arrayType=
                ( GenericArrayType)type;returnTypes.newArrayType(capture(arrayType.getGenericComponentType
            (
            ) )) ; }if (
                type instanceof ParameterizedType ){ParameterizedType parameterizedType=
                ( ParameterizedType)type;
                    returnTypes.newParameterizedTypeWithOwner(captureNullable(parameterizedType.
                    getOwnerType()),( Class<?>)parameterizedType
                    .getRawType(),capture(parameterizedType.getActualTypeArguments
            (
            ) )) ; }if (
                type instanceof WildcardType ){WildcardType wildcardType=
                (WildcardType) type ; Type[]lowerBounds=wildcardType
                . getLowerBounds(); if (lowerBounds . length
                    ==0) { // ? extends something changes to capture-of Type[]upperBounds=wildcardType
                    . getUpperBounds (
                        )
                            ; Stringname="capture#"+
                            id .
                            incrementAndGet ()+"-of ? extends "+Stream.of(upperBounds).map(Object::toString).collect(Collectors.joining
                    ( "&"));
                        returnTypes.newArtificialTypeVariable (WildcardCapturer .class,name,wildcardType.
                getUpperBounds ( )
                    ) ;}
                else
            {
            return type ;}}thrownew
        AssertionError

        ( "must have been one of the known types" );}private Type captureNullable( @
            Nullable Typetype ) {if (
                type ==null
            )
            { returnnull;}return
        capture

        ( type); }privateType[] capture( Type
            []types ) { Type []result=newType[
            types .length ] ; for( int i =0;i <types. length
                ;i++) { result[i]=capture(types
            [
            i ])
        ;
    }

    return result ; } }
        static final classTypeVariableKey{private finalTypeVariable

        <?>var;TypeVariableKey (TypeVariable <
            ?>var ) {this.var =checkNotNull(
        var

        ,"var"
        ) ; }@Override public
            int hashCode(){returnObjects.hash(var .getGenericDeclaration(),var.
        getName

        ()
        ) ; }@Override publicboolean equals
            ( Objectobj ) {if (
                obj instanceof TypeVariableKey ){TypeVariableKey that=
                ( TypeVariableKey)obj;returnequalsTypeVariable(
            that . var
                ) ;}
            else
        {

        returnfalse
        ; } }@Override public
            String toString(){returnvar
        .

        toString ( );} staticTypeVariableKey forLookup
            ( Typet ) {if (
                t instanceof TypeVariable){returnnewTypeVariableKey(( TypeVariable<?
            > ) t
                ) ;}
            else
        {

        return null;} }boolean equalsType
            ( Typetype ) {if (
                type instanceofTypeVariable){returnequalsTypeVariable(( TypeVariable<?
            > ) type
                ) ;}
            else
        {

        return false ;}}privatebooleanequalsTypeVariable (TypeVariable <
            ? >that){returnvar.getGenericDeclaration().equals(that
                . getGenericDeclaration())&&var.getName().equals(that.
        getName
    (
)
