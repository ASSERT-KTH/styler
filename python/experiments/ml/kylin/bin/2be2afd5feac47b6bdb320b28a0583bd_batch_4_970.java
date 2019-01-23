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
public class ByteArray implements Comparable<ByteArray>, Serializable {

    private static final long serialVersionUID = 1L;

    public static ByteArray allocate(int length) {
        return new ByteArray(new byte[length]);
    }

    public static ByteArray copyOf(byte[] array, int offset, int length) {
        byte[] space = new byte[length];
        System.arraycopy(array, offset, space, 0, length);
        return new ByteArray(space, 0, length);
    }

    // ============================================================================

    private byte[] data;
    private int offset;
    private int length;

    public ByteArray() {
        this(null, 0, 0);
    }

    public ByteArray(int capacity) {
        this(new byte[capacity], 0, capacity);
    }

    public ByteArray(byte[] data) {
        this(data, 0, data == null ? 0 : data.length);
    }

    public ByteArray(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public byte[] array() {
        return data;
    }

    public int offset() {
        return offset;
    }

    public int length() {
        return length;
    }

    //notice this will have a length header
    public void exportData(ByteBuffer out) {
        BytesUtil.writeByteArray(this.data, this.offset, this.length, out);
    }

    public static ByteArray importData(ByteBuffer in) {
        byte[] bytes = BytesUtil.readByteArray(in);
        return new ByteArray(bytes);
    }

    public ByteBuffer asBuffer() {
        if (data == null)
            return null;
        else if (offset == 0 &&length ==data.length
            ) returnByteBuffer.wrap(data)
        ;
            else returnByteBuffer.wrap(data ,offset ,length).slice()
    ;

    } publicbyte[ ]toBytes( )
        { returnBytes.copy(this.array() ,this.offset() ,this.length())
    ;

    } public voidsetLength( intpos )
        {this. length =pos
    ;

    } public voidreset(byte[ ]data , intoffset , intlen )
        {this. data =data
        ;this. offset =offset
        ;this. length =len
    ;

    } public byteget( inti )
        { returndata[ offset +i]
    ;

    }@
    Override public inthashCode( )
        { if( data ==null )
            { return0
        ; } else
            { if( length <=Bytes. SIZEOF_LONG && length >0 )
                {
                // to avoid hash collision of byte arrays those are converted from nearby integers/longs, which is the case for kylin dictionary long value =BytesUtil.readLong(data ,offset ,length)
                ; return(int )( value ^( value>> >32))
            ;
            } returnBytes.hashCode(data ,offset ,length)
        ;
    }

    }@
    Override public booleanequals( Objectobj )
        { if( this ==obj
            ) returntrue
        ; if( obj ==null
            ) returnfalse
        ; if(getClass( ) !=obj.getClass()
            ) returnfalse
        ; ByteArray o =(ByteArray )obj
        ; if(this. data == null &&o. data ==null
            ) returntrue
        ; else if(this. data == null ||o. data ==null
            ) returnfalse
        ;
            else returnBytes.equals(this.data ,this.offset ,this.length ,o.data ,o.offset ,o.length)
    ;

    }@
    Override public intcompareTo( ByteArrayo )
        { if(this. data == null &&o. data ==null
            ) return0
        ; else if(this. data ==null
            ) return-1
        ; else if(o. data ==null
            ) return1
        ;
            else returnBytes.compareTo(this.data ,this.offset ,this.length ,o.data ,o.offset ,o.length)
    ;

    } public StringtoReadableText( )
        { if( data ==null )
            { returnnull
        ; } else
            { returnBytesUtil.toHex(data ,offset ,length)
        ;
    }

    }@
    Override public StringtoString( )
        { if( data ==null
            ) returnnull
        ;
            else returnBytes.toStringBinary(data ,offset ,length)
    ;

}
