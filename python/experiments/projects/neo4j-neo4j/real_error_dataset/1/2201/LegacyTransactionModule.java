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
package org.neo4j.server.modules;

import java.util.List;

import org.neo4j.configuration.Config;
import org.neo4j.server.configuration.ServerSettings;
import org.neo4j.server.http.cypher.LegacyTransactionService;
import org.neo4j.server.http.cypher.format.input.json.JsonMessageBodyReader;
import org.neo4j.server.http.cypher.format.output.json.JsonMessageBodyWriter;
import org.neo4j.server.web.WebServer;

/**
 * Mounts the legacy transaction module.
 */
public class LegacyTransactionModule implements ServerModule
{
    private final Config config;
    private final WebServer webServer;

    public LegacyTransactionModule( WebServer webServer, Config config )
    {
        this.webServer = webServer;
        this.config = config;
    }

    @Override
    public void start()
    {
        webServer.addJAXRSClasses( jaxRsClasses(), mountPoint(), null );
    }

    private List<Class<?>> jaxRsClasses()
    {
        return List.of( LegacyTransactionService.class, JsonMessageBodyReader.class, JsonMessageBodyWriter.class );
    }

    @Override
    public void stop()
    {
        webServer.removeJAXRSClasses( jaxRsClasses(), mountPoint() );
    }

    private String mountPoint()
    {
        return config.get( ServerSettings.rest_api_path ).getPath();
    }
}
