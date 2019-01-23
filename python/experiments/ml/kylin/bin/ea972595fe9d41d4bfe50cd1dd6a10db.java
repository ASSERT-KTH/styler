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
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {

    private JsonUtil() {
        throw new IllegalStateException("Class JsonUtil is an utility class !");
    }

    // reuse the object mapper to save memory footprint
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper indentMapper = new ObjectMapper();
    private static final ObjectMapper typeMapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);indentMapper.configure( SerializationFeature.INDENT_OUTPUT
        ,true);typeMapper.
    enableDefaultTyping

    ( ) ;}public static <T> TreadValue (Filesrc, Class<
            T >valueType )throws IOException ,
        JsonParseException ,JsonMappingException{returnmapper. readValue(src
    ,

    valueType ) ;}public static <T> TreadValue (Stringcontent, Class<
            T >valueType )throws IOException ,
        JsonParseException ,JsonMappingException{returnmapper. readValue(content
    ,

    valueType ) ;}public static <T> TreadValue (Readersrc, Class<
            T >valueType )throws IOException ,
        JsonParseException ,JsonMappingException{returnmapper. readValue(src
    ,

    valueType ) ;}public static <T> TreadValue (InputStreamsrc, Class<
            T >valueType )throws IOException ,
        JsonParseException ,JsonMappingException{returnmapper. readValue(src
    ,

    valueType ) ;}public static <T>TreadValue (byte []src, Class<
            T >valueType )throws IOException ,
        JsonParseException ,JsonMappingException{returnmapper. readValue(src
    ,

    valueType ) ;}public static <T> TreadValue (Stringcontent, TypeReference<
            T >valueTypeRef )throws IOException ,
        JsonParseException ,JsonMappingException{returnmapper. readValue(content
    ,

    valueTypeRef ) ;}publicstatic Map< String,String >readValueAsMap ( String content
        )throwsIOException{TypeReference< HashMap<String , String > >typeRef=newTypeReference< HashMap<String,String >
        >(
        ) {};returnmapper. readValue(content
    ,

    typeRef ) ; }publicstatic JsonNodereadValueAsTree ( String content
        ) throwsIOException{returnmapper.readTree
    (

    content ) ;}public static <T> TreadValueWithTyping (InputStreamsrc, Class< T > valueType
        ) throwsIOException{returntypeMapper. readValue(src
    ,

    valueType ) ; }publicstatic voidwriteValueIndent ( OutputStreamout
            , Objectvalue )throws IOException ,
        JsonGenerationException,JsonMappingException{indentMapper. writeValue(out
    ,

    value ) ; }publicstatic voidwriteValue ( OutputStreamout
            , Objectvalue )throws IOException ,
        JsonGenerationException,JsonMappingException{mapper. writeValue(out
    ,

    value ) ; }publicstatic StringwriteValueAsString ( Object value
        ) throwsJsonProcessingException{returnmapper.writeValueAsString
    (

    value ) ;}public staticbyte[ ]writeValueAsBytes ( Object value
        ) throwsJsonProcessingException{returnmapper.writeValueAsBytes
    (

    value ) ; }publicstatic StringwriteValueAsIndentString ( Object value
        ) throwsJsonProcessingException{returnindentMapper.writeValueAsString
    (

    value ) ; }publicstatic voidwriteValueWithTyping ( OutputStreamout , Object value
        )throwsIOException{typeMapper. writeValue(out
    ,
value
