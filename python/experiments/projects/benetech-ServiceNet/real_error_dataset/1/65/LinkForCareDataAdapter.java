package org.benetech.servicenet.adapter.linkforcare;

import static org.benetech.servicenet.config.Constants.LINK_FOR_CARE_PROVIDER;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.benetech.servicenet.adapter.SingleDataAdapter;
import org.benetech.servicenet.adapter.linkforcare.model.LinkForCareData;
import org.benetech.servicenet.adapter.shared.model.ImportData;
import org.benetech.servicenet.adapter.shared.model.SingleImportData;
import org.benetech.servicenet.domain.DataImportReport;
import org.benetech.servicenet.domain.Location;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.Phone;
import org.benetech.servicenet.domain.Service;
import org.benetech.servicenet.domain.ServiceTaxonomy;
import org.benetech.servicenet.domain.Taxonomy;
import org.benetech.servicenet.manager.ImportManager;
import org.benetech.servicenet.service.TransactionSynchronizationService;
import org.benetech.servicenet.type.ListType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("LinkForCareDataAdapter")
public class LinkForCareDataAdapter extends SingleDataAdapter {

    @Autowired
    private ImportManager importManager;

    @Autowired private TransactionSynchronizationService transactionSynchronizationService;

    @Override
    public DataImportReport importData(SingleImportData data) {
        Gson gson = new Gson();
        DataImportReport report;
        if (data.getData() != null) {
            try (JsonReader reader = new JsonReader(new FileReader(data.getData()))) {
                reader.beginArray();
                while (reader.hasNext()) {
                    JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                    LinkForCareData entity = gson.fromJson(jsonObject, LinkForCareData.class);
                    persistLinkForCareData(entity, data);
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            report = data.getReport();
        } else {
            List<LinkForCareData> entities = gson.fromJson(data.getSingleObjectData(), new ListType<>(LinkForCareData.class));

            report = persistLinkForCareData(entities, data);
        }
        transactionSynchronizationService.updateOrganizationMatchesWithoutSynchronization();
        return report;
    }

    private void persistLinkForCareData(LinkForCareData entity, ImportData importData) {
        LinkForCareDataMapper mapper = LinkForCareDataMapper.INSTANCE;
        try {
            Location location = getLocationToPersist(mapper, entity);
            Service service = getServiceToPersist(mapper, entity, location);
            Set<Phone> phones = getPhonesToPersist(mapper, entity);
            Organization organization = getOrganizationToPersist(mapper, entity, location, service, phones);
            importOrganization(organization, entity.getOrganizationId(), importData);
        } catch (Exception e) {
            log.warn("Skipping organization with name: " + entity.getOrganizationName(), e);
        }
    }

    private DataImportReport persistLinkForCareData(List<LinkForCareData> data, ImportData importData) {
        for (LinkForCareData entity : data) {
            persistLinkForCareData(entity, importData);
        }

        return importData.getReport();
    }

    private Organization getOrganizationToPersist(LinkForCareDataMapper mapper, LinkForCareData entity,
        Location location, Service service, Set<Phone> phones) {
        Organization organization = mapper.extractOrganization(entity);
        organization.setLocations(Set.of(location));
        organization.setServices(Set.of(service));
        phones.forEach(phone -> phone.setOrganization(organization));
        organization.setPhones(phones);
        return organization;
    }

    private Location getLocationToPersist(LinkForCareDataMapper mapper, LinkForCareData entity) {
        Location location = mapper.extractLocation(entity);
        mapper.extractPhysicalAddress(entity).ifPresent(location::setPhysicalAddress);
        return location;
    }

    private Set<Phone> getPhonesToPersist(LinkForCareDataMapper mapper, LinkForCareData entity) {
        Optional<Phone> phone = mapper.extractPhone(entity);
        Optional<Phone> additionalPhone = mapper.extractTollFreePhone(entity);
        return Stream.of(phone, additionalPhone)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
    }

    private Service getServiceToPersist(LinkForCareDataMapper mapper, LinkForCareData entity, Location location) {
        Service service = mapper.extractService(entity, location);
        mapper.extractEligibility(entity).ifPresent(service::setEligibility);
        service.setTaxonomies(getServiceTaxonomiesToPersist(mapper, entity));
        return service;
    }

    private void importOrganization(Organization organization, String externalDbId, ImportData importData) {
        importManager.createOrUpdateOrganization(organization, externalDbId, importData, false);
    }

    private Set<ServiceTaxonomy> getServiceTaxonomiesToPersist(LinkForCareDataMapper mapper, LinkForCareData entity) {
        Set<ServiceTaxonomy> serviceTaxonomies = new HashSet<>();

        Set<String> types = mapper.extractServiceTypes(entity);
        types
            .forEach(name -> {
                Taxonomy taxonomy = new Taxonomy().externalDbId(name).name(name).providerName(LINK_FOR_CARE_PROVIDER);
                serviceTaxonomies.add(new ServiceTaxonomy()
                    .taxonomy(taxonomy)
                    .providerName(LINK_FOR_CARE_PROVIDER)
                    .externalDbId(entity.getOrganizationId() + name));
            });

        return serviceTaxonomies;
    }
}
