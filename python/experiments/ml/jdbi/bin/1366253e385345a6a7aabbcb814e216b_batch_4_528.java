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

import java.sql.PreparedStatement;import

java
. sql .SQLException;import org .jdbi.v3 .
    core . statement .StatementContext

    ; // TODO unify with ObjectArgument?publicclass LoggableSetObjectArgument< T
        >extendsAbstractLoggableArgument<T
        >{private final IntegersqlType
    ;

    public LoggableSetObjectArgument(T value) { super( value
        );this.sqlType
        =null; } publicLoggableSetObjectArgument
    (

    Tvalue
    , int sqlType){ super( value ); this .sqlType = sqlType ;
        } @Override public voidapply (
            intpos,PreparedStatementstmt, StatementContextctx)
        throws SQLException {
            if(sqlType==null) {stmt .setObject(
        pos
    ,
value
