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
package org.neo4j.bolt.v1.messaging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.bolt.runtime.BoltConnection;
import org.neo4j.bolt.runtime.BoltResponseHandler;
import org.neo4j.bolt.runtime.BoltResult;
import org.neo4j.bolt.runtime.Neo4jError;
import org.neo4j.bolt.messaging.BoltResponseMessageWriter;
import org.neo4j.bolt.v1.messaging.response.FailureMessage;
import org.neo4j.bolt.v1.messaging.response.FatalFailureMessage;
import org.neo4j.bolt.v1.messaging.response.SuccessMessage
; importorg.neo4j.bolt.v1.packstream.PackOutputClosedException
; importorg.neo4j.kernel.api.exceptions.Status
; importorg.neo4j.logging.Log
; importorg.neo4j.values.AnyValue
; importorg.neo4j.values.virtual.MapValue
; importorg.neo4j.values.virtual.MapValueBuilder

; import staticorg.neo4j.bolt.v1.messaging.response.IgnoredMessage.IGNORED_MESSAGE

; public class MessageProcessingHandler implements
BoltResponseHandler
    {
    // Errors that are expected when the client disconnects mid-operation private static finalSet<Status > CLIENT_MID_OP_DISCONNECT_ERRORS
            = newHashSet<> (Arrays.asList (Status.Transaction.Terminated ,Status.Transaction. LockClientStopped ))
    ; private final MapValueBuilder metadata = newMapValueBuilder()

    ; protected final Loglog
    ; protected final BoltConnectionconnection
    ; protected final BoltResponseMessageWritermessageWriter

    ; private Neo4jErrorerror
    ; private booleanignored

    ; publicMessageProcessingHandler ( BoltResponseMessageWritermessageWriter , BoltConnectionconnection , Log logger
    )
        {this. messageWriter =messageWriter
        ;this. connection =connection
        ;this. log =logger
    ;

    }@
    Override public voidonRecords ( BoltResultresult , boolean pull ) throws
    Exception
    {

    }@
    Override public voidonMetadata ( Stringkey , AnyValue value
    )
        {metadata.add (key , value)
    ;

    }@
    Override public voidmarkIgnored(
    )
        {this. ignored =true
    ;

    }@
    Override public voidmarkFailed ( Neo4jError error
    )
        {this. error =error
    ;

    }@
    Override public voidonFinish(
    )
        {
        try
            { if ( ignored
            )
                {messageWriter.write ( IGNORED_MESSAGE)
            ;
            } else if ( error != null
            )
                {publishError (messageWriter , error)
            ;
            }
            else
                {messageWriter.write ( newSuccessMessage (getMetadata( ) ))
            ;
        }
        } catch ( Throwable e
        )
            {connection.stop()
            ;log.error ("Failed to write response to driver" , e)
        ;
        }
        finally
            {clearState()
        ;
    }

    } MapValuegetMetadata(
    )
        { returnmetadata.build()
    ;

    } private voidclearState(
    )
        { error =null
        ; ignored =false
        ;metadata.clear()
    ;

    } private voidpublishError ( BoltResponseMessageWritermessageWriter , Neo4jError error
    )
        {
        try
            { if (error.isFatal( )
            )
                {messageWriter.write ( newFatalFailureMessage (error.status() ,error.message( ) ))
            ;
            }
            else
                {messageWriter.write ( newFailureMessage (error.status() ,error.message( ) ))
            ;
        }
        } catch ( PackOutputClosedException e
        )
            {
            // Can't write error to the client, because the connection is closed.

            // Very likely our error is related to the connection being closed.
            // If the error is that the transaction was terminated, then the error is a side-effect of
            // us cleaning up stuff that was running when the client disconnected. Log a warning without
            // stack trace to highlight clients are disconnecting while stuff is running: if (CLIENT_MID_OP_DISCONNECT_ERRORS.contains (error.status( ) )
            )
                {log.warn ( "Client %s disconnected while query was running. Session has been cleaned up. "
                        + "This can be caused by temporary network problems, but if you see this often, "
                        +"ensure your applications are properly waiting for operations to complete before exiting." ,e.clientAddress( ))
                ;return
            ;

            }
            // If the error isn't that the tx was terminated, log it to the console for debugging. It's likely
            // there are other "ok" errors that we can whitelist into the conditional above over time.log.warn ( "Unable to send error back to the client. " +e.getMessage() ,error.cause( ))
        ;
        } catch ( Throwable t
        )
            {
            // some unexpected error happened while writing exception back to the client
            // log it together with the original error being suppressedt.addSuppressed (error.cause( ))
            ;log.error ("Unable to send error back to the client" , t)
        ;
    }
}
