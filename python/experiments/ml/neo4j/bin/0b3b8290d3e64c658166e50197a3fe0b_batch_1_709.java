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
package org.neo4j.server.plugins;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.graphdb.GraphDatabaseService
; importorg.neo4j.graphdb.Node
; importorg.neo4j.graphdb.Relationship
; importorg.neo4j.helpers.collection.NestingIterable

;
/**
 * @deprecated Server plugins are deprecated for removal in the next major release. Please use unmanaged extensions instead.
 */@
Deprecated public final class
ServerExtender
    {@SuppressWarnings ( "unchecked"
    ) private finalMap<Class<?> ,Map<String ,PluginPoint> > targetToPluginMap = newHashMap()
    ; private PluginPointFactorypluginPointFactory

    ;ServerExtender ( PluginPointFactory pluginPointFactory
    )
        {this. pluginPointFactory =pluginPointFactory
        ;targetToPluginMap.put (Node.class , newConcurrentHashMap<>( ))
        ;targetToPluginMap.put (Relationship.class , newConcurrentHashMap<>( ))
        ;targetToPluginMap.put (GraphDatabaseService.class , newConcurrentHashMap<>( ))
    ;

    }Iterable<PluginPoint >getExtensionsFor (Class<? > type
    )
        {Map<String ,PluginPoint > ext =targetToPluginMap.get ( type)
        ; if ( ext == null
        )
            { returnCollections.emptyList()
        ;
        } returnext.values()
    ;

    }Iterable<PluginPoint >all(
    )
        { return newNestingIterable<PluginPoint ,Map<String ,PluginPoint>>
                (targetToPluginMap.values( )
        )
            {@
            Override protectedIterator<PluginPoint >createNestedIterator
                    (Map<String ,PluginPoint > item
            )
                { returnitem.values().iterator()
            ;
        }}
    ;

    } PluginPointgetExtensionPoint (Class<? >type , String method
            ) throws
    PluginLookupException
        {Map<String ,PluginPoint > ext =targetToPluginMap.get ( type)
        ; PluginPoint plugin =null
        ; if ( ext != null
        )
            { plugin =ext.get ( method)
        ;
        } if ( plugin == null
        )
            { throw newPluginLookupException ( "No plugin \"" + method + "\" for " + type)
        ;
        } returnplugin
    ;

    } voidaddExtension (Class<? >type , PluginPoint plugin
    )
        {Map<String ,PluginPoint > ext =targetToPluginMap.get ( type)
        ; if ( ext == null
        )
            { throw newIllegalStateException ( "Cannot extend " + type)
        ;
        }add (ext , plugin)
    ;

    }@
    Deprecated public voidaddGraphDatabaseExtensions ( PluginPoint plugin
    )
        {add (targetToPluginMap.get (GraphDatabaseService. class) , plugin)
    ;

    }@
    Deprecated public voidaddNodeExtensions ( PluginPoint plugin
    )
        {add (targetToPluginMap.get (Node. class) , plugin)
    ;

    }@
    Deprecated public voidaddRelationshipExtensions ( PluginPoint plugin
    )
        {add (targetToPluginMap.get (Relationship. class) , plugin)
    ;

    } private static voidadd (Map<String ,PluginPoint >extensions , PluginPoint plugin
    )
        { if (extensions.get (plugin.name( ) ) != null
        )
            { throw newIllegalArgumentException
                    (
                            "This plugin already has an plugin point with the name \"" +plugin.name( ) + "\"")
        ;
        }extensions.put (plugin.name() , plugin)
    ;

    }@
    Deprecated public PluginPointFactorygetPluginPointFactory(
    )
        { returnpluginPointFactory
    ;
}
