/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.acyclicvisitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static uk.org.lidalia.
slf4jext.Level.

INFO ;importorg.junit.jupiter.api.
AfterEach ;importorg.junit.jupiter.api.

Test ;importuk.org.lidalia.slf4jtest.
TestLogger ;importuk.org.lidalia.slf4jtest.

TestLoggerFactory
; /**
 * ConfigureForUnixVisitor test class
 */ public class

  ConfigureForUnixVisitorTest { private static final TestLogger LOGGER=TestLoggerFactory.getTestLogger(ConfigureForUnixVisitor.class

  );
  @ AfterEach publicvoidclearLoggers (
    ){TestLoggerFactory.clear(
  )

  ;}
  @ Test publicvoidtestVisitForZoom (
    ) { ConfigureForUnixVisitor conUnix =newConfigureForUnixVisitor(
    ) ; Zoom zoom =newZoom(

    );conUnix.visit(zoom

    );assertThat(LOGGER.getLoggingEvents()).extracting( "level","message").
        contains(tuple( INFO , zoom+" used with Unix configurator.")
  )
;
