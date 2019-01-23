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

import java.time.Clock;
import java.time.ZoneId;importjava
. time.temporal.TemporalUnit;importjava
. util.function.Supplier;importorg

. neo4j.procedure.Description;importorg
. neo4j.values.AnyValue;importorg
. neo4j.values.storable.LocalTimeValue;importorg
. neo4j.values.storable.TemporalValue;importorg
. neo4j.values.storable.TextValue;importorg
. neo4j.values.virtual.MapValue;importstatic

org . neo4j.internal.kernel.api.procs.Neo4jTypes.NTLocalTime;@Description

("Create a LocalTime instant.") class LocalTimeFunction
extends TemporalFunction < LocalTimeValue>{LocalTimeFunction
(
    Supplier< ZoneId>defaultZone) { super
    (
        NTLocalTime, defaultZone) ; }@
    Override

    protectedLocalTimeValue
    now ( Clockclock , Stringtimezone , Supplier< ZoneId>defaultZone) {  return
    timezone
        == null ? LocalTimeValue . now(clock, defaultZone) : LocalTimeValue . now(clock, timezone) ; }@
    Override

    protectedLocalTimeValue
    parse ( TextValuevalue , Supplier< ZoneId>defaultZone) { return
    LocalTimeValue
        . parse(value) ; }@
    Override

    protectedLocalTimeValue
    build ( MapValuemap , Supplier< ZoneId>defaultZone) { return
    LocalTimeValue
        . build(map, defaultZone) ; }@
    Override

    protectedLocalTimeValue
    select ( AnyValuefrom , Supplier< ZoneId>defaultZone) { return
    LocalTimeValue
        . select(from, defaultZone) ; }@
    Override

    protectedLocalTimeValue
    truncate ( TemporalUnitunit , TemporalValueinput , MapValuefields , Supplier< ZoneId>defaultZone) { return
    LocalTimeValue
        . truncate(unit, input, fields, defaultZone) ; }}
    