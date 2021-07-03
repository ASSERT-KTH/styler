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

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * Mock Me API for unit testing.
 * @author Sherif Waly (sherifwaly95@gmail.com)
 * @version $Id: f4d72cb380150d0ad165c5bcf6e42510bc52f0ed $
 * @since 1.0.0
 */
final class MkMe implements Me {

    /**
     * VersionEye server.
     */
    private MkServer server;
    
    /**
     * Authenticated user.
     */
    private String username;
    
    /**
     * Ctor.
     * @param server VersionEye server storage.
     * @param username The authenticated user's name.
     */
    MkMe(final MkServer server, final String username) {
        this.server = server;
        this.username = username;
    }
    
    @Override
    public Authenticated about() throws IOException {
        final JsonArray online = this.server.storage().build()
            .getJsonArray("authenticated");
        for(int idx = 0; idx < online.size(); idx++) {
            final JsonObject user = online.getJsonObject(idx);
            if (user.getJsonObject(this.username) != null) {
                return new MkJsonAuthenticated(
                    user.getJsonObject(this.username)
                );
            }
        }
        throw new IllegalStateException(
            "User " + this.username + " is not logged in!"
        );
    }

    @Override
    public Comments comments() {
        throw new UnsupportedOperationException(
            "Mock User Comments API is not yet implemented."
        );
    }

    @Override
    public Favorites favorites() {
        throw new UnsupportedOperationException(
            "Mock User Favorites API is not yet implemented."
        );
    }

}
