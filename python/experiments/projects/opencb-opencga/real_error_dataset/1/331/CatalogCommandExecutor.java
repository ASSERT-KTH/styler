/*
 * Copyright 2015-2017 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.opencga.app.cli.admin.executors;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencb.commons.datastore.core.DataStoreServerAddress;
import org.opencb.commons.datastore.mongodb.MongoDataStoreManager;
import org.opencb.opencga.analysis.demo.AnalysisDemo;
import org.opencb.opencga.app.cli.admin.AdminCliOptionsParser;
import org.opencb.opencga.catalog.exceptions.CatalogException;
import org.opencb.opencga.catalog.managers.CatalogManager;
import org.opencb.opencga.master.monitor.MonitorService;
import org.opencb.opencga.catalog.utils.CatalogDemo;
import org.opencb.opencga.core.config.Admin;
import org.opencb.opencga.core.models.Panel;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by imedina on 02/03/15.
 */
public class CatalogCommandExecutor extends AdminCommandExecutor {

    private AdminCliOptionsParser.CatalogCommandOptions catalogCommandOptions;

    public CatalogCommandExecutor(AdminCliOptionsParser.CatalogCommandOptions catalogCommandOptions) {
        super(catalogCommandOptions.commonOptions);
        this.catalogCommandOptions = catalogCommandOptions;
    }



    @Override
    public void execute() throws Exception {
        logger.debug("Executing catalog admin command line");

        String subCommandString = catalogCommandOptions.getParsedSubCommand();
        switch (subCommandString) {
            case "demo":
                demo();
                break;
            case "install":
                install();
                break;
            case "delete":
                delete();
                break;
            case "index":
                index();
                break;
            case "export":
                export();
                break;
            case "import":
                importDatabase();
                break;
            case "daemon":
                daemons();
                break;
            case "panel":
                panels();
                break;
            default:
                logger.error("Subcommand not valid");
                break;
        }

    }

    private void demo() throws CatalogException, IOException, URISyntaxException {
        if (catalogCommandOptions.demoCatalogCommandOptions.prefix != null) {
            configuration.setDatabasePrefix(catalogCommandOptions.demoCatalogCommandOptions.prefix);
        } else {
            configuration.setDatabasePrefix("demo");
        }
        configuration.setOpenRegister(true);

        if (configuration.getAdmin() == null) {
            configuration.setAdmin(new Admin());
        }

        configuration.getAdmin().setSecretKey("demo");
        configuration.getAdmin().setAlgorithm("HS256");

        CatalogDemo.createDemoDatabase(configuration, adminPassword, catalogCommandOptions.demoCatalogCommandOptions.force);
        CatalogManager catalogManager = new CatalogManager(configuration);
        sessionId = catalogManager.getUserManager().login("user1", "user1_pass");
        AnalysisDemo.insertPedigreeFile(catalogManager, Paths.get(this.appHome).resolve("scripts/examples/20130606_g1k.ped"), sessionId);
    }

    private void export() throws CatalogException {
        AdminCliOptionsParser.ExportCatalogCommandOptions commandOptions = catalogCommandOptions.exportCatalogCommandOptions;
        validateConfiguration(commandOptions);

        CatalogManager catalogManager = new CatalogManager(configuration);
        String token = catalogManager.getUserManager().loginAsAdmin(adminPassword);

        if (StringUtils.isNotEmpty(commandOptions.project)) {
            catalogManager.getProjectManager().exportReleases(commandOptions.project, commandOptions.release, commandOptions.outputDir, token);
        } else if (StringUtils.isNotEmpty(commandOptions.study) && StringUtils.isNotEmpty(commandOptions.inputFile)) {
            catalogManager.getProjectManager().exportByFileNames(commandOptions.study, Paths.get(commandOptions.outputDir).toFile(),
                    Paths.get(commandOptions.inputFile).toFile(), token);
        }
    }

    private void importDatabase() throws CatalogException, IOException {
        AdminCliOptionsParser.ImportCatalogCommandOptions commandOptions = catalogCommandOptions.importCatalogCommandOptions;
        validateConfiguration(commandOptions);

        CatalogManager catalogManager = new CatalogManager(configuration);
        String token = catalogManager.getUserManager().loginAsAdmin(adminPassword);

        catalogManager.getProjectManager().importReleases(commandOptions.owner, commandOptions.directory, token);
    }

    private void install() throws CatalogException, URISyntaxException {
        AdminCliOptionsParser.InstallCatalogCommandOptions commandOptions = catalogCommandOptions.installCatalogCommandOptions;

        validateConfiguration(commandOptions);

        this.configuration.getAdmin().setAlgorithm("HS256");

        this.configuration.getAdmin().setSecretKey(commandOptions.secretKey);
        if (StringUtils.isEmpty(configuration.getAdmin().getSecretKey())) {
            configuration.getAdmin().setSecretKey(RandomStringUtils.randomAlphabetic(16));
        }

        if (StringUtils.isEmpty(commandOptions.commonOptions.adminPassword)) {
            throw new CatalogException("No admin password found. Please, insert your password.");
        }

        CatalogManager catalogManager = new CatalogManager(configuration);
        if (catalogManager.existsCatalogDB()) {
            if (commandOptions.force) {
                // The password of the old db should match the one to be used in the new installation. Otherwise, they can obtain the same
                // results calling first to "catalog delete" and then "catalog install"
                String token = catalogManager.getUserManager().loginAsAdmin(commandOptions.commonOptions.adminPassword);
                catalogManager.deleteCatalogDB(token);
            } else {
                throw new CatalogException("A database called " + catalogManager.getCatalogDatabase() + " already exists");
            }
        }

        logger.info("\nInstalling database {} in {}\n", catalogManager.getCatalogDatabase(),
                configuration.getCatalog().getDatabase().getHosts());

        catalogManager.installCatalogDB(configuration.getAdmin().getSecretKey(), commandOptions.commonOptions.adminPassword,
                commandOptions.email, commandOptions.organization);
    }

    /**
     * Checks if the database exists.
     *
     * @return true if exists.
     */
    private boolean checkDatabaseExists(String database) {
        List<DataStoreServerAddress> dataStoreServerAddresses = new ArrayList<>();
        for (String host : configuration.getCatalog().getDatabase().getHosts()) {
            if (host.contains(":")) {
                String[] split = host.split(":");
                Integer port = Integer.valueOf(split[1]);
                dataStoreServerAddresses.add(new DataStoreServerAddress(split[0], port));
            } else {
                dataStoreServerAddresses.add(new DataStoreServerAddress(host, 27017));
            }
        }
        MongoDataStoreManager mongoDataStoreManager = new MongoDataStoreManager(dataStoreServerAddresses);
        return mongoDataStoreManager.exists(database);
    }

    private void delete() throws CatalogException, URISyntaxException {
        validateConfiguration(catalogCommandOptions.deleteCatalogCommandOptions);

        CatalogManager catalogManager = new CatalogManager(configuration);
        String token = catalogManager.getUserManager()
                .loginAsAdmin(catalogCommandOptions.deleteCatalogCommandOptions.commonOptions.adminPassword);

        if (!checkDatabaseExists(catalogManager.getCatalogDatabase())) {
            throw new CatalogException("The database " + catalogManager.getCatalogDatabase() + " does not exist.");
        }
        logger.info("\nDeleting database {} from {}\n", catalogManager.getCatalogDatabase(), configuration.getCatalog().getDatabase()
                .getHosts());
        catalogManager.deleteCatalogDB(token);
    }

    private void index() throws CatalogException {
        validateConfiguration(catalogCommandOptions.indexCatalogCommandOptions);

        CatalogManager catalogManager = new CatalogManager(configuration);
        String token = catalogManager.getUserManager()
                .loginAsAdmin(catalogCommandOptions.indexCatalogCommandOptions.commonOptions.adminPassword);

        if (!checkDatabaseExists(catalogManager.getCatalogDatabase())) {
            throw new CatalogException("The database " + catalogManager.getCatalogDatabase() + " does not exist.");
        }

        logger.info("\nChecking and installing non-existing indexes in {} in {}\n",
                catalogManager.getCatalogDatabase(), configuration.getCatalog().getDatabase().getHosts());

        catalogManager.installIndexes(token);
    }

    private void daemons() throws Exception {
        validateConfiguration(catalogCommandOptions.daemonCatalogCommandOptions);

        CatalogManager catalogManager = new CatalogManager(configuration);
        String token = catalogManager.getUserManager()
                .loginAsAdmin(catalogCommandOptions.daemonCatalogCommandOptions.commonOptions.adminPassword);

        if (catalogCommandOptions.daemonCatalogCommandOptions.start) {
            // Server crated and started
            MonitorService monitorService =
                    new MonitorService(configuration, appHome, token);
            monitorService.start();
            monitorService.blockUntilShutdown();
            logger.info("Shutting down OpenCGA Storage REST server");
        }

        if (catalogCommandOptions.daemonCatalogCommandOptions.stop) {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:" + configuration.getMonitor().getPort())
                    .path("opencga")
                    .path("monitor")
                    .path("admin")
                    .path("stop");
            Response response = target.request().get();
            logger.info(response.toString());
        }
    }

    private void panels() throws CatalogException, IOException {
        validateConfiguration(catalogCommandOptions.panelCatalogCommandOptions);

        try (CatalogManager catalogManager = new CatalogManager(configuration)) {
            String token = catalogManager.getUserManager()
                    .loginAsAdmin(catalogCommandOptions.panelCatalogCommandOptions.commonOptions.adminPassword);

            if (catalogCommandOptions.panelCatalogCommandOptions.panelAppImport) {
                catalogManager.getPanelManager().importPanelApp(token, catalogCommandOptions.panelCatalogCommandOptions.overwrite);
            } else if (StringUtils.isNotEmpty(catalogCommandOptions.panelCatalogCommandOptions.panelImport)) {
                importPanels(catalogManager, token);
            } else if (StringUtils.isNotEmpty(catalogCommandOptions.panelCatalogCommandOptions.delete)) {
                deletePanels(catalogManager, token);
            } else {
                logger.error("Expected --import or --delete parameter. Nothing to do.");
            }
        }

    }

    private void importPanels(CatalogManager catalogManager, String token) throws IOException {
        Path path = Paths.get(catalogCommandOptions.panelCatalogCommandOptions.panelImport);

        if (path.toFile().isDirectory()) {
            // Load all the json files from the directory
            try (Stream<Path> paths = Files.walk(path)) {
                paths
                        .filter(Files::isRegularFile)
                        .forEach(filePath -> {
                            // Import the panel file
                            Panel panel;
                            try {
                                panel = Panel.load(FileUtils.openInputStream(filePath.toFile()));
                            } catch (IOException e) {
                                logger.error("Could not load file {}. {}", filePath.toString(), e.getMessage());
                                return;
                            }
                            try {
                                catalogManager.getPanelManager().create(panel,
                                        catalogCommandOptions.panelCatalogCommandOptions.overwrite, token);
                                logger.info("Panel {} imported", panel.getId());
                            } catch (CatalogException e) {
                                logger.error("Could not import {} - {}", panel.getId(), e.getMessage());
                            }
                        });
            }
        } else {
            // Import the panel file
            Panel panel = Panel.load(FileUtils.openInputStream(path.toFile()));
            try {
                catalogManager.getPanelManager().create(panel, catalogCommandOptions.panelCatalogCommandOptions.overwrite,
                        token);
                logger.info("Panel {} imported", panel.getId());
            } catch (CatalogException e) {
                logger.error("Could not import {} - {}", panel.getId(), e.getMessage());
            }
        }
    }

    private void deletePanels(CatalogManager catalogManager, String token) {
        String[] panelIds = catalogCommandOptions.panelCatalogCommandOptions.delete.split(",");
        for (String panelId : panelIds) {
            try {
                catalogManager.getPanelManager().delete(panelId, token);
                logger.info("Panel {} deleted", panelId);
            } catch (CatalogException e) {
                logger.error("Could not delete panel {} - {}", panelId, e.getMessage());
            }
        }
    }

    private void validateConfiguration(AdminCliOptionsParser.CatalogDatabaseCommandOptions catalogOptions) {
        if (catalogOptions.databaseUser != null) {
            configuration.getCatalog().getDatabase().setUser(catalogOptions.databaseUser);
        }
        if (catalogOptions.databasePassword != null) {
            configuration.getCatalog().getDatabase().setPassword(catalogOptions.databasePassword);
        }
        if (catalogOptions.prefix != null) {
            configuration.setDatabasePrefix(catalogOptions.prefix);
        }
        if (catalogOptions.databaseHost != null) {
            configuration.getCatalog().getDatabase().setHosts(Collections.singletonList(catalogOptions.databaseHost));
        }
    }
}
