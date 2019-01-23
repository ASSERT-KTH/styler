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
package org.neo4j.bolt.v3.runtime.integration;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.bolt.BoltChannel;
import org.neo4j.bolt.runtime.BoltStateMachine;
import org.neo4j.bolt.runtime.BoltStateMachineFactoryImpl;
import org.neo4j.bolt.security.auth.Authentication;
import org.neo4j.bolt.security.auth.BasicAuthentication;
import org.neo4j.dbms.database.DatabaseManager;
import org.neo4j.graphdb.DependencyResolver;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.graphdb.factory.GraphDatabaseSettings
; importorg.neo4j.io.IOUtils
; importorg.neo4j.kernel.api.security.AuthManager
; importorg.neo4j.kernel.api.security.UserManagerSupplier
; importorg.neo4j.kernel.configuration.Config
; importorg.neo4j.kernel.internal.GraphDatabaseAPI
; importorg.neo4j.logging.internal.NullLogService
; importorg.neo4j.test.TestGraphDatabaseFactory
; importorg.neo4j.udc.UsageData

; public class SessionExtension implementsBeforeEachCallback ,
AfterEachCallback
    { private GraphDatabaseAPIgdb
    ; private BoltStateMachineFactoryImplboltFactory
    ; privateList<BoltStateMachine > runningMachines = newArrayList<>()
    ; private booleanauthEnabled

    ; private Authenticationauthentication ( AuthManagerauthManager , UserManagerSupplier userManagerSupplier
    )
        { return newBasicAuthentication (authManager , userManagerSupplier)
    ;

    } public BoltStateMachinenewMachine ( longversion , BoltChannel boltChannel
    )
        { if ( boltFactory == null
        )
            { throw newIllegalStateException ( "Cannot access test environment before test is running.")
        ;
        } BoltStateMachine machine =boltFactory.newStateMachine (version , boltChannel)
        ;runningMachines.add ( machine)
        ; returnmachine
    ;

    }@
    Override public voidbeforeEach ( ExtensionContext extensionContext
    )
        {Map<Setting<?>,String > config = newHashMap<>()
        ;config.put (GraphDatabaseSettings.auth_enabled ,Boolean.toString ( authEnabled ))
        ; gdb =(GraphDatabaseAPI ) newTestGraphDatabaseFactory().newImpermanentDatabase ( config)
        ; DependencyResolver resolver =gdb.getDependencyResolver()
        ; Authentication authentication =authentication (resolver.resolveDependency (AuthManager. class)
                ,resolver.resolveDependency (UserManagerSupplier. class ))
        ; boltFactory = newBoltStateMachineFactoryImpl
                (resolver.resolveDependency (DatabaseManager. class)
                , newUsageData ( null)
                ,authentication
                ,Clock.systemUTC()
                ,Config.defaults()
                ,NullLogService.getInstance(
        ))
    ;

    }@
    Override public voidafterEach ( ExtensionContext extensionContext
    )
        {
        try
            { if ( runningMachines != null
            )
                {IOUtils.closeAll ( runningMachines)
            ;
        }
        } catch ( Throwable e
        )
            {e.printStackTrace()
        ;

        }gdb.shutdown()
    ;
}
