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
importjava.util.Map;
import com.fasterxml.jackson.

core .JsonGenerationException;importcom.fasterxml.jackson.
core .JsonParseException;importcom.fasterxml.jackson.
core .JsonProcessingException;importcom.fasterxml.jackson.
core .type.TypeReference;importcom.fasterxml.jackson.
databind .DeserializationFeature;importcom.fasterxml.jackson.
databind .JsonMappingException;importcom.fasterxml.jackson.
databind .JsonNode;importcom.fasterxml.jackson.
databind .ObjectMapper;importcom.fasterxml.jackson.
databind .SerializationFeature;publicclassJsonUtil{privateJsonUtil(

) { throw new

    IllegalStateException ("Class JsonUtil is an utility class !") ;
        } // reuse the object mapper to save memory footprint privatestaticfinalObjectMappermapper
    =

    new
    ObjectMapper ( ) ; private static final ObjectMapperindentMapper=new
    ObjectMapper ( ) ; private static final ObjectMappertypeMapper=new
    ObjectMapper ( ) ; static { mapper .configure(DeserializationFeature

    . FAIL_ON_UNKNOWN_PROPERTIES
        ,false);indentMapper.configure( SerializationFeature.INDENT_OUTPUT
        ,true);typeMapper.enableDefaultTyping( );}
        publicstatic<T>T
    readValue

    ( File src,Class < T>valueType )throws IOException,JsonParseException, JsonMappingException{
            return mapper. readValue( src ,
        valueType );}publicstatic< T>T
    readValue

    ( String content,Class < T>valueType )throws IOException,JsonParseException, JsonMappingException{
            return mapper. readValue( content ,
        valueType );}publicstatic< T>T
    readValue

    ( Reader src,Class < T>valueType )throws IOException,JsonParseException, JsonMappingException{
            return mapper. readValue( src ,
        valueType );}publicstatic< T>T
    readValue

    ( InputStream src,Class < T>valueType )throws IOException,JsonParseException, JsonMappingException{
            return mapper. readValue( src ,
        valueType );}publicstatic< T>T
    readValue

    ( byte []src , Class<T>valueType )throws IOException,JsonParseException, JsonMappingException{
            return mapper. readValue( src ,
        valueType );}publicstatic< T>T
    readValue

    ( String content,TypeReference < T>valueTypeRef )throws IOException,JsonParseException, JsonMappingException{
            return mapper. readValue( content ,
        valueTypeRef );}publicstaticMap <String,
    String

    > readValueAsMap (Stringcontent) throwsIOException {TypeReference< HashMap< String , String
        >>typeRef=newTypeReference <HashMap< String , String >>(){} ;returnmapper.readValue (
        content,
        typeRef );}publicstaticJsonNode readValueAsTree(String
    content

    ) throws IOException {returnmapper .readTree ( content )
        ; }publicstatic<T>T
    readValueWithTyping

    ( InputStream src,Class < T>valueType )throws IOException{returntypeMapper .readValue ( src ,
        valueType );}publicstaticvoid writeValueIndent(OutputStream
    out

    , Object value )throwsIOException ,JsonGenerationException , JsonMappingException{
            indentMapper .writeValue (out , value
        );}publicstaticvoid writeValue(OutputStream
    out

    , Object value )throwsIOException ,JsonGenerationException , JsonMappingException{
            mapper .writeValue (out , value
        );}publicstaticString writeValueAsString(Object
    value

    ) throws JsonProcessingException {returnmapper .writeValueAsString ( value )
        ; }publicstaticbyte[]writeValueAsBytes
    (

    Object value )throwsJsonProcessingException {returnmapper .writeValueAsBytes ( value )
        ; }publicstaticStringwriteValueAsIndentString(Object
    value

    ) throws JsonProcessingException {returnindentMapper .writeValueAsString ( value )
        ; }publicstaticvoidwriteValueWithTyping(OutputStream
    out

    , Object value )throwsIOException {typeMapper . writeValue( out , value
        );}}