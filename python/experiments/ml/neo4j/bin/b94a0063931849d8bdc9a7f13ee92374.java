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
package org.neo4j.kernel.impl.transaction.log;

import java.io.File;

import org.neo4j.kernel.impl.transaction.log.entry.LogEntryCommit;
import org.neo4j.kernel.impl.transaction.log.files.LogFileCreationMonitor;
import org.neo4j.kernel.impl.transaction.log.rotation.LogRotation;
import org.neo4j.kernel.recovery.RecoveryMonitor;
import org.neo4j.kernel.recovery.RecoveryStartInformationProvider;
import org.neo4j.logging.Log;

import static java.lang.String.format;

public class LoggingLogFileMonitor implements LogFileCreationMonitor,
        LogRotation.Monitor, RecoveryMonitor,
        RecoveryStartInformationProvider.Monitor
{
    private long firstTransactionRecovered = -1;
    private long lastTransactionRecovered;
    private final Log log;

    public LoggingLogFileMonitor ( Log log
    )
        {this. log =log
    ;

    }@
    Override public voidrecoveryRequired ( LogPosition startPosition
    )
        {log.info ( "Recovery required from position " + startPosition)
    ;

    }@
    Override public voidrecoveryCompleted ( int numberOfRecoveredTransactions
    )
        { if ( numberOfRecoveredTransactions != 0
        )
            {log.info (format ("Recovery completed. %d transactions, first:%d, last:%d recovered"
                    ,numberOfRecoveredTransactions ,firstTransactionRecovered , lastTransactionRecovered ))
        ;
        }
        else
            {log.info ( "No recovery required")
        ;
    }

    }@
    Override public voidfailToRecoverTransactionsAfterCommit ( Throwablet , LogEntryCommitcommitEntry , LogPosition recoveryToPosition
    )
        {log.warn (format ( "Fail to recover all transactions. Last recoverable transaction id:%d, committed "
                        +"at:%d. Any later transaction after %s are unreadable and will be truncated."
                ,commitEntry.getTxId() ,commitEntry.getTimeWritten() , recoveryToPosition) , t)
    ;

    }@
    Override public voidfailToRecoverTransactionsAfterPosition ( Throwablet , LogPosition recoveryFromPosition
    )
        {log.warn (format ( "Fail to recover all transactions. Any later transactions after position %s are "
                +"unreadable and will be truncated." , recoveryFromPosition) , t)
    ;

    }@
    Override public voidstartedRotating ( long currentVersion
    )
    {

    }@
    Override public voidfinishedRotating ( long currentVersion
    )
    {

    }@
    Override public voidtransactionRecovered ( long txId
    )
        { if ( firstTransactionRecovered ==- 1
        )
            { firstTransactionRecovered =txId
        ;
        } lastTransactionRecovered =txId
    ;

    }@
    Override public voidcreated ( FilelogFile , longlogVersion , long lastTransactionId
    )
        {log.info (format ("Rotated to transaction log [%s] version=%d, last transaction in previous log=%d"
                ,logFile ,logVersion , lastTransactionId ))
    ;

    }@
    Override public voidnoCommitsAfterLastCheckPoint ( LogPosition logPosition
    )
        {log.info (format ("No commits found after last check point (which is at %s)"
                , logPosition != null ?logPosition.toString( ) : "<no log position given>" ))
    ;

    }@
    Override public voidcommitsAfterLastCheckPoint ( LogPositionlogPosition , long firstTxIdAfterLastCheckPoint
    )
        {log.info (format
                ("Commits found after last check point (which is at %s). First txId after last checkpoint: %d "
                ,logPosition , firstTxIdAfterLastCheckPoint ))
    ;

    }@
    Override public voidnoCheckPointFound(
    )
        {log.info ( "No check point found in transaction log")
    ;
}
