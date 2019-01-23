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
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        indentMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        typeMapper.enableDefaultTyping();
    }

    public static <T> T readValue(File src, Class<T> valueType)
            throws IOException, JsonParseException, JsonMappingException
        { returnmapper.readValue(src ,valueType)
    ;

    } public static<T > TreadValue( Stringcontent ,Class<T >valueType ) throwsIOException ,JsonParseException ,
JsonMappingException

{ return mapper.readValue ( content,valueType ); }publicstatic< T>
        T readValue( Readersrc , Class
    < T>valueType)throwsIOException ,JsonParseException,
JsonMappingException

{ return mapper.readValue ( src,valueType ); }publicstatic< T>
        T readValue( InputStreamsrc , Class
    < T>valueType)throwsIOException ,JsonParseException,
JsonMappingException

{ return mapper.readValue ( src,valueType ); }publicstatic< T>
        T readValue( byte[ ] src
    , Class<T>valueType) throwsIOException,
JsonParseException

, JsonMappingException {returnmapper . readValue(src,valueType ); }publicstatic< T>
        T readValue( Stringcontent , TypeReference
    < T>valueTypeRef)throwsIOException ,JsonParseException,
JsonMappingException

{ return mapper.readValue ( content,valueTypeRef ); }publicstaticMap <String
        , String> readValueAsMap( String content
    ) throwsIOException{TypeReference<HashMap <String,
String

> > typeRef=newTypeReference <HashMap <String, String> > ( )
    {};returnmapper. readValue(content , typeRef ) ;}publicstaticJsonNodereadValueAsTree (Stringcontent)throws IOException
    {return
    mapper .readTree(content); }publicstatic
<

T > T readValueWithTyping(InputStream src, Class < T
    > valueType)throwsIOException{returntypeMapper
.

readValue ( src,valueType ) ;}public staticvoid writeValueIndent(OutputStreamout ,Object value ) throws
    IOException ,JsonGenerationException,JsonMappingException{indentMapper .writeValue(
out

, value ) ;}public staticvoid writeValue (OutputStream
        out ,Object value) throws IOException
    ,JsonGenerationException,JsonMappingException{mapper .writeValue(
out

, value ) ;}public staticString writeValueAsString (Object
        value )throws JsonProcessingException{ return mapper
    .writeValueAsString(value); }publicstatic
byte

[ ] writeValueAsBytes (Objectvalue )throws JsonProcessingException { return
    mapper .writeValueAsBytes(value);}
public

static String writeValueAsIndentString(Object value)throws JsonProcessingException{ return indentMapper .
    writeValueAsString (value);}publicstatic
void

writeValueWithTyping ( OutputStream out,Object value) throws IOException {
    typeMapper .writeValue(out,value)
;

} } 