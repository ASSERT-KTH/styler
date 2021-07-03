package org.benetech.servicenet.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.benetech.servicenet.domain.ExclusionsConfig;
import org.benetech.servicenet.domain.GeocodingResult;
import org.benetech.servicenet.domain.Location;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.Silo;
import org.benetech.servicenet.domain.UserGroup;
import org.benetech.servicenet.domain.UserProfile;
import org.benetech.servicenet.domain.view.ActivityInfo;
import org.benetech.servicenet.errors.BadRequestAlertException;
import org.benetech.servicenet.repository.ActivityRepository;
import org.benetech.servicenet.repository.ProviderRecordsRepository;
import org.benetech.servicenet.service.ActivityService;
import org.benetech.servicenet.service.ExclusionsConfigService;
import org.benetech.servicenet.service.OrganizationMatchService;
import org.benetech.servicenet.service.OrganizationService;
import org.benetech.servicenet.service.RecordsService;
import org.benetech.servicenet.service.UserService;
import org.benetech.servicenet.service.dto.ActivityDTO;
import org.benetech.servicenet.service.dto.ActivityFilterDTO;
import org.benetech.servicenet.service.dto.ActivityRecordDTO;
import org.benetech.servicenet.service.dto.GeocodingResultDTO;
import org.benetech.servicenet.service.dto.LocationRecordDTO;
import org.benetech.servicenet.service.dto.ProviderRecordDTO;
import org.benetech.servicenet.service.dto.ProviderRecordForMapDTO;
import org.benetech.servicenet.service.dto.Suggestions;
import org.benetech.servicenet.service.dto.provider.DeactivatedOrganizationDTO;
import org.benetech.servicenet.service.dto.provider.ProviderFilterDTO;
import org.benetech.servicenet.service.exceptions.ActivityCreationException;
import org.benetech.servicenet.service.mapper.OrganizationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Activity.
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    private final ActivityRepository activityRepository;

    private final RecordsService recordsService;

    private final ExclusionsConfigService exclusionsConfigService;

    private final OrganizationMatchService organizationMatchService;

    private final OrganizationService organizationService;

    private final UserService userService;

    private final ProviderRecordsRepository providerRecordsRepository;

    private final OrganizationMapper organizationMapper;

    public ActivityServiceImpl(ActivityRepository activityRepository, RecordsService recordsService,
        ExclusionsConfigService exclusionsConfigService, OrganizationMatchService organizationMatchService,
        OrganizationService organizationService, UserService userService,
        ProviderRecordsRepository providerRecordsRepository,
        OrganizationMapper organizationMapper) {
        this.activityRepository = activityRepository;
        this.recordsService = recordsService;
        this.exclusionsConfigService = exclusionsConfigService;
        this.organizationMatchService = organizationMatchService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.providerRecordsRepository = providerRecordsRepository;
        this.organizationMapper = organizationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityDTO> getAllOrganizationActivities(Pageable pageable, UUID systemAccountId,
        String search, ActivityFilterDTO activityFilterDTO) {

        Map<UUID, ExclusionsConfig> exclusionsMap = exclusionsConfigService.getAllBySystemAccountId();

        List<ActivityDTO> activities = new ArrayList<>();
        Page<ActivityInfo> activitiesInfo = findAllActivitiesInfoWithOwnerId(systemAccountId, search, pageable,
            activityFilterDTO);
        long totalElements = activitiesInfo.getTotalElements();
        for (ActivityInfo info : activitiesInfo) {
            try {
                activities.add(getEntityActivity(info, exclusionsMap));
            } catch (ActivityCreationException ex) {
                log.error(ex.getMessage());
            }
        }

        return new PageImpl<>(
            activities,
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()),
            totalElements
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActivityRecordDTO> getOneByOrganizationId(UUID orgId) {
        log.debug("Creating Activity Record for organization: {}", orgId);
        try {
            Optional<ActivityRecordDTO> opt = recordsService.getRecordFromOrganization(
                organizationService.findOne(orgId).get()
            );
            ActivityRecordDTO record = opt.orElseThrow(() -> new ActivityCreationException(
                String.format("Activity record couldn't be created for organization: %s", orgId)));

            return Optional.of(record);
        } catch (IllegalAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityRecordDTO> getPartnerActivitiesByOrganizationId(UUID orgId) {
        return organizationMatchService.findAllNotHiddenForOrganization(orgId).stream().filter(match -> !match.isDismissed())
            .map(match -> {
                try {
                    return recordsService.getRecordFromOrganization(
                        organizationService.findOne(match.getPartnerVersionId()).get()
                    ).get();
                } catch (IllegalAccessException | NoSuchElementException e) {
                    throw new ActivityCreationException(
                        String.format("Activity record couldn't be created for organization: %s",
                            match.getPartnerVersionId()));
                }
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderRecordDTO> getPartnerActivitiesForCurrentUser() {
        UserProfile userProfile = userService.getCurrentUserProfile();
        Set<UserGroup> userGroups = userProfile.getUserGroups();
        List<Organization> organizations;
        if (userGroups == null || userGroups.isEmpty()) {
            organizations = organizationService.findAllByUserProfile(userProfile);
        } else {
            organizations = organizationService.findAllByUserGroups(new ArrayList<>(userGroups));
        }
        return organizations.stream()
            .map(this::getProviderRecordDTO)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderRecordDTO> getAllPartnerActivities(ProviderFilterDTO providerFilterDTO,
        String search, Pageable pageable) {
        UserProfile userProfile = userService.getCurrentUserProfile();
        Page<Organization> organizations = providerRecordsRepository
            .findAllWithFilters(userProfile, providerFilterDTO, search, pageable);
        return organizations.map(this::getProviderRecordDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderRecordDTO> getAllPartnerActivitiesPublic(ProviderFilterDTO providerFilterDTO,
        Silo silo, String search, Pageable pageable) {
        Page<Organization> organizations = providerRecordsRepository
            .findAllWithFiltersPublic(providerFilterDTO, silo, search, pageable);
        return organizations.map(this::getProviderRecordDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderRecordForMapDTO> getAllPartnerActivitiesForMap(ProviderFilterDTO providerFilterDTO,
        String search) {
        UserProfile userProfile = userService.getCurrentUserProfile();
        Page<ProviderRecordForMapDTO> providerRecordForMapDTOList = providerRecordsRepository
            .findAllWithFiltersForMap(userProfile, providerFilterDTO, search)
            .map(org -> this.filterLocations(org, providerFilterDTO))
            .map(this::getProviderRecordDTO)
            .map(this::toProviderRecordForMapDTO);
        return providerRecordForMapDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderRecordForMapDTO> getAllPartnerActivitiesForMap(ProviderFilterDTO providerFilterDTO,
        String search, Silo silo) {
        Page<ProviderRecordForMapDTO> providerRecordForMapDTOList = providerRecordsRepository
            .findAllWithFiltersForMap(silo, providerFilterDTO, search)
            .map(this::getProviderRecordDTO)
            .map(this::toProviderRecordForMapDTO);
        return providerRecordForMapDTOList;
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderRecordDTO getPartnerActivityById(UUID id) {
        Optional<Organization> optOrganization = organizationService.findOne(id);
        return optOrganization.map(this::getProviderRecordDTO).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderRecordDTO getPartnerActivityById(UUID id, Silo silo) {
        Optional<Organization> optOrganization = organizationService.findOneByIdAndSilo(id, silo);
        if (optOrganization.isEmpty()) {
            throw new BadRequestAlertException("There is no organization with this id and silo", "providerRecord", "idnull");
        }
        return optOrganization.map(this::getProviderRecordDTO).orElse(null);
    }

    @Override
    public Suggestions getNameSuggestions(
        ActivityFilterDTO activityFilterDTO, UUID systemAccountId, String search) {

        Page<ActivityInfo> activities = (systemAccountId != null) ?
            activityRepository.findAllWithFilters(systemAccountId, search, activityFilterDTO, Pageable.unpaged())
            : Page.empty();
        List<String> orgNames = activities.stream()
            .map(ActivityInfo::getName)
            .distinct().collect(Collectors.toList());
        List<String> serviceNames = activities.stream()
            .map(ActivityInfo::getOrganization).flatMap(o -> o.getServices().stream())
            .map(org.benetech.servicenet.domain.Service::getName)
            .distinct().collect(Collectors.toList());
        return new Suggestions(orgNames, serviceNames);
    }

    @Override
    public List<DeactivatedOrganizationDTO> getAllDeactivatedRecords() {
        List<Organization> organizations = organizationService
            .findAllByAccountNameAndNotActiveAndCurrentUser();
        return organizations.stream()
            .map(this.organizationMapper::toDeactivatedOrganizationDto)
            .collect(Collectors.toList());
    }

    private ActivityDTO getEntityActivity(ActivityInfo info, Map<UUID, ExclusionsConfig> exclusionsMap) {
        log.debug("Creating Activity for organization: {}", info.getId());

        return recordsService.getActivityDTOFromActivityInfo(info, exclusionsMap);
    }

    private Page<ActivityInfo> findAllActivitiesInfoWithOwnerId(UUID ownerId, String search,
        Pageable pageable, ActivityFilterDTO activityFilterDTO) {
        if (ownerId != null) {
            return activityRepository.findAllWithFilters(ownerId, search, activityFilterDTO, pageable);
        } else {
            return new PageImpl<>(Collections.emptyList(), pageable, Collections.emptyList().size());
        }
    }

    private ProviderRecordDTO getProviderRecordDTO(Organization organization) {
        try {
            Optional<ProviderRecordDTO> opt = recordsService.getProviderRecordFromOrganization(organization);
            return opt.orElse(null);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private ProviderRecordForMapDTO toProviderRecordForMapDTO(ProviderRecordDTO providerRecordDTO) {
        Set<LocationRecordDTO> locationRecordDTOS = providerRecordDTO.getLocations();
        Set<GeocodingResultDTO> geocodingResultDTOS = new HashSet<>();
        locationRecordDTOS.forEach(locationRecordDTO -> geocodingResultDTOS
            .addAll(locationRecordDTO.getLocation().getGeocodingResults())
        );
        return new ProviderRecordForMapDTO(
            providerRecordDTO.getOrganization().getId(), geocodingResultDTOS
        );
    }

    private Organization filterLocations(Organization organization, ProviderFilterDTO providerFilterDTO) {
        Set<Location> locations = organization.getLocations().stream()
            .peek(location -> location.setGeocodingResults(
                location.getGeocodingResults().stream()
                    .filter(Objects::nonNull)
                    .filter(geo -> this.filterLocation(geo, providerFilterDTO))
                    .collect(Collectors.toCollection(ArrayList::new))))
            .collect(Collectors.toCollection(HashSet::new));
        organization.setLocations(locations);
        return organization;
    }

    private boolean filterLocation(GeocodingResult geocodingResult, ProviderFilterDTO providerFilterDTO) {
        return this.compareCity(geocodingResult, providerFilterDTO)
            && this.compareRegion(geocodingResult, providerFilterDTO)
            && this.comparePostalCode(geocodingResult, providerFilterDTO);
    }

    private boolean compareCity(GeocodingResult geocodingResult, ProviderFilterDTO providerFilterDTO) {
        if (StringUtils.isEmpty(geocodingResult.getLocality()) || StringUtils.isEmpty(providerFilterDTO.getCity())) {
            return true;
        }
        return geocodingResult.getLocality().equals(providerFilterDTO.getCity());
    }

    private boolean compareRegion(GeocodingResult geocodingResult, ProviderFilterDTO providerFilterDTO) {
        if (StringUtils.isEmpty(geocodingResult.getAdministrativeAreaLevel2()) || StringUtils.isEmpty(providerFilterDTO.getRegion())) {
            return true;
        }
        return geocodingResult.getAdministrativeAreaLevel2().equals(providerFilterDTO.getRegion());
    }

    private boolean comparePostalCode(GeocodingResult geocodingResult, ProviderFilterDTO providerFilterDTO) {
        if (StringUtils.isEmpty(geocodingResult.getPostalCode()) || StringUtils.isEmpty(providerFilterDTO.getZip())) {
            return true;
        }
        return geocodingResult.getPostalCode().equals(providerFilterDTO.getZip());
    }
}
