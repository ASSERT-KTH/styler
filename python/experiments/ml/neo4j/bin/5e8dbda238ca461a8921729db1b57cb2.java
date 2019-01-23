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
package org.neo4j.kernel.impl.proc.temporal;

import java.time.
Clock ;importjava.time.
ZoneId ;importjava.time.temporal.
TemporalUnit ;importjava.util.function.

Supplier ;importorg.neo4j.procedure.
Description ;importorg.neo4j.values.
AnyValue ;importorg.neo4j.values.storable.
LocalTimeValue ;importorg.neo4j.values.storable.
TemporalValue ;importorg.neo4j.values.storable.
TextValue ;importorg.neo4j.values.virtual.

MapValue ; importstaticorg.neo4j.internal.kernel.api.procs.Neo4jTypes.

NTLocalTime;@ Description (
"Create a LocalTime instant." ) class LocalTimeFunctionextendsTemporalFunction<
LocalTimeValue
    >{ LocalTimeFunction(Supplier< ZoneId >
    defaultZone
        ){ super( NTLocalTime ,defaultZone
    )

    ;}
    @ Override protectedLocalTimeValue now (Clock clock ,String timezone,Supplier< ZoneId  >
    defaultZone
        ) { return timezone == null?LocalTimeValue. now( clock , defaultZone ):LocalTimeValue. now( clock ,timezone
    )

    ;}
    @ Override protectedLocalTimeValue parse (TextValue value,Supplier< ZoneId >
    defaultZone
        ) {returnLocalTimeValue. parse (value
    )

    ;}
    @ Override protectedLocalTimeValue build (MapValue map,Supplier< ZoneId >
    defaultZone
        ) {returnLocalTimeValue. build( map ,defaultZone
    )

    ;}
    @ Override protectedLocalTimeValue select (AnyValue from,Supplier< ZoneId >
    defaultZone
        ) {returnLocalTimeValue. select( from ,defaultZone
    )

    ;}
    @ Override protectedLocalTimeValue truncate (TemporalUnit unit ,TemporalValue input ,MapValue fields,Supplier< ZoneId >
    defaultZone
        ) {returnLocalTimeValue. truncate( unit, input, fields ,defaultZone
    )
;
