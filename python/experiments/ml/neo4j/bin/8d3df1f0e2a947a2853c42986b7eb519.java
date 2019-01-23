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
import org.neo4j.kernel.impl.transaction.log.
files .LogFilesBuilder;importorg.
neo4j .test.rule.TestDirectory;importstaticorg

. junit .Assert.assertEquals;importstaticorg
. mockito .ArgumentMatchers.any;importstaticorg
. mockito .ArgumentMatchers.eq;importstaticorg
. mockito .Mockito.mock;importstaticorg
. mockito .Mockito.never;importstaticorg
. mockito .Mockito.times;importstaticorg
. mockito .Mockito.verify;importstaticorg
. mockito .Mockito.when;importstaticorg
. neo4j .kernel.impl.transaction.log.entry.LogHeader.LOG_HEADER_SIZE;importstaticorg
. neo4j .kernel.impl.transaction.log.entry.LogHeaderWriter.encodeLogVersion;importstaticorg
. neo4j .kernel.impl.transaction.log.entry.LogVersions.CURRENT_LOG_VERSION;publicclassReaderLogVersionBridgeTest

{ @ Rule
public
    finalTestDirectory
    testDirectory = TestDirectory . testDirectory ();privatefinalFileSystemAbstraction
    fs = mock ( FileSystemAbstraction .class );private finalLogVersionedStoreChannel
    channel = mock ( LogVersionedStoreChannel .class );private finallong

    version = 10L ; private LogFileslogFiles
    ; @ Beforepublic

    voidsetUp
    ( ) throwsException{ logFiles =
    prepareLogFiles
        ( ) ;}@Test
    public

    voidshouldOpenTheNextChannelWhenItExists
    ( ) throwsIOException{ // given final
    StoreChannel
        newStoreChannel
        = mock ( StoreChannel .class );final ReaderLogVersionBridgebridge
        = new ReaderLogVersionBridge ( logFiles ); when (channel

        .getVersion ()).thenReturn (version); when (channel
        .getLogFormatVersion ()).thenReturn (CURRENT_LOG_VERSION); when (fs
        .fileExists (any(File .class ))) . thenReturn (true); when (fs
        .open (any(File .class ),eq (OpenMode .READ ))) . thenReturn (newStoreChannel); when (newStoreChannel
        .read (ArgumentMatchers.< ByteBuffer>any())). then (invocationOnMock->{ ByteBuffer buffer
        =
            invocationOnMock . getArgument (0); buffer .putLong
            (encodeLogVersion(version +1 ) ) ; buffer .putLong
            (42); return LOG_HEADER_SIZE;
            } );
        // when finalLogVersionedStoreChannel

        result
        = bridge . next (channel); // then PhysicalLogVersionedStoreChannelexpected

        =
        new PhysicalLogVersionedStoreChannel (
                newStoreChannel ,version +1 , CURRENT_LOG_VERSION ); assertEquals (expected
        ,result ); verify (channel
        ,times (1 )) . close ();}@Test
    public

    voidshouldReturnOldChannelWhenThereIsNoNextChannel
    ( ) throwsIOException{ // given final
    ReaderLogVersionBridge
        bridge
        = new ReaderLogVersionBridge ( logFiles ); when (channel

        .getVersion ()).thenReturn (version); when (fs
        .open (any(File .class ),eq (OpenMode .READ ))) . thenThrow (newFileNotFoundException( ) );// when finalLogVersionedStoreChannel

        result
        = bridge . next (channel); // then assertEquals(

        channel
        ,result ); verify (channel
        ,never () ).close ();}@Test
    public

    voidshouldReturnOldChannelWhenNextChannelHasntGottenCompleteHeaderYet
    ( ) throwsException{ // given final
    ReaderLogVersionBridge
        bridge
        = new ReaderLogVersionBridge ( logFiles ); final StoreChannelnextVersionWithIncompleteHeader
        = mock ( StoreChannel .class );when (nextVersionWithIncompleteHeader
        .read (any(ByteBuffer .class ))) . thenReturn (LOG_HEADER_SIZE/2 ) ; when (channel

        .getVersion ()).thenReturn (version); when (fs
        .fileExists (any(File .class ))) . thenReturn (true); when (fs
        .open (any(File .class ),eq (OpenMode .READ ))) . thenReturn (nextVersionWithIncompleteHeader); // when finalLogVersionedStoreChannel

        result
        = bridge . next (channel); // then assertEquals(

        channel
        ,result ); verify (channel
        ,never () ).close ();}privateLogFiles
    prepareLogFiles

    ( ) throwsIOException{ return LogFilesBuilder
    .
        logFilesBasedOnlyBuilder (testDirectory.directory (),fs). build ();}}