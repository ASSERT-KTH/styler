package ru.kontur.vostok.hercules.sink;

import com.codahale.metrics.Meter;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kontur.vostok.hercules.configuration.Scopes;
import ru.kontur.vostok.hercules.configuration.util.PropertiesUtil;
import ru.kontur.vostok.hercules.health.MetricsCollector;
import ru.kontur.vostok.hercules.kafka.util.serialization.EventDeserializer;
import ru.kontur.vostok.hercules.kafka.util.serialization.UuidDeserializer;
import ru.kontur.vostok.hercules.protocol.Event;
import ru.kontur.vostok.hercules.util.PatternMatcher;
import ru.kontur.vostok.hercules.util.properties.PropertyDescription;
import ru.kontur.vostok.hercules.util.properties.PropertyDescriptions;
import ru.kontur.vostok.hercules.util.text.StringUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 * @author Gregory Koshelev
 */
public class Sink {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sink.class);

    private volatile boolean running = false;

    private final ExecutorService executor;
    private final String applicationId;
    private final Properties properties;
    private final Processor processor;
    private final List<PatternMatcher> patternMatchers;

    private final Duration pollTimeout;
    private final int batchSize;
    private final long availabilityTimeoutMs;

    private final Pattern pattern;
    private final KafkaConsumer<UUID, Event> consumer;

    private final Meter droppedEventsMeter;
    private final Meter processedEventsMeter;
    private final Meter rejectedEventsMeter;
    private final Meter totalEventsMeter;

    public Sink(
            ExecutorService executor,
            String applicationId,
            Properties properties,
            Processor processor,
            List<PatternMatcher> patternMatchers,
            EventDeserializer deserializer,
            MetricsCollector metricsCollector) {
        this.executor = executor;
        this.applicationId = applicationId;
        this.properties = properties;
        this.processor = processor;
        this.patternMatchers = patternMatchers;

        this.pollTimeout = Duration.ofMillis(Props.POLL_TIMEOUT_MS.extract(properties));
        this.batchSize = Props.BATCH_SIZE.extract(properties);
        this.availabilityTimeoutMs = Props.AVAILABILITY_TIMEOUT_MS.extract(properties);

        String consumerGroupId = Props.GROUP_ID.extract(properties);
        if (StringUtil.isNullOrEmpty(consumerGroupId)) {
            consumerGroupId = ConsumerUtil.toGroupId(applicationId, patternMatchers);
        }

        this.pattern = PatternMatcher.matcherListToRegexp(patternMatchers);

        Properties consumerProperties = PropertiesUtil.ofScope(properties, Scopes.CONSUMER);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProperties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, batchSize);

        UuidDeserializer keyDeserializer = new UuidDeserializer();
        EventDeserializer valueDeserializer = deserializer;

        this.consumer = new KafkaConsumer<>(consumerProperties, keyDeserializer, valueDeserializer);

        droppedEventsMeter = metricsCollector.meter("droppedEvents");
        processedEventsMeter = metricsCollector.meter("processedEvents");
        rejectedEventsMeter = metricsCollector.meter("rejectedEvents");
        totalEventsMeter = metricsCollector.meter("totalEvents");
    }

    /**
     * Start sink.
     */
    public final void start() {
        running = true;

        executor.execute(this::run);
    }

    /**
     * Stop Sink.
     */
    public final void stop() {
        running = false;

        try {
            consumer.wakeup();
        } catch (Exception ex) {
            /* ignore */
        }

        try {
            consumer.close();
        } catch (Exception ex) {
            /* ignore */
        }

        postStop();
    }

    /**
     * Check Sink running status.
     *
     * @return {@code true} if Sink is running and {@code false} if Sink is stopping
     */
    public final boolean isRunning() {
        return running;
    }

    /**
     * Main Sink logic. Sink poll events from Kafka and processes them using {@link Processor} if possible.
     * <p>
     * Sink awaits availability of {@link Processor}. Also, it controls {@link #isRunning()} during operations.
     */
    public final void run() {
        while (isRunning()) {
            try {
                if (processor.isAvailable()) {

                    subscribe();

                    while (processor.isAvailable()) {
                        ConsumerRecords<UUID, Event> pollResult;
                        try {
                            pollResult = poll();
                        } catch (WakeupException ex) {
                            /*
                             * WakeupException is used to terminate polling
                             */
                            return;
                        }

                        Set<TopicPartition> partitions = pollResult.partitions();

                        // ConsumerRecords::count works for O(n), where n is partition count
                        int eventCount = pollResult.count();
                        List<Event> events = new ArrayList<>(eventCount);

                        int droppedEvents = 0;

                        for (TopicPartition partition : partitions) {
                            List<ConsumerRecord<UUID, Event>> records = pollResult.records(partition);
                            for (ConsumerRecord<UUID, Event> record : records) {
                                Event event = record.value();
                                if (event == null) {// Received non-deserializable data, should be ignored
                                    droppedEvents++;
                                    continue;
                                }
                                events.add(event);
                            }
                        }

                        ProcessorResult result = processor.process(events);
                        if (result.isSuccess()) {
                            try {
                                commit();
                                droppedEventsMeter.mark(droppedEvents);
                                processedEventsMeter.mark(result.getProcessedEvents());
                                rejectedEventsMeter.mark(result.getRejectedEvents());
                                totalEventsMeter.mark(events.size());
                            } catch (CommitFailedException ex) {
                                LOGGER.warn("Commit failed due to rebalancing", ex);
                                continue;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("Unspecified exception has been acquired", ex);
            } finally {
                unsubscribe();
            }

            processor.awaitAvailability(availabilityTimeoutMs);
        }
    }

    /**
     * Perform additional stop operations when Event consuming was terminated.
     */
    protected void postStop() {

    }

    /**
     * Subscribe Sink. Should be called before polling
     */
    protected final void subscribe() {
        consumer.subscribe(pattern);
    }

    /**
     * Unsubscribe Sink. Should be called if Sink cannot process Events.
     */
    protected final void unsubscribe() {
        LOGGER.debug("Sink unsubscribe if any");
        try {
            consumer.unsubscribe();
        } catch (Exception ex) {
            /* ignore */
        }
    }

    /**
     * Poll Events from Kafka. Should be called when Sink subscribed.
     *
     * @return polled Events
     * @throws WakeupException if poll terminated due to shutdown
     */
    protected final ConsumerRecords<UUID, Event> poll() throws WakeupException {
        return consumer.poll(pollTimeout);
    }

    protected final void commit() {
        consumer.commitSync();
    }

    protected final void commit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        consumer.commitSync(offsets);
    }

    private static class Props {
        static final PropertyDescription<Long> POLL_TIMEOUT_MS =
                PropertyDescriptions.longProperty("pollTimeoutMs").
                        withDefaultValue(6_000L).
                        build();

        static final PropertyDescription<Integer> BATCH_SIZE =
                PropertyDescriptions.integerProperty("batchSize").
                        withDefaultValue(1000).
                        build();

        static final PropertyDescription<String> GROUP_ID =
                PropertyDescriptions.stringProperty("groupId").
                        withDefaultValue(null).
                        build();

        static final PropertyDescription<Long> AVAILABILITY_TIMEOUT_MS =
                PropertyDescriptions.longProperty("availabilityTimeoutMs").
                        withDefaultValue(2_000L).
                        build();
    }
}
