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
package org.neo4j.bolt.v1.packstream;importjava

. io.IOException;/**
 * This is what {@link PackStream} uses to ingest data, implement this on top of any data source of your choice to
 * deserialize the stream with {@link PackStream}.
 */public

interface
PackInput { /** Consume one byte */
byte
    readByte
    ( )throwsIOException ; /** Consume a 2-byte signed integer */short

    readShort
    ( )throwsIOException ; /** Consume a 4-byte signed integer */int

    readInt
    ( )throwsIOException ; /** Consume an 8-byte signed integer */long

    readLong
    ( )throwsIOException ; /** Consume an 8-byte IEEE 754 "double format" floating-point number */double

    readDouble
    ( )throwsIOException ; /** Consume a specified number of bytes */PackInput

    readBytes
    ( byte[ ]into, intoffset , inttoRead ) throws IOException ; /** Get the next byte without forwarding the internal pointer */byte

    peekByte
    ( )throwsIOException ; }