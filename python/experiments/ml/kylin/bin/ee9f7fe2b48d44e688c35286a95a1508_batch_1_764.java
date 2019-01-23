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

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @author yangli9
 */
public class ByteArrayimplementsComparable<ByteArray >,Serializable{privatestatic
final longserialVersionUID=1L;public

static
ByteArray allocate ( int length){returnnew ByteArray (

    new byte [ length ] ) ;}

    public static ByteArray copyOf(byte [] array
        , int offset,int length){byte[]
    space

    = new byte [length];System .arraycopy ( array, offset ,space ,
        0,length ) ; return newByteArray(space,
        0,length);} // ============================================================================private byte[ ]data ;privateint
        offset ; privateintlength; publicByteArray (){
    this

    (

    null ,0, 0)
    ; } publicByteArray
    ( int capacity)

    { this(new byte
        [capacity], 0, capacity);
    }

    public ByteArray(byte [] data
        ){this (data,0, data== null?0
    :

    data .length);} publicByteArray (
        byte[]data ,int offset , int length ) { this.data=data
    ;

    this .offset=offset; this. length =length ; }public byte
        []array ( ){
        returndata; } publicint
        offset() { returnoffset
    ;

    } publicintlength (){ return
        length ;}
    //notice this will have a length header

    public void exportData(ByteBuffer out
        ) {BytesUtil
    .

    writeByteArray ( this.data ,
        this .offset
    ,

    this
    . length ,out) ;} public
        staticByteArrayimportData(ByteBufferin){ byte[]bytes =BytesUtil.readByteArray (in)
    ;

    return new ByteArray (bytes) ;} public
        ByteBufferasBuffer( ) { if(data==null)return
        null ; elseif(offset==
    0

    && length ==data. length
        ) returnByteBuffer . wrap(
            data );
        else return ByteBuffer. wrap ( data , offset ,length).
            slice ();}publicbyte[
        ]
            toBytes (){returnBytes. copy( this.array(),this
    .

    offset (), this.length (
        ) );}publicvoidsetLength(intpos) {this.length=pos ;}publicvoidreset(byte
    [

    ] data ,intoffset ,int len
        ){this . data=
    data

    ; this .offset=offset; this. length =len ; }public byte
        get(int i ){
        returndata[ offset +i
        ];} @ Overridepublic
    int

    hashCode ( ){if (data ==
        null ){return 0 ;}else
    {

    if(
    length <= Bytes.SIZEOF_LONG &&
        length >0 ) {// to avoid hash collision of byte arrays those are converted from nearby integers/longs, which is the case for kylin dictionary long
            value =BytesUtil
        . readLong (
            data ,offset , length); return ( int )( value
                ^
                ( value > >>32)); }return Bytes.hashCode
                ( data,offset ,length ) ;} }@Override publicbooleanequals(
            Object
            obj ){if(this== obj) returntrue;
        if
    (

    obj==
    null ) returnfalse; if( getClass
        ( )!= obj .getClass
            ( ))
        return false; ByteArray o=
            ( ByteArray)
        obj ;if(this . data==null&&o.
            data ==null
        ) return true ;elseif (this
        . data==null|| o . data ==null) return false;
            else returnBytes
        . equals (this.data , this . offset,this . length,
            o .data
        ,
            o .offset,o.length); }@Overridepublic intcompareTo(ByteArray o){if (this.data ==null&&o.
    data

    ==null
    ) return 0;else if( this
        . data==null) return - 1 ;elseif ( o.
            data ==null
        ) return 1;elsereturn Bytes .compareTo
            ( this.data
        , this .offset,this . length,
            o .data
        ,
            o .offset,o.length); }publicStringtoReadableText (){if (data==null ){returnnull ;}else{return
    BytesUtil

    . toHex (data, offset
        , length) ; }} @
            Override publicString
        toString ( )
            { if(data==null) returnnull ;elsereturn
        Bytes
    .

    toStringBinary(
    data , offset,length )
        ; }} 