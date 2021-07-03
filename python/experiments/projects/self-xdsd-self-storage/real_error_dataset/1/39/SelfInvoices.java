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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.contracts.invoices.StoredInvoice;
import org.jooq.Record;
import org.jooq.Result;

import java.util.Iterator;

import static com.selfxdsd.storage.generated.jooq.Tables.SLF_INVOICES_XDSD;

/**
 * Invoices in Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.4
 */
public final class SelfInvoices implements Invoices {

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
    public SelfInvoices(
        final Storage storage,
        final Database database
    ) {
        this.storage = storage;
        this.database = database;
    }

    @Override
    public Invoice getById(final int id) {
        final Result<Record> result = this.database.jooq()
            .select()
            .from(SLF_INVOICES_XDSD)
            .where(SLF_INVOICES_XDSD.INVOICEID.eq(id))
            .fetch();
        if(!result.isEmpty()) {
            return this.buildInvoice(result.get(0));
        }
        return null;
    }

    @Override
    public Invoice createNewInvoice(final Contract.Id contractId) {
        return null;
    }

    @Override
    public Invoice active() {
        return null;
    }

    @Override
    public Invoices ofContract(final Contract.Id id) {
        return null;
    }

    @Override
    public Iterator<Invoice> iterator() {
        throw new UnsupportedOperationException(
            "You cannot iterate over all Invoices."
        );
    }

    /**
     * Builds an Invoice from a {@link Record}.
     * @param record Record.
     * @return Invoice.
     */
    private Invoice buildInvoice(final Record record){
        return new StoredInvoice(
            record.getValue(SLF_INVOICES_XDSD.INVOICEID),
            new Contract.Id(
                record.getValue(SLF_INVOICES_XDSD.REPO_FULLNAME),
                record.getValue(SLF_INVOICES_XDSD.USERNAME),
                record.getValue(SLF_INVOICES_XDSD.PROVIDER),
                record.getValue(SLF_INVOICES_XDSD.ROLE)
            ),
            record.getValue(SLF_INVOICES_XDSD.CREATEDAT),
            record.getValue(SLF_INVOICES_XDSD.PAYMENT_TIMESTAMP),
            record.getValue(SLF_INVOICES_XDSD.TRANSACTIONID),
            this.storage
        );
    }

}
