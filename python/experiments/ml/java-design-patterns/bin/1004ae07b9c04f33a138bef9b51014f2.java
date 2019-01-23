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
package com.iluwatar.proxy.utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.
classic.spi.
ILoggingEvent ;importch.qos.logback.core.
AppenderBase ;importorg.slf4j.

LoggerFactory ;importjava.util.
LinkedList ;importjava.util.


List
; /**
 * InMemory Log Appender Util.
 */ public class InMemoryAppenderextendsAppenderBase< ILoggingEvent
  > {privateList< ILoggingEvent > log =newLinkedList<>(

  ) ;publicInMemoryAppender (Class clazz
    ){(( Logger)LoggerFactory.getLogger(clazz)).addAppender(this
    );start(
  )

  ; }publicInMemoryAppender (
    ){(( Logger)LoggerFactory.getLogger("root")).addAppender(this
    );start(
  )

  ;}
  @ Override protectedvoidappend (ILoggingEvent eventObject
    ){log.add(eventObject
  )

  ; } publicbooleanlogContains (String message
    ) {returnlog.stream().anyMatch ( event->event.getFormattedMessage().equals(message)
  )

  ; } publicintgetLogSize (
    ) {returnlog.size(
  )
;
