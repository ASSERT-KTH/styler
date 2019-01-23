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
import com.fasterxml.jackson.core.type.
TypeReference;importcom.fasterxml.jackson.databind
. DeserializationFeature;importcom.fasterxml.jackson.databind
. JsonMappingException;importcom.fasterxml.jackson.databind
. JsonNode;importcom.fasterxml.jackson.databind
. ObjectMapper;importcom.fasterxml.jackson.databind

. SerializationFeature ; public

    class JsonUtil{private JsonUtil
        ( ) {thrownewIllegalStateException(
    "Class JsonUtil is an utility class !"

    )
    ; } // reuse the object mapper to save memory footprint private static final ObjectMapper mapper=newObjectMapper
    ( ) ; private static final ObjectMapper indentMapper=newObjectMapper
    ( ) ; private static final ObjectMapper typeMapper=newObjectMapper

    ( )
        ;static{mapper.configure(DeserializationFeature .FAIL_ON_UNKNOWN_PROPERTIES,
        false);indentMapper.configure(SerializationFeature .INDENT_OUTPUT,
        true);typeMapper.enableDefaultTyping
    (

    ) ; }publicstatic < T>T readValue( Filesrc,Class <T
            > valueType) throwsIOException , JsonParseException
        , JsonMappingException{returnmapper.readValue (src,
    valueType

    ) ; }publicstatic < T>T readValue( Stringcontent,Class <T
            > valueType) throwsIOException , JsonParseException
        , JsonMappingException{returnmapper.readValue (content,
    valueType

    ) ; }publicstatic < T>T readValue( Readersrc,Class <T
            > valueType) throwsIOException , JsonParseException
        , JsonMappingException{returnmapper.readValue (src,
    valueType

    ) ; }publicstatic < T>T readValue( InputStreamsrc,Class <T
            > valueType) throwsIOException , JsonParseException
        , JsonMappingException{returnmapper.readValue (src,
    valueType

    ) ; }publicstatic < T>TreadValue( byte[ ]src,Class <T
            > valueType) throwsIOException , JsonParseException
        , JsonMappingException{returnmapper.readValue (src,
    valueType

    ) ; }publicstatic < T>T readValue( Stringcontent,TypeReference <T
            > valueTypeRef) throwsIOException , JsonParseException
        , JsonMappingException{returnmapper.readValue (content,
    valueTypeRef

    ) ; }publicstaticMap <String ,String> readValueAsMap( String content )
        throwsIOException{TypeReference<HashMap <String, String > > typeRef=newTypeReference<HashMap <String,String> >
        ()
        { };returnmapper.readValue (content,
    typeRef

    ) ; } publicstaticJsonNode readValueAsTree( String content )
        throws IOException{returnmapper.readTree(
    content

    ) ; }publicstatic < T>T readValueWithTyping( InputStreamsrc,Class <T > valueType )
        throws IOException{returntypeMapper.readValue (src,
    valueType

    ) ; } publicstaticvoid writeValueIndent( OutputStream out,
            Object value) throwsIOException , JsonGenerationException
        ,JsonMappingException{indentMapper.writeValue (out,
    value

    ) ; } publicstaticvoid writeValue( OutputStream out,
            Object value) throwsIOException , JsonGenerationException
        ,JsonMappingException{mapper.writeValue (out,
    value

    ) ; } publicstaticString writeValueAsString( Object value )
        throws JsonProcessingException{returnmapper.writeValueAsString(
    value

    ) ; }publicstatic byte[] writeValueAsBytes( Object value )
        throws JsonProcessingException{returnmapper.writeValueAsBytes(
    value

    ) ; } publicstaticString writeValueAsIndentString( Object value )
        throws JsonProcessingException{returnindentMapper.writeValueAsString(
    value

    ) ; } publicstaticvoid writeValueWithTyping( OutputStream out, Object value )
        throwsIOException{typeMapper.writeValue (out,
    value
)
