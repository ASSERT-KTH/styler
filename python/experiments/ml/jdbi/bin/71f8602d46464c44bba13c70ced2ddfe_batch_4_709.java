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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static org.jdbi.v3.lib.internal.com_google_guava.guava.v21_0.Preconditions.checkArgument;
import static org.jdbi.v3.lib.internal.com_google_guava.guava.v21_0.Preconditions.checkNotNull;
import static org.jdbi.v3.lib.internal.com_google_guava.guava.v21_0.Preconditions.checkState;

public final class TypeResolver {

    private final TypeTable typeTable;

    public TypeResolver() {
        this.typeTable = new TypeTable();
    }

    private TypeResolver(TypeTable typeTable) {
        this.typeTable = typeTable;
    }

    static TypeResolver accordingTo(Type type) {
        return new TypeResolver().where(TypeMappingIntrospector.getTypeMappings(type));
    }

    public TypeResolver where(Type formal, Type actual) {
        Map<TypeVariableKey, Type> mappings = new HashMap<>();
        populateTypeMappings(mappings, checkNotNull(formal, "formal"), checkNotNull(actual, "actual"));
        return where(mappings);
    }

    TypeResolver where(Map<TypeVariableKey, ? extends Type> mappings) {
        return new TypeResolver(typeTable.where(mappings));
    }

    private static void populateTypeMappings(final Map<TypeVariableKey, Type> mappings, Type from, final Type to) {
        if (from.equals(to)) {
            return;
        }
        new TypeVisitor() {
            @Override
            void visitTypeVariable(TypeVariable<?> typeVariable) {
                mappings.put(new TypeVariableKey(typeVariable), to);
            }

            @Override
            void visitWildcardType(WildcardType fromWildcardType) {
                if (!(to instanceof WildcardType)) {
                    return; // okay to say <?> is anything
                }
                WildcardType toWildcardType = (WildcardType) to;
                Type[] fromUpperBounds = fromWildcardType.getUpperBounds();
                Type[] toUpperBounds = toWildcardType.getUpperBounds();
                Type[] fromLowerBounds = fromWildcardType.getLowerBounds();
                Type[] toLowerBounds = toWildcardType.getLowerBounds();
                checkArgument(
                    fromUpperBounds.length == toUpperBounds.length
                        && fromLowerBounds.length == toLowerBounds.length,
                    "Incompatible type: %s vs. %s",
                    fromWildcardType,
                    to);
                for (int i = 0; i < fromUpperBounds.length; i++) {
                    populateTypeMappings(mappings, fromUpperBounds[i], toUpperBounds[i]);
                }
                for (int i = 0; i < fromLowerBounds.length; i++) {
                    populateTypeMappings(mappings, fromLowerBounds[i], toLowerBounds[i]);
                }
            }

            @Override
            void visitParameterizedType(ParameterizedType fromParameterizedType) {
                if (to instanceof WildcardType) {
                    return; // Okay to say Foo<A> is <?>}ParameterizedTypetoParameterizedType=expectArgument ( ParameterizedType
                        . class,to); if (fromParameterizedType .
                        getOwnerType(
                            )!= null&&toParameterizedType.getOwnerType( )!=null){populateTypeMappings(
                    mappings
                    ,fromParameterizedType
                        .getOwnerType(),toParameterizedType.getOwnerType());}checkArgument(
                        fromParameterizedType.
                        getRawType(
                        ).equals
                    (toParameterizedType. getRawType ( )),"Inconsistent raw type: %s vs. %s",fromParameterizedType
                    ,to) ; Type []fromArgs=fromParameterizedType.
                    getActualTypeArguments(
                        );Type [ ]toArgs=toParameterizedType
                        .getActualTypeArguments
                        ()
                        ;checkArgument(
                    fromArgs .length == toArgs .length , "%s not compatible with %s" ,fromParameterizedType,toParameterizedType );for (
                        inti=0 ;i<fromArgs. length;i++){
                    populateTypeMappings
                (

                mappings,
                fromArgs [i] ,toArgs [
                    i ]) ; }} @
                        Overridevoid visitGenericArrayType
                    (
                    GenericArrayType fromArrayType ) {if(toinstanceofWildcardType)
                    {return; // Okay to say A[] is <?> }Type componentType= Types.getComponentType
                    (to); checkArgument(componentType!=null, "%s is not an array type.",to
                )

                ;populateTypeMappings
                ( mappings,fromArrayType.getGenericComponentType( ), componentType
                    ) ;} @ Overridevoid visitClass
                        (Class <
                    ?
                    >
                    fromClass
                    )
                    { if (toinstanceof WildcardType ) { return ; // Okay to say Foo is <?>}// Can't map from a raw class to anything other than itself or a wildcard.
                // You can't say "assuming String is Integer".
            // And we don't support "assuming String is T"; user has to say "assuming T is String".thrownewIllegalArgumentException("No type mapping from "+
        fromClass

        + " to " +to) ;} }
            .visit(from );}
            public TyperesolveType ( Typetype )
                { checkNotNull(type,"type");if(type instanceofTypeVariable)
            { return typeTable .resolve ( (TypeVariable <
                ? >)type); }elseif
            ( type instanceof ParameterizedType) { returnresolveParameterizedType (
                ( ParameterizedType)type); }elseif
            ( type instanceof GenericArrayType) { returnresolveGenericArrayType (
                ( GenericArrayType)type); }elseif
            ( type instanceof
                WildcardType
                ) {return
            resolveWildcardType
        (

        ( WildcardType)type );}else{ // if Class<?>, no resolution needed, we are done.return type
            ;}} private Type [ ]resolveTypes(Type[]types
            ) {Type [ ] result= new Type [types.length ];for (
                inti=0 ; i<types.length;i++
            )
            { result[
        i

        ] = resolveType(types [i ]
            );} return result ;}privateWildcardTyperesolveWildcardType(
            WildcardTypetype) { Type []lowerBounds=type.
            getLowerBounds ( );Type[]upperBounds=type. getUpperBounds();returnnew
        Types

        . WildcardTypeImpl (resolveTypes( lowerBounds) ,
            resolveTypes ( upperBounds ));}privateType
            resolveGenericArrayType ( GenericArrayType type){TypecomponentType
            = type.getGenericComponentType();Type
        resolvedComponentType

        = resolveType (componentType) ;return Types
            . newArrayType ( resolvedComponentType);}privateParameterizedType
            resolveParameterizedType ( ParameterizedType type) { Typeowner = type . getOwnerType();Type
            resolvedOwner = ( owner==null)?null:resolveType(

            owner); Type resolvedRawType =resolveType(type.getRawType
            ()) ; Type []args=type
            . getActualTypeArguments();
                Type[ ]resolvedArgs=resolveTypes(args ); returnTypes.
        newParameterizedTypeWithOwner

        ( resolvedOwner ,(Class < ?>)resolvedRawType,resolvedArgs ); } privatestatic <
            T >
                T expectArgument(Class<T>type
            , Object arg) {try {
                return type .cast( arg ) ; }catch(ClassCastExceptione) {thrownew
            IllegalArgumentException
        (

        arg
        + " is not a " + type .
            getSimpleName ( ),e) ;} }/**
     * A TypeTable maintains mapping from {@link TypeVariable} to types.
     */

            privatestaticclass TypeTable
                {privatefinal Map <TypeVariableKey,Type
            >

            map ;TypeTable(){this .map =emptyMap (
                );} private TypeTable(Map <TypeVariableKey,Type>map){
            this

            .
            map = unmodifiableMap(newLinkedHashMap<> ( map )) ;} /**
         * Returns a new {@code TypeResolver} with {@code variable} mapping to {@code type}.
         */
                finalTypeTablewhere( Map< TypeVariableKey , ? extendsType>mappings){
                Map<TypeVariableKey,Type>builder
                = newLinkedHashMap<>(); builder . putAll( map ) ;for(Map.Entry <
                    TypeVariableKey , ? extendsType>mapping:mappings
                    . entrySet ( )){TypeVariableKeyvariable=
                    mapping.getKey();Typetype=mapping .getValue ();
                    checkArgument(!variable.equalsType (type)
                ,
                "Type variable %s bound to itself" , variable);builder.
            put

            ( variable ,type) ;}returnnew TypeTable( builder
                ) ; } final Typeresolve
                ( final TypeVariable
                    < ?>var )
                        {final
                        TypeTable unguarded =this;TypeTableguarded= newTypeTable ( ){ @
                            Override publicTyperesolveInternal(TypeVariable<?>intermediateVar,TypeTableforDependent){if( intermediateVar
                                . getGenericDeclaration(
                            )
                            . equals(var.getGenericDeclaration( )))
                        {
                    returnintermediateVar
                ; }returnunguarded. resolveInternal(intermediateVar
            ,

            forDependent );}};return resolveInternal( var ,guarded )
                ; } Type resolveInternal(TypeVariable<? >var,TypeTableforDependants)
                { Typetype = map. get
                    (newTypeVariableKey ( var ));if(type
                    == null){Type [ ]bounds =
                        var .getBounds
                    (
                    );if ( bounds . length==0){returnvar;}Type
                    [ ]resolvedBounds=newTypeResolver(
                        forDependants ).resolveTypes(bounds) ;if( Types
                        . NativeTypeVariableEquals.
                    NATIVE_TYPE_VARIABLE_ONLY
                    && Arrays.equals(
                        bounds,resolvedBounds)){ returnvar;}returnTypes .newArtificialTypeVariable(
                var
                .
                getGenericDeclaration ( ),var.getName(),resolvedBounds)
            ;
        }

        // in case the type is yet another type variable. return new TypeResolver ( forDependants ) .

            resolveType ( type ) ; } } privatestaticfinalclass

            TypeMappingIntrospector extends TypeVisitor{privatestatic finalWildcardCapturer WILDCARD_CAPTURER = new WildcardCapturer();privatefinal

            Map <TypeVariableKey,Type >mappings =newHashMap <> (
                ) ; static Map <TypeVariableKey,Type
                >getTypeMappings(TypecontextType){TypeMappingIntrospectorintrospector=newTypeMappingIntrospector
                ( );introspector .visit(WILDCARD_CAPTURER.capture(contextType))
            ;

            returnunmodifiableMap
            ( newLinkedHashMap<>(introspector .mappings )
                );}@OverridevoidvisitClass(Class
                <?>clazz){visit(clazz
            .

            getGenericSuperclass(
            ) );visit (clazz .
                getGenericInterfaces()) ; } @OverridevoidvisitParameterizedType(ParameterizedType parameterizedType){Class<?
                >rawClass=(Class< ? > )parameterizedType.getRawType()
                ;TypeVariable< ? > []vars=rawClass.
                getTypeParameters();Type [ ]typeArgs=parameterizedType
                    .getActualTypeArguments ();checkState (vars.length==
                typeArgs .length , "Expected %s type variables, but got %s" ,typeArgs . length ,vars.length );for (
                    inti= 0;i<vars.length; i++){map(
                new
                TypeVariableKey(vars[i
                ]),typeArgs[i]);
            }

            visit(
            rawClass );visit(parameterizedType. getOwnerType( )
                );}@OverridevoidvisitTypeVariable(TypeVariable
            <

            ?>
            t ){visit (t .
                getBounds());}@Overridevoid
            visitWildcardType

            ( WildcardType t){ visit (t . getUpperBounds () )
                ; }privatevoidmap(finalTypeVariableKeyvar ,
                    finalType
                arg
                ) {if ( mappings .containsKey ( var )) { return ;}for(Typet=arg;t!=null ;
                    t =mappings.get(TypeVariableKey.forLookup (
                        t ) ) ){
                        if (var . equalsType( t
                            ) ) {Typex=arg;while(x!=null)
                        {
                        x=
                    mappings
                .
                remove(TypeVariableKey.forLookup( x))
            ;
        }

        return ; } } mappings .

            put ( var , arg ) ;}}private

            static finalclassWildcardCapturer {private final
                AtomicIntegerid=new AtomicInteger()
                ; Typecapture ( Typetype )
                    { checkNotNull(
                type
                , "type") ; if( type
                    instanceof Class)
                {
                return type; } if( type
                    instanceof TypeVariable ) {returntype ;}
                    if (typeinstanceofGenericArrayType){GenericArrayTypearrayType=(GenericArrayType)type;
                return
                Types .newArrayType ( capture( arrayType
                    . getGenericComponentType ( ))) ;}
                    if (typeinstanceofParameterizedType
                        ){ParameterizedTypeparameterizedType=(ParameterizedType)type
                        ;returnTypes.newParameterizedTypeWithOwner( captureNullable(parameterizedType.getOwnerType(
                        )),(Class<?>)parameterizedType
                .
                getRawType () , capture( parameterizedType
                    . getActualTypeArguments ( ))) ;}
                    if(type instanceof WildcardType ){WildcardTypewildcardType=(
                    WildcardType )type;Type [ ]lowerBounds = wildcardType
                        .getLowerBounds( ) ; if(lowerBounds.length==
                        0 ) {
                            // ? extends something changes to capture-of
                                Type []upperBounds=wildcardType
                                . getUpperBounds
                                ( );Stringname="capture#"+id.incrementAndGet()+"-of ? extends "+Stream.of(upperBounds).map(
                        Object ::toString).
                            collect(Collectors. joining( "&"));returnTypes.
                    newArtificialTypeVariable ( WildcardCapturer
                        . class,
                    name
                ,
                wildcardType . getUpperBounds());
            }

            else { returntype;} } thrownew AssertionError
                ( "must have been one of the known types") ; }private Type
                    captureNullable (@
                Nullable
                Type type){if(
            type

            == null){ returnnull;}return capture( type
                );} private Type [ ]capture(Type[]types
                ) {Type [ ] result= new Type [types.length ];for (
                    inti=0 ; i<types.length;i++
                )
                { result[
            i
        ]

        = capture ( types [
            i ] );}return result;

            }}staticfinalclassTypeVariableKey {private final
                TypeVariable<? > var;TypeVariableKey( TypeVariable<?
            >

            var)
            { this .var= checkNotNull
                ( var,"var");}@Overridepublicint hashCode(){returnObjects.
            hash

            (var
            . getGenericDeclaration (), var. getName
                ( )) ; }@ Override
                    public boolean equals (Objectobj ){
                    if (objinstanceofTypeVariableKey){TypeVariableKey
                that = (
                    TypeVariableKey )obj
                ;
            return

            equalsTypeVariable(
            that . var); }
                else {returnfalse;}}
            @

            Override public StringtoString( ){ return
                var .toString ( ); }
                    static TypeVariableKey forLookup(Typet){if( tinstanceofTypeVariable
                ) { return
                    new TypeVariableKey(
                (
            TypeVariable

            < ?>) t) ;
                } else{ return null; }
                    } booleanequalsType(Typetype){if (typeinstanceof
                TypeVariable ) {
                    return equalsTypeVariable(
                (
            TypeVariable

            < ? >)type);} else{ return
                false ;}}privatebooleanequalsTypeVariable(TypeVariable<?>that){
                    return var.getGenericDeclaration().equals(that.getGenericDeclaration())&&
            var
        .
    getName
    