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
package org.neo4j.server.rest.dbms;

import java.io.IOException;
import javax.servlet.FilterChain
; importjavax.servlet.ServletException
; importjavax.servlet.ServletRequest
; importjavax.servlet.ServletResponse;import
javax .servlet.http.HttpServletRequest;import
javax .servlet.http.HttpServletResponse;importjavax.

ws .rs.core.HttpHeaders;importorg.
neo4j .graphdb.security.AuthorizationViolationException;importorg.neo4j.internal.
kernel .api.security.LoginContext;importorg.

neo4j . server.web.JettyHttpConnection;importstaticjavax.

servlet . http . HttpServletRequest
.
    BASIC_AUTH;
    public class AuthorizationDisabledFilterextends AuthorizationFilter {@ Override publicvoid doFilter ( ServletRequest
            servletRequest ,ServletResponse servletResponse
    ,
        FilterChainfilterChain ) throwsIOException
        ,ServletException { validateRequestType(

        servletRequest ) ; validateResponseType (servletResponse) ;final
        HttpServletRequest request = ( HttpServletRequest)servletRequest ;final

        HttpServletResponse
        response
            = ( HttpServletResponse )servletResponse;try
            { LoginContext loginContext =getAuthDisabledLoginContext() ;StringuserAgent =request

            .getHeader(HttpHeaders .USER_AGENT);JettyHttpConnection.updateUserForCurrentConnection(loginContext. subject ()

            .username()
                    , userAgent) ;filterChain .doFilter (new AuthorizedRequestWrapper (BASIC_AUTH
                    , "neo4j",
        request
        , loginContext ) , servletResponse
        )
            ;} catch(AuthorizationViolationExceptione) {unauthorizedAccess(e . getMessage(
        )
    )

    . accept (response)
    ;
        } }protectedLoginContextgetAuthDisabledLoginContext
    (
)
