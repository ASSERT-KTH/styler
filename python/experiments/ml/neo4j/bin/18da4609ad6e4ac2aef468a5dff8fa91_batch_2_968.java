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
package org.neo4j.kernel.impl.transaction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.io.fs.OpenMode;
import org.neo4j.io.fs.StoreChannel;
import org.neo4j.kernel.impl.transaction.log.LogVersionedStoreChannel;
import org.neo4j.kernel.impl.transaction.log.PhysicalLogVersionedStoreChannel;
import org.neo4j.kernel.impl.transaction.log.ReaderLogVersionBridge;
import org.neo4j.kernel.impl.transaction.log.files.LogFiles;
import org.neo4j.kernel.impl.transaction.log.files.LogFilesBuilder;
import org.neo4j.test.rule.TestDirectory;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.
never ; importstaticorg.mockito.Mockito.
times ; importstaticorg.mockito.Mockito.
verify ; importstaticorg.mockito.Mockito.when;importstaticorg.neo4j.kernel.
impl . transaction.log.entry.LogHeader.LOG_HEADER_SIZE;importstaticorg.neo4j.kernel.
impl . transaction.log.entry.LogHeaderWriter.encodeLogVersion;importstaticorg.neo4j.kernel.

impl . transaction
.
    log.
    entry . LogVersions . CURRENT_LOG_VERSION ;publicclassReaderLogVersionBridgeTest{@
    Rule public final TestDirectory testDirectory =TestDirectory .testDirectory( );
    private final FileSystemAbstraction fs = mock( FileSystemAbstraction.class );

    private final LogVersionedStoreChannel channel = mock(
    LogVersionedStoreChannel . class)

    ;private
    final long version=10L ; private
    LogFiles
        logFiles ; @Beforepublicvoid
    setUp

    ()
    throws Exception {logFiles= prepareLogFiles (
    )
        ;
        } @ Test public voidshouldOpenTheNextChannelWhenItExists ()throws IOException{
        // given final StoreChannel newStoreChannel = mock( StoreChannel .class

        ); finalReaderLogVersionBridgebridge=new ReaderLogVersionBridge(logFiles) ; when(
        channel. getVersion()). thenReturn(version) ; when(
        channel. getLogFormatVersion()) .thenReturn (CURRENT_LOG_VERSION) ; when (fs.fileExists ( any(
        File. class))) .thenReturn (true) ;when (fs .open( any ( File.class) , eq(
        OpenMode. READ))) .thenReturn(newStoreChannel);when( newStoreChannel .read(ArgumentMatchers . <
        ByteBuffer
            > any ( ))). then (invocationOnMock
            ->{ByteBufferbuffer =invocationOnMock . getArgument ( 0 );
            buffer.putLong( encodeLogVersion (version
            + 1)
        ) ;buffer

        .
        putLong ( 42 ) ;returnLOG_HEADER_SIZE; } );

        // when
        final LogVersionedStoreChannel result
                = bridge. next( channel ) ;// then PhysicalLogVersionedStoreChannel expected=
        newPhysicalLogVersionedStoreChannel (newStoreChannel , version+
        1, CURRENT_LOG_VERSION) ;assertEquals ( expected ,result);verify(
    channel

    ,times
    ( 1 )). close (
    )
        ;
        } @ Test public void shouldReturnOldChannelWhenThereIsNoNextChannel( ) throwsIOException

        {// given finalReaderLogVersionBridgebridge=new ReaderLogVersionBridge(logFiles) ; when(
        channel. getVersion()) .thenReturn (version) ;when (fs .open( any ( File.class) , eq(OpenMode .READ

        )
        ) ) . thenThrow (newFileNotFoundException( ) );

        // when
        finalLogVersionedStoreChannel result= bridge .next
        (channel ); // thenassertEquals( channel,result);verify
    (

    channel,
    never ( )). close (
    )
        ;
        } @ Test public void shouldReturnOldChannelWhenNextChannelHasntGottenCompleteHeaderYet( ) throwsException
        { // given final ReaderLogVersionBridge bridge= newReaderLogVersionBridge( logFiles)
        ;final StoreChannelnextVersionWithIncompleteHeader=mock (StoreChannel .class) ; when (nextVersionWithIncompleteHeader.read ( any ( ByteBuffer.

        class) )).thenReturn( LOG_HEADER_SIZE/2) ; when(
        channel. getVersion()) .thenReturn (version) ; when (fs.fileExists ( any(
        File. class))) .thenReturn (true) ;when (fs .open( any ( File.class) , eq(

        OpenMode
        . READ ) ) ).thenReturn( nextVersionWithIncompleteHeader );

        // when
        finalLogVersionedStoreChannel result= bridge .next
        (channel ); // thenassertEquals( channel,result);verify
    (

    channel , never() ) .
    close
        ( );}private LogFilesprepareLogFiles()throwsIOException { returnLogFilesBuilder.logFilesBasedOnlyBuilder(testDirectory
    .
directory
