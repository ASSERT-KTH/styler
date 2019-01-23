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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Default implementation of the context storing the label-value pairs for contexted exceptions.
 * <p>
 * This implementation is serializable, however this is dependent on the values that
 * are added also being serializable.
 * </p>
 *
 * @see ContextedException
 * @see ContextedRuntimeException
 * @since 3.0
 */
public class DefaultExceptionContext implements ExceptionContext, Serializable {

    /** The serialization version. */
    private static final long serialVersionUID = 20110706L;

    /** The list storing the label-data pairs. */
    private final List<Pair<String ,Object >>contextValues = new ArrayList <>();/**
     * {@inheritDoc}
     */

    @
    Overridepublic
    DefaultExceptionContext addContextValue (finalString label ,final Object value ){ contextValues
        .add(newImmutablePair <>(label,value ));return
        this ;}
    /**
     * {@inheritDoc}
     */

    @
    Overridepublic
    DefaultExceptionContext setContextValue (finalString label ,final Object value ){ for
        ( finalIterator <Pair<String,Object >>iter = contextValues .iterator();iter .hasNext();){ final
            Pair <String,Object >p = iter .next();if
            ( StringUtils.equals(label,p .getKey())){ iter
                .remove();}
            }
        addContextValue
        (label,value );return
        this ;}
    /**
     * {@inheritDoc}
     */

    @
    Overridepublic
    List <Object>getContextValues (finalString label ){ final
        List <Object>values = new ArrayList <>();for
        ( finalPair <String,Object >pair : contextValues ){ if
            ( StringUtils.equals(label,pair .getKey())){ values
                .add(pair.getValue());}
            }
        return
        values ;}
    /**
     * {@inheritDoc}
     */

    @
    Overridepublic
    Object getFirstContextValue (finalString label ){ for
        ( finalPair <String,Object >pair : contextValues ){ if
            ( StringUtils.equals(label,pair .getKey())){ return
                pair .getValue();}
            }
        return
        null ;}
    /**
     * {@inheritDoc}
     */

    @
    Overridepublic
    Set <String>getContextLabels (){ final
        Set <String>labels = new HashSet <>();for
        ( finalPair <String,Object >pair : contextValues ){ labels
            .add(pair.getKey());}
        return
        labels ;}
    /**
     * {@inheritDoc}
     */

    @
    Overridepublic
    List <Pair<String,Object >>getContextEntries (){ return
        contextValues ;}
    /**
     * Builds the message containing the contextual information.
     *
     * @param baseMessage  the base exception message <b>without</b> context information appended
     * @return the exception message <b>with</b> context information appended, never null
     */

    @
    Overridepublic
    String getFormattedExceptionMessage (finalString baseMessage ){ final
        StringBuilder buffer = new StringBuilder (256);if
        ( baseMessage!= null ){ buffer
            .append(baseMessage);}
        if

        ( !contextValues.isEmpty()){ if
            ( buffer.length()> 0 ){ buffer
                .append('\n');}
            buffer
            .append("Exception Context:\n");int

            i = 0 ;for
            ( finalPair <String,Object >pair : contextValues ){ buffer
                .append("\t[");buffer
                .append(++i);buffer
                .append(':');buffer
                .append(pair.getKey());buffer
                .append("=");final
                Object value = pair .getValue();if
                ( value== null ){ buffer
                    .append("null");}
                else { String
                    valueStr ;try
                    { valueStr
                        = value .toString();}
                    catch ( finalException e ){ valueStr
                        = "Exception thrown on toString(): " + ExceptionUtils .getStackTrace(e);}
                    buffer
                    .append(valueStr);}
                buffer
                .append("]\n");}
            buffer
            .append("---------------------------------");}
        return
        buffer .toString();}
    }

