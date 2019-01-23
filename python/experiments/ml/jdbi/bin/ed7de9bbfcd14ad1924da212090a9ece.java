/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.mapper.reflect;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException
; importjava.util.ArrayList
; importjava.util.List
; importjava.util.Map
; importjava.util.Optional
; importjava.util.concurrent.ConcurrentHashMap
; importorg.jdbi.v3.core.mapper.ColumnMapper
; importorg.jdbi.v3.core.mapper.Nested
; importorg.jdbi.v3.core.mapper.RowMapper
; importorg.jdbi.v3.core.mapper.RowMapperFactory
; importorg.jdbi.v3.core.mapper.SingleColumnMapper
; importorg.jdbi.v3.core.qualifier.QualifiedType
; importorg.jdbi.v3.core.statement.StatementContext

; import staticorg.jdbi.v3.core.mapper.reflect.ReflectionMapperUtil.anyColumnsStartWithPrefix
; import staticorg.jdbi.v3.core.mapper.reflect.ReflectionMapperUtil.findColumnIndex
; import staticorg.jdbi.v3.core.mapper.reflect.ReflectionMapperUtil.getColumnNames
; import staticorg.jdbi.v3.core.qualifier.Qualifiers.getQualifiers

;
/**
 * A row mapper which maps the columns in a statement into an object, using reflection
 * to set fields on the object. All declared fields of the class and its superclasses
 * may be set. Nested properties are not supported.
 *
 * The mapped class must have a default constructor.
 */ public classFieldMapper<T > implementsRowMapper<T >
    { private static final String DEFAULT_PREFIX =""

    ; private static final String NO_MATCHING_COLUMNS
        ="Mapping fields for type %s didn't find any matching columns in result set"

    ; private static final String UNMATCHED_COLUMNS_STRICT
        ="Mapping type %s could not match fields for columns: %s"

    ; private static final String TYPE_NOT_INSTANTIABLE
        ="A type, %s, was mapped which was not instantiable"
    ; private static final String CANNOT_ACCESS_PROPERTY ="Unable to access property, %s"

    ;
    /**
     * Returns a mapper factory that maps to the given bean class
     *
     * @param type the mapped class
     * @return a mapper factory that maps to the given bean class
     */ public static RowMapperFactoryfactory(Class<? >type )
        { returnRowMapperFactory.of(type ,FieldMapper.of(type))
    ;

    }
    /**
     * Returns a mapper factory that maps to the given bean class
     *
     * @param type the mapped class
     * @param prefix the column name prefix for each mapped field
     * @return a mapper factory that maps to the given bean class
     */ public static RowMapperFactoryfactory(Class<? >type , Stringprefix )
        { returnRowMapperFactory.of(type ,FieldMapper.of(type ,prefix))
    ;

    }
    /**
     * Returns a mapper for the given bean class
     *
     * @param <T> the type to map
     * @param type the mapped class
     * @return a mapper for the given bean class
     */ public static<T >RowMapper<T >of(Class<T >type )
        { returnFieldMapper.of(type ,DEFAULT_PREFIX)
    ;

    }
    /**
     * Returns a mapper for the given bean class
     *
     * @param <T> the type to map
     * @param type the mapped class
     * @param prefix the column name prefix for each mapped field
     * @return a mapper for the given bean class
     */ public static<T >RowMapper<T >of(Class<T >type , Stringprefix )
        { return newFieldMapper<>(type ,prefix)
    ;

    } private finalClass<T >type
    ; private final Stringprefix
    ; private finalMap<Field ,FieldMapper<?> > nestedMappers = newConcurrentHashMap<>()

    ; privateFieldMapper(Class<T >type , Stringprefix )
        {this. type =type
        ;this. prefix =prefix.toLowerCase()
    ;

    }@
    Override public Tmap( ResultSetrs , StatementContextctx ) throws SQLException
        { returnspecialize(rs ,ctx).map(rs ,ctx)
    ;

    }@
    Override publicRowMapper<T >specialize( ResultSetrs , StatementContextctx ) throws SQLException
        { finalList<String > columnNames =getColumnNames(rs)
        ; finalList<ColumnNameMatcher > columnNameMatchers
                =ctx.getConfig(ReflectionMappers.class).getColumnNameMatchers()
        ; finalList<String > unmatchedColumns = newArrayList<>(columnNames)

        ;RowMapper<T > mapper =specialize0(ctx ,columnNames ,columnNameMatchers ,unmatchedColumns
            ).orElseThrow(( ) -> newIllegalArgumentException(String.format(NO_MATCHING_COLUMNS ,type)))

        ; if(ctx.getConfig(ReflectionMappers.class).isStrictMatching(
            ) &&anyColumnsStartWithPrefix(unmatchedColumns ,prefix ,columnNameMatchers) )
            { throw newIllegalArgumentException
                (String.format(UNMATCHED_COLUMNS_STRICT ,type.getSimpleName() ,unmatchedColumns))
        ;

        } returnmapper
    ;

    } privateOptional<RowMapper<T> >specialize0( StatementContextctx
                                               ,List<String >columnNames
                                               ,List<ColumnNameMatcher >columnNameMatchers
                                               ,List<String >unmatchedColumns )
        { finalList<RowMapper<?> > mappers = newArrayList<>()
        ; finalList<Field > fields = newArrayList<>()

        ; for(Class<? > aType =type ; aType !=null ; aType =aType.getSuperclass() )
            { for( Field field :aType.getDeclaredFields() )
                { Nested anno =field.getAnnotation(Nested.class)
                ; if( anno ==null )
                    { String paramName = prefix +paramName(field)

                    ;findColumnIndex(paramName ,columnNames ,columnNameMatchers ,( ) ->debugName(field)
                        ).ifPresent( index ->
                            { QualifiedType type =QualifiedType.of
                                (field.getGenericType()
                                ,getQualifiers(field))
                            ;ColumnMapper<? > mapper =ctx.findColumnMapperFor(type
                                ).orElse((r ,n ,c ) ->r.getObject(n))
                            ;mappers.add( newSingleColumnMapper<>(mapper , index +1))
                            ;fields.add(field)

                            ;unmatchedColumns.remove(columnNames.get(index))
                        ;})
                ; } else
                    { String nestedPrefix = prefix +anno.value().toLowerCase()

                    ; if(anyColumnsStartWithPrefix(columnNames ,nestedPrefix ,columnNameMatchers) )
                        {
                            nestedMappers.computeIfAbsent(field , f -> newFieldMapper<>(field.getType() ,nestedPrefix)
                            ).specialize0(ctx ,columnNames ,columnNameMatchers ,unmatchedColumns
                            ).ifPresent( mapper ->
                                {mappers.add(mapper)
                                ;fields.add(field)
                            ;})
                    ;
                }
            }
        }

        } if(mappers.isEmpty( ) &&!columnNames.isEmpty() )
            { returnOptional.empty()
        ;

        } returnOptional.of((r ,c ) ->
            { T obj =construct()

            ; for( int i =0 ; i <mappers.size() ;i++ )
                {RowMapper<? > mapper =mappers.get(i)
                ; Field field =fields.get(i)

                ; Object value =mapper.map(r ,ctx)
                ;writeField(obj ,field ,value)
            ;

            } returnobj
        ;})
    ;

    } private static StringparamName( Fieldfield )
        { returnOptional.ofNullable(field.getAnnotation(ColumnName.class)
                ).map(ColumnName::value
                ).orElseGet(field::getName)
    ;

    } private StringdebugName( Fieldfield )
        { returnString.format("%s.%s" ,type.getSimpleName() ,field.getName())
    ;

    } private Tconstruct( )
        { try
            { returntype.newInstance()
        ; } catch( Exceptione )
            { throw newIllegalArgumentException(String.format(TYPE_NOT_INSTANTIABLE ,type.getName()) ,e)
        ;
    }

    } private voidwriteField( Tobj , Fieldfield , Objectvalue )
        { try
            {field.setAccessible(true)
            ;field.set(obj ,value)
        ; } catch( IllegalAccessExceptione )
            { throw newIllegalArgumentException(String.format(CANNOT_ACCESS_PROPERTY ,field.getName()) ,e)
        ;
    }
}
