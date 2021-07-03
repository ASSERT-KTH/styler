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
package org.neo4j.fabric.executor;

import org.neo4j.kernel.api.exceptions.Status;

public class FabricException extends RuntimeException implements Status.HasStatus
{
    private final Status statusCode;

    public FabricException( Status statusCode, Throwable cause )
    {
        super( cause );
        this.statusCode = statusCode;
    }

    public FabricException( Status statusCode, String message, Object... parameters )
    {
        super( String.format( message, parameters ) );
        this.statusCode = statusCode;
    }

    public FabricException( Status statusCode, String message, Throwable cause )
    {
        super( message, cause );
        this.statusCode = statusCode;
    }

    @Override
    public Status status()
    {
        return statusCode;
    }
}
