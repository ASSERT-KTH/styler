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
import com.fasterxml.jackson.databind.
SerializationFeature

;
public class JsonUtil { private JsonUtil ( ){thrownew
IllegalStateException ( "Class JsonUtil is an utility class !" ) ; } // reuse the object mapper to save memory footprint privatestaticfinalObjectMapper
mapper = new ObjectMapper ( ) ; privatestaticfinalObjectMapper

indentMapper =
newObjectMapper();privatestaticfinal ObjectMappertypeMapper=
newObjectMapper();static{mapper .configure(
DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false)
;

indentMapper . configure(SerializationFeature . INDENT_OUTPUT,true ); typeMapper.enableDefaultTyping( );
    } publicstatic <T > T
readValue (Filesrc,Class< T>valueType
)

throws IOException ,JsonParseException, JsonMappingException {returnmapper .readValue (src,valueType );
    } publicstatic <T > T
readValue (Stringcontent,Class< T>valueType
)

throws IOException ,JsonParseException, JsonMappingException {returnmapper .readValue (content,valueType );
    } publicstatic <T > T
readValue (Readersrc,Class< T>valueType
)

throws IOException ,JsonParseException, JsonMappingException {returnmapper .readValue (src,valueType );
    } publicstatic <T > T
readValue (InputStreamsrc,Class< T>valueType
)

throws IOException ,JsonParseException, JsonMappingException {returnmapper.readValue (src ,valueType); }public
    static <T >T readValue (
byte []src,Class< T>valueType
)

throws IOException ,JsonParseException, JsonMappingException {returnmapper .readValue (src,valueType );
    } publicstatic <T > T
readValue (Stringcontent,TypeReference< T>valueTypeRef
)

throws IOException ,JsonParseException,JsonMappingException {return mapper.readValue (content , valueTypeRef )
;}publicstaticMap< String,String > readValueAsMap ( Stringcontent)throwsIOException{ TypeReference<HashMap<String ,
String>
> typeRef=newTypeReference<HashMap <String,
String

> > ( ){} ;return mapper . readValue
( content,typeRef);}public
static

JsonNode readValueAsTree (Stringcontent ) throwsIOException{ returnmapper .readTree(content ); } public static
< T>TreadValueWithTyping(InputStream src,Class
<

T > valueType )throwsIOException {return typeMapper .readValue
    ( src, valueType) ; }
publicstaticvoidwriteValueIndent(OutputStream out,Object
value

) throws IOException ,JsonGenerationException, JsonMappingException{ indentMapper .writeValue
    ( out, value) ; }
publicstaticvoidwriteValue(OutputStream out,Object
value

) throws IOException ,JsonGenerationException, JsonMappingException{ mapper . writeValue
( out,value);}public
static

String writeValueAsString (Objectvalue )throwsJsonProcessingException {return mapper . writeValueAsString
( value);}publicstaticbyte
[

] writeValueAsBytes ( Objectvalue) throwsJsonProcessingException { return mapper
. writeValueAsBytes(value);}public
static

String writeValueAsIndentString ( Objectvalue) throwsJsonProcessingException { returnindentMapper . writeValueAsString (
value);}publicstatic voidwriteValueWithTyping(
OutputStream
out
