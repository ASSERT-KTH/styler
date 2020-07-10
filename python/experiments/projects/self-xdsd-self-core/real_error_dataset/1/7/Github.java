/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core;

import com.selfxdsd.api.Invitations;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.api.User;
import java.net.URI;

/**
 * Github as a Provider.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #26:30min Bring in Gizzly mock HTTP Server (see jcabi-http),
 *  so we can mock the Github server and write unit tests for the methods
 *  that perform HTTP Requests.
 */
public final class Github implements Provider {

    /**
     * User.
     */
    private final User user;

    /**
     * Github's URI.
     */
    private final URI uri;

    /**
     * Storge where we might save some stuff.
     */
    private final Storage storage;

    /**
     *Token used for making API Requests which require
     *user authentication.
     */
    private final String accessToken;

    /**
     * Constructor.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     */
    public Github(final User user, final Storage storage) {
        this(user, storage, URI.create("https://api.github.com"));
    }

    /**
     * Constructor.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     * @param uri Base URI of Github's API.
     */
    public Github(final User user, final Storage storage, final URI uri) {
        this(user, storage, uri, "");
    }

    /**
     * Private ctor, it can only be used by the withToken(...) method
     * to return an instance which has a token.
     * @param user User.
     * @param storage Self Storage
     * @param uri Base URI of the provider's API (e.g. api.github.com)
     * @param accessToken Access token.
     */
    private Github(
        final User user, final Storage storage,
        final URI uri, final String accessToken
    ) {
        this.user = user;
        this.storage = storage;
        this.uri = uri;
        this.accessToken = accessToken;
    }

    @Override
    public String name() {
        return "github";
    }

    @Override
    public Repo repo(final String name) {
        final URI repo = URI.create(
            this.uri.toString() + "/repos/" + this.user.username() + "/" + name
        );
        return new GithubRepo(this.user, repo, this.storage);
    }

    @Override
    public Invitations invitations() {
        if(this.accessToken == null || this.accessToken.isEmpty()) {
            throw new IllegalStateException(
                "Can't fetch invitations without a user's access token"
            );
        }
        return new GithubRepoInvitations(
            URI.create(
                this.uri.toString() + "/user/repository_invitations"
            ),
            this.accessToken
        );
    }

    @Override
    public Provider withToken(final String accessToken) {
        return new Github(this.user, this.storage, this.uri, accessToken);
    }
}
