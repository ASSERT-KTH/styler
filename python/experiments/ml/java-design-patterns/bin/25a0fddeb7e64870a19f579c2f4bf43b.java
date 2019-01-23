/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package domainapp.integtests.tests.modules.simple;

import static org.junit.Assert.assertEquals;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import com.google.common.base.Throwables;

import domainapp.dom.modules.simple.SimpleObject;
import domainapp.dom.modules.simple.SimpleObjects;
import domainapp.fixture.modules.simple.SimpleObjectsTearDown;
import domainapp.fixture.scenarios.RecreateSimpleObjects;
import domainapp.integtests.tests.SimpleAppIntegTest;

/**
 * Fixture Pattern Integration Test
 */
public class SimpleObjectsIntegTest extends SimpleAppIntegTest

  {@
  Inject FixtureScriptsfixtureScripts
  ;@
  Inject SimpleObjectssimpleObjects

  ;@
  Test public voidtestListAll( ) throws Exception

    {
    // given RecreateSimpleObjects fs = newRecreateSimpleObjects()
    ;fixtureScripts.runFixtureScript(fs ,null)
    ;nextTransaction()

    ;
    // when finalList<SimpleObject > all =wrap(simpleObjects).listAll()

    ;
    // thenassertEquals(fs.getSimpleObjects().size() ,all.size())

    ; SimpleObject simpleObject =wrap(all.get(0))
    ;assertEquals(fs.getSimpleObjects().get(0).getName() ,simpleObject.getName())
  ;

  }@
  Test public voidtestListAllWhenNone( ) throws Exception

    {
    // given FixtureScript fs = newSimpleObjectsTearDown()
    ;fixtureScripts.runFixtureScript(fs ,null)
    ;nextTransaction()

    ;
    // when finalList<SimpleObject > all =wrap(simpleObjects).listAll()

    ;
    // thenassertEquals(0 ,all.size())
  ;

  }@
  Test public voidtestCreate( ) throws Exception

    {
    // given FixtureScript fs = newSimpleObjectsTearDown()
    ;fixtureScripts.runFixtureScript(fs ,null)
    ;nextTransaction()

    ;
    // whenwrap(simpleObjects).create("Faz")

    ;
    // then finalList<SimpleObject > all =wrap(simpleObjects).listAll()
    ;assertEquals(1 ,all.size())
  ;

  }@
  Test public voidtestCreateWhenAlreadyExists( ) throws Exception

    {
    // given FixtureScript fs = newSimpleObjectsTearDown()
    ;fixtureScripts.runFixtureScript(fs ,null)
    ;nextTransaction()
    ;wrap(simpleObjects).create("Faz")
    ;nextTransaction()

    ;
    // thenexpectedExceptions.expectCause(causalChainContains(SQLIntegrityConstraintViolationException.class))

    ;
    // whenwrap(simpleObjects).create("Faz")
    ;nextTransaction()
  ;

  } private staticMatcher< ? extendsThrowable >causalChainContains( finalClass<? >cls )
    { return newTypeSafeMatcher<Throwable>( )
      {@
      Override protected booleanmatchesSafely( Throwableitem )
        { finalList<Throwable > causalChain =Throwables.getCausalChain(item)
        ; for( Throwable throwable :causalChain )
          { if(cls.isAssignableFrom(throwable.getClass()) )
            { returntrue
          ;
        }
        } returnfalse
      ;

      }@
      Override public voiddescribeTo( Descriptiondescription )
        {description.appendText( "exception with causal chain containing " +cls.getSimpleName())
      ;
    }}
  ;
}
