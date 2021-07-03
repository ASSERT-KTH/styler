package org.benetech.servicenet.manager;

import lombok.extern.slf4j.Slf4j;
import org.benetech.servicenet.adapter.shared.model.ImportData;
import org.benetech.servicenet.builder.ReportErrorMessageBuilder;
import org.benetech.servicenet.domain.DataImportReport;
import org.benetech.servicenet.domain.Location;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.Service;
import org.benetech.servicenet.domain.Taxonomy;
import org.benetech.servicenet.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImportManagerImpl implements ImportManager {

    @Autowired
    private ImportService importService;

    @Override
    public Organization createOrUpdateOrganization(Organization filledOrganization, String externalDbId,
                                                   ImportData importData, boolean overwriteLastUpdated) {
        try {
            return importService.createOrUpdateOrganization(filledOrganization, externalDbId, importData, overwriteLastUpdated);
        } catch (Exception e) {
            handleError(e, importData.getReport());
            return null;
        }
    }

    @Override
    public Taxonomy createOrUpdateTaxonomy(Taxonomy taxonomy, String externalDbId, String providerName,
                                           DataImportReport report) {
        try {
            return importService.createOrUpdateTaxonomy(taxonomy, externalDbId, providerName, report);
        } catch (Exception e) {
            handleError(e, report);
            return null;
        }
    }

    @Override
    public Location createOrUpdateLocation(Location filledLocation, String externalDbId, ImportData importData) {
        try {
            return importService.createOrUpdateLocation(filledLocation, externalDbId, importData);
        } catch (Exception e) {
            handleError(e, importData.getReport());
            return null;
        }
    }

    @Override
    public Service createOrUpdateService(Service filledService, String externalDbId,
                                         String providerName, DataImportReport report) {
        try {
            return importService.createOrUpdateService(filledService, externalDbId, providerName, report);
        } catch (Exception e) {
            handleError(e, report);
            return null;
        }
    }

    private void handleError(Exception e, DataImportReport report) {
        report.setErrorMessage(ReportErrorMessageBuilder.buildForError(e.getMessage(), report));
        log.error(e.getMessage(), e);
    }
}
