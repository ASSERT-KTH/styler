/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.kylin.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util

.
HashMap ;importjava.util.
Map ;importcom.fasterxml.

jackson .core.JsonGenerationException;importcom.fasterxml.
jackson .core.JsonParseException;importcom.fasterxml.
jackson .core.JsonProcessingException;importcom.fasterxml.
jackson .core.type.TypeReference;importcom.fasterxml.
jackson .databind.DeserializationFeature;importcom.fasterxml.
jackson .databind.JsonMappingException;importcom.fasterxml.
jackson .databind.JsonNode;importcom.fasterxml.
jackson .databind.ObjectMapper;importcom.fasterxml.
jackson .databind.SerializationFeature;publicclassJsonUtil{private

JsonUtil ( ) {

    throw newIllegalStateException( "Class JsonUtil is an utility class !"
        ) ; }// reuse the object mapper to save memory footprintprivatestaticfinal
    ObjectMapper

    mapper
    = new ObjectMapper ( ) ; private staticfinalObjectMapperindentMapper
    = new ObjectMapper ( ) ; private staticfinalObjectMappertypeMapper
    = new ObjectMapper ( ) ; static {mapper.configure

    ( DeserializationFeature
        .FAIL_ON_UNKNOWN_PROPERTIES,false);indentMapper. configure(SerializationFeature
        .INDENT_OUTPUT,true);typeMapper. enableDefaultTyping()
        ;}publicstatic<T
    >

    T readValue (Filesrc , Class<T >valueType )throwsIOException, JsonParseException,
            JsonMappingException {return mapper. readValue (
        src ,valueType);}public static<T
    >

    T readValue (Stringcontent , Class<T >valueType )throwsIOException, JsonParseException,
            JsonMappingException {return mapper. readValue (
        content ,valueType);}public static<T
    >

    T readValue (Readersrc , Class<T >valueType )throwsIOException, JsonParseException,
            JsonMappingException {return mapper. readValue (
        src ,valueType);}public static<T
    >

    T readValue (InputStreamsrc , Class<T >valueType )throwsIOException, JsonParseException,
            JsonMappingException {return mapper. readValue (
        src ,valueType);}public static<T
    >

    T readValue (byte[ ] src,Class<T >valueType )throwsIOException, JsonParseException,
            JsonMappingException {return mapper. readValue (
        src ,valueType);}public static<T
    >

    T readValue (Stringcontent , TypeReference<T >valueTypeRef )throwsIOException, JsonParseException,
            JsonMappingException {return mapper. readValue (
        content ,valueTypeRef);}public staticMap<
    String

    , String >readValueAsMap(String content) throwsIOException{ TypeReference< HashMap < String
        ,String>>typeRef= newTypeReference< HashMap < String ,String>>() {};returnmapper .
        readValue(
        content ,typeRef);}public staticJsonNodereadValueAsTree
    (

    String content ) throwsIOException{ returnmapper . readTree (
        content );}publicstatic<T
    >

    T readValueWithTyping (InputStreamsrc , Class<T >valueType )throwsIOException{ returntypeMapper . readValue (
        src ,valueType);}public staticvoidwriteValueIndent
    (

    OutputStream out , Objectvalue) throwsIOException , JsonGenerationException,
            JsonMappingException {indentMapper .writeValue ( out
        ,value);}public staticvoidwriteValue
    (

    OutputStream out , Objectvalue) throwsIOException , JsonGenerationException,
            JsonMappingException {mapper .writeValue ( out
        ,value);}public staticStringwriteValueAsString
    (

    Object value ) throwsJsonProcessingException{ returnmapper . writeValueAsString (
        value );}publicstaticbyte[
    ]

    writeValueAsBytes ( Objectvalue) throwsJsonProcessingException{ returnmapper . writeValueAsBytes (
        value );}publicstaticStringwriteValueAsIndentString
    (

    Object value ) throwsJsonProcessingException{ returnindentMapper . writeValueAsString (
        value );}publicstaticvoidwriteValueWithTyping
    (

    OutputStream out , Objectvalue) throwsIOException { typeMapper. writeValue ( out
        ,value);}} 