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
package com.selfxdsd.core.mock;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.tasks.StoredTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory Tasks for test purposes.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #134:30min Implement and unit test method
 *  ofContributor(...) which should return the specified
 *  Contributor tasks using class
 *  {@link com.selfxdsd.core.tasks.ContributorTasks}.
 */
public final class InMemoryTasks implements Tasks {

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Tasks "table".
     */
    private final Map<InMemoryTasks.TaskKey, Task> tasks = new HashMap<>();

    /**
     * Ctor.
     * @param storage Parent storage.
     */
    public InMemoryTasks(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public Iterator<Task> iterator() {
        return this.tasks.values().iterator();
    }

    @Override
    public Task getById(
        final String issueId,
        final String repoFullName,
        final String provider
    ) {
        return this.tasks.get(new TaskKey(issueId, repoFullName, provider));
    }

    @Override
    public Task register(final Issue issue) {
        final Project project = this.storage.projects().getProjectById(
            issue.repoFullName(), issue.provider()
        );
        if(project == null) {
            throw new IllegalStateException(
                "Project not found, can't register Issue."
            );
        } else {
            final Task newTask = new StoredTask(
                project, issue, issue.role(), issue.provider(), this.storage
            );
            this.tasks.put(
                new TaskKey(
                    issue.issueId(),
                    issue.repoFullName(),
                    issue.provider()
                ),
                newTask
            );
            return newTask;
        }
    }

    @Override
    public Tasks ofProject(
        final String repoFullName,
        final String repoProvider
    ) {
        return null;
    }

    @Override
    public Tasks ofContributor(final String username, final String provider) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * A Task's primary key.
     */
    public static final class TaskKey {

        /**
         * Issue ID.
         */
        private final String issueId;

        /**
         * Repo full name.
         */
        private final String repoFullName;

        /**
         * Provider.
         */
        private final String provider;

        /**
         * Constructor.
         * @param issueId Given Issue ID.
         * @param repoFullName Repo full name.
         * @param provider Given provider.
         */
        TaskKey(
            final String issueId,
            final String repoFullName,
            final String provider
        ) {
            this.issueId = issueId;
            this.repoFullName = repoFullName;
            this.provider = provider;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            final TaskKey taskKey = (TaskKey) object;
            return this.issueId.equals(taskKey.issueId)
                && this.provider.equals(taskKey.provider)
                && this.repoFullName.equals(taskKey.repoFullName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                this.issueId,
                this.provider,
                this.repoFullName
            );
        }
    }
}
