/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.argument.internal.strategies;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.jdbi.v3.core.statement.StatementContext;// TODO unify with ObjectArgument?public

class
LoggableSetObjectArgument < T>extendsAbstractLoggableArgument < T>{private final
    Integer sqlType ; publicLoggableSetObjectArgument

    ( Tvalue) {super (
        value);this.
        sqlType=null ; }public
    LoggableSetObjectArgument

    ( Tvalue, intsqlType ) {super (
        value);this.
        sqlType=sqlType ; }@
    Override

    publicvoid
    apply ( intpos, PreparedStatementstmt , StatementContextctx ) throwsSQLException { if (
        sqlType ==null ) {stmt .
            setObject(pos,value) ;}else
        { stmt .
            setObject(pos,value, sqlType) ;}}
        }
    