/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.io.fs;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OffsetChannelTest
{
    private long offset = 128;
    private StoreChannel actual = mock( StoreChannel.class );

    private OffsetChannel channel = new OffsetChannel( actual, offset );

    private ByteBuffer buf = ByteBuffer.allocate( 0 );
    private ByteBuffer[] buffers = new ByteBuffer[0];

    @Test
    void tryLock() throws Exception
    {
        channel.tryLock();
        verify( actual ).

        tryLock( ) ;}@Testvoidwrite
    (

    )throws
    Exception {channel. write (
    buf
        );verify( actual ).
        write( buf );}@ Test voidwriteAll
    (

    )throws
    Exception {channel. writeAll (
    buf
        );verify( actual ).
        writeAll( buf );}@ Test voidwriteAllWithPosition
    (

    )throws
    Exception {longposition = 500
    ;
        channel . writeAll (buf
        ,position); verify( actual ).
        writeAll( buf ,position+offset ); } @ Test voidread
    (

    )throws
    Exception {channel. read (
    buf
        );verify( actual ).
        read( buf );}@ Test voidforce
    (

    )throws
    Exception {channel. force (
    false
        );verify( actual ).
        force( false );}@ Test voidreadWithPosition
    (

    )throws
    Exception {longposition = 500
    ;
        channel . read (buf
        ,position); verify( actual ).
        read( buf ,position+offset ); } @ Test voidposition
    (

    )throws
    Exception {longposition = 500
    ;
        when ( actual .position
        () ).thenReturn(position );assertEquals( position -offset
        ,channel . position () );verify(actual ).
        position( ) ;}@TestvoidpositionWithPosition
    (

    )throws
    Exception {longposition = 500
    ;
        channel . position (position
        );verify( actual ).
        position( 500 +offset); } @ Test voidsize
    (

    )throws
    Exception {longsize = 256
    ;
        when ( actual .size
        () ).thenReturn(size );assertEquals( 256 -offset
        ,channel . size () );verify(actual ).
        size( ) ;}@Testvoidtruncate
    (

    )throws
    Exception {longsize = 256
    ;
        channel . truncate (size
        );verify( actual ).
        truncate( size +offset); } @ Test voidflush
    (

    )throws
    Exception {channel. flush (
    )
        ;verify(actual).
        flush( ) ;}@TestvoidwriteMultiple
    (

    )throws
    Exception {channel. write (
    buffers
        );verify( actual ).
        write( buffers );}@ Test voidwriteMultipleExtended
    (

    )throws
    Exception {intoff = 16
    ;
        int len = 32;
        channel . write (buffers
        ,off,len ); verify( actual ).
        write( buffers ,off,len ); }@ Test voidreadMultiple
    (

    )throws
    Exception {channel. read (
    buffers
        );verify( actual ).
        read( buffers );}@ Test voidreadMultipleExtended
    (

    )throws
    Exception {intoff = 16
    ;
        int len = 32;
        channel . read (buffers
        ,off,len ); verify( actual ).
        read( buffers ,off,len ); }@ Test voidisOpen
    (

    ){
    channel .isOpen(
    )
        ;verify(actual).
        isOpen( ) ;}@Testvoidclose
    (

    )throws
    Exception {channel. close (
    )
        ;verify(actual).
        close( ) ;}}