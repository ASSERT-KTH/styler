/*
 * Copyright (c) 2013-2018 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package fr.inria.atlanmod.neoemf.data.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import fr.inria.atlanmod.commons.annotation.Static;
import fr.inria.atlanmod.neoemf.data.AbstractBackendFactory;
import fr.inria.atlanmod.neoemf.data.Backend;
import fr.inria.atlanmod.neoemf.data.BackendFactory;

import fr.inria.atlanmod.neoemf.data.mongodb.config.MongoDbConfig;

import java.net.URL;
import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A {@link BackendFactory} that creates {@link MongoDbBackend} instances.
 */
@ParametersAreNonnullByDefault
public class MongoDbBackendFactory extends AbstractBackendFactory<MongoDbConfig> {

    /**
     * The literal description of the factory.
     */
    private static final String NAME = "mongodb";

    /**
     * Constructs a new {@code MongoDbBackendFactory}.
     */
    protected MongoDbBackendFactory() {
    }

    /**
     * Returns the instance of this class.
     *
     * @return the instance of this class
     */
    @Nonnull
    public static BackendFactory getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Nonnull
    @Override
    protected Backend createRemoteBackend(URL url, MongoDbConfig config) throws Exception
    {
        final boolean isReadOnly = config.isReadOnly();

        // TODO Start/Create the database

        //TODO Move this to MongoDbConfig / use url in parameters
        final MongoClientURI mongoClientURI = new MongoClientURI("mongodb://localhost:27017");
        final String databaseName = "neoemf-ter";

        MongoClient client = new MongoClient(mongoClientURI);

        MongoDatabase database = client.getDatabase(databaseName);

        return createMapper(config.getMapping());
    }

    /**
     * The initialization-on-demand holder of the singleton of this class.
     */
    @Static
    private static final class Holder {

        /**
         * The instance of the outer class.
         */
        static final BackendFactory INSTANCE = new MongoDbBackendFactory();
    }
}
