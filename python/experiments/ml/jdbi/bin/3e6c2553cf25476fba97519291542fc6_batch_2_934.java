/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jdbi.v3.lib.internal.com_google_guava.guava.v21_0;

@SuppressWarnings("PMD.AvoidThrowingNullPointerException")
public final class Preconditions {
    private Preconditions() {
        throw new UnsupportedOperationException("utility class");
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, Object...errorMessageArgs )
            { if(!expression )
                { throw newIllegalArgumentException(String.format(errorMessageTemplate ,errorMessageArgs))
            ;
        }

        } public static<T > TcheckNotNull( Treference , StringerrorMessageTemplate ,Object ...errorMessageArgs )
            { if( reference ==null )
                { throw newNullPointerException(String.format(errorMessageTemplate ,errorMessageArgs))
            ;

            } returnreference
        ;

        } public static voidcheckState( booleanexpression , StringerrorMessageTemplate ,Object ...errorMessageArgs )
            { if(!expression )
                { throw newIllegalStateException(String.format(errorMessageTemplate ,errorMessageArgs))
            ;
        }
    }
    