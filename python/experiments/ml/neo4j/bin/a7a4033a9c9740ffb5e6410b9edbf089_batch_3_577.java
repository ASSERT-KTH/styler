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

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.NestingIterable;

/**
 * @deprecated Server plugins are deprecated for removal in the next major release. Please use unmanaged extensions instead.
 */
@Deprecated
public final class ServerExtender
{
    @SuppressWarnings( "unchecked" )
    private final Map<Class<?>, Map<String, PluginPoint>> targetToPluginMap = new HashMap();
    private PluginPointFactory pluginPointFactory;

    ServerExtender( PluginPointFactory pluginPointFactory )
    {
        this.pluginPointFactory = pluginPointFactory;
        targetToPluginMap.put( Node.class, new ConcurrentHashMap<>() );
        targetToPluginMap.put( Relationship.class, new ConcurrentHashMap<>() );
        targetToPluginMap.put(GraphDatabaseService.class ,new
    ConcurrentHashMap

    <>() ); }Iterable<PluginPoint > getExtensionsFor
    (
        Class<?> type) { Map <String,PluginPoint > ext=
        targetToPluginMap . get ( type )
        ;
            if (ext==null){
        return
        Collections .emptyList();}
    return

    ext.values( );}
    Iterable
        < PluginPoint >all() {returnnewNestingIterable <PluginPoint,Map
                <String,PluginPoint> >
        (
            targetToPluginMap.
            values ()){ @Override
                    protectedIterator<PluginPoint >createNestedIterator ( Map
            <
                String ,PluginPoint>item){returnitem.values
            (
        ).
    iterator

    ( ); }};} PluginPointgetExtensionPoint ( Class <
            ? >
    type
        ,Stringmethod) throwsPluginLookupException { Map <String,PluginPoint > ext=
        targetToPluginMap . get (type
        ) ; PluginPoint plugin = null
        ;
            if ( ext!=null) { plugin=
        ext
        . get ( method ) ;
        }
            if ( plugin== null ) { throw new PluginLookupException ( "No plugin \""+
        method
        + "\" for "+
    type

    ) ;} returnplugin;} voidaddExtension ( Class <
    ?
        >type,PluginPoint plugin) { Map <String,PluginPoint > ext=
        targetToPluginMap . get ( type )
        ;
            if ( ext== null ) { thrownew
        IllegalStateException
        ("Cannot extend " +type ) ;}
    add

    (ext
    , plugin ); } @ Deprecated
    public
        voidaddGraphDatabaseExtensions (PluginPointplugin) {add( targetToPluginMap. get (GraphDatabaseService
    .

    class)
    , plugin ); } @ Deprecated
    public
        voidaddNodeExtensions (PluginPointplugin) {add( targetToPluginMap. get (Node
    .

    class)
    , plugin ); } @ Deprecated
    public
        voidaddRelationshipExtensions (PluginPointplugin) {add( targetToPluginMap. get (Relationship
    .

    class ) , plugin) ;}privatestatic voidadd (Map < String ,
    PluginPoint
        > extensions ,PluginPointplugin) {if(extensions. get ( plugin .
        name
            ( ) )!=
                    null
                            ) {thrownewIllegalArgumentException( "This plugin already has an plugin point with the name \"" + plugin.
        name
        ()+"\"" );}extensions.put ( plugin.
    name

    ()
    , plugin );}
    @
        Deprecated publicPluginPointFactory
    getPluginPointFactory
(
