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
package org.neo4j.test.rule.dump;

import org.hamcrest.Matcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.

helpers.Args
; importorg.neo4j.helpers.collection.Pair
; importorg.neo4j.logging.FormattedLogProvider
; importorg.neo4j.logging.Log
; importorg.neo4j.logging.LogProvider

; import staticorg.hamcrest.Matchers.isIn
; import staticorg.neo4j.helpers.Format.time

; public class
DumpProcessInformation
    { private static final String HEAP ="heap"
    ; private static final String DIR ="dir"

    ; public static voidmain (String[ ] args ) throws
    Exception
        { Args arg =Args.withFlags ( HEAP).parse ( args == null ? newString[0 ] : args)
        ; boolean doHeapDump =arg.getBoolean (HEAP ,false , true)
        ;String[ ] containing =arg.orphans().toArray ( newString[arg.orphans().size() ])
        ; String dumpDir =arg.get (DIR , "data")
        ; newDumpProcessInformation (FormattedLogProvider.toOutputStream (System. out) , newFile ( dumpDir )).dumpRunningProcesses
                (doHeapDump , containing)
    ;

    } private final Loglog
    ; private final FileoutputDirectory

    ; publicDumpProcessInformation ( LogProviderlogProvider , File outputDirectory
    )
        {this. log =logProvider.getLog (getClass( ))
        ;this. outputDirectory =outputDirectory
    ;

    } public voiddumpRunningProcesses ( booleanincludeHeapDump ,String ... javaPidsContainingClassNames
            ) throws
    Exception
        {outputDirectory.mkdirs()
        ; for (Pair<Long ,String > pid :getJPids (isIn ( javaPidsContainingClassNames ) )
        )
            {doThreadDump ( pid)
            ; if ( includeHeapDump
            )
                {doHeapDump ( pid)
            ;
        }
    }

    } public FiledoThreadDump (Pair<Long ,String > pid ) throws
    Exception
        { File outputFile = newFile (outputDirectory ,fileName ("threaddump" , pid ))
        ;log.info ( "Creating thread dump of " + pid + " to " +outputFile.getAbsolutePath( ))
        ;String[ ] cmdarray = newString[ ]{"jstack" , "" +pid.first()}
        ; Process process =Runtime.getRuntime().exec ( cmdarray)
        ;writeProcessOutputToFile (process , outputFile)
        ; returnoutputFile
    ;

    } public voiddoHeapDump (Pair<Long ,String > pid ) throws
    Exception
        { File outputFile = newFile (outputDirectory ,fileName ("heapdump" , pid ))
        ;log.info ( "Creating heap dump of " + pid + " to " +outputFile.getAbsolutePath( ))
        ;String[ ] cmdarray = newString[ ]{"jmap" , "-dump:file=" +outputFile.getAbsolutePath() , "" +pid.first( )}
        ;Runtime.getRuntime().exec ( cmdarray).waitFor()
    ;

    } public voiddoThreadDump (Matcher<String > processFilter ) throws
    Exception
        { for (Pair<Long,String > pid :getJPids ( processFilter )
        )
            {doThreadDump ( pid)
        ;
    }

    } publicCollection<Pair<Long ,String> >getJPids (Matcher<String > filter ) throws
    Exception
        { Process process =Runtime.getRuntime().exec ( newString[ ] {"jps" , "-l" })
        ; BufferedReader reader = newBufferedReader ( newInputStreamReader (process.getInputStream( ) ))
        ; String line =null
        ;Collection<Pair<Long ,String> > jPids = newArrayList<>()
        ;Collection<Pair<Long ,String> > excludedJPids = newArrayList<>()
        ; while (( line =reader.readLine() ) != null
        )
            { int spaceIndex =line.indexOf ( ' ')
            ; String name =line.substring ( spaceIndex + 1)
            ;
            // Work-around for a windows problem where if your java.exe is in a directory
            // containing spaces the value in the second column from jps output will be
            // something like "C:\Program" if it was under "C:\Program Files\Java..."
            // If that's the case then use the PID instead if (name.contains ( ":" )
            )
                { String pid =line.substring (0 , spaceIndex)
                ; name =pid
            ;

            }Pair<Long ,String > pid =Pair.of (Long.parseLong (line.substring (0 , spaceIndex )) , name)
            ; if (name.contains (DumpProcessInformation.class.getSimpleName( ) )
                    ||name.contains ( "Jps" )
                    ||name.contains ( "eclipse.equinox" )
                    ||!filter.matches ( name )
            )
                {excludedJPids.add ( pid)
                ;continue
            ;
            }jPids.add ( pid)
        ;
        }process.waitFor()

        ;log.info ( "Found jPids:" + jPids + ", excluded:" + excludedJPids)

        ; returnjPids
    ;

    } private voidwriteProcessOutputToFile ( Processprocess , File outputFile ) throws
    Exception
        { BufferedReader reader = newBufferedReader ( newInputStreamReader (process.getInputStream( ) ))
        ; String line =null
        ; try ( PrintStream out = newPrintStream ( outputFile )
        )
            { while (( line =reader.readLine() ) != null
            )
                {out.println ( line)
            ;
        }
        }process.waitFor()
    ;

    } private static StringfileName ( Stringcategory ,Pair<Long,String > pid
    )
        { returntime().replace (':' , '_').replace ('.' , '_' )
                + "-" + category
                + "-" +pid.first( )
                + "-" +pid.other()
    ;
}
