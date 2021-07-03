/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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

import com.selfxdsd.api.ApiToken;
import com.selfxdsd.api.ApiTokens;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.StoredApiToken;
import com.selfxdsd.core.StoredUser;
import org.jooq.Record;
import org.jooq.Result;

import java.util.Iterator;
import static com.selfxdsd.storage.generated.jooq.Tables.SLF_APITOKENS_XDSD;
import static com.selfxdsd.storage.generated.jooq.tables.SlfUsersXdsd.SLF_USERS_XDSD;

/**
 * Api tokens in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.27
 * @todo #232:60min Implement method ofUser(...) and write integration
 *  tests for it.
 */
public final class SelfApiTokens implements ApiTokens {

    /**
     * Parent Storage.
     */
    private final Storage storage;

    /**
     * Database.
     */
    private final Database database;

    /**
     * Ctor.
     * @param storage Parent Storage.
     * @param database Database.
     */
    public SelfApiTokens(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public ApiToken getById(final String token) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_APITOKENS_XDSD)
            .join(SLF_USERS_XDSD)
            .on(
                SLF_APITOKENS_XDSD.USERNAME.eq(SLF_USERS_XDSD.USERNAME).and(
                    SLF_APITOKENS_XDSD.PROVIDER.eq(SLF_USERS_XDSD.PROVIDER)
                )
            ).where(SLF_APITOKENS_XDSD.TOKEN.eq(token)).fetch();
        if(result.size() > 0) {
            final Record record = result.get(0);
            return new StoredApiToken(
                this.storage,
                record.getValue(SLF_APITOKENS_XDSD.NAME),
                record.getValue(SLF_APITOKENS_XDSD.TOKEN),
                record.getValue(SLF_APITOKENS_XDSD.EXPIRESAT),
                new StoredUser(
                    record.get(SLF_USERS_XDSD.USERNAME),
                    record.get(SLF_USERS_XDSD.EMAIL),
                    record.get(SLF_USERS_XDSD.ROLE),
                    record.get(SLF_USERS_XDSD.PROVIDER),
                    this.storage
                )
            );
        }
        return null;
    }

    @Override
    public ApiTokens ofUser(final User user) {
        return null;
    }

    @Override
    public Iterator<ApiToken> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all ApiTokens in Self. "
            + "Call #ofUser(...) first."
        );
    }
}
