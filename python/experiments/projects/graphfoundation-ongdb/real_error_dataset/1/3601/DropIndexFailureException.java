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
package org.neo4j.kernel.api.exceptions.schema;

import org.neo4j.internal.kernel.api.TokenNameLookup;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.internal.kernel.api.exceptions.schema.SchemaKernelException;
import org.neo4j.internal.kernel.api.schema.SchemaDescriptor;
import org.neo4j.internal.kernel.api.schema.SchemaUtil;
import org.neo4j.kernel.api.exceptions.Status;

import static java.lang.String.format;

public class DropIndexFailureException extends SchemaKernelException
{
    private final SchemaDescriptor descriptor;
    private static final String message = "Unable to drop index on %s: %s";

    public DropIndexFailureException( SchemaDescriptor descriptor, SchemaKernelException cause )
    {
        super( Status.Schema.IndexDropFailed, format( message, descriptor.userDescription( SchemaUtil.idTokenNameLookup ),
                        cause.getMessage() ), cause );
        this.descriptor = descriptor;
    }

    @Override
    public String getUserMessage( TokenNameLookup tokenNameLookup )
    {
        return format( message, descriptor.userDescription( tokenNameLookup ),
                ((KernelException) getCause()).getUserMessage( tokenNameLookup ) );
    }
}
