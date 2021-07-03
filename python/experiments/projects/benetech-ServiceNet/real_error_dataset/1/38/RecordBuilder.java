package org.benetech.servicenet.service.factory.records.builder;

import static org.benetech.servicenet.service.factory.records.builder.FilteredEntityBuilder.buildCollection;
import static org.benetech.servicenet.service.factory.records.builder.FilteredEntityBuilder.buildObject;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.benetech.servicenet.domain.Contact;
import org.benetech.servicenet.domain.DailyUpdate;
import org.benetech.servicenet.domain.FieldExclusion;
import org.benetech.servicenet.domain.Location;
import org.benetech.servicenet.domain.LocationExclusion;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.Service;
import org.benetech.servicenet.domain.UserProfile;
import org.benetech.servicenet.service.UserService;
import org.benetech.servicenet.service.dto.ActivityRecordDTO;
import org.benetech.servicenet.service.dto.ConflictDTO;
import org.benetech.servicenet.service.dto.ContactDTO;
import org.benetech.servicenet.service.dto.DailyUpdateDTO;
import org.benetech.servicenet.service.dto.FieldExclusionDTO;
import org.benetech.servicenet.service.dto.LocationRecordDTO;
import org.benetech.servicenet.service.dto.OrganizationDTO;
import org.benetech.servicenet.service.dto.OrganizationMatchDTO;
import org.benetech.servicenet.service.dto.OwnerDTO;
import org.benetech.servicenet.service.dto.ProviderRecordDTO;
import org.benetech.servicenet.service.dto.UserDTO;
import org.benetech.servicenet.service.dto.external.RecordDetailsDTO;
import org.benetech.servicenet.service.dto.ServiceRecordDTO;
import org.benetech.servicenet.service.dto.external.RecordDetailsOrganizationDTO;
import org.benetech.servicenet.service.mapper.ContactMapper;
import org.benetech.servicenet.service.mapper.DailyUpdateMapper;
import org.benetech.servicenet.service.mapper.FieldExclusionMapper;
import org.benetech.servicenet.service.mapper.LocationMapper;
import org.benetech.servicenet.service.mapper.OrganizationMapper;
import org.benetech.servicenet.service.mapper.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordBuilder {

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private FieldExclusionMapper exclusionMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private DailyUpdateMapper dailyUpdateMapper;

    public ActivityRecordDTO buildBasicRecord(Organization organization, ZonedDateTime lastUpdated,
        List<ConflictDTO> conflictDTOS, Set<LocationExclusion> locationExclusions) {
        OwnerDTO owner = userService.getUserDtoOfOrganization(organization);
        return new ActivityRecordDTO(
            mapOrganization(organization),
            lastUpdated,
            mapLocations(filterLocations(organization.getLocations(), locationExclusions)),
            mapServices(organization.getServices()),
            mapContacts(organization.getContacts()),
            new HashSet<>(),
            conflictDTOS,
            owner);
    }

    public RecordDetailsDTO buildRecordDetails(Organization organization, List<ConflictDTO> conflictDTOs,
        List<OrganizationMatchDTO> orgMatchDTOs, Set<RecordDetailsOrganizationDTO> partnerOrgs
    ) {
        return new RecordDetailsDTO(
            mapOrganizationForRecordDetails(organization),
            partnerOrgs,
            orgMatchDTOs,
            conflictDTOs
        );
    }

    public ActivityRecordDTO buildFilteredRecord(Organization organization, ZonedDateTime lastUpdated,
        List<ConflictDTO> conflictDTOS, Set<FieldExclusion> baseExclusions, Set<LocationExclusion> locationExclusions)
        throws IllegalAccessException {
        OwnerDTO owner = userService.getUserDtoOfOrganization(organization);
        return new ActivityRecordDTO(
            mapOrganization(buildObject(organization, Organization.class, baseExclusions)),
            lastUpdated,
            mapLocations(buildCollection(filterLocations(organization.getLocations(), locationExclusions),
                Location.class, baseExclusions)),
            mapServices(buildCollection(organization.getServices(), Service.class, baseExclusions)),
            mapContacts(buildCollection(organization.getContacts(), Contact.class, baseExclusions)),
            mapExclusions(baseExclusions),
            conflictDTOS,
            owner);
    }

    public ProviderRecordDTO buildBasicProviderRecord(Organization organization, ZonedDateTime lastUpdated,
        Set<LocationExclusion> locationExclusions) {
        UserDTO user = this.getUserDtoOfOrganization(organization);
        return new ProviderRecordDTO(
            mapOrganization(organization),
            lastUpdated,
            mapLocations(filterLocations(organization.getLocations(), locationExclusions)),
            mapServices(organization.getServices()),
            user,
            mapDailyUpdates(organization.getDailyUpdates())
        );
    }

    public ProviderRecordDTO buildFilteredProviderRecord(Organization organization, ZonedDateTime lastUpdated,
        Set<FieldExclusion> baseExclusions, Set<LocationExclusion> locationExclusions)
        throws IllegalAccessException {
        UserDTO user = this.getUserDtoOfOrganization(organization);
        return new ProviderRecordDTO(
            mapOrganization(buildObject(organization, Organization.class, baseExclusions)),
            lastUpdated,
            mapLocations(buildCollection(filterLocations(organization.getLocations(), locationExclusions),
                Location.class, baseExclusions)),
            mapServices(buildCollection(organization.getServices(), Service.class, baseExclusions)),
            user,
            mapDailyUpdates(organization.getDailyUpdates())
        );
    }

    public ProviderRecordDTO filterProviderRecord(ProviderRecordDTO providerRecord,
        Set<FieldExclusion> baseExclusions, Set<LocationExclusion> locationExclusions) throws IllegalAccessException {
        UserDTO user = userService.getUser(providerRecord.getUserLogin());

        providerRecord.setOwner(user);
        providerRecord.setLocations(filterLocationRecords(providerRecord.getLocations(), locationExclusions));

        return buildObject(providerRecord, ProviderRecordDTO.class, Organization.class, baseExclusions);
    }

    public ProviderRecordDTO filterProviderRecord(ProviderRecordDTO providerRecord,
        Set<LocationExclusion> locationExclusions) throws IllegalAccessException {
        UserDTO user = userService.getUser(providerRecord.getUserLogin());

        providerRecord.setOwner(user);
        providerRecord.setLocations(filterLocationRecords(providerRecord.getLocations(), locationExclusions));

        return providerRecord;
    }

    private Set<LocationRecordDTO> filterLocationRecords(Set<LocationRecordDTO> locations, Set<LocationExclusion> locationExclusions) {
        if (locationExclusions == null || locationExclusions.isEmpty()) {
            return locations;
        }

        return locations.stream()
            .filter(location -> locationExclusions.stream().noneMatch(exclusion -> isExcluded(location, exclusion)))
            .collect(Collectors.toSet());
    }

    private boolean isExcluded(LocationRecordDTO location, LocationExclusion exclusion) {
        return Optional.ofNullable(location.getPhysicalAddress())
            .map(address -> (StringUtils.isNotBlank(exclusion.getRegion())
                && StringUtils.containsIgnoreCase(address.getRegion(), exclusion.getRegion()))
                || (StringUtils.isNotBlank(exclusion.getCity())
                && StringUtils.containsIgnoreCase(address.getCity(), exclusion.getCity())))
            .orElse(false);
    }

    private Set<Location> filterLocations(Set<Location> locations, Set<LocationExclusion> locationExclusions) {
        if (locationExclusions == null || locationExclusions.isEmpty()) {
            return locations;
        }

        return locations.stream()
            .filter(location -> locationExclusions.stream().noneMatch(exclusion -> isExcluded(location, exclusion)))
            .collect(Collectors.toSet());
    }

    private boolean isExcluded(Location location, LocationExclusion exclusion) {
        return Optional.ofNullable(location.getPhysicalAddress())
            .map(address -> (StringUtils.isNotBlank(exclusion.getRegion())
                && StringUtils.containsIgnoreCase(address.getRegion(), exclusion.getRegion()))
                || (StringUtils.isNotBlank(exclusion.getCity())
                && StringUtils.containsIgnoreCase(address.getCity(), exclusion.getCity())))
            .orElse(false);
    }

    private Set<DailyUpdateDTO> mapDailyUpdates(Set<DailyUpdate> dailyUpdates) {
        return dailyUpdates.stream()
            .map(dailyUpdateMapper::toDto)
            .collect(Collectors.toSet());
    }

    private Set<FieldExclusionDTO> mapExclusions(Set<FieldExclusion> exclusions) {
        return exclusions.stream()
            .map(exclusionMapper::toDto).collect(Collectors.toSet());
    }

    private Set<LocationRecordDTO> mapLocations(Set<Location> locations) {
        return locations.stream()
            .map(locationMapper::toRecord)
            .collect(Collectors.toSet());
    }

    private Set<ServiceRecordDTO> mapServices(Set<Service> services) {
        return services.stream()
            .map(serviceMapper::toRecord)
            .collect(Collectors.toSet());
    }

    private Set<ContactDTO> mapContacts(Set<Contact> contacts) {
        return contacts.stream()
            .map(contactMapper::toDto)
            .collect(Collectors.toSet());
    }

    private OrganizationDTO mapOrganization(Organization organization) {
        return organizationMapper.toDto(organization);
    }

    private RecordDetailsOrganizationDTO mapOrganizationForRecordDetails(Organization organization) {
        return organizationMapper.toRecordDetailsDto(organization);
    }

    private UserDTO getUserDtoOfOrganization(Organization organization) {
        UserDTO result = null;
        Set<UserProfile> userProfiles = organization.getUserProfiles();
        if (userProfiles.size() > 0) {
            UserProfile userProfile = new ArrayList<UserProfile>(userProfiles).get(userProfiles.size() - 1);
            result = userService.getUser(userProfile.getLogin());
        }
        return result;
    }
}
