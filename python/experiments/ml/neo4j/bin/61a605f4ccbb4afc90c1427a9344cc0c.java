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
package org.neo4j.kernel.impl.proc;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.stream.Stream;

import org.neo4j.collection.RawIterator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.internal.kernel.api.exceptions.ProcedureException;
import org.neo4j.internal.kernel.api.procs.Neo4jTypes;
import org.neo4j.kernel.api.ResourceTracker;
import org.neo4j.kernel.api.StubResourceManager;
import org.neo4j.kernel.api.exceptions.ResourceCloseFailureException;
import org.neo4j.kernel.api.proc.BasicContext;
import org.neo4j.kernel.api.proc.CallableProcedure;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.logging.Log;
import org.neo4j.logging.NullLog;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Procedure;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.neo4j.graphdb.factory.GraphDatabaseSettings.procedure_whitelist;
import static org.neo4j.helpers.collection.Iterators.asList;
import static org.neo4j.internal.kernel.api.procs.ProcedureSignature.procedureSignature;

@SuppressWarnings( "WeakerAccess" )
public class ReflectiveProcedureTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ReflectiveProcedureCompiler procedureCompiler;
    private ComponentRegistry components;
    private final ResourceTracker resourceTracker = new StubResourceManager();

    @Before
    public void setUp()
    {
        components = new ComponentRegistry();
        procedureCompiler = new ReflectiveProcedureCompiler( new TypeMappers(), components, components,
                NullLog.getInstance(), ProcedureConfig.DEFAULT );
    }

    @Test
    public void shouldInjectLogging() throws KernelException
    {
        // Given
        Log log = spy( Log.class );
        components.register( Log.class, ctx -> log );
        CallableProcedure procedure =
                procedureCompiler.compileProcedure( LoggingProcedure.class, null, true ).get( 0 );

        // When
        procedure.apply( new BasicContext(), new Object[0], resourceTracker );

        // Then
        verify( log ).debug( "1" );
        verify( log ).info( "2" );
        verify( log ).warn( "3" );
        verify( log ).error( "4" );
    }

    @Test
    public void shouldCompileProcedure() throws Throwable
    {
        // When
        List<CallableProcedure> procedures = compile( SingleReadOnlyProcedure.class );

        // Then
        assertEquals( 1, procedures.size() );
        assertThat( procedures.get( 0 ).signature(), Matchers.equalTo(
                procedureSignature( "org", "neo4j", "kernel", "impl", "proc", "listCoolPeople" )
                        .out( "name", Neo4jTypes.NTString )
                        .build() ) );
    }

    @Test
    public void shouldRunSimpleReadOnlyProcedure() throws Throwable
    {
        // Given
        CallableProcedure proc = compile( SingleReadOnlyProcedure.class ).get( 0 );

        // When
        RawIterator<Object[],ProcedureException> out = proc.apply( new BasicContext(), new Object[0], resourceTracker );

        // Then
        assertThat( asList( out ), contains(
                new Object[]{"Bonnie"},
                new Object[]{"Clyde"}
        ) );
    }

    @Test
    public void shouldIgnoreClassesWithNoProcedures() throws Throwable
    {
        // WhenList<CallableProcedure > procedures =compile (PrivateConstructorButNoProcedures. class)

        ;
        // ThenassertEquals (0 ,procedures.size( ))
    ;

    }@
    Test public voidshouldRunClassWithMultipleProceduresDeclared( ) throws
    Throwable
        {
        // GivenList<CallableProcedure > compiled =compile (MultiProcedureProcedure. class)
        ; CallableProcedure bananaPeople =compiled.get ( 0)
        ; CallableProcedure coolPeople =compiled.get ( 1)

        ;
        // WhenRawIterator<Object[],ProcedureException > coolOut =coolPeople.apply ( newBasicContext() , newObject[0] , resourceTracker)
        ;RawIterator<Object[],ProcedureException > bananaOut =bananaPeople.apply ( newBasicContext() , newObject[0] , resourceTracker)

        ;
        // ThenassertThat (asList ( coolOut) ,contains
                ( newObject[]{"Bonnie"}
                , newObject[]{"Clyde"
        } ))

        ;assertThat (asList ( bananaOut) ,contains
                ( newObject[]{"Jake" ,18L}
                , newObject[]{"Pontus" ,2L
        } ))
    ;

    }@
    Test public voidshouldGiveHelpfulErrorOnConstructorThatRequiresArgument( ) throws
    Throwable
        {
        // Expectexception.expect (ProcedureException. class)
        ;exception.expectMessage ( "Unable to find a usable public no-argument constructor "
                                 + "in the class `WierdConstructorProcedure`. Please add a "
                                 + "valid, public constructor, recompile the class and try again.")

        ;
        // Whencompile (WierdConstructorProcedure. class)
    ;

    }@
    Test public voidshouldGiveHelpfulErrorOnNoPublicConstructor( ) throws
    Throwable
        {
        // Expectexception.expect (ProcedureException. class)
        ;exception.expectMessage ( "Unable to find a usable public no-argument constructor "
                                 + "in the class `PrivateConstructorProcedure`. Please add "
                                 + "a valid, public constructor, recompile the class and try again.")

        ;
        // Whencompile (PrivateConstructorProcedure. class)
    ;

    }@
    Test public voidshouldAllowVoidOutput( ) throws
    Throwable
        {
        // When CallableProcedure proc =compile (ProcedureWithVoidOutput. class).get ( 0)

        ;
        // ThenassertEquals (0 ,proc.signature().outputSignature().size( ))
        ;assertFalse (proc.apply (null , newObject[0] , resourceTracker).hasNext( ))
    ;

    }@
    Test public voidshouldGiveHelpfulErrorOnProcedureReturningInvalidRecordType( ) throws
    Throwable
        {
        // Expectexception.expect (ProcedureException. class)
        ;exception.expectMessage (String.format( "Procedures must return a Stream of records, where a record is a concrete class%n"
                                 + "that you define, with public non-final fields defining the fields in the record.%n"
                                 + "If you''d like your procedure to return `String`, you could define a record class "
                                 + "like:%n"
                                 + "public class Output '{'%n"
                                 + "    public String out;%n"
                                 + "'}'%n"
                                 + "%n"
                                 + "And then define your procedure as returning `Stream<Output>`."))

        ;
        // Whencompile (ProcedureWithInvalidRecordOutput. class).get ( 0)
    ;

    }@
    Test public voidshouldGiveHelpfulErrorOnContextAnnotatedStaticField( ) throws
    Throwable
        {
        // Expectexception.expect (ProcedureException. class)
        ;exception.expectMessage (String.format( "The field `gdb` in the class named `ProcedureWithStaticContextAnnotatedField` is "
                                 + "annotated as a @Context field,%n"
                                 + "but it is static. @Context fields must be public, non-final and non-static,%n"
                                 + "because they are reset each time a procedure is invoked."))

        ;
        // Whencompile (ProcedureWithStaticContextAnnotatedField. class).get ( 0)
    ;

    }@
    Test public voidshouldAllowNonStaticOutput( ) throws
    Throwable
        {
        // When CallableProcedure proc =compile (ProcedureWithNonStaticOutputRecord. class).get ( 0)

        ;
        // ThenassertEquals (1 ,proc.signature().outputSignature().size( ))
    ;

    }@
    Test public voidshouldAllowOverridingProcedureName( ) throws
    Throwable
        {
        // When CallableProcedure proc =compile (ProcedureWithOverriddenName. class).get ( 0)

        ;
        // ThenassertEquals("org.mystuff.thisisActuallyTheName" ,proc.signature().name().toString( ))
    ;

    }@
    Test public voidshouldAllowOverridingProcedureNameWithoutNamespace( ) throws
    Throwable
        {
        // When CallableProcedure proc =compile (ProcedureWithSingleName. class).get ( 0)

        ;
        // ThenassertEquals("singleName" ,proc.signature().name().toString( ))
    ;

    }@
    Test public voidshouldGiveHelpfulErrorOnNullMessageException( ) throws
    Throwable
        {
        // Given CallableProcedure proc =compile (ProcedureThatThrowsNullMsgExceptionAtInvocation. class).get ( 0)

        ;
        // Expectexception.expect (ProcedureException. class)
        ;exception.expectMessage ( "Failed to invoke procedure `org.neo4j.kernel.impl.proc.throwsAtInvocation`: "
                                 + "Caused by: java.lang.IndexOutOfBoundsException")

        ;
        // Whenproc.apply ( newBasicContext() , newObject[0] , resourceTracker)
    ;

    }@
    Test public voidshouldCloseResourcesAndGiveHelpfulErrorOnMidStreamException( ) throws
    Throwable
        {
        // Given CallableProcedure proc =compile (ProcedureThatThrowsNullMsgExceptionMidStream. class).get ( 0)

        ;
        // Expectexception.expect (ProcedureException. class)
        ;exception.expectMessage ( "Failed to invoke procedure `org.neo4j.kernel.impl.proc.throwsInStream`: "
                                 + "Caused by: java.lang.IndexOutOfBoundsException")

        ;
        // Expect that we get a suppressed exception from Stream.onClose (which also verifies that we actually call
        // onClose on the first exception)exception.expect ( newBaseMatcher<Exception>(
        )
            {@
            Override public voiddescribeTo ( Description description
            )
                {description.appendText ( "a suppressed exception with cause ExceptionDuringClose")
            ;

            }@
            Override public booleanmatches ( Object item
            )
                { Exception e =(Exception )item
                ; for ( Throwable suppressed :e.getSuppressed( )
                )
                    { if ( suppressed instanceof ResourceCloseFailureException
                    )
                        { if (suppressed.getCause( ) instanceof ExceptionDuringClose
                        )
                            { returntrue
                        ;
                    }
                }
                } returnfalse
            ;
        } })

        ;
        // WhenRawIterator<Object[],ProcedureException > stream
                =proc.apply ( newBasicContext() , newObject[0] , resourceTracker)
        ; if (stream.hasNext( )
        )
            {stream.next()
        ;
    }

    }@
    Test public voidshouldSupportProcedureDeprecation( ) throws
    Throwable
        {
        // Given Log log =mock(Log.class)
        ; ReflectiveProcedureCompiler procedureCompiler = newReflectiveProcedureCompiler ( newTypeMappers() ,components
                ,components ,log ,ProcedureConfig. DEFAULT)

        ;
        // WhenList<CallableProcedure > procs
                =procedureCompiler.compileProcedure (ProcedureWithDeprecation.class ,null , true)

        ;
        // Thenverify ( log).warn ( "Use of @Procedure(deprecatedBy) without @Deprecated in badProc")
        ;verifyNoMoreInteractions ( log)
        ; for ( CallableProcedure proc : procs
        )
            { String name =proc.signature().name().name()
            ;proc.apply ( newBasicContext() , newObject[0] , resourceTracker)
            ; switch ( name
            )
            { case"newProc"
                :assertFalse ("Should not be deprecated" ,proc.signature().deprecated().isPresent( ))
                ;break
            ; case"oldProc"
            : case"badProc"
                :assertTrue ("Should be deprecated" ,proc.signature().deprecated().isPresent( ))
                ;assertThat (proc.signature().deprecated().get() ,equalTo ( "newProc" ))
                ;break
            ;default
                :fail ( "Unexpected procedure: " + name)
            ;
        }
    }

    }@
    Test public voidshouldLoadWhiteListedProcedure( ) throws
    Throwable
        {
        // Given ProcedureConfig config = newProcedureConfig
                (Config.defaults (procedure_whitelist , "org.neo4j.kernel.impl.proc.listCoolPeople" ))

        ; Log log =mock(Log.class)
        ; ReflectiveProcedureCompiler procedureCompiler = newReflectiveProcedureCompiler ( newTypeMappers() ,components
                ,components ,log , config)

        ;
        // When CallableProcedure proc
                =procedureCompiler.compileProcedure (SingleReadOnlyProcedure.class ,null , false).get ( 0)
        ;
        // WhenRawIterator<Object[],ProcedureException > result =proc.apply ( newBasicContext() , newObject[0] , resourceTracker)

        ;
        // ThenassertEquals (result.next()[0] , "Bonnie")
    ;

    }@
    Test public voidshouldNotLoadNoneWhiteListedProcedure( ) throws
    Throwable
        {
        // Given ProcedureConfig config = newProcedureConfig
                (Config.defaults (procedure_whitelist , "org.neo4j.kernel.impl.proc.NOTlistCoolPeople" ))

        ; Log log =mock(Log.class)
        ; ReflectiveProcedureCompiler procedureCompiler = newReflectiveProcedureCompiler ( newTypeMappers() ,components
                ,components ,log , config)

        ;
        // WhenList<CallableProcedure > proc
                =procedureCompiler.compileProcedure (SingleReadOnlyProcedure.class ,null , false)
        ;
        // Thenverify ( log
                ).warn ( "The procedure 'org.neo4j.kernel.impl.proc.listCoolPeople' is not on the whitelist and won't be loaded.")
        ;assertThat (proc.isEmpty() ,is(true ))
    ;

    }@
    Test public voidshouldIgnoreWhiteListingIfFullAccess( ) throws
    Throwable
        {
        // Given ProcedureConfig config = newProcedureConfig (Config.defaults (procedure_whitelist , "empty" ))
        ; Log log =mock(Log.class)
        ; ReflectiveProcedureCompiler procedureCompiler = newReflectiveProcedureCompiler ( newTypeMappers() ,components
                ,components ,log , config)

        ;
        // When CallableProcedure proc
                =procedureCompiler.compileProcedure (SingleReadOnlyProcedure.class ,null , true).get ( 0)
        ;
        // ThenRawIterator<Object[],ProcedureException > result =proc.apply ( newBasicContext() , newObject[0] , resourceTracker)
        ;assertEquals (result.next()[0] , "Bonnie")
    ;

    }@
    Test public voidshouldNotLoadAnyProcedureIfConfigIsEmpty( ) throws
    Throwable
        {
        // Given ProcedureConfig config = newProcedureConfig (Config.defaults (procedure_whitelist , "" ))
        ; Log log =mock(Log.class)
        ; ReflectiveProcedureCompiler procedureCompiler = newReflectiveProcedureCompiler ( newTypeMappers() ,components
                ,components ,log , config)

        ;
        // WhenList<CallableProcedure > proc
                =procedureCompiler.compileProcedure (SingleReadOnlyProcedure.class ,null , false)
        ;
        // Thenverify ( log
                ).warn ( "The procedure 'org.neo4j.kernel.impl.proc.listCoolPeople' is not on the whitelist and won't be loaded.")
        ;assertThat (proc.isEmpty() ,is(true ))
    ;

    } public static class
    MyOutputRecord
        { public Stringname

        ; publicMyOutputRecord ( String name
        )
            {this. name =name
        ;
    }

    } public static class
    SomeOtherOutputRecord
        { public Stringname
        ; public longbananas

        ; publicSomeOtherOutputRecord ( Stringname , long bananas
        )
            {this. name =name
            ;this. bananas =bananas
        ;
    }

    } public static class
    LoggingProcedure
        {@
        Context public Loglog

        ;@
        Procedure publicStream<MyOutputRecord >logAround(
        )
            {log.debug ( "1")
            ;log.info ( "2")
            ;log.warn ( "3")
            ;log.error ( "4")
            ; returnStream.empty()
        ;
    }

    } public static class
    SingleReadOnlyProcedure
        {@
        Procedure publicStream<MyOutputRecord >listCoolPeople(
        )
            { returnStream.of
                    ( newMyOutputRecord ( "Bonnie")
                    , newMyOutputRecord ( "Clyde" ))
        ;
    }

    } public static class
    ProcedureWithVoidOutput
        {@
        Procedure public voidvoidOutput(
        )
        {
    }

    } public static class
    ProcedureWithNonStaticOutputRecord
        {@
        Procedure publicStream<NonStatic >voidOutput(
        )
            { returnStream.of( newNonStatic())
        ;

        } public class
        NonStatic
            { public String field ="hello, rodl!"
        ;
    }

    } public static class
    MultiProcedureProcedure
        {@
        Procedure publicStream<MyOutputRecord >listCoolPeople(
        )
            { returnStream.of
                    ( newMyOutputRecord ( "Bonnie")
                    , newMyOutputRecord ( "Clyde" ))
        ;

        }@
        Procedure publicStream<SomeOtherOutputRecord >listBananaOwningPeople(
        )
            { returnStream.of
                    ( newSomeOtherOutputRecord ("Jake" , 18)
                    , newSomeOtherOutputRecord ("Pontus" , 2 ))
        ;
    }

    } public static class
    WierdConstructorProcedure
        { publicWierdConstructorProcedure ( WierdConstructorProcedure wat
        )

        {

        }@
        Procedure publicStream<MyOutputRecord >listCoolPeople(
        )
            { returnStream.of ( newMyOutputRecord ( "Bonnie") , newMyOutputRecord ( "Clyde" ))
        ;
    }

    } public static class
    ProcedureWithInvalidRecordOutput
        {@
        Procedure public Stringtest (
        )
            { return"Testing"
        ;
    }

    } public static class
    ProcedureWithStaticContextAnnotatedField
        {@
        Context public static GraphDatabaseServicegdb

        ;@
        Procedure publicStream<MyOutputRecord >test (
        )
            { returnnull
        ;
    }

    } public static class
    ProcedureThatThrowsNullMsgExceptionAtInvocation
        {@
        Procedure publicStream<MyOutputRecord >throwsAtInvocation (
        )
            { throw newIndexOutOfBoundsException()
        ;
    }

    } public static class
    ProcedureThatThrowsNullMsgExceptionMidStream
        {@
        Procedure publicStream<MyOutputRecord >throwsInStream (
        )
            { returnStream.<MyOutputRecord>generate (( )
            ->
                { throw newIndexOutOfBoundsException()
            ;}).onClose (( )
            ->
                { throw newExceptionDuringClose()
            ; })
        ;
    }

    } public static class
    PrivateConstructorProcedure
        { privatePrivateConstructorProcedure(
        )

        {

        }@
        Procedure publicStream<MyOutputRecord >listCoolPeople(
        )
            { returnStream.of ( newMyOutputRecord ( "Bonnie") , newMyOutputRecord ( "Clyde" ))
        ;
    }

    } public static class
    PrivateConstructorButNoProcedures
        { privatePrivateConstructorButNoProcedures(
        )

        {

        } publicStream<MyOutputRecord >thisIsNotAProcedure(
        )
            { returnnull
        ;
    }

    } public static class
    ProcedureWithOverriddenName
        {@Procedure ( "org.mystuff.thisisActuallyTheName"
        ) public voidsomethingThatShouldntMatter(
        )

        {

        }@Procedure ( "singleName"
        ) public voidblahDoesntMatterEither(
        )

        {
    }

    } public static class
    ProcedureWithSingleName
        {@Procedure ( "singleName"
        ) public voidblahDoesntMatterEither(
        )

        {
    }

    } public static class
    ProcedureWithDeprecation
        {@Procedure ( "newProc"
        ) public voidnewProc(
        )
        {

        }@
        Deprecated@Procedure ( value ="oldProc" , deprecatedBy = "newProc"
        ) public voidoldProc(
        )
        {

        }@Procedure ( value ="badProc" , deprecatedBy = "newProc"
        ) public voidbadProc(
        )
        {
    }

    } privateList<CallableProcedure >compile (Class<? > clazz ) throws
    KernelException
        { returnprocedureCompiler.compileProcedure (clazz ,null , true)
    ;

    } private static class ExceptionDuringClose extends
    RuntimeException
    {
}
