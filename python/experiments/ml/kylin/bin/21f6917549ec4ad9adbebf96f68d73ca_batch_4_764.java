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
public class ByteArrayimplementsComparable<ByteArray>
, Serializable{privatestaticfinallong

serialVersionUID
= 1L ; public staticByteArrayallocate(int length )

    { return new ByteArray ( new byte[

    length ] ) ;}public staticByteArray copyOf
        ( byte []array ,intoffset,intlength
    )

    { byte [ ]space=newbyte [length ] ;System . arraycopy( array
        ,offset, space , 0 ,length);return
        newByteArray(space,0 ,length ); }// ============================================================================ privatebyte[
        ] data ;privateintoffset ;private intlength;
    public

    ByteArray

    ( ){this (null
    , 0 ,0
    ) ; }public

    ByteArray (intcapacity )
        {this(new byte[ capacity],
    0

    , capacity); }public ByteArray
        (byte[ ]data){this (data ,0,
    data

    == null?0:data .length )
        ;}publicByteArray (byte [ ] data , int offset ,intlength){
    this

    . data=data;this .offset = offset; this .length =
        length;} public byte[
        ]array( ) {return
        data;} public intoffset
    (

    ) {returnoffset ;}public int
        length ()
    {

    return length ;}//notice this will have a length header public
        void exportData(
    ByteBuffer

    out ) {BytesUtil. writeByteArray
        ( this.
    data

    ,
    this . offset,this .length ,
        out);}publicstaticByteArrayimportData (ByteBufferin) {byte[] bytes=BytesUtil
    .

    readByteArray ( in );return newByteArray (
        bytes); } public ByteBufferasBuffer(){if(
        data == null)returnnull;
    else

    if ( offset==0 &&
        length ==data . length)
            return ByteBuffer.
        wrap ( data) ; else return ByteBuffer . wrap(data,
            offset ,length).slice()
        ;
            } publicbyte[]toBytes( ){ returnBytes.copy(this.
    array

    ( ),this .offset( )
        , this.length());}publicvoid setLength(intpos){ this.length=pos;}
    public

    void reset (byte[ ]data ,
        intoffset, int len)
    {

    this . data=data;this .offset = offset; this .length =
        len;} public byteget
        (inti ) {return
        data[offset + i]
    ;

    } @ Overridepublicint hashCode( )
        { if(data == null){
    return

    0;
    } else {if( length
        <= Bytes. SIZEOF_LONG &&length >
            0 ){
        // to avoid hash collision of byte arrays those are converted from nearby integers/longs, which is the case for kylin dictionary long value
            = BytesUtil. readLong (data, offset , length ); return
                (
                int ) ( value^(value>> >32 ));
                } returnBytes. hashCode( data ,offset ,length) ;}}@
            Override
            public booleanequals(Objectobj) {if (this==
        obj
    )

    returntrue
    ; if (obj== null) return
        false ;if ( getClass(
            ) !=obj
        . getClass( ) )return
            false ;ByteArray
        o =(ByteArray) obj ;if(this.data
            == null&&
        o . data ==null) returntrue
        ; elseif(this . data == null||o . data==
            null )return
        false ; elsereturnBytes. equals ( this .data, this .offset
            , this.
        length
            , o.data,o.offset, o.length) ;}@Override publicintcompareTo( ByteArrayo){ if(this.data
    ==

    null&&
    o . data==null )return 0
        ; elseif(this . data == null)return - 1;
            else if(
        o . data==null) return 1;
            else returnBytes.
        compareTo ( this.data, this .offset
            , this.
        length
            , o.data,o.offset, o.length) ;}publicString toReadableText(){ if(data== null){returnnull
    ;

    } else {returnBytesUtil .
        toHex (data , offset, length
            ) ;}
        } @ Override
            public StringtoString(){if (data ==null)
        return
    null

    ;else
    return Bytes .toStringBinary( data
        , offset, length );
            } }