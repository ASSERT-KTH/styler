package edu.vanderbilt.accre.laurelin;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.sources.DataSourceRegister;
import org.apache.spark.sql.sources.v2.DataSourceOptions;
import org.apache.spark.sql.sources.v2.DataSourceV2;
import org.apache.spark.sql.sources.v2.ReadSupport;
import org.apache.spark.sql.sources.v2.reader.DataSourceReader;
import org.apache.spark.util.CollectionAccumulator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.vanderbilt.accre.laurelin.cache.CacheFactory;
import edu.vanderbilt.accre.laurelin.root_proxy.IOProfile.Event;
import edu.vanderbilt.accre.laurelin.spark_ttree.Reader;

public class Root implements DataSourceV2, ReadSupport, DataSourceRegister {
    static final Logger logger = LogManager.getLogger();

    /**
     * Accumulator for IOProfiling information
     */
    private static CollectionAccumulator<Event.Storage> ioAccum = null;

    /**
     * In Spark2.X, datasource objects have a very .. interesting lifetime where
     * they're created every time an antecedent operation needs one. Since we
     * do a lot of work to initialize the reader (loading all the files, etc..),
     * this is very unperformant
     *
     * <p>Utilize a cache where the key is the DataSourceOptions and the value
     * is the actual reader object. Unfortunately, since the readers themselves
     * are transient, we need to make these soft references so they're not
     * eagerly garbage collected
     */
    private static LoadingCache<DataSourceReaderKey,
                                DataSourceReader> dedupDataSource =
                                    CacheBuilder.newBuilder()
                                    .softValues()
                                    .maximumSize(100)
                                    .build(
                                       new CacheLoader<DataSourceReaderKey,
                                                       DataSourceReader>() {
                                            @Override
                                            public DataSourceReader load(DataSourceReaderKey key) {
                                                logger.trace("Construct new reader");
                                                DataSourceOptions options = new DataSourceOptions(key.options);
                                                boolean traceIO = key.traceIO;
                                                SparkContext context = key.context;
                                                CacheFactory basketCacheFactory = new CacheFactory();
                                                if ((traceIO) && (context != null)) {
                                                    synchronized (Root.class) {
                                                        ioAccum = new CollectionAccumulator<Event.Storage>();
                                                        context.register(ioAccum, "edu.vanderbilt.accre.laurelin.ioprofile");
                                                    }
                                                }
                                                return new Reader(options, basketCacheFactory, context, ioAccum);
                                            }
                                            });

    static class DataSourceReaderKey {
        @Override
        public int hashCode() {
            return Objects.hash(context, options, traceIO);
        }

        @Override
        public boolean equals(Object obj) {
            DataSourceReaderKey other = (DataSourceReaderKey) obj;
            return (context == other.context) && options.equals(other.options)
                    && traceIO == other.traceIO;
        }

        public DataSourceReaderKey(DataSourceOptions options, SparkContext context, boolean traceIO) {
            this.traceIO = traceIO;
            this.context = context;
            this.options = options.asMap();

        }

        boolean traceIO;
        SparkContext context;
        Map<String, String> options;
    }

    /**
     * This is called by Spark, unlike the following function that accepts a
     * SparkContext
     * @param options DS options
     */
    @Override
    public DataSourceReader createReader(DataSourceOptions options) {
        return createReader(options, SparkContext.getOrCreate(), false);
    }

    /**
     * Used for unit-tests when there is no current spark context
     * @param options DS options
     * @param context spark context to use
     * @param traceIO whether or not to trace the IO operations
     * @return new reader
     */
    public DataSourceReader createReader(DataSourceOptions options, SparkContext context, boolean traceIO) {
        try {
            return dedupDataSource.get(new DataSourceReaderKey(options, context, traceIO));
        } catch (ExecutionException e) {
            throw new RuntimeException("Could not load DataSourceReader", e);
        }
    }

    @Override
    public String shortName() {
        return "root";
    }

}
