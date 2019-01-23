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
import java.io.OutputStream;importjava
. io.Reader;importjava
. util.HashMap;importjava
. util.Map;importcom

. fasterxml.jackson.core.JsonGenerationException;importcom
. fasterxml.jackson.core.JsonParseException;importcom
. fasterxml.jackson.core.JsonProcessingException;importcom
. fasterxml.jackson.core.type.TypeReference;importcom
. fasterxml.jackson.databind.DeserializationFeature;importcom
. fasterxml.jackson.databind.JsonMappingException;importcom
. fasterxml.jackson.databind.JsonNode;importcom
. fasterxml.jackson.databind.ObjectMapper;importcom
. fasterxml.jackson.databind.SerializationFeature;publicclass

JsonUtil { private JsonUtil

    ( ){throw new
        IllegalStateException ( "Class JsonUtil is an utility class !");}// reuse the object mapper to save memory footprint
    private

    static
    final ObjectMapper mapper = new ObjectMapper ( );privatestatic
    final ObjectMapper indentMapper = new ObjectMapper ( );privatestatic
    final ObjectMapper typeMapper = new ObjectMapper ( );static{

    mapper .
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false) ;indentMapper.
        configure(SerializationFeature.INDENT_OUTPUT,true) ;typeMapper.
        enableDefaultTyping();}public
    static

    < T >TreadValue ( Filesrc, Class< T>valueType) throwsIOException
            , JsonParseException, JsonMappingException{ return mapper
        . readValue(src,valueType) ;}public
    static

    < T >TreadValue ( Stringcontent, Class< T>valueType) throwsIOException
            , JsonParseException, JsonMappingException{ return mapper
        . readValue(content,valueType) ;}public
    static

    < T >TreadValue ( Readersrc, Class< T>valueType) throwsIOException
            , JsonParseException, JsonMappingException{ return mapper
        . readValue(src,valueType) ;}public
    static

    < T >TreadValue ( InputStreamsrc, Class< T>valueType) throwsIOException
            , JsonParseException, JsonMappingException{ return mapper
        . readValue(src,valueType) ;}public
    static

    < T >TreadValue ( byte[]src, Class< T>valueType) throwsIOException
            , JsonParseException, JsonMappingException{ return mapper
        . readValue(src,valueType) ;}public
    static

    < T >TreadValue ( Stringcontent, TypeReference< T>valueTypeRef) throwsIOException
            , JsonParseException, JsonMappingException{ return mapper
        . readValue(content,valueTypeRef) ;}public
    static

    Map < String,String> readValueAsMap( Stringcontent) throwsIOException { TypeReference <
        HashMap<String,String> >typeRef= new TypeReference < HashMap<String,String> >(){} ;
        returnmapper
        . readValue(content,typeRef) ;}public
    static

    JsonNode readValueAsTree ( Stringcontent) throwsIOException { return mapper
        . readTree(content);}public
    static

    < T >TreadValueWithTyping ( InputStreamsrc, Class< T>valueType) throwsIOException { return typeMapper
        . readValue(src,valueType) ;}public
    static

    void writeValueIndent ( OutputStreamout, Objectvalue ) throwsIOException
            , JsonGenerationException, JsonMappingException{ indentMapper .
        writeValue(out,value) ;}public
    static

    void writeValue ( OutputStreamout, Objectvalue ) throwsIOException
            , JsonGenerationException, JsonMappingException{ mapper .
        writeValue(out,value) ;}public
    static

    String writeValueAsString ( Objectvalue) throwsJsonProcessingException { return mapper
        . writeValueAsString(value);}public
    static

    byte [ ]writeValueAsBytes( Objectvalue) throwsJsonProcessingException { return mapper
        . writeValueAsBytes(value);}public
    static

    String writeValueAsIndentString ( Objectvalue) throwsJsonProcessingException { return indentMapper
        . writeValueAsString(value);}public
    static

    void writeValueWithTyping ( OutputStreamout, Objectvalue ) throwsIOException { typeMapper .
        writeValue(out,value) ;}}
    