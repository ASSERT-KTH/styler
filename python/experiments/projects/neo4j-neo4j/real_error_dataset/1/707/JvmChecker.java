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
package org.neo4j.kernel.info;

import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.neo4j.graphdb.config.Setting;
import org.neo4j.logging.Log;

import static java.util.regex.Pattern.compile;
import static org.neo4j.configuration.ExternalSettings.initial_heap_size;
import static org.neo4j.configuration.ExternalSettings.max_heap_size;

public class JvmChecker
{
    private static final int SUPPORTED_FEATURE_VERSION = 11;
    static final String INCOMPATIBLE_JVM_WARNING = "You are using an unsupported Java runtime. Please" +
            " use Oracle(R) Java(TM) 11, OpenJDK(TM) 11.";
    static final String INCOMPATIBLE_JVM_VERSION_WARNING = "You are using an unsupported version of " +
            "the Java runtime. Please use Oracle(R) Java(TM) 11 or OpenJDK(TM) 11.";
    private static final Pattern SUPPORTED_JAVA_NAME_PATTERN = compile( "(Java HotSpot\\(TM\\)|OpenJDK) (64-Bit Server|Server) VM" );
    private static final String NO_SERIALIZATION_FILTER_WARNING = "The version of the Java runtime you are using " +
            " does not include some important security features. Please use a JRE of version 8u121 or higher.";

    private final Log log;
    private final JvmMetadataRepository jvmMetadataRepository;

    public JvmChecker( Log log, JvmMetadataRepository jvmMetadataRepository )
    {
        this.log = log;
        this.jvmMetadataRepository = jvmMetadataRepository;
    }

    public void checkJvmCompatibilityAndIssueWarning()
    {
        String javaVmName = jvmMetadataRepository.getJavaVmName();
        Runtime.Version javaVersion = jvmMetadataRepository.getJavaVersion();

        if ( !SUPPORTED_JAVA_NAME_PATTERN.matcher( javaVmName ).matches() )
        {
            log.warn( INCOMPATIBLE_JVM_WARNING );
        }
        else if ( javaVersion.feature() != SUPPORTED_FEATURE_VERSION )
        {
            log.warn( INCOMPATIBLE_JVM_VERSION_WARNING );
        }
        List<String> jvmArguments = jvmMetadataRepository.getJvmInputArguments();
        MemoryUsage heapMemoryUsage = jvmMetadataRepository.getHeapMemoryUsage();
        if ( missingOption( jvmArguments, "-Xmx" ) )
        {
            log.warn( memorySettingWarning( max_heap_size, heapMemoryUsage.getMax() ) );
        }
        if ( missingOption( jvmArguments, "-Xms" ) )
        {
            log.warn( memorySettingWarning( initial_heap_size, heapMemoryUsage.getInit() ) );
        }
        if ( !serializationFilterIsAvailable() )
        {
            log.warn( NO_SERIALIZATION_FILTER_WARNING );
        }
    }

    static String memorySettingWarning( Setting<?> setting, long currentUsage )
    {
        return "The " + setting.name() + " setting has not been configured. It is recommended that this " +
                "setting is always explicitly configured, to ensure the system has a balanced configuration. " +
                "Until then, a JVM computed heuristic of " + currentUsage + " bytes is used instead. " +
                "Run `neo4j-admin memrec` for memory configuration suggestions.";
    }

    private static boolean missingOption( List<String> jvmArguments, String option )
    {
        String normalizedOption = option.toUpperCase( Locale.ROOT );
        return jvmArguments.stream().noneMatch( o -> o.toUpperCase( Locale.ROOT ).startsWith( normalizedOption ) );
    }

    boolean serializationFilterIsAvailable()
    {
        //As part of JEP290 ObjectInputFilter was backported to JDK 8 in version 121, but under a different package.
        Stream<String> classNames = Stream.of( "sun.misc.ObjectInputFilter", "java.io.ObjectInputFilter" );
        return classNames.anyMatch( className ->
        {
            try
            {
                Class.forName( className );
            }
            catch ( ClassNotFoundException e )
            {
                return false;
            }
            return true;
        } );

    }
}
