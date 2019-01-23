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

importjava

. io.Serializable;importjava
. nio.ByteBuffer;/**
 * @author yangli9
 */public

class
ByteArray implements Comparable < ByteArray>,Serializable{ private static

    final long serialVersionUID = 1L ; publicstatic

    ByteArray allocate ( intlength) {return new
        ByteArray ( newbyte[ length]);}public
    static

    ByteArray copyOf ( byte[]array, intoffset , intlength ) {byte [
        ]space= new byte [ length];System.
        arraycopy(array,offset, space, 0, length) ;returnnew
        ByteArray ( space,0, length) ;}// ============================================================================
    private

    byte

    [ ]data; privateint
    offset ; privateint
    length ; publicByteArray

    ( ){this (
        null,0, 0) ;}public
    ByteArray

    ( intcapacity) {this (
        newbyte[ capacity],0, capacity) ;}public
    ByteArray

    ( byte[]data) {this (
        data,0, data== null ? 0 : data . length);}public
    ByteArray

    ( byte[]data, intoffset , intlength ) {this .
        data=data ; this.
        offset=offset ; this.
        length=length ; }public
    byte

    [ ]array( ){return data
        ; }public
    int

    offset ( ){return offset
        ; }public
    int

    length ( ){return length
        ; }//notice this will have a length header
    public

    void
    exportData ( ByteBufferout) {BytesUtil .
        writeByteArray(this.data,this. offset,this. length,out) ;}public
    static

    ByteArray importData ( ByteBufferin) {byte [
        ]bytes= BytesUtil . readByteArray(in);returnnew
        ByteArray ( bytes);}public
    ByteBuffer

    asBuffer ( ){if (
        data ==null ) returnnull
            ; elseif
        ( offset ==0 && length == data . length)returnByteBuffer
            . wrap(data);elsereturn
        ByteBuffer
            . wrap(data,offset, length) .slice();}public
    byte

    [ ]toBytes( ){return Bytes
        . copy(this.array(),this. offset(),this. length());}public
    void

    setLength ( intpos) {this .
        length=pos ; }public
    void

    reset ( byte[]data, intoffset , intlen ) {this .
        data=data ; this.
        offset=offset ; this.
        length=len ; }public
    byte

    get ( inti) {return data
        [ offset+i ] ;}@
    Override

    publicint
    hashCode ( ){if (
        data ==null ) {return 0
            ; }else
        { if (
            length <=Bytes . SIZEOF_LONG&&length > 0 ) {// to avoid hash collision of byte arrays those are converted from nearby integers/longs, which is the case for kylin dictionary long
                value
                = BytesUtil . readLong(data,offset, length) ;return(
                int )(value ^( value >> >32) );}return
            Bytes
            . hashCode(data,offset, length) ;}}
        @
    Override

    publicboolean
    equals ( Objectobj) {if (
        this ==obj ) returntrue
            ; if(
        obj ==null ) returnfalse
            ; if(
        getClass ()!=obj . getClass())returnfalse
            ; ByteArrayo
        = ( ByteArray )obj; if(
        this .data==null && o . data==null ) returntrue
            ; elseif
        ( this .data==null || o . data==null ) returnfalse
            ; elsereturn
        Bytes
            . equals(this.data,this. offset,this. length,o. data,o. offset,o. length);}@
    Override

    publicint
    compareTo ( ByteArrayo) {if (
        this .data==null && o . data==null ) return0
            ; elseif
        ( this .data==null ) return-
            1 ;elseif
        ( o .data==null ) return1
            ; elsereturn
        Bytes
            . compareTo(this.data,this. offset,this. length,o. data,o. offset,o. length);}public
    String

    toReadableText ( ){if (
        data ==null ) {return null
            ; }else
        { return BytesUtil
            . toHex(data,offset, length) ;}}
        @
    Override

    publicString
    toString ( ){if (
        data ==null ) returnnull
            ; elsereturn
        Bytes
            . toStringBinary(data,offset, length) ;}}
    