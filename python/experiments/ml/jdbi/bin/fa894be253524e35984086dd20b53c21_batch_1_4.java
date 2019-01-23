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
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;importjava
. util.HashMap;importjava
. util.LinkedHashMap;importjava
. util.Map;importjava
. util.Objects;importjava
. util.concurrent.atomic.
AtomicInteger ;importjava.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;importstatic
java .util.Collections.emptyMap

; import staticjava.util.Collections.unmodifiableMap
; import staticorg.jdbi.v3.lib
. internal .com_google_guava.guava.v21_0.Preconditions.checkArgument;importstaticorg.jdbi.v3.lib
. internal .com_google_guava.guava.v21_0.Preconditions.checkNotNull;importstaticorg.jdbi.v3.lib
. internal .com_google_guava.guava.v21_0.Preconditions.checkState;publicfinalclassTypeResolver{privatefinalTypeTabletypeTable

; public TypeResolver ( )

    { this . typeTable=

    new TypeTable() ;
        }privateTypeResolver ( TypeTable typeTable){this
    .

    typeTable =typeTable; }static TypeResolver
        accordingTo(Type type ){
    return

    new TypeResolver (). where( TypeMappingIntrospector
        . getTypeMappings (type));}publicTypeResolverwhere(Typeformal,Type
    actual

    ) { Map<TypeVariableKey ,Type > mappings= new
        HashMap<>( ); populateTypeMappings ( mappings ,checkNotNull(formal,"formal"
        ),checkNotNull( actual,"actual") );return where(mappings) ;}TypeResolverwhere
        ( Map<TypeVariableKey,?
    extends

    Type >mappings){returnnew TypeResolver ( typeTable. where( mappings
        ) ) ;}privatestaticvoidpopulateTypeMappings(finalMap<
    TypeVariableKey

    , Type > mappings,Type from,finalType to) {if ( from. equals ( to) )
        { return;}newTypeVisitor(){ @
            Overridevoid
        visitTypeVariable
        ( TypeVariable<? >
            typeVariable)
            { mappings.put(newTypeVariableKey (typeVariable )
                ,to);} @OverridevoidvisitWildcardType( WildcardTypefromWildcardType)
            {

            if(
            ! (toinstanceof WildcardType) )
                { return;// okay to say <?> is anything} WildcardType toWildcardType=( WildcardType
                    )to ;
                Type
                [ ] fromUpperBounds =fromWildcardType. getUpperBounds(
                );Type [ ] toUpperBounds=toWildcardType.getUpperBounds(
                );Type [ ] fromLowerBounds=fromWildcardType.getLowerBounds(
                );Type [ ] toLowerBounds=toWildcardType.getLowerBounds(
                );checkArgument ( fromUpperBounds .length==toUpperBounds.length
                &&fromLowerBounds
                    .length== toLowerBounds .length,
                        "Incompatible type: %s vs. %s" ,fromWildcardType, to );for(
                    inti
                    =0
                    ;i<
                fromUpperBounds .length ; i ++) { populateTypeMappings (mappings,fromUpperBounds [i] ,
                    toUpperBounds[i] );}for( inti=0;i
                <
                fromLowerBounds .length ; i ++) { populateTypeMappings (mappings,fromLowerBounds [i] ,
                    toLowerBounds[i] );}}@ OverridevoidvisitParameterizedType(ParameterizedTypefromParameterizedType
                )
            {

            if(
            to instanceofWildcardType) {return ;
                // Okay to say Foo<A> is <?> }ParameterizedType toParameterizedType =expectArgument (
                    ParameterizedType. class
                ,
                to ) ; if(fromParameterizedType.getOwnerType( )!=null
                && toParameterizedType.getOwnerType()!= null )
                    { populateTypeMappings(mappings,fromParameterizedType . getOwnerType( )
                    ,toParameterizedType
                        .getOwnerType ());}checkArgument (fromParameterizedType.getRawType().
                equals
                (toParameterizedType
                    .getRawType()),"Inconsistent raw type: %s vs. %s",fromParameterizedType,to);Type[
                    ]fromArgs
                    =fromParameterizedType
                    .getActualTypeArguments(
                );Type [ ] toArgs=toParameterizedType.getActualTypeArguments(
                );checkArgument ( fromArgs .length==toArgs.length
                ,"%s not compatible with %s"
                    ,fromParameterizedType, toParameterizedType );for(
                    inti
                    =0
                    ;i<
                fromArgs .length ; i ++) { populateTypeMappings (mappings,fromArgs [i] ,
                    toArgs[i] );}}@ OverridevoidvisitGenericArrayType(GenericArrayTypefromArrayType
                )
            {

            if(
            to instanceofWildcardType) {return ;
                // Okay to say A[] is <?> }Type componentType =Types .
                    getComponentType( to
                )
                ; checkArgument ( componentType!=null,"%s is not an array type.",to
                );populateTypeMappings ( mappings, fromArrayType. getGenericComponentType()
                ,componentType); }@OverridevoidvisitClass( Class<?
            >

            fromClass)
            { if(toinstanceofWildcardType) {return ;
                // Okay to say Foo is <?> }// Can't map from a raw class to anything other than itself or a wildcard. // You can't say "assuming String is Integer". // And we don't support "assuming String is T"; user has to say "assuming T is String".throw new
                    IllegalArgumentException( "No type mapping from "
                +
                fromClass
                +
                " to "
                + to );} } . visit ( from );}
            public
        TyperesolveType(Typetype){
    checkNotNull

    ( type ,"type") ;if (
        typeinstanceofTypeVariable) {returntypeTable
        . resolve( ( TypeVariable< ?
            > )type);}elseif(typeinstanceof ParameterizedType){
        return resolveParameterizedType ( (ParameterizedType ) type) ;
            } elseif(typeinstanceof GenericArrayType){
        return resolveGenericArrayType ( (GenericArrayType ) type) ;
            } elseif(typeinstanceof WildcardType){
        return resolveWildcardType ( (WildcardType ) type) ;
            } else{// if Class<?>, no resolution needed, we are done.returntype ;}}
        private Type [
            ]
            resolveTypes (Type
        [
    ]

    types ){Type []result=new Type[ types
        .length] ; for ( inti=0;i<
        types .length ; i ++) { result [i]= resolveType(types [
            i]); } returnresult;}privateWildcardTyperesolveWildcardType(
        WildcardType
        type ){
    Type

    [ ] lowerBounds=type .getLowerBounds (
        );Type [ ] upperBounds=type.getUpperBounds(
        );return new Types .WildcardTypeImpl(resolveTypes(lowerBounds
        ) , resolveTypes(upperBounds));}privateType resolveGenericArrayType(GenericArrayTypetype){
    Type

    componentType = type.getGenericComponentType () ;
        Type resolvedComponentType = resolveType(componentType);return
        Types . newArrayType (resolvedComponentType);}
        private ParameterizedTyperesolveParameterizedType(ParameterizedTypetype){
    Type

    owner = type.getOwnerType () ;
        Type resolvedOwner = (owner==null)?
        null : resolveType (owner ) ;Type resolvedRawType = resolveType (type.getRawType(
        ) ) ; Type[]args=type.getActualTypeArguments(

        );Type [ ] resolvedArgs=resolveTypes(args)
        ;returnTypes . newParameterizedTypeWithOwner (resolvedOwner,(Class
        < ?>)resolvedRawType
            ,resolvedArgs );}privatestatic< T> TexpectArgument(
    Class

    < T >type, Object arg){try{return type. cast (arg )
        ; }
            catch (ClassCastExceptione){thrownew
        IllegalArgumentException ( arg+ " is not a "+ type
            . getSimpleName (), e ) ; }}/**
     * A TypeTable maintains mapping from {@link TypeVariable} to types.
     */privatestaticclass TypeTable{private
        final
    Map

    <
    TypeVariableKey , Type > map
        ; TypeTable (){this .map =emptyMap

        (); }
            privateTypeTable( Map <TypeVariableKey,Type
        >

        map ){this.map= unmodifiableMap( newLinkedHashMap <
            >(map ) );} /**
         * Returns a new {@code TypeResolver} with {@code variable} mapping to {@code type}.
         */finalTypeTablewhere(Map<TypeVariableKey
        ,

        ?
        extends Type >mappings){Map< TypeVariableKey , Type> builder= new
            LinkedHashMap<>( ); builder . putAll (map);for(
            Map.Entry<TypeVariableKey,?
            extends Type>mapping:mappings.entrySet ( ) ){ TypeVariableKey variable =mapping.getKey() ;
                Type type = mapping.getValue();
                checkArgument ( ! variable.equalsType(type)
                ,"Type variable %s bound to itself",variable);builder.put( variable, type);
                }returnnewTypeTable(builder );}
            final
            Type resolve (finalTypeVariable<?
        >

        var ) {finalTypeTable unguarded=this; TypeTableguarded =
            new TypeTable ( ) {@
            Override public Type
                resolveInternal (TypeVariable< ?
                    >intermediateVar
                    , TypeTable forDependent){if(intermediateVar .getGenericDeclaration ( ). equals
                        ( var.getGenericDeclaration())){returnintermediateVar;}returnunguarded.resolveInternal (
                            intermediateVar ,forDependent
                        )
                        ; }};returnresolveInternal( var,guarded
                    )
                ;}
            Type resolveInternal(TypeVariable< ?>var
        ,

        TypeTable forDependants){Typetype= map. get (new TypeVariableKey
            ( var ) );if(type ==null){Type[
            ] bounds= var .getBounds (
                );if ( bounds .length==0){
                return var;}Type [ ]resolvedBounds =
                    new TypeResolver(
                forDependants
                ).resolveTypes ( bounds ) ;if(Types.NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY&&Arrays
                . equals(bounds,resolvedBounds)
                    ) {returnvar;}return Types.newArtificialTypeVariable (
                    var .getGenericDeclaration
                (
                ) ,var.getName
                    (),resolvedBounds); }// in case the type is yet another type variable.returnnewTypeResolver( forDependants).
            resolveType
            (
            type ) ;}}privatestaticfinalclassTypeMappingIntrospectorextendsTypeVisitor
        {
    private

    static final WildcardCapturer WILDCARD_CAPTURER = new WildcardCapturer (

        ) ; private final Map < TypeVariableKey ,Type>mappings

        = new HashMap<>( ); static Map < TypeVariableKey,Type>getTypeMappings(

        Type contextType){TypeMappingIntrospector introspector= newTypeMappingIntrospector( ); introspector
            . visit ( WILDCARD_CAPTURER .capture(contextType
            ));returnunmodifiableMap(newLinkedHashMap<>(introspector
            . mappings)) ;}@OverridevoidvisitClass(Class<?
        >

        clazz)
        { visit(clazz.getGenericSuperclass( )) ;
            visit(clazz.getGenericInterfaces());
            }@OverridevoidvisitParameterizedType(ParameterizedTypeparameterizedType)
        {

        Class<
        ? >rawClass= (Class <
            ?>)parameterizedType . getRawType ();TypeVariable<? >[]vars=rawClass
            .getTypeParameters();Type [ ] typeArgs=parameterizedType.getActualTypeArguments(
            );checkState ( vars .length==typeArgs.length
            ,"Expected %s type variables, but got %s",typeArgs. length ,vars.length
                ); for(inti =0;i<
            vars .length ; i ++) { map (newTypeVariableKey( vars[i ]
                ),typeArgs [i]);}visit( rawClass);visit(parameterizedType
            .
            getOwnerType());
            }@OverridevoidvisitTypeVariable(TypeVariable<?
        >

        t)
        { visit(t.getBounds( )) ;
            }@OverridevoidvisitWildcardType(WildcardTypet)
        {

        visit(
        t .getUpperBounds( )) ;
            }privatevoidmap(finalTypeVariableKeyvar,
        final

        Type arg ){if ( mappings. containsKey ( var) )
            { return;}for(Typet= arg
                ;t
            !=
            null ;t = mappings .get ( TypeVariableKey .forLookup ( t ))){if(var.equalsType(t) )
                { Typex=arg;while(x !=
                    null ) { x=
                    mappings .remove ( TypeVariableKey. forLookup
                        ( x ));}return;}}mappings.put(
                    var
                    ,arg
                )
            ;
            }}privatestaticfinalclass WildcardCapturer{private
        final
    AtomicInteger

    id = new AtomicInteger ( )

        ; Type capture ( Type type ){checkNotNull(

        type ,"type") ;if (
            typeinstanceofClass) {returntype
            ; }if ( typeinstanceof TypeVariable
                ) {return
            type
            ; }if ( typeinstanceof GenericArrayType
                ) {GenericArrayType
            arrayType
            = (GenericArrayType ) type; return
                Types . newArrayType (capture( arrayType.
                getGenericComponentType ()));}if(typeinstanceofParameterizedType){ParameterizedType
            parameterizedType
            = (ParameterizedType ) type; return
                Types . newParameterizedTypeWithOwner (captureNullable( parameterizedType.
                getOwnerType ()),
                    (Class<?>)parameterizedType.getRawType
                    (),capture(parameterizedType .getActualTypeArguments()))
                    ;}if(typeinstanceofWildcardType){WildcardType
            wildcardType
            = (WildcardType ) type; Type
                [ ] lowerBounds =wildcardType. getLowerBounds(
                );if ( lowerBounds .length==0){
                // ? extends something changes to capture-of Type[]upperBounds = wildcardType. getUpperBounds (
                    );String name = "capture#"+id.incrementAndGet(
                    ) + "-of ? extends "
                        +
                            Stream .of(upperBounds)
                            . map
                            ( Object::toString).collect(Collectors.joining("&"));returnTypes.newArtificialTypeVariable(WildcardCapturer.class,
                    name ,wildcardType.getUpperBounds
                        ()); }else {returntype;}}throw
                new AssertionError (
                    "must have been one of the known types" );
                }
            private
            Type captureNullable (@NullableTypetype
        )

        { if (type==null ) {return null
            ; }return capture (type )
                ; }private
            Type
            [ ]capture(Type[
        ]

        types ){Type []result=new Type[ types
            .length] ; for ( inti=0;i<
            types .length ; i ++) { result [i]= capture(types [
                i]); } returnresult;}}staticfinalclass
            TypeVariableKey
            { privatefinal
        TypeVariable
    <

    ? > var ; TypeVariableKey
        ( TypeVariable <?>var ){

        this.var=checkNotNull( var, "var"
            );} @ OverridepublicinthashCode (){
        return

        Objects.
        hash ( var.getGenericDeclaration (
            ) ,var.getName());}@ Overridepublicbooleanequals(Objectobj
        )

        {if
        ( obj instanceofTypeVariableKey) {TypeVariableKey that
            = (TypeVariableKey ) obj; return
                equalsTypeVariable ( that .var) ;}
                else {returnfalse;}}@
            Override public String
                toString ()
            {
        return

        var.
        toString ( );} static
            TypeVariableKey forLookup(Typet){
        if

        ( t instanceofTypeVariable) {return new
            TypeVariableKey (( TypeVariable <? >
                ) t );}else{returnnull; }}boolean
            equalsType ( Type
                type ){
            if
        (

        type instanceofTypeVariable) {return equalsTypeVariable
            ( (TypeVariable < ?> )
                type );}else{returnfalse; }}private
            boolean equalsTypeVariable (
                TypeVariable <?
            >
        that

        ) { returnvar.getGenericDeclaration() .equals (
            that .getGenericDeclaration())&&var.getName().equals(
                that .getName());}}}