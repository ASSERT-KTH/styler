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
                    return;
                // Okay to say Foo<A> is <?>
                } ParameterizedType toParameterizedType =expectArgument(ParameterizedType.class ,to)
                ; if(fromParameterizedType.getOwnerType( ) !=
                    null &&toParameterizedType.getOwnerType( ) !=null )
                    {populateTypeMappings
                        (mappings ,fromParameterizedType.getOwnerType() ,toParameterizedType.getOwnerType())
                ;
                }checkArgument
                    (fromParameterizedType.getRawType().equals(toParameterizedType.getRawType())
                    ,"Inconsistent raw type: %s vs. %s"
                    ,fromParameterizedType
                    ,to)
                ;Type[ ] fromArgs =fromParameterizedType.getActualTypeArguments()
                ;Type[ ] toArgs =toParameterizedType.getActualTypeArguments()
                ;checkArgument
                    (fromArgs. length ==toArgs.length
                    ,"%s not compatible with %s"
                    ,fromParameterizedType
                    ,toParameterizedType)
                ; for( int i =0 ; i <fromArgs.length ;i++ )
                    {populateTypeMappings(mappings ,fromArgs[i] ,toArgs[i])
                ;
            }

            }@
            Override voidvisitGenericArrayType( GenericArrayTypefromArrayType )
                { if( to instanceofWildcardType )
                    {return ;
                // Okay to say A[] is <?>
                } Type componentType =Types.getComponentType(to)
                ;checkArgument( componentType !=null ,"%s is not an array type." ,to)
                ;populateTypeMappings(mappings ,fromArrayType.getGenericComponentType() ,componentType)
            ;

            }@
            Override voidvisitClass(Class<? >fromClass )
                { if( to instanceofWildcardType )
                    {return ;
                // Okay to say Foo is <?>
                }
                // Can't map from a raw class to anything other than itself or a wildcard.
                // You can't say "assuming String is Integer".
                // And we don't support "assuming String is T"; user has to say "assuming T is String". throw newIllegalArgumentException( "No type mapping from " + fromClass + " to " +to)
            ;
        }}.visit(from)
    ;

    } public TyperesolveType( Typetype )
        {checkNotNull(type ,"type")
        ; if( type instanceofTypeVariable )
            { returntypeTable.resolve((TypeVariable<?> )type)
        ; } else if( type instanceofParameterizedType )
            { returnresolveParameterizedType((ParameterizedType )type)
        ; } else if( type instanceofGenericArrayType )
            { returnresolveGenericArrayType((GenericArrayType )type)
        ; } else if( type instanceofWildcardType )
            { returnresolveWildcardType((WildcardType )type)
        ; } else
            {
            // if Class<?>, no resolution needed, we are done. returntype
        ;
    }

    } privateType[ ]resolveTypes(Type[ ]types )
        {Type[ ] result = newType[types.length]
        ; for( int i =0 ; i <types.length ;i++ )
            {result[i ] =resolveType(types[i])
        ;
        } returnresult
    ;

    } private WildcardTyperesolveWildcardType( WildcardTypetype )
        {Type[ ] lowerBounds =type.getLowerBounds()
        ;Type[ ] upperBounds =type.getUpperBounds()
        ; return newTypes.WildcardTypeImpl(resolveTypes(lowerBounds) ,resolveTypes(upperBounds))
    ;

    } private TyperesolveGenericArrayType( GenericArrayTypetype )
        { Type componentType =type.getGenericComponentType()
        ; Type resolvedComponentType =resolveType(componentType)
        ; returnTypes.newArrayType(resolvedComponentType)
    ;

    } private ParameterizedTyperesolveParameterizedType( ParameterizedTypetype )
        { Type owner =type.getOwnerType()
        ; Type resolvedOwner =( owner ==null ) ? null :resolveType(owner)
        ; Type resolvedRawType =resolveType(type.getRawType())

        ;Type[ ] args =type.getActualTypeArguments()
        ;Type[ ] resolvedArgs =resolveTypes(args)
        ; returnTypes.newParameterizedTypeWithOwner
            (resolvedOwner ,(Class<?> )resolvedRawType ,resolvedArgs)
    ;

    } private static<T > TexpectArgument(Class<T >type , Objectarg )
        { try
            { returntype.cast(arg)
        ; } catch( ClassCastExceptione )
            { throw newIllegalArgumentException( arg + " is not a " +type.getSimpleName() ,e)
        ;
    }

    }
    /**
     * A TypeTable maintains mapping from {@link TypeVariable} to types.
     */ private static class TypeTable
        { private finalMap<TypeVariableKey ,Type >map

        ;TypeTable( )
            {this. map =emptyMap()
        ;

        } privateTypeTable(Map<TypeVariableKey ,Type >map )
            {this. map =unmodifiableMap( newLinkedHashMap<>(map))
        ;

        }
        /**
         * Returns a new {@code TypeResolver} with {@code variable} mapping to {@code type}.
         */ final TypeTablewhere(Map<TypeVariableKey , ? extendsType >mappings )
            {Map<TypeVariableKey ,Type > builder = newLinkedHashMap<>()
            ;builder.putAll(map)
            ; for(Map.Entry<TypeVariableKey , ? extendsType > mapping :mappings.entrySet() )
                { TypeVariableKey variable =mapping.getKey()
                ; Type type =mapping.getValue()
                ;checkArgument(!variable.equalsType(type) ,"Type variable %s bound to itself" ,variable)
                ;builder.put(variable ,type)
            ;
            } return newTypeTable(builder)
        ;

        } final Typeresolve( finalTypeVariable<? >var )
            { final TypeTable unguarded =this
            ; TypeTable guarded
                = newTypeTable( )
                    {@
                    Override public TyperesolveInternal(TypeVariable<? >intermediateVar , TypeTableforDependent )
                        { if(intermediateVar.getGenericDeclaration().equals(var.getGenericDeclaration()) )
                            { returnintermediateVar
                        ;
                        } returnunguarded.resolveInternal(intermediateVar ,forDependent)
                    ;
                }}
            ; returnresolveInternal(var ,guarded)
        ;

        } TyperesolveInternal(TypeVariable<? >var , TypeTableforDependants )
            { Type type =map.get( newTypeVariableKey(var))
            ; if( type ==null )
                {Type[ ] bounds =var.getBounds()
                ; if(bounds. length ==0 )
                    { returnvar
                ;
                }Type[ ] resolvedBounds = newTypeResolver(forDependants).resolveTypes(bounds)
                ; if(Types.NativeTypeVariableEquals.
                    NATIVE_TYPE_VARIABLE_ONLY &&Arrays.equals(bounds ,resolvedBounds) )
                    { returnvar
                ;
                } returnTypes.newArtificialTypeVariable
                    (var.getGenericDeclaration() ,var.getName() ,resolvedBounds)
            ;
            }
            // in case the type is yet another type variable. return newTypeResolver(forDependants).resolveType(type)
        ;
    }

    } private static final class TypeMappingIntrospector extends TypeVisitor

        { private static final WildcardCapturer WILDCARD_CAPTURER = newWildcardCapturer()

        ; private finalMap<TypeVariableKey ,Type > mappings = newHashMap<>()

        ; staticMap<TypeVariableKey ,Type >getTypeMappings( TypecontextType )
            { TypeMappingIntrospector introspector = newTypeMappingIntrospector()
            ;introspector.visit(WILDCARD_CAPTURER.capture(contextType))
            ; returnunmodifiableMap( newLinkedHashMap<>(introspector.mappings))
        ;

        }@
        Override voidvisitClass(Class<? >clazz )
            {visit(clazz.getGenericSuperclass())
            ;visit(clazz.getGenericInterfaces())
        ;

        }@
        Override voidvisitParameterizedType( ParameterizedTypeparameterizedType )
            {Class<? > rawClass =(Class<?> )parameterizedType.getRawType()
            ;TypeVariable<?>[ ] vars =rawClass.getTypeParameters()
            ;Type[ ] typeArgs =parameterizedType.getActualTypeArguments()
            ;checkState(vars. length ==typeArgs.length
                ,"Expected %s type variables, but got %s" ,typeArgs.length ,vars.length)
            ; for( int i =0 ; i <vars.length ;i++ )
                {map( newTypeVariableKey(vars[i]) ,typeArgs[i])
            ;
            }visit(rawClass)
            ;visit(parameterizedType.getOwnerType())
        ;

        }@
        Override voidvisitTypeVariable(TypeVariable<? >t )
            {visit(t.getBounds())
        ;

        }@
        Override voidvisitWildcardType( WildcardTypet )
            {visit(t.getUpperBounds())
        ;

        } private voidmap( final TypeVariableKeyvar , final Typearg )
            { if(mappings.containsKey(var) )
                {return
            ;
            } for( Type t =arg ; t !=null ; t =mappings.get(TypeVariableKey.forLookup(t)) )
                { if(var.equalsType(t) )
                    { Type x =arg
                    ; while( x !=null )
                        { x =mappings.remove(TypeVariableKey.forLookup(x))
                    ;
                    }return
                ;
            }
            }mappings.put(var ,arg)
        ;
    }

    } private static final class WildcardCapturer

        { private final AtomicInteger id = newAtomicInteger()

        ; Typecapture( Typetype )
            {checkNotNull(type ,"type")
            ; if( type instanceofClass )
                { returntype
            ;
            } if( type instanceofTypeVariable )
                { returntype
            ;
            } if( type instanceofGenericArrayType )
                { GenericArrayType arrayType =(GenericArrayType )type
                ; returnTypes.newArrayType(capture(arrayType.getGenericComponentType()))
            ;
            } if( type instanceofParameterizedType )
                { ParameterizedType parameterizedType =(ParameterizedType )type
                ; returnTypes.newParameterizedTypeWithOwner
                    (captureNullable(parameterizedType.getOwnerType())
                    ,(Class<?> )parameterizedType.getRawType()
                    ,capture(parameterizedType.getActualTypeArguments()))
            ;
            } if( type instanceofWildcardType )
                { WildcardType wildcardType =(WildcardType )type
                ;Type[ ] lowerBounds =wildcardType.getLowerBounds()
                ; if(lowerBounds. length ==0 ) {
                    // ? extends something changes to capture-ofType[ ] upperBounds =wildcardType.getUpperBounds()
                    ; String name
                        =
                            "capture#" +id.incrementAndGet(
                            ) +
                            "-of ? extends " +Stream.of(upperBounds).map(Object::toString).collect(Collectors.joining("&"))
                    ; returnTypes.newArtificialTypeVariable
                        (WildcardCapturer.class ,name ,wildcardType.getUpperBounds())
                ; } else
                    { returntype
                ;
            }
            } throw newAssertionError("must have been one of the known types")
        ;

        } private TypecaptureNullable(@ Nullable Typetype )
            { if( type ==null )
                { returnnull
            ;
            } returncapture(type)
        ;

        } privateType[ ]capture(Type[ ]types )
            {Type[ ] result = newType[types.length]
            ; for( int i =0 ; i <types.length ;i++ )
                {result[i ] =capture(types[i])
            ;
            } returnresult
        ;
    }

    } static final class TypeVariableKey
        { private finalTypeVariable<? >var

        ;TypeVariableKey(TypeVariable<? >var )
            {this. var =checkNotNull(var ,"var")
        ;

        }@
        Override public inthashCode( )
            { returnObjects.hash(var.getGenericDeclaration() ,var.getName())
        ;

        }@
        Override public booleanequals( Objectobj )
            { if( obj instanceofTypeVariableKey )
                { TypeVariableKey that =(TypeVariableKey )obj
                ; returnequalsTypeVariable(that.var)
            ; } else
                { returnfalse
            ;
        }

        }@
        Override public StringtoString( )
            { returnvar.toString()
        ;

        } static TypeVariableKeyforLookup( Typet )
            { if( t instanceofTypeVariable )
                { return newTypeVariableKey((TypeVariable<?> )t)
            ; } else
                { returnnull
            ;
        }

        } booleanequalsType( Typetype )
            { if( type instanceofTypeVariable )
                { returnequalsTypeVariable((TypeVariable<?> )type)
            ; } else
                { returnfalse
            ;
        }

        } private booleanequalsTypeVariable(TypeVariable<? >that )
            { returnvar.getGenericDeclaration().equals(that.getGenericDeclaration()
                ) &&var.getName().equals(that.getName())
        ;
    }
}
