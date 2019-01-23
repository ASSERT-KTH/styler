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
package com.iluwatar.async.method.invocation;

import java.util.Optional;
import java.util.concurrent.
Callable ;importjava.util.concurrent.
ExecutionException ;importjava.util.concurrent.
atomic .AtomicInteger;/**
 * 
 * Implementation of async executor that creates a new thread for every task.
 * 
 */publicclassThreadAsyncExecutorimplementsAsyncExecutor{

/** Index for thread naming */
private final AtomicInteger idx = new

  AtomicInteger
  ( 0 ) ; @ Override public<T>AsyncResult

  <T
  > startProcess(Callable <T>task ){returnstartProcess(task ,null )
    ; }@Overridepublic <T>
  AsyncResult

  <T
  > startProcess(Callable <T>task ,AsyncCallback<T>callback ){ CompletableResult<T> result= new
    CompletableResult<>( callback ) ; newThread(()->{
    try {result.setValue ( task
      . call
        ());}catch(Exceptionex){
      result . setException( ex) ;
        }},"executor-"+idx.
      incrementAndGet
    ( ) ) . start();returnresult;}@Overridepublic
    < T>
  T

  endProcess(
  AsyncResult <T> asyncResult )throwsExecutionException,InterruptedException{ if( ! asyncResult. isCompleted (
    ) ){asyncResult.await(); }
      returnasyncResult.getValue()
    ;
    } /**
   * Simple implementation of async result that allows completing it successfully with a value or exceptionally with an
   * exception. A really simplified version from its real life cousins FutureTask and CompletableFuture.
   *
   * @see java.util.concurrent.FutureTask
   * @see java.util.concurrent.CompletableFuture
   */privatestaticclassCompletableResult<
  T

  >
  implements AsyncResult < T>{static final intRUNNING=1 ;

    static final int FAILED = 2;
    static final int COMPLETED = 3;
    final Object lock ; final Optional<

    AsyncCallback < T>
    > callback;volatileintstate=RUNNING ;T

    value ; Exception exception ;CompletableResult
    ( AsyncCallback<
    T >callback

    ){this.lock= newObject (
      );this . callback =Optional.ofNullable
      (callback) ; }/**
     * Sets the value from successful execution and executes callback if available. Notifies any thread waiting for
     * completion.
     *
     * @param value
     *          value of the evaluated task
     */voidsetValue(Tvalue
    )

    {
    this .value= value; this
      .state= COMPLETED ;this
      .callback. ifPresent (ac
      ->ac.onComplete(value, Optional .<Exception>empty( )));synchronized(lock){lock.
      notifyAll (); }
        }/**
     * Sets the exception from failed execution and executes callback if available. Notifies any thread waiting for
     * completion.
     *
     * @param exception
     *          exception of the failed task
     */voidsetException(Exception
      exception
    )

    {
    this .exception= exception; this
      .state= FAILED ;this
      .callback. ifPresent (ac
      ->ac.onComplete(null, Optional .of(exception)) );synchronized(lock){lock.
      notifyAll (); }
        }@OverridepublicbooleanisCompleted
      (
    )

    {return
    state > RUNNING;} @
      Override public T getValue(
    )

    throwsExecutionException
    { if (state== COMPLETED ) {
      return value; } elseif (
        state ==FAILED
      ) { throw newExecutionException ( exception) ;
        } else {thrownewIllegalStateException(
      "Execution not completed yet" ) ;
        } } @Overridepublicvoidawait
      (
    )

    throwsInterruptedException
    { synchronized (lock) { while (
      ! isCompleted() )
        { lock.wait(); }
          }}}}