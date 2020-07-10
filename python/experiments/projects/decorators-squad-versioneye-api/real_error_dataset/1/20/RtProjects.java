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
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;

import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;

/**
 * Real implementation of {@link Projects}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 37d89961ebdf3729c3fe03fea7ef883ac1087c0a $
 * @since 1.0.0
 */
final class RtProjects implements Projects {

    /**
     * HTTP request for /projects.
     */
    private Request req;
    
    /**
     * The team responsible for these projects.
     */
    private Team team;
    
    /**
     * Ctor.
     * @param req Request for /projects.
     * @param team Team responsible for these projects;
     */
    RtProjects(final Request req, final Team team) {
        this.req = req.uri()
            .path("/projects")
            .queryParam("api_key", team.organization().apiKey())
            .back();
        this.team = team;
    }
    
    @Override
    public List<Project> fetch() throws IOException {
        final List<Project> projects = new ArrayList<>();
        final JsonArray fetched = this.req.uri()
            .queryParam("orga_name", this.team.organization().name())
            .queryParam("team_name", this.team.name()).back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json()
            .readArray();
        for(int idx = 0; idx<fetched.size(); idx++) {
            projects.add(
                new RtProject(this.req, this.team, fetched.getJsonObject(idx))
            );
        }
        return projects;
    }

    @Override
    public Team team() {
        return this.team;
    }

}
