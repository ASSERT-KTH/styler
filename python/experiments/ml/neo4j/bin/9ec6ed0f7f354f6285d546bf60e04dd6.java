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
package org.neo4j.kernel.impl.transaction.log.stresstest.


workload ;importjava.util.
ArrayList ;importjava.util.
Collection ;importjava.util.
List ;importjava.util.concurrent.

ThreadLocalRandom ;importorg.neo4j.kernel.impl.api.
TransactionHeaderInformation ;importorg.neo4j.kernel.impl.api.
TransactionToApply ;importorg.neo4j.kernel.impl.store.record.
NodeRecord ;importorg.neo4j.kernel.impl.transaction.command.
Command ;importorg.neo4j.kernel.impl.transaction.log.
PhysicalTransactionRepresentation ;importorg.neo4j.storageengine.api.

StorageCommand ; importstaticjava.lang.System.
currentTimeMillis ; importstaticorg.neo4j.kernel.impl.transaction.TransactionHeaderInformationFactory.

DEFAULT ;
class
    TransactionRepresentationFactory { private final CommandGenerator commandGenerator =newCommandGenerator(

    ) ;TransactionToApply nextTransaction ( long
    txId
        ) { PhysicalTransactionRepresentation
                representation =new PhysicalTransactionRepresentation(createRandomCommands ()
        ) ; TransactionHeaderInformation headerInfo=DEFAULT.create(
        );representation. setHeader(headerInfo.getAdditionalHeader( ),headerInfo.getMasterId(
                ),headerInfo.getAuthorId( ),headerInfo.getAuthorId( ), txId,currentTimeMillis( ) ,42
        ) ; returnnew TransactionToApply (representation
    )

    ; }privateCollection< StorageCommand>createRandomCommands
    (
        ) { int commandNum=ThreadLocalRandom.current(). nextInt( 1 ,17
        );List< StorageCommand > commands =newArrayList< > (commandNum
        ) ; for ( int i= 0 ; i< commandNum; i
        ++
            ){commands. add(commandGenerator.nextCommand ()
        )
        ; }return
    commands

    ; } private static
    class
        CommandGenerator { private NodeRecordGenerator nodeRecordGenerator =newNodeRecordGenerator(

        ) ;CommandnextCommand
        (
            ) { returnnewCommand. NodeCommand(nodeRecordGenerator.nextRecord( ),nodeRecordGenerator.nextRecord ()
        )
    ;

    } } private static
    class

        NodeRecordGenerator {NodeRecordnextRecord
        (
            ) { ThreadLocalRandom random=ThreadLocalRandom.current(
            ) ; returnnew NodeRecord(random.nextLong( ),random.nextBoolean( ),random.nextBoolean(
                    ),random.nextLong( ),random.nextLong( ),random.nextLong ()
        )
    ;
}
