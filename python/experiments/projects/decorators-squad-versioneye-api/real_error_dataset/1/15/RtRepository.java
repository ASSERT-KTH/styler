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

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * Real implementation of {@link Repository}.
 * @author Sherif Waly (sherifwaly95@gmai.com)
 * @version $Id: 436da02be9171ee6e3c5586842a290b3ba9951fa $
 * @since 1.0.0
 *
 */
final class RtRepository implements Repository {

    /**
     * This repository as a JsonObject.
     */
    private JsonObject repository;
    
    /**
     * Ctor.
     * @param repository Json comment as it is returned by the API.
     */
    RtRepository(final JsonObject repository) {
        this.repository = repository;
    }

    @Override
    public String name() {
        return this.repository.getString("name");
    }
    
    @Override
    public String fullname() {
        return this.repository.getString("fullname");
    }

    @Override
    public String language() {
        return this.repository.getString("language");
    }

    @Override
    public String ownerLogin() {
        return this.repository.getString("owner_login");
    }

    @Override
    public String ownerType() {
        return this.repository.getString("owner_type");
    }

    @Override
    public String description() {
        return this.repository.getString("description");
    }

    @Override
    public boolean isPrivate() {
        return this.repository.getBoolean("private");
    }

    @Override
    public boolean fork() {
        return this.repository.getBoolean("fork");
    }

    @Override
    public List<String> branches() {
        List<String> branches = new ArrayList<String>();
        try {
            final JsonArray jsonBranches =
                this.repository.getJsonArray("branches");
            for(int i = 0; i < jsonBranches.size(); i++) {
                branches.add(jsonBranches.getString(i));
            }
        } catch(final ClassCastException castException) {
            throw new IllegalStateException(
                "Github repository should have at least the master branch"
            );
        }
        return branches;
    }

    @Override
    public JsonObject json() {
        return this.repository;
    }
}
