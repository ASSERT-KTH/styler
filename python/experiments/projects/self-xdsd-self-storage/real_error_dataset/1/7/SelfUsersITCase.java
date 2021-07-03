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
package com.selfxdsd.storage;

import com.selfxdsd.api.User;
import com.selfxdsd.api.Users;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration tests for {@link SelfUsers}.
 * Read the package-info.java if you want to run these tests manually.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class SelfUsersITCase {

    /**
     * SelfUsers can find and return a Users from the
     * Database.
     */
    @Test
    public void returnsUser() {
        final Users users = new SelfJooq(new H2Database()).users();
        final User found = users.user("vlad", "github");
        MatcherAssert.assertThat(
            found.username(),
            Matchers.equalTo("vlad")
        );
        MatcherAssert.assertThat(
            found.provider().name(),
            Matchers.equalTo("github")
        );
        MatcherAssert.assertThat(
            found.email(),
            Matchers.equalTo("vlad@example.com")
        );
    }

    /**
     * SelfUsers returns null if the User is not found.
     */
    @Test
    public void returnsNullIfUserMissing() {
        final Users users = new SelfJooq(new H2Database()).users();
        final User found = users.user("missing", "github");
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

    /**
     * SelfUsers can be iterated.
     */
    @Test
    public void iteratesUsers() {
        final Users users = new SelfJooq(new H2Database()).users();
        for(final User user : users) {
            MatcherAssert.assertThat(user, Matchers.notNullValue());
        }
    }

}
