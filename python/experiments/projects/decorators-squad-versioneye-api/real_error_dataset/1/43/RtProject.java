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
import com.jcabi.http.Request;

/**
 * Real implementation of {@link Project}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 2a0d9f205e782f6515ae5526279ac95bb6ea3f4c $
 * @since 1.0.0
 */
final class RtProject implements Project {

    /**
     * Project's team.
     */
    private Team team;
    
    /**
     * HTTP request.
     */
    private Request req;
    
    /**
     * This project as Json.
     */
    private JsonObject project;

    /**
     * Ctor.
     * @param req HTTP request for this project.
     * @param team This project's team.
     * @param project This project as Json.
     */
    RtProject(final Request req, final Team team, final JsonObject project){
        this.req = req;
        this.team = team;
        this.project = project;
    }
    
    @Override
    public Team team() {
        return this.team;
    }

    @Override
    public Organization organization() {
        return this.team.organization();
    }

    @Override
    public String projectId() {
        return this.project.getString("ids");
    }

    @Override
    public String name() {
        return this.project.getString("name");
    }

    @Override
    public String type() {
        return this.project.getString("project_type", "");
    }

    @Override
    public boolean publicProject() {
        return this.project.getBoolean("public", false);
    }

    @Override
    public boolean privateScm() {
        return this.project.getBoolean("private_scm", false);
    }

    @Override
    public String period() {
        return this.project.getString("period", "");
    }

    @Override
    public String source() {
        return this.project.getString("source", "");
    }

    @Override
    public int dependencies() {
        return this.project.getInt("dep_number", 0);
    }

    @Override
    public int outdated() {
        return this.project.getInt("out_number", 0);
    }

    @Override
    public int licensesUnknown() {
        return this.project.getInt("licenses_unknown", 0);
    }

    @Override
    public int licensesRed() {
        return this.project.getInt("licenses_red", 0);
    }

    @Override
    public String whitelist() {
        return this.project.getString("license_whitelist_name", "");
    }

    @Override
    public List<String> childIds() {
        final List<String> children = new ArrayList<>();
        final JsonArray ids = this.project.getJsonArray("child_ids");
        for(int idx = 0; idx< ids.size(); idx++) {
            children.add(ids.getString(idx));
        }
        return children;
    }

    @Override
    public String parentId() {
        return this.project.getString("parent_id", "");
    }

    @Override
    public String createdAt() {
        return this.project.getString("created_at", "");
    }

    @Override
    public String updatedAt() {
        return this.project.getString("updated_at", "");
    }

    @Override
    public JsonObject json() {
        return this.project;
    }

}
