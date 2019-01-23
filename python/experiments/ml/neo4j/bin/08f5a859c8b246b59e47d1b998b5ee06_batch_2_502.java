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
package org.neo4j.server.modules;

import java.net.URI;
import java.util.List;
import java.util.function.Supplier;

import org.neo4j.kernel.configuration.Config;
import org.neo4j.logging.LogProvider;
import org.neo4j.server.configuration.ServerSettings;
import org.neo4j.server.plugins.DefaultPluginManager;
import org.neo4j.server.plugins.PluginManager;
import org.neo4j.server.rest.web.BatchOperationService;
import org.neo4j.server.rest.web.CollectUserAgentFilter;
import org.neo4j.server.rest.web.CorsFilter;
import org.neo4j.server.rest.

web.CypherService;importorg
. neo4j.server.rest.web.DatabaseMetadataService;importorg
. neo4j.server.rest.web.ExtensionService;importorg
. neo4j.server.rest.web.ResourcesService;importorg
. neo4j.server.rest.web.RestfulGraphDatabase;importorg
. neo4j.server.rest.web.TransactionalService;importorg
. neo4j.server.web.WebServer;importorg
. neo4j.udc.UsageData;importorg
. neo4j.udc.UsageDataKeys;importorg
. neo4j.util.concurrent.RecentK;importstatic

java . util.Arrays.asList;importstatic
org . neo4j.server.configuration.ServerSettings.http_access_control_allow_origin;/**
 * Mounts the database REST API.
 */public

class
RESTApiModule implements ServerModule { private
final
    Config config ; privatefinal
    WebServer webServer ; privatefinal
    Supplier < UsageData>userDataSupplier; privatefinal
    LogProvider logProvider ; privatePluginManager

    plugins ; publicRESTApiModule

    ( WebServerwebServer , Configconfig , Supplier< UsageData>userDataSupplier, LogProviderlogProvider ) { this
    .
        webServer=webServer ; this.
        config=config ; this.
        userDataSupplier=userDataSupplier ; this.
        logProvider=logProvider ; }@
    Override

    publicvoid
    start ( ){URI
    restApiUri
        = restApiUri ( ); webServer.

        addFilter(newCollectUserAgentFilter ( clientNames( )), "/*") ; webServer.
        addFilter(newCorsFilter ( logProvider, config. get(http_access_control_allow_origin) ) , "/*") ; webServer.
        addJAXRSClasses(getClassNames( ),restApiUri. toString(),null) ; loadPlugins(
        );}private
    RecentK

    < String>clientNames( ){return
    userDataSupplier
        . get().get(UsageDataKeys. clientNames); }private
    List

    < String>getClassNames( ){return
    asList
        ( RestfulGraphDatabase.
                class.getName(),TransactionalService.
                class.getName(),CypherService.
                class.getName(),DatabaseMetadataService.
                class.getName(),ExtensionService.
                class.getName(),ResourcesService.
                class.getName(),BatchOperationService.
                class.getName()); }@
    Override

    publicvoid
    stop ( ){webServer
    .
        removeJAXRSClasses(getClassNames( ),restApiUri( ).toString()); }private
    URI

    restApiUri ( ){return
    config
        . get(ServerSettings. rest_api_path); }private
    void

    loadPlugins ( ){plugins
    =
        new DefaultPluginManager ( logProvider) ; }public
    PluginManager

    getPlugins ( ){return
    plugins
        ; }}
    