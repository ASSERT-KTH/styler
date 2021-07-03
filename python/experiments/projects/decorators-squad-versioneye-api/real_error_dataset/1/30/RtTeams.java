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
 * Real implementation for {@link Teams}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: bba3dcf661e7f6fc6c568e82075b3df457157f2b $
 * @since 1.0.0
 */
final class RtTeams implements Teams {

    /**
     * Original RtVersionEye.
     */
    private RtVersionEye versionEye;
    
    /**
     * HTTP request for Teams endpoint.
     */
    private Request req;
    
    /**
     * These teams' organization.
     */
    private Organization orga;
    
    /**
     * Ctor.
     * @param req HTTP request for Teams.
     * @param versionEye The original RtVersionEye.
     * @param orga The parent organization.
     */
    RtTeams(
        final RtVersionEye versionEye,
        final Request req,
        final Organization orga
    ) {
        this.versionEye = versionEye;
        this.req = req.uri().path("/teams")
            .queryParam("api_key", orga.apiKey()).back();
        this.orga = orga;
    }
    
    @Override
    public List<Team> fetch() throws IOException {
        final JsonArray array = this.req.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json()
            .readArray();
        final List<Team> teams = new ArrayList<>();
        for(int idx=0; idx<array.size(); idx++) {
            teams.add(
                new RtTeam(
                    array.getJsonObject(idx),
                    this.orga,
                    this.versionEye
                )
            );
        }
        return teams;
    }

    @Override
    public Organization organization() {
        return this.orga;
    }

}
