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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Implementation of async executor that creates a new thread for every task.
 * 
 */
public class ThreadAsyncExecutor implements AsyncExecutor {

  /** Index for thread naming */
  private final AtomicInteger idx = new AtomicInteger(0);

  @Override
  public <T>AsyncResult<T >startProcess(Callable<T >task )
    { returnstartProcess(task ,null)
  ;

  }@
  Override public<T >AsyncResult<T >startProcess(Callable<T >task ,AsyncCallback<T >callback )
    {CompletableResult<T > result = newCompletableResult<>(callback)
    ; newThread(( ) ->
      { try
        {result.setValue(task.call())
      ; } catch( Exceptionex )
        {result.setException(ex)
      ;
    } } , "executor-" +idx.incrementAndGet()).start()
    ; returnresult
  ;

  }@
  Override public<T > TendProcess(AsyncResult<T >asyncResult ) throwsExecutionException , InterruptedException
    { if(!asyncResult.isCompleted() )
      {asyncResult.await()
    ;
    } returnasyncResult.getValue()
  ;

  }
  /**
   * Simple implementation of async result that allows completing it successfully with a value or exceptionally with an
   * exception. A really simplified version from its real life cousins FutureTask and CompletableFuture.
   *
   * @see java.util.concurrent.FutureTask
   * @see java.util.concurrent.CompletableFuture
   */ private static classCompletableResult<T > implementsAsyncResult<T >

    { static final int RUNNING =1
    ; static final int FAILED =2
    ; static final int COMPLETED =3

    ; final Objectlock
    ; finalOptional<AsyncCallback<T> >callback

    ; volatile int state =RUNNING
    ; Tvalue
    ; Exceptionexception

    ;CompletableResult(AsyncCallback<T >callback )
      {this. lock = newObject()
      ;this. callback =Optional.ofNullable(callback)
    ;

    }
    /**
     * Sets the value from successful execution and executes callback if available. Notifies any thread waiting for
     * completion.
     *
     * @param value
     *          value of the evaluated task
     */ voidsetValue( Tvalue )
      {this. value =value
      ;this. state =COMPLETED
      ;this.callback.ifPresent( ac ->ac.onComplete(value ,Optional.<Exception>empty()))
      ; synchronized(lock )
        {lock.notifyAll()
      ;
    }

    }
    /**
     * Sets the exception from failed execution and executes callback if available. Notifies any thread waiting for
     * completion.
     *
     * @param exception
     *          exception of the failed task
     */ voidsetException( Exceptionexception )
      {this. exception =exception
      ;this. state =FAILED
      ;this.callback.ifPresent( ac ->ac.onComplete(null ,Optional.of(exception)))
      ; synchronized(lock )
        {lock.notifyAll()
      ;
    }

    }@
    Override public booleanisCompleted( )
      { return state >RUNNING
    ;

    }@
    Override public TgetValue( ) throws ExecutionException
      { if( state ==COMPLETED )
        { returnvalue
      ; } else if( state ==FAILED )
        { throw newExecutionException(exception)
      ; } else
        { throw newIllegalStateException("Execution not completed yet")
      ;
    }

    }@
    Override public voidawait( ) throws InterruptedException
      { synchronized(lock )
        { while(!isCompleted() )
          {lock.wait()
        ;
      }
    }
  }
}
