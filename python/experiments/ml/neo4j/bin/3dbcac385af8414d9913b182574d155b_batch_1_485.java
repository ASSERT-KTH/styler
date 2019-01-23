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
package org.neo4j.bolt.v1.transport.integration;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import org.neo4j.bolt.messaging.Neo4jPack;
import org.neo4j.bolt.messaging.RequestMessage;
import org.neo4j.bolt.messaging.ResponseMessage;
import org.neo4j.bolt.v1.transport.socket.client.TransportConnection;
import org.neo4j.function.Predicates;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.neo4j.bolt.v1.messaging.util.MessageMatchers.responseMessage;
import static org.neo4j.bolt.v1.messaging.util.MessageMatchers.serialize;

public class TransportTestUtil
{
    protected final Neo4jPack neo4jPack;
    private final MessageEncoder messageEncoder;

    public TransportTestUtil( Neo4jPack neo4jPack )
    {
        this( neo4jPack, new MessageEncoderV1() );
    }

    public TransportTestUtil( Neo4jPack neo4jPack, MessageEncoder messageEncoder )
    {
        this.neo4jPack = neo4jPack;
        this.messageEncoder = messageEncoder;
    }

    public Neo4jPack getNeo4jPack()
    {
        return neo4jPack;
    }

    public byte[] chunk( RequestMessage... messages ) throws IOException
    {
        return chunk( 32, messages );
    }

    public byte[] chunk( ResponseMessage... messages ) throws IOException
    {
        return chunk( 32, messages );
    }

    public byte[] chunk( int chunkSize, RequestMessage... messages ) throws IOException
    {
        byte[][] serializedMessages = new byte[messages.length][];
        for ( int i = 0; i < messages.length; i++ )
        {
            serializedMessages[i] = messageEncoder.encode( neo4jPack, messages[i] );
        }
        return chunk( chunkSize, serializedMessages );
    }

    public byte[] chunk( int chunkSize, ResponseMessage... messages ) throws IOException
    {
        byte[][]serializedMessages
        = new byte [ messages .length ] [ ];for( inti =
        0
            ;i<messages . length; i++ ){serializedMessages[ i]
        =
        serialize (neo4jPack ,messages [ i]
    )

    ; }returnchunk (chunkSize , serializedMessages) ;}publicbyte [ ]
    chunk
        ( int chunkSize ,byte[] ... messages){ByteBuffer output =ByteBuffer

        . allocate (10000) . order ( BIG_ENDIAN
        )
            ; for ( byte[]wholeMessage
            : messages ) { int left
            =
                wholeMessage . length ;while(left >0 ) {int
                size=Math. min(left , chunkSize)

                ; output . putShort(( short )size
                );intoffset =wholeMessage .length - left;

                output . put(
            wholeMessage
            ,offset,size );left -= size;
        }

        output.putShort((short

        )0) ; } output .flip();byte[]arrayOutput
        =newbyte[ output .limit
        ( )]
    ;

    output .get( arrayOutput);
    return
        arrayOutput ;} publicbyte[]defaultAcceptedVersions( ){ returnacceptedVersions ( neo4jPack.
    version

    ( ),0 ,0 , 0) ; }public byte [] acceptedVersions ( long
    option1
        , long option2 ,longoption3, long option4 ){ByteBuffer bb=ByteBuffer. allocate (5
        *Integer.BYTES ) .order
        (BIG_ENDIAN); bb.putInt ( 0x6060B017)
        ;bb.putInt ((int ) option1)
        ;bb.putInt ((int ) option2)
        ;bb.putInt ((int ) option3)
        ; bb.putInt((int
    )

    option4)
    ; return bb.array( ); } @SafeVarargspublicfinalMatcher < TransportConnection
    >
        eventuallyReceives ( finalMatcher<ResponseMessage>...
        messages
            ){
            return new TypeSafeMatcher< TransportConnection > (
            )
                {
                @
                    Override protected booleanmatchesSafely(TransportConnection conn ) { try
                    {
                        for ( Matcher < ResponseMessage> matchesMessage :messages
                        ){ finalResponseMessage message =receiveOneResponseMessage
                    (
                    conn );
                assertThat
                ( message , matchesMessage )
                ;
                    } return true; } catch(
                Exception
            e

            ){
            throw new RuntimeException( e ) ;
            }
                }@Overridepublic voiddescribeTo (Description description) { description.
            appendValueList
        ("Messages["
    ,

    "," , "]", messages ) ; } };
            }
    public
        ResponseMessage receiveOneResponseMessage ( TransportConnection conn)throwsIOException
        , InterruptedException { ByteArrayOutputStream
        bytes
            = new ByteArrayOutputStream () ; while(

            true ) { int size =
            receiveChunkHeader
                (conn) ; if (size>0 ) {byte
                []received= conn .recv
            (
            size
            )
                ; bytes. write( received);}else {return
            responseMessage
        (
    neo4jPack

    , bytes .toByteArray ( ) ) ; }} }
    public
        intreceiveChunkHeader( TransportConnection conn )throwsIOException, InterruptedException {byte
        [ ]raw=conn.recv ( 2) ; return ( (raw[0] & 0xff)<< 8 |(
    raw

    [ 1]&0xff ))&
    0xffff
        ; }public Matcher <TransportConnection>eventuallyReceivesSelectedProtocolVersion() {return eventuallyReceives( newbyte[ ]{0,0, 0,
    (

    byte ) neo4jPack.version( )} ) ;}public static Matcher
    <
        TransportConnection > eventuallyReceives(finalbyte[]
        expected
            ){return newTypeSafeMatcher

            <TransportConnection
            > ( ){ byte [ ]
            received
                ;
                @
                    Override protected booleanmatchesSafely(TransportConnection item){ try{
                    received =item.recv (expected . length)
                ;
                return Arrays . equals (
                received
                    , expected ); } catch(
                Exception
            e

            ){
            throw new RuntimeException( e ) ;
            }
                }@Overridepublic void describeTo(
                Descriptiondescription ){ description .appendText
            (

            "to receive ")
            ; appendBytes (description , expected) ; } @
            Override
                protectedvoiddescribeMismatchSafely( TransportConnection item,
                DescriptionmismatchDescription ){ mismatchDescription .appendText
            (

            "received " ); appendBytes (mismatchDescription ,received) ; }
            void
                appendBytes(Descriptiondescription ,byte [] bytes) { description.
            appendValueList
        ("RawBytes["
    ,

    "," , "]",bytes) ;}}
    ;
        } public staticMatcher<TransportConnection>eventuallyDisconnects
        (
            ){
            return new TypeSafeMatcher< TransportConnection > (
            )
                { @ Override protectedboolean matchesSafely
                (
                    TransportConnection
                    connection
                        ){BooleanSuppliercondition = ()->{try{connection.send(
                        newbyte[] { 0,
                    0
                    } ) ; connection .
                    recv
                        (
                        1 ) ; }catch
                    (
                    Exception e)
                {// take an IOException on send/receive as evidence of disconnection
                return
                e
                    instanceofIOException;} returnfalse ;} ;try{ Predicates.
                    await (condition
                ,
                2 , TimeUnit . SECONDS
                )
                    ; returntrue
                ;
            }

            catch(
            Exception e ){ return false ;
            }
                }@Overridepublic void describeTo(
            Description
        description)
    {

    description . appendText("Eventually Disconnects") ;}}
    ;
        } public staticMatcher<TransportConnection>serverImmediatelyDisconnects
        (
            ){
            return new TypeSafeMatcher< TransportConnection > (
            )
                {
                @
                    OverrideprotectedbooleanmatchesSafely ( TransportConnectionconnection
                )
                { try { connection .
                recv
                    (
                    1 ) ; }catch
                (
                Exception e)
            {

            // take an IOException on send/receive as evidence of disconnectionreturn
            e instanceof IOException; } return false
            ;
                }@Overridepublic void describeTo(
            Description
        description)
    {

    description . appendText
    (
        "Eventually Disconnects"); }} ; }public interfaceMessageEncoder { byte [ ]encode
        (Neo4jPackneo4jPack ,RequestMessage ... messages) throwsIOException ; byte [ ]encode
    (

    Neo4jPack neo4jPack , ResponseMessage ... messages
    )
        throwsIOException
        ; }privatestatic classMessageEncoderV1 implements MessageEncoder{ @Override public byte [ ]
        encode
            ( Neo4jPackneo4jPack ,RequestMessage ... messages)
        throws

        IOException{
        return serialize(neo4jPack ,messages ) ;} @Override public byte [ ]
        encode
            ( Neo4jPackneo4jPack ,ResponseMessage ... messages)
        throws
    IOException
{
