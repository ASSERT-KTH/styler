package org.opencb.opencga.app.cli.admin.executors.migration.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.StringUtils;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.datastore.core.QueryOptions;
import org.opencb.opencga.analysis.variant.manager.VariantStorageManager;
import org.opencb.opencga.app.cli.admin.options.MigrationCommandOptions;
import org.opencb.opencga.catalog.db.api.FileDBAdaptor;
import org.opencb.opencga.catalog.db.api.ProjectDBAdaptor;
import org.opencb.opencga.catalog.managers.CatalogManager;
import org.opencb.opencga.core.models.file.File;
import org.opencb.opencga.core.models.file.FileIndex;
import org.opencb.opencga.core.models.project.DataStore;
import org.opencb.opencga.core.models.project.Project;
import org.opencb.opencga.core.models.study.Study;
import org.opencb.opencga.storage.core.StorageEngineFactory;
import org.opencb.opencga.core.config.storage.StorageConfiguration;
import org.opencb.opencga.storage.core.metadata.models.ProjectMetadata;
import org.opencb.opencga.storage.core.metadata.StudyConfiguration;
import org.opencb.opencga.storage.core.metadata.VariantStorageMetadataManager;
import org.opencb.opencga.storage.core.variant.VariantStorageEngine;
import org.opencb.opencga.core.models.common.GenericRecordAvroJsonMixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.opencb.opencga.storage.core.variant.annotation.annotators.AbstractCellBaseVariantAnnotator.toCellBaseSpeciesName;

/**
 * Created on 10/07/18.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class NewProjectMetadataMigration {

    private final StorageConfiguration storageConfiguration;
    private final CatalogManager catalogManager;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(NewProjectMetadataMigration.class);

    public NewProjectMetadataMigration(StorageConfiguration storageConfiguration, CatalogManager catalogManager,
                                       MigrationCommandOptions.MigrateV1_4_0CommandOptions options) {
        this.storageConfiguration = storageConfiguration;
        this.catalogManager = catalogManager;
        objectMapper = new ObjectMapper()
                .addMixIn(GenericRecord.class, GenericRecordAvroJsonMixin.class);
    }

    public void migrate(String sessionId) throws Exception {
        StorageEngineFactory storageEngineFactory = StorageEngineFactory.get(storageConfiguration);

        List<Project> projects = catalogManager.getProjectManager().get(new Query(), new QueryOptions(
                QueryOptions.INCLUDE, Arrays.asList(
                ProjectDBAdaptor.QueryParams.NAME.key(),
                ProjectDBAdaptor.QueryParams.ID.key(),
                ProjectDBAdaptor.QueryParams.FQN.key(),
                ProjectDBAdaptor.QueryParams.ORGANISM.key(),
                ProjectDBAdaptor.QueryParams.STUDY.key()
        )), sessionId).getResults();

        Set<DataStore> dataStores = new HashSet<>();
        for (Project project : projects) {
            logger.info("Migrating project " + project.getName());

            for (Study study : project.getStudies()) {
                logger.info("Migrating study " + study.getName());

                long numIndexedFiles = catalogManager.getFileManager()
                        .count(study.getFqn(), new Query(FileDBAdaptor.QueryParams.INTERNAL_INDEX_STATUS_NAME.key(), Arrays.asList(
                                FileIndex.IndexStatus.TRANSFORMED,
                                FileIndex.IndexStatus.TRANSFORMING,
                                FileIndex.IndexStatus.LOADING,
                                FileIndex.IndexStatus.INDEXING,
                                FileIndex.IndexStatus.READY
                                )), sessionId)
                        .getNumTotalResults();

                if (numIndexedFiles > 0) {
                    DataStore dataStore = VariantStorageManager.getDataStore(catalogManager, study.getFqn(), File.Bioformat.VARIANT, sessionId);
                    // Check only once per datastore
                    if (dataStores.add(dataStore)) {

                        VariantStorageEngine variantStorageEngine = storageEngineFactory
                                .getVariantStorageEngine(dataStore.getStorageEngine(), dataStore.getDbName());
                        VariantStorageMetadataManager scm = variantStorageEngine.getMetadataManager();

                        Map<String, Integer> currentCounters = scm.updateProjectMetadata(projectMetadata -> {
                            if (projectMetadata == null || StringUtils.isEmpty(projectMetadata.getSpecies())) {
                                logger.info("Create ProjectMetadata for project " + project.getFqn());

                                String scientificName = toCellBaseSpeciesName(project.getOrganism().getScientificName());
                                projectMetadata = new ProjectMetadata(
                                        scientificName,
                                        project.getOrganism().getAssembly(),
                                        project.getCurrentRelease());

                            } else {
                                logger.info("ProjectMetadata already exists for project " + project.getFqn() + ". Nothing to do!");
                            }
                            return projectMetadata;
                        }).getCounters();
                        // Update counters
                        if (currentCounters.isEmpty()) {
                            logger.info(" * Update internal id counters for project " + project.getFqn());

                            Map<String, Integer> counters = new HashMap<>();

                            for (String studyName : scm.getStudyNames()) {
                                StudyConfiguration studyConfiguration = scm.getStudyConfiguration(studyName, null).first();
                                int studyId = studyConfiguration.getId();

                                updateMaxCounter(counters, "file", studyConfiguration.getFileIds().values());
                                updateMaxCounter(counters, "file_" + studyId, studyConfiguration.getFileIds().values());
                                updateMaxCounter(counters, "sample", studyConfiguration.getSampleIds().values());
                                updateMaxCounter(counters, "sample_" + studyId, studyConfiguration.getSampleIds().values());
                                updateMaxCounter(counters, "cohort", studyConfiguration.getCohortIds().values());
                                updateMaxCounter(counters, "cohort_" + studyId, studyConfiguration.getCohortIds().values());
                                updateMaxCounter(counters, "study", Collections.singleton(studyId));
                            }

                            scm.updateProjectMetadata(projectMetadata -> {
                                projectMetadata.setCounters(counters);
                                return projectMetadata;
                            });

                        }

                    }
                } else {
                    logger.info("Nothing to migrate!");
                }
            }
        }
    }

    protected void updateMaxCounter(Map<String, Integer> counters, String idType, Collection<Integer> ids) {
        Integer maxId = ids.stream().max(Integer::compareTo).orElse(0);
        counters.compute(idType, (k, value) -> value == null ? maxId : Math.max(maxId, value));
    }

}
