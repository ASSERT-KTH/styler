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
                    return; // Okay to say Foo<A> is <?>
                }
                ParameterizedType toParameterizedType = expectArgument(ParameterizedType.class, to);
                if (fromParameterizedType.getOwnerType() != null
                    && toParameterizedType.getOwnerType() != null) {
                    populateTypeMappings(
                        mappings, fromParameterizedType.getOwnerType(), toParameterizedType.getOwnerType());
                }
                checkArgument(
                    fromParameterizedType.getRawType().equals(toParameterizedType.getRawType()),
                    "Inconsistent raw type: %s vs. %s",
                    fromParameterizedType,
                    to);
                Type[] fromArgs = fromParameterizedType.getActualTypeArguments();
                Type[] toArgs = toParameterizedType.getActualTypeArguments();
                checkArgument(
                    fromArgs.length == toArgs.length,
                    "%s not compatible with %s",
                    fromParameterizedType,
                    toParameterizedType);
                for (int i = 0; i < fromArgs.length; i++) {
                    populateTypeMappings(mappings, fromArgs[i], toArgs[i]);
                }
            }

            @Override
            void visitGenericArrayType(GenericArrayType fromArrayType) {
                if (to instanceof WildcardType) {
                    return; // Okay to say A[] is <?>
                }
                Type componentType = Types.getComponentType(to);
                checkArgument(componentType != null, "%s is not an array type.", to);
                populateTypeMappings(mappings, fromArrayType.getGenericComponentType()
                ,componentType);} @Overridevoid
            visitClass

            (Class
            < ?>fromClass){if (to instanceof
                WildcardType ){ return ;// Okay to say Foo is <?> }
                    // Can't map from a raw class to anything other than itself or a wildcard.// You can't say "assuming String is Integer". // And we don't support "assuming String is T"; user has to say "assuming T is String".
                throw
                new
                IllegalArgumentException
                (
                "No type mapping from " + fromClass+" to " + to ) ; } }.visit
            (
        from);}publicTyperesolveType
    (

    Type type ){checkNotNull (type ,
        "type");if (typeinstanceof
        TypeVariable ){ return typeTable. resolve
            ( (TypeVariable<?>)type);} elseif(
        type instanceof ParameterizedType ){ return resolveParameterizedType( (
            ParameterizedType )type);} elseif(
        type instanceof GenericArrayType ){ return resolveGenericArrayType( (
            GenericArrayType )type);} elseif(
        type instanceof WildcardType ){ return resolveWildcardType( (
            WildcardType )type);} else{// if Class<?>, no resolution needed, we are done.
        return type ;
            }
            } privateType
        [
    ]

    resolveTypes (Type[ ]types){Type [] result
        =newType [ types . length];for(inti
        = 0; i < types. length ; i++){ result[i ]
            =resolveType(types [ i]);}returnresult;
        }
        private WildcardTyperesolveWildcardType
    (

    WildcardType type ){Type [] lowerBounds
        =type. getLowerBounds ( );Type[]upperBounds
        =type. getUpperBounds ( );returnnewTypes.
        WildcardTypeImpl ( resolveTypes(lowerBounds),resolveTypes(upperBounds) );}privateTyperesolveGenericArrayType
    (

    GenericArrayType type ){Type componentType= type
        . getGenericComponentType ( );TyperesolvedComponentType=resolveType
        ( componentType ) ;returnTypes.newArrayType
        ( resolvedComponentType);}privateParameterizedTyperesolveParameterizedType
    (

    ParameterizedType type ){Type owner= type
        . getOwnerType ( );TyperesolvedOwner=(
        owner == null )? null :resolveType ( owner ) ;TyperesolvedRawType=resolveType
        ( type . getRawType());Type[]args

        =type. getActualTypeArguments ( );Type[]resolvedArgs
        =resolveTypes( args ) ;returnTypes.newParameterizedTypeWithOwner
        ( resolvedOwner,(Class
            <? >)resolvedRawType,resolvedArgs) ;} privatestatic<
    T

    > T expectArgument(Class < T>type,Objectarg ){ try {return type
        . cast
            ( arg);}catch(ClassCastException
        e ) {throw newIllegalArgumentException (
            arg + " is not a "+type . getSimpleName ( ),e);} }/**
     * A TypeTable maintains mapping from {@link TypeVariable} to types.
     */private
        static
    class

    TypeTable
    { private final Map <
        TypeVariableKey , Type>map; TypeTable( ){

        this.map =
            emptyMap() ; }privateTypeTable(
        Map

        < TypeVariableKey,Type>map) {this .map =
            unmodifiableMap(new LinkedHashMap <>( map));}/**
         * Returns a new {@code TypeResolver} with {@code variable} mapping to {@code type}.
         */finalTypeTable
        where

        (
        Map < TypeVariableKey,?extendsType> mappings ) {Map <TypeVariableKey ,
            Type>builder= newLinkedHashMap < > ( );builder.putAll(
            map);for(Map.
            Entry <TypeVariableKey,?extendsType> mapping : mappings. entrySet ( )){TypeVariableKeyvariable= mapping
                . getKey ( );Typetype=mapping
                . getValue ( );checkArgument(!variable
                .equalsType(type),"Type variable %s bound to itself",variable) ;builder .put(
                variable,type);} returnnewTypeTable
            (
            builder ) ;}finalTyperesolve
        (

        final TypeVariable <?> var){final TypeTableunguarded =
            this ; TypeTable guarded =new
            TypeTable ( )
                { @Overridepublic Type
                    resolveInternal(
                    TypeVariable < ?>intermediateVar,TypeTableforDependent ){ if (intermediateVar .
                        getGenericDeclaration ().equals(var.getGenericDeclaration())){returnintermediateVar; }
                            return unguarded.
                        resolveInternal
                        ( intermediateVar,forDependent);} };return
                    resolveInternal
                (var
            , guarded);} TyperesolveInternal(
        TypeVariable

        < ?>var,TypeTableforDependants ){ Type type= map
            . get ( newTypeVariableKey(var) );if(type==
            null ){ Type [] bounds
                =var. getBounds ( );if(bounds.
                length ==0){ return var; }
                    Type []
                resolvedBounds
                =newTypeResolver ( forDependants ) .resolveTypes(bounds);if(Types.
                NativeTypeVariableEquals .NATIVE_TYPE_VARIABLE_ONLY&&Arrays.equals
                    ( bounds,resolvedBounds)){ returnvar; }
                    return Types.
                newArtificialTypeVariable
                ( var.getGenericDeclaration(
                    ),var.getName( ),resolvedBounds);} // in case the type is yet another type variable.returnnew
            TypeResolver
            (
            forDependants ) .resolveType(type);}}privatestatic
        final
    class

    TypeMappingIntrospector extends TypeVisitor { private static final WildcardCapturer

        WILDCARD_CAPTURER = new WildcardCapturer ( ) ; privatefinalMap<

        TypeVariableKey , Type>mappings= newHashMap < > ( );staticMap<TypeVariableKey

        , Type>getTypeMappings( TypecontextType ){TypeMappingIntrospector introspector= new
            TypeMappingIntrospector ( ) ; introspector.visit(
            WILDCARD_CAPTURER.capture(contextType));returnunmodifiableMap(new
            LinkedHashMap <>( introspector.mappings));}@Overridevoid
        visitClass

        (Class
        < ?>clazz){visit (clazz .
            getGenericSuperclass());visit(clazz.
            getGenericInterfaces());}@Overridevoid
        visitParameterizedType

        (ParameterizedType
        parameterizedType ){Class <? >
            rawClass=(Class < ? >)parameterizedType.getRawType( );TypeVariable<?>
            []vars=rawClass. getTypeParameters ( );Type[]typeArgs
            =parameterizedType. getActualTypeArguments ( );checkState(vars.
            length==typeArgs.length , "Expected %s type variables, but got %s",typeArgs.
                length, vars.length) ;for(inti
            = 0; i < vars. length ; i++){ map(new TypeVariableKey
                (vars[ i]),typeArgs[i] );}visit(rawClass
            )
            ;visit(parameterizedType.
            getOwnerType());}@Overridevoid
        visitTypeVariable

        (TypeVariable
        < ?>t){visit (t .
            getBounds());}@Overridevoid
        visitWildcardType

        (WildcardType
        t ){visit (t .
            getUpperBounds());}privatevoidmap
        (

        final TypeVariableKey var,final Type arg) { if (mappings .
            containsKey (var)){return;} for
                (Type
            t
            = arg; t != null; t = mappings. get ( TypeVariableKey.forLookup(t))){if(var .
                equalsType (t)){Typex= arg
                    ; while ( x!=
                    null ){ x =mappings .
                        remove ( TypeVariableKey.forLookup(x));}return;}
                    }
                    mappings.
                put
            (
            var,arg);} }privatestatic
        final
    class

    WildcardCapturer { private final AtomicInteger id

        = new AtomicInteger ( ) ; Typecapture(Type

        type ){checkNotNull (type ,
            "type");if (typeinstanceof
            Class ){ return type; }
                if (type
            instanceof
            TypeVariable ){ return type; }
                if (type
            instanceof
            GenericArrayType ){ GenericArrayType arrayType= (
                GenericArrayType ) type ;returnTypes .newArrayType
                ( capture(arrayType.getGenericComponentType()));}if(type
            instanceof
            ParameterizedType ){ ParameterizedType parameterizedType= (
                ParameterizedType ) type ;returnTypes .newParameterizedTypeWithOwner
                ( captureNullable(parameterizedType.
                    getOwnerType()),(Class<?
                    >)parameterizedType.getRawType( ),capture(parameterizedType.
                    getActualTypeArguments()));}if(type
            instanceof
            WildcardType ){ WildcardType wildcardType= (
                WildcardType ) type ;Type[ ]lowerBounds
                =wildcardType. getLowerBounds ( );if(lowerBounds.
                length ==0){ // ? extends something changes to capture-of Type[ ] upperBounds
                    =wildcardType. getUpperBounds ( );Stringname="capture#"
                    + id .
                        incrementAndGet
                            ( )+"-of ? extends "+Stream
                            . of
                            ( upperBounds).map(Object::toString).collect(Collectors.joining("&"));returnTypes.newArtificialTypeVariable
                    ( WildcardCapturer.class,
                        name,wildcardType. getUpperBounds( ));}else{return
                type ; }
                    } thrownew
                AssertionError
            (
            "must have been one of the known types" ) ;}privateTypecaptureNullable
        (

        @ Nullable Typetype){ if (type ==
            null ){ return null; }
                return capture(
            type
            ) ;}privateType[
        ]

        capture (Type[ ]types){Type [] result
            =newType [ types . length];for(inti
            = 0; i < types. length ; i++){ result[i ]
                =capture(types [ i]);}returnresult;
            }
            } staticfinal
        class
    TypeVariableKey

    { private final TypeVariable <
        ? > var;TypeVariableKey( TypeVariable<

        ?>var){this .var =
            checkNotNull(var , "var");} @Overridepublic
        int

        hashCode(
        ) { returnObjects. hash
            ( var.getGenericDeclaration(),var.getName( ));}@Overridepublic
        boolean

        equals(
        Object obj ){if (obj instanceof
            TypeVariableKey ){ TypeVariableKey that= (
                TypeVariableKey ) obj ;returnequalsTypeVariable (that
                . var);}else{return
            false ; }
                } @Override
            public
        String

        toString(
        ) { returnvar. toString
            ( );}staticTypeVariableKeyforLookup
        (

        Type t ){if (t instanceof
            TypeVariable ){ return newTypeVariableKey (
                ( TypeVariable <?>)t);} else{return
            null ; }
                } booleanequalsType
            (
        Type

        type ){if (type instanceof
            TypeVariable ){ return equalsTypeVariable( (
                TypeVariable <?>)type);} else{return
            false ; }
                } privateboolean
            equalsTypeVariable
        (

        TypeVariable < ?>that){return var. getGenericDeclaration
            ( ).equals(that.getGenericDeclaration())&&var.getName
                ( ).equals(that.getName());}}}