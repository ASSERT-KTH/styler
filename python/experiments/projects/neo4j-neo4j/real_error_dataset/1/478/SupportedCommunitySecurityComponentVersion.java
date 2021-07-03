/*
 * Copyright (c) 2002-2020 "Neo4j,"
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
package org.neo4j.server.security.systemgraph.versions;

import java.util.Optional;

import org.neo4j.cypher.internal.security.SecureHasher;
import org.neo4j.cypher.internal.security.SystemGraphCredential;
import org.neo4j.dbms.database.ComponentVersion;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.impl.security.User;
import org.neo4j.logging.Log;
import org.neo4j.server.security.auth.UserRepository;
import org.neo4j.string.UTF8;
import org.neo4j.util.Preconditions;

import static java.lang.String.format;
import static org.neo4j.kernel.api.security.AuthManager.INITIAL_PASSWORD;
import static org.neo4j.kernel.api.security.AuthManager.INITIAL_USER_NAME;
import static org.neo4j.server.security.systemgraph.SystemGraphRealmHelper.IS_SUSPENDED;
import static org.neo4j.server.security.systemgraph.UserSecurityGraphComponentVersion.LATEST_COMMUNITY_SECURITY_COMPONENT_VERSION;

public class SupportedCommunitySecurityComponentVersion extends KnownCommunitySecurityComponentVersion
{
    private final UserRepository userRepository;
    private final SecureHasher secureHasher;

    SupportedCommunitySecurityComponentVersion( ComponentVersion componentVersion, Log log, UserRepository userRepository )
    {
        super( componentVersion, log );
        this.userRepository = userRepository;
        this.secureHasher = new SecureHasher();
    }

    @Override
    public void upgradeSecurityGraph( Transaction tx, KnownCommunitySecurityComponentVersion latest )
    {
        Preconditions.checkState( latest.version == LATEST_COMMUNITY_SECURITY_COMPONENT_VERSION,
                format("Latest version should be %s but was %s", LATEST_COMMUNITY_SECURITY_COMPONENT_VERSION, latest.version ));
        this.setVersionProperty( tx, latest.version );
    }

    @Override
    public void setupUsers( Transaction tx ) throws Exception
    {
        addDefaultUser( tx );
    }

    private void addDefaultUser( Transaction tx ) throws Exception
    {
        Optional<User> initialUser = getInitialUser();
        if ( initialUser.isPresent() )
        {
            User user = initialUser.get();
            log.info( String.format( "Setting up initial user from `auth.ini` file: %s", user.name() ) );
            addUser( tx, INITIAL_USER_NAME, user.credentials(), user.passwordChangeRequired(), user.hasFlag( IS_SUSPENDED ) );
        }
        else
        {
            SystemGraphCredential credential = SystemGraphCredential.createCredentialForPassword( UTF8.encode( INITIAL_PASSWORD ), secureHasher );
            log.info( String.format( "Setting up initial user from defaults: %s", INITIAL_USER_NAME ) );
            addUser( tx, INITIAL_USER_NAME, credential, true, false );
        }
    }

    @Override
    public void updateInitialUserPassword( Transaction tx ) throws Exception
    {
        Optional<User> initialUser = getInitialUser();
        if ( initialUser.isPresent() )
        {
            updateInitialUserPassword( tx, initialUser.get() );
        }
        else
        {
            log.debug( "Not updating initial user password: No initial user found in `auth.ini`" );
        }
    }

    private Optional<User> getInitialUser() throws Exception
    {
        userRepository.start();
        log.debug( "Opened `auth.ini` file to find the initial user" );
        if ( userRepository.numberOfUsers() == 0 )
        {
            log.debug( "Not updating initial user password: No initial user found in `auth.ini`" );
        }
        if ( userRepository.numberOfUsers() == 1 )
        {
            // In alignment with InternalFlatFileRealm we only allow the INITIAL_USER_NAME here for now
            // (This is what we get from the `set-initial-password` command
            User initialUser = userRepository.getUserByName( INITIAL_USER_NAME );
            if ( initialUser == null )
            {
                String errorMessage = "Invalid `auth.ini` file: the user in the file is not named " + INITIAL_USER_NAME;
                log.error( errorMessage );
                throw new IllegalStateException( errorMessage );
            }
            log.debug( "Valid `auth.ini` file: found initial user" );
            return Optional.of( initialUser );
        }
        else if ( userRepository.numberOfUsers() > 1 )
        {
            String errorMessage = "Invalid `auth.ini` file: the file contains more than one user";
            log.error( errorMessage );
            throw new IllegalStateException( errorMessage );
        }
        return Optional.empty();
    }
}
