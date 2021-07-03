package nl.knaw.huygens.timbuctoo.v5.rml;

import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.ImportPropertyDescriptions;
import nl.knaw.huygens.timbuctoo.bulkupload.savers.RdfSaver;
import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.ThrowingErrorHandler;
import nl.knaw.huygens.timbuctoo.rml.datasource.joinhandlers.HashMapBasedJoinHandler;
import nl.knaw.huygens.timbuctoo.server.endpoints.v2.bulkupload.JexlRowFactory;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataProvider;
import nl.knaw.huygens.timbuctoo.v5.dataset.DummyDataProvider;
import nl.knaw.huygens.timbuctoo.v5.dataset.EntityProcessor;
import nl.knaw.huygens.timbuctoo.v5.dataset.RdfProcessor;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.NonPersistentBdbDatabaseCreator;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfSerializer;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class DataSourceStoreTest {

  @Test
  public void itWorks() throws Exception {
    NonPersistentBdbDatabaseCreator dbCreator = new NonPersistentBdbDatabaseCreator();
    DummyDataProvider dataProvider = new DummyDataProvider();
    DataSourceStore dataSourceStore = new DataSourceStore(
      "userId",
      "dataSetId",
      dbCreator,
      new DataProvider() {
        @Override
        public void subscribeToRdf(RdfProcessor processor, String cursor) {
          RdfSaver instance = new RdfSaver("dataSetId", "fileName", new RdfSerializer() {
            @Override
            public MediaType getMediaType() {
              return null;
            }

            @Override
            public Charset getCharset() {
              return null;
            }

            @Override
            public void onPrefix(String prefix, String iri) throws LogStorageFailedException {

            }

            @Override
            public void onRelation(String subject, String predicate, String object, String graph)
              throws LogStorageFailedException {
              try {
                processor.addRelation("", subject, predicate, object, graph);
              } catch (RdfProcessingFailedException e) {
                throw new LogStorageFailedException(e.getCause());
              }
            }

            @Override
            public void onValue(String subject, String predicate, String value, String valueType, String graph)
              throws LogStorageFailedException {
              try {
                processor.addValue("", subject, predicate, value, valueType, graph);
              } catch (RdfProcessingFailedException e) {
                throw new LogStorageFailedException(e.getCause());
              }
            }

            @Override
            public void onLanguageTaggedString(String subject, String predicate, String value, String language, String graph)
              throws LogStorageFailedException {
              try {
                processor.addValue("", subject, predicate, value, language, graph);
              } catch (RdfProcessingFailedException e) {
                throw new LogStorageFailedException(e.getCause());
              }
            }

            @Override
            public void close() throws LogStorageFailedException {
              try {
                processor.finish();
              } catch (RdfProcessingFailedException e) {
                throw new LogStorageFailedException(e.getCause());
              }
            }
          });
          try {
            processor.start();
          } catch (RdfProcessingFailedException e) {
            throw new RuntimeException(e.getCause());
          }

          final String collection1 = instance.addCollection("collection1");
          ImportPropertyDescriptions importPropertyDescriptions = new ImportPropertyDescriptions();
          importPropertyDescriptions.getOrCreate(1).setPropertyName("propName1");
          importPropertyDescriptions.getOrCreate(2).setPropertyName("propName2");
          instance.addPropertyDescriptions(collection1, importPropertyDescriptions);
          instance.addEntity(collection1, ImmutableMap.of("propName1", "value1", "propName2", "val2"));
          instance.addEntity(collection1, ImmutableMap.of("propName1", "entVal1", "propName2", "entVal2"));
          final String collection2 = instance.addCollection("collection2");
          ImportPropertyDescriptions importPropertyDescriptions1 = new ImportPropertyDescriptions();
          importPropertyDescriptions1.getOrCreate(1).setPropertyName("prop3");
          importPropertyDescriptions1.getOrCreate(2).setPropertyName("prop4");
          instance.addPropertyDescriptions(collection1, importPropertyDescriptions1);
          instance.addEntity(collection2, ImmutableMap.of("prop3", "value1", "prop4", "val2"));
          instance.addEntity(collection2, ImmutableMap.of("prop3", "entVal1", "prop4", "entVal2"));

        }

        @Override
        public void subscribeToEntities(EntityProcessor processor, String cursor) {

        }
      }
    );

    RdfDataSource rdfDataSource = new RdfDataSource(dataSourceStore,
      "http://timbuctoo/collections/dataSetId/fileName/1",
      new JexlRowFactory(ImmutableMap.of(), new HashMapBasedJoinHandler())
    );
    RdfDataSource rdfDataSource2 = new RdfDataSource(dataSourceStore,
      "http://timbuctoo/collections/dataSetId/fileName/2",
      new JexlRowFactory(ImmutableMap.of(), new HashMapBasedJoinHandler())
    );

    final List<String> collection1;
    final List<String> collection2;
    try (Stream<Row> stream = rdfDataSource.getRows(new ThrowingErrorHandler())) {
      collection1 = stream
        .map(x -> x.getRawValue("propName1") + ":" + x.getRawValue("propName2"))
        .collect(toList());
    }
    try (Stream<Row> stream = rdfDataSource2.getRows(new ThrowingErrorHandler())) {
      collection2 = stream
        .map(x -> x.getRawValue("prop3") + ":" + x.getRawValue("prop4"))
        .collect(toList());
    }

    assertThat(collection1, contains("value1:val2", "entVal1:entVal2"));
    assertThat(collection2, contains("value1:val2", "entVal1:entVal2"));
    dbCreator.close();
  }

}
