/**
 * Copyright (c) 2017, Mihai Emil Andronache
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.amihaiemil.versioneye;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;

/**
 * Mock VersionEye for unit testing.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 7fd8a7c2c380c88fa328e9bb1000891cec4712c7 $
 * @since 1.0.0
 * @todo #72:30min/DEV Continue implementing the mock API.
 *  Mocks for Organizations, Teams etc are needed.
 */
public final class MkVersionEye implements VersionEye {

    /**
     * VersionEye server.
     */
    private MkServer server;
    
    /**
     * Authenticated user's username.
     */
    private String username;
    
    /**
     * Ctor.
     */
    public MkVersionEye() {
        this.server = new MkJsonServer();
    }
    
    /**
     * Ctor.
     * @param authenticated Mock Authenticated User.
     */
    public MkVersionEye(final Authenticated authenticated) {
        this(new MkJsonServer(), authenticated);
    }
    
    /**
     * Ctor.
     * @param server VersionEye server storage. See {@link MkServer}
     * @param user Mock Authenticated User.
     */
    public MkVersionEye(
        final MkServer server, final Authenticated user
    ) {
        this.server = server;
        this.username = user.username();
        this.authenticate(user);
    }
    
    @Override
    public Services services() {
        return new MkServices(this.server);
    }

    @Override
    public Users users() {
        return new MkUsers(this.server);
    }

    @Override
    public VersionEye trusted() throws IOException {
        return this;
    }

    @Override
    public Me me() {
        return new MkMe(this.server, this.username);
    }
    
    /**
     * Add authenticated user to the MkServer.
     * @param authenticated The user to authenticate.
     */
    private void authenticate(final Authenticated authenticated) {
        final JsonArray online = this.server.storage().build()
            .getJsonArray("authenticated");
        final JsonArrayBuilder users = Json.createArrayBuilder();
        for(final JsonValue user: online) {
            users.add(user);
        }
        users.add(Json.createObjectBuilder().add(
            this.username, authenticated.json())
        );
        this.server.storage().add("authenticated", users.build());
    }

    @Override
    public Organizations organizations() {
        return null;
    }

    @Override
    public Github github() {
        return null;
    }
    
    @Override
    public Security security() {
        return null;
    }

}
