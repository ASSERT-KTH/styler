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
package com.selfxdsd.core.contracts.invoices;

import com.selfxdsd.api.InvoicedTask;
import com.selfxdsd.api.InvoicedTasks;
import com.selfxdsd.api.storage.Storage;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Invoiced tasks belonging to an Invoice.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.7
 */
public final class InvoiceTasks implements InvoicedTasks {

    /**
     * ID of the Invoice to which these InvoicedTasks belong.
     */
    private final int invoiceId;

    /**
     * Storage context.
     */
    private final Storage storage;

    /**
     * Stored invoiced tasks.
     * We use a stream because we don't want to load in
     * memory all the contract invoices.
     * <br>
     * In order to "reuse" them, since streams are one time use only,
     * we wrap the stream in a supplier, .
     */
    private final Supplier<Stream<InvoicedTask>> tasks;

    /**
     * Ctor.
     *
     * @param invoiceId ID of the Invoice to which these
     *  InvoiceTasks belong.
     * @param tasks Supplier of the InvoicedTask Stream.
     * @param storage Storage.
     */
    public InvoiceTasks(
        final int invoiceId,
        final Supplier<Stream<InvoicedTask>> tasks,
        final Storage storage
    ) {
        this.invoiceId = invoiceId;
        this.storage = storage;
        this.tasks = tasks;
    }

    @Override
    public InvoicedTasks ofInvoice(final int invoiceId) {
        if(this.invoiceId == invoiceId) {
            return this;
        }
        throw new IllegalStateException(
            "Already seeing the tasks invoiced with Invoice #" + this.invoiceId
          + ", you cannot see other InvoicedTasks here."
        );
    }

    @Override
    public Iterator<InvoicedTask> iterator() {
        return this.tasks.get().iterator();
    }
}
