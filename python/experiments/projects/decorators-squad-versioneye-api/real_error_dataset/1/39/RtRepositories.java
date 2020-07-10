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
 * Real implementation of {@link Repositories}.
 * @author Sherif Waly (sherifwaly95@gmail.com)
 * @version $Id: 9bf2b089d62f1ad99517f1d752989a18134afd4c $
 * @since 1.0.0
 * @todo #112:30min/DEV Modify repository(repositoryKey) method to return
 *  a correct repository as json returned is incompatible with Repository
 *  one.
 */
final class RtRepositories implements Repositories {

    /**
     * HTTP request.
     */
    private Request req;
    
    /**
     * Ctor.
     * @param entry HTTP request.
     */
    RtRepositories(final Request entry) {
        this.req = entry;
    }
    
    @Override
    public List<Repository> fetch(final int page) throws IOException {
        final JsonArray results = this.req.uri()
            .queryParam("page", String.valueOf(page)).back().fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json()
            .readObject()
            .getJsonArray("repos");
        final List<Repository> repositories = new ArrayList<>();
        for(int idx=0; idx<results.size(); idx++) {
            repositories.add(
                new RtRepository(results.getJsonObject(idx), this.req)
            );
        }
        return repositories;
    }

    @Override
    public Paging paging(final int page) throws IOException {
        return new JsonPaging(
            this.req.uri()
                .queryParam("page", String.valueOf(page)).back().fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .as(JsonResponse.class)
                .json()
                .readObject()
                .getJsonObject("paging")
        );
    }

    @Override
    public Page<Repository> paginated() {
        return new RepositoriesPage(this);
    }

    @Override
    public Repositories language(final String language) {
        return new RtRepositories(
            this.req.uri().queryParam("lang", language).back()
        );
    }

    @Override
    public Repositories isPrivate(final boolean isPrivate) {
        return new RtRepositories(
            this.req.uri()
                .queryParam("private", String.valueOf(isPrivate))
                .back()
        );
    }

    @Override
    public Repositories organizationName(final String organizationName) {
        return new RtRepositories(
            this.req.uri()
                .queryParam("orga_name", organizationName)
                .back()
        );
    }

    @Override
    public Repositories organizationType(final String organizationType) {
        return new RtRepositories(
            this.req.uri()
                .queryParam("orga_type", organizationType)
                .back()
        );
    }

    @Override
    public Repositories onlyImported(final boolean onlyImported) {
        return new RtRepositories(
            this.req.uri()
                .queryParam("only_imported", String.valueOf(onlyImported))
                .back()
        );
    }

    @Override
    public Repository repository(final String repositoryKey)
        throws IOException {
        return new RtRepository(
            this.req.uri()
            .path(repositoryKey.replace('.', '~').replace('/', ':'))
            .back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json()
            .readObject(),
            this.req
        );
    }
}
