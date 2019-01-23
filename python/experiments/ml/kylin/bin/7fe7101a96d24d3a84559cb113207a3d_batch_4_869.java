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

package org.apache.kylin.common.persistence;

import java.
io .DataInputStream;importjava.
io .DataOutputStream;importjava.

io .IOException;importorg.apache.commons.

lang.StringUtils;@
SuppressWarnings ( "serial" ) public class StringEntityextendsRootPersistentEntityimplements Comparable

    < StringEntity > {publicstaticfinal Serializer < StringEntity >serializer=newSerializer< StringEntity
        >(
        ) { @Overridepublic voidserialize ( StringEntityobj , DataOutputStream out
            )throwsIOException{out.writeUTF(obj
        .

        str)
        ; } @Overridepublic StringEntitydeserialize ( DataInputStream in
            ) throws IOException {Stringstr=in.
            readUTF ( );returnnewStringEntity
        (
    str)

    ; }}

    ; Stringstr; publicStringEntity (
        Stringstr) { this.
    str

    =str
    ; } @Overridepublic int
        hashCode ( ) { finalint
        prime = 31 ;intresult=super.
        hashCode ( ) ; result = prime*result + (( str == null )?0:str.hashCode
        ( ))
    ;

    returnresult
    ; } @Overridepublic booleanequals (
        Object obj) { if(
            obj ==this
        ) returntrue;if ( !(obj
            instanceof StringEntity)
        ) returnfalse;returnStringUtils.equals( this.str, ((StringEntity)obj)
    .

    str)
    ; } @Overridepublic String
        toString ()
    {

    returnstr
    ; } @Overridepublic intcompareTo (
        StringEntity o){if ( this.
            str ==null) return o . str == null?0
        : -1;if ( o.
            str ==null

        ) return1;returnthis.str.compareTo(o
    .
str
