/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.exception;

import java.io.Serializable;
import java.util.
ArrayList ;importjava.util.
HashSet ;importjava.util.
Iterator ;importjava.util.
List ;importjava.util.

Set ;importorg.apache.commons.lang3.
StringUtils ;importorg.apache.commons.lang3.tuple.
ImmutablePair ;importorg.apache.commons.lang3.tuple.

Pair
; /**
 * Default implementation of the context storing the label-value pairs for contexted exceptions.
 * <p>
 * This implementation is serializable, however this is dependent on the values that
 * are added also being serializable.
 * </p>
 *
 * @see ContextedException
 * @see ContextedRuntimeException
 * @since 3.0
 */ public class DefaultExceptionContextimplements ExceptionContext ,

    Serializable
    { /** The serialization version. */ private static final long serialVersionUID=

    20110706L
    ; /** The list storing the label-data pairs. */ privatefinalList<Pair< String,Object > > contextValues =newArrayList<>(

    )
    ;/**
     * {@inheritDoc}
     */
    @ Override publicDefaultExceptionContextaddContextValue ( finalString label , finalObject value
        ){contextValues.add (newImmutablePair<>( label,value)
        ) ;return
    this

    ;
    }/**
     * {@inheritDoc}
     */
    @ Override publicDefaultExceptionContextsetContextValue ( finalString label , finalObject value
        ) {for (finalIterator<Pair< String,Object > > iter=contextValues.iterator( );iter.hasNext() ;
            ) {finalPair< String, Object > p=iter.next(
            ) ;if(StringUtils.equals( label,p.getKey() )
                ){iter.remove(
            )
        ;
        }}addContextValue( label,value
        ) ;return
    this

    ;
    }/**
     * {@inheritDoc}
     */
    @ OverridepublicList< Object>getContextValues ( finalString label
        ) {finalList< Object > values =newArrayList<>(
        ) ;for (finalPair< String, Object > pair: contextValues
            ) {if(StringUtils.equals( label,pair.getKey() )
                ){values.add(pair.getValue()
            )
        ;
        } }return
    values

    ;
    }/**
     * {@inheritDoc}
     */
    @ Override publicObjectgetFirstContextValue ( finalString label
        ) {for (finalPair< String, Object > pair: contextValues
            ) {if(StringUtils.equals( label,pair.getKey() )
                ) {returnpair.getValue(
            )
        ;
        } }return
    null

    ;
    }/**
     * {@inheritDoc}
     */
    @ OverridepublicSet< String>getContextLabels (
        ) {finalSet< String > labels =newHashSet<>(
        ) ;for (finalPair< String, Object > pair: contextValues
            ){labels.add(pair.getKey()
        )
        ; }return
    labels

    ;
    }/**
     * {@inheritDoc}
     */
    @ OverridepublicList<Pair< String,Object >>getContextEntries (
        ) {return
    contextValues

    ;
    }/**
     * Builds the message containing the contextual information.
     *
     * @param baseMessage  the base exception message <b>without</b> context information appended
     * @return the exception message <b>with</b> context information appended, never null
     */
    @ Override publicStringgetFormattedExceptionMessage ( finalString baseMessage
        ) { final StringBuilder buffer =newStringBuilder(256
        ) ;if ( baseMessage!= null
            ){buffer.append(baseMessage
        )

        ; }if(!contextValues.isEmpty( )
            ) {if(buffer.length ( )> 0
                ){buffer.append('\n'
            )
            ;}buffer.append("Exception Context:\n"

            ) ; int i=
            0 ;for (finalPair< String, Object > pair: contextValues
                ){buffer.append("\t["
                );buffer.append(++i
                );buffer.append(':'
                );buffer.append(pair.getKey()
                );buffer.append("="
                ) ; final Object value=pair.getValue(
                ) ;if ( value== null
                    ){buffer.append("null"
                ) ; }
                    else {String
                    valueStr ;
                        try { valueStr=value.toString(
                    ) ; }catch ( finalException e
                        ) { valueStr = "Exception thrown on toString(): "+ExceptionUtils.getStackTrace(e
                    )
                    ;}buffer.append(valueStr
                )
                ;}buffer.append("]\n"
            )
            ;}buffer.append("---------------------------------"
        )
        ; }returnbuffer.toString(
    )

;
