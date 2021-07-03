/*
 * Copyright (c) 2002-2020 "Neo4j,"
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
package org.neo4j.kernel.impl.transaction.log.entry;

import java.util.Objects;

import org.neo4j.kernel.impl.transaction.log.LogPosition;

import static org.neo4j.kernel.impl.transaction.log.entry.LogEntryParserSetV4_0.V4_0;

public class LogEntryInlinedCheckPoint extends AbstractLogEntry
{
    private final LogPosition logPosition;

    public LogEntryInlinedCheckPoint( LogPosition logPosition )
    {
        this( V4_0.versionByte(), logPosition );
    }

    LogEntryInlinedCheckPoint( byte version, LogPosition logPosition )
    {
        super( version, LogEntryTypeCodes.LEGACY_CHECK_POINT );
        this.logPosition = logPosition;
    }

    public LogPosition getLogPosition()
    {
        return logPosition;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        LogEntryInlinedCheckPoint that = (LogEntryInlinedCheckPoint) o;
        return Objects.equals( logPosition, that.logPosition );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( logPosition );
    }

    @Override
    public String toString()
    {
        return "CheckPoint{logPosition=" + logPosition + '}';
    }
}
