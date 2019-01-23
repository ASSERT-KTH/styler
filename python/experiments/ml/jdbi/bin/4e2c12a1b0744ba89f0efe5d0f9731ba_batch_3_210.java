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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.
concurrent .ConcurrentHashMap;importorg.jdbi.v3.core.
mapper .ColumnMapper;importorg.jdbi.v3.core.
mapper .Nested;importorg.jdbi.v3.core.
mapper .RowMapper;importorg.jdbi.v3.core.
mapper .RowMapperFactory;importorg.jdbi.v3.core.
mapper .SingleColumnMapper;importorg.jdbi.v3.core.
qualifier .QualifiedType;importorg.jdbi.v3.core.

statement . StatementContext;importstaticorg.jdbi.v3.core.mapper.reflect.
ReflectionMapperUtil . anyColumnsStartWithPrefix;importstaticorg.jdbi.v3.core.mapper.reflect.
ReflectionMapperUtil . findColumnIndex;importstaticorg.jdbi.v3.core.mapper.reflect.
ReflectionMapperUtil . getColumnNames;importstaticorg.jdbi.v3.core.qualifier.

Qualifiers
. getQualifiers ;/**
 * A row mapper which maps the columns in a statement into an object, using reflection
 * to set fields on the object. All declared fields of the class and its superclasses
 * may be set. Nested properties are not supported.
 *
 * The mapped class must have a default constructor.
 */publicclass FieldMapper <T>implements RowMapper
    < T > { private static finalString

    DEFAULT_PREFIX = "" ; private static
        finalString

    NO_MATCHING_COLUMNS = "Mapping fields for type %s didn't find any matching columns in result set" ; private static
        finalString

    UNMATCHED_COLUMNS_STRICT = "Mapping type %s could not match fields for columns: %s" ; private static
        finalString
    TYPE_NOT_INSTANTIABLE = "A type, %s, was mapped which was not instantiable" ; private static finalString

    CANNOT_ACCESS_PROPERTY
    = "Unable to access property, %s" ; /**
     * Returns a mapper factory that maps to the given bean class
     *
     * @param type the mapped class
     * @return a mapper factory that maps to the given bean class
     */publicstaticRowMapperFactoryfactory( Class< ?
        > type){returnRowMapperFactory. of(type,FieldMapper.of(
    type

    )
    ) ; } /**
     * Returns a mapper factory that maps to the given bean class
     *
     * @param type the mapped class
     * @param prefix the column name prefix for each mapped field
     * @return a mapper factory that maps to the given bean class
     */publicstaticRowMapperFactoryfactory( Class< ? >type ,
        String prefix){returnRowMapperFactory. of(type,FieldMapper. of(type,
    prefix

    )
    ) ; }/**
     * Returns a mapper for the given bean class
     *
     * @param <T> the type to map
     * @param type the mapped class
     * @return a mapper for the given bean class
     */public static<T> RowMapper<T>of( Class< T
        > type){returnFieldMapper. of(type
    ,

    DEFAULT_PREFIX
    ) ; }/**
     * Returns a mapper for the given bean class
     *
     * @param <T> the type to map
     * @param type the mapped class
     * @param prefix the column name prefix for each mapped field
     * @return a mapper for the given bean class
     */public static<T> RowMapper<T>of( Class< T >type ,
        String prefix ){returnnewFieldMapper< >(type
    ,

    prefix ) ;}privatefinal Class<
    T > type ;private
    final String prefix;privatefinal Map<Field,FieldMapper < ? > >nestedMappers=newConcurrentHashMap<

    > ();privateFieldMapper( Class< T >type ,
        Stringprefix) { this.
        type=type ; this.prefix=prefix.
    toLowerCase

    ()
    ; } @Overridepublic Tmap ( ResultSetrs , StatementContext ctx
        ) throwsSQLException{return specialize(rs,ctx). map(rs
    ,

    ctx)
    ; }@Overridepublic RowMapper<T >specialize ( ResultSetrs , StatementContext ctx
        ) throwsSQLException{final List < String>columnNames=getColumnNames
        ( rs);final List <
                ColumnNameMatcher>columnNameMatchers=ctx.getConfig(ReflectionMappers.class).
        getColumnNameMatchers ();final List < String >unmatchedColumns=newArrayList<>

        (columnNames); RowMapper < T>mapper= specialize0( ctx, columnNames,
            columnNameMatchers,unmatchedColumns). orElseThrow ( ()->newIllegalArgumentException(String. format(NO_MATCHING_COLUMNS,type

        ) ));if(ctx.getConfig(ReflectionMappers.class)
            . isStrictMatching()&& anyColumnsStartWithPrefix( unmatchedColumns,prefix ,
            columnNameMatchers ) ){
                thrownewIllegalArgumentException(String. format(UNMATCHED_COLUMNS_STRICT,type. getSimpleName(),
        unmatchedColumns

        ) );
    }

    return mapper;}privateOptional<RowMapper <T> >specialize0
                                               (StatementContextctx, List<
                                               String>columnNames, List<
                                               ColumnNameMatcher>columnNameMatchers, List< String
        > unmatchedColumns){finalList<RowMapper < ? > >mappers=newArrayList<
        > ();final List < Field >fields=newArrayList<

        > ();for( Class < ?> aType = type; aType != null;aType=aType. getSuperclass
            ( )) { for (Fieldfield:aType. getDeclaredFields
                ( ) ) {Nestedanno=field.getAnnotation(Nested
                . class) ; if( anno
                    == null ) { String paramName=prefix+paramName

                    (field); findColumnIndex( paramName, columnNames, columnNameMatchers ,()->debugName
                        (field)) . ifPresent
                            ( index -> {QualifiedTypetype=
                                QualifiedType.of(field.
                                getGenericType(),getQualifiers(
                            field)); ColumnMapper < ?>mapper=ctx.
                                findColumnMapperFor(type).orElse (( r, n ,c)->r.getObject(
                            n));mappers .add(newSingleColumnMapper< > ( mapper,index+
                            1));fields.add

                            (field);unmatchedColumns.remove(columnNames.get(
                        index))
                ; } )
                    ; } else { String nestedPrefix=prefix+anno.value().

                    toLowerCase ();if( anyColumnsStartWithPrefix( columnNames,nestedPrefix ,
                        columnNameMatchers
                            )){nestedMappers. computeIfAbsent ( field ,f->newFieldMapper<>(field. getType()
                            ,nestedPrefix)). specialize0( ctx, columnNames,
                            columnNameMatchers,unmatchedColumns) . ifPresent
                                (mapper->{mappers.add
                                (mapper);fields.add
                            (field)
                    ;
                }
            )
        ;

        } }}}if(mappers . isEmpty()&&!columnNames. isEmpty
            ( )){returnOptional.
        empty

        ( );}returnOptional.of (( r ,
            c ) -> {Tobj=

            construct () ; for (int i = 0;i<mappers. size() ;
                i++){ RowMapper < ?>mapper=mappers.get
                ( i ) ;Fieldfield=fields.get

                ( i ) ;Objectvalue=mapper. map(r
                ,ctx); writeField( obj,field
            ,

            value );
        }returnobj
    ;

    } ) ; }privatestatic StringparamName (
        Field field){returnOptional.ofNullable(field.getAnnotation(ColumnName
                .class)).map(
                ColumnName::value).orElseGet(field
    ::

    getName ) ;}private StringdebugName (
        Field field){returnString. format("%s.%s",type. getSimpleName(),field.getName
    (

    ) ) ;}private T
        construct (
            ) {try{returntype.
        newInstance ( ); }catch (
            Exception e ){thrownewIllegalArgumentException(String. format(TYPE_NOT_INSTANTIABLE,type.getName ())
        ,
    e

    ) ; }}private voidwriteField ( Tobj , Fieldfield ,
        Object value
            ){try{field.setAccessible
            (true);field. set(obj
        , value ); }catch (
            IllegalAccessException e ){thrownewIllegalArgumentException(String. format(CANNOT_ACCESS_PROPERTY,field.getName ())
        ,
    e
)
