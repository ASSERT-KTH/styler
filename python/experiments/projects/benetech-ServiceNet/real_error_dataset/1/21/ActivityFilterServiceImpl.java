package org.benetech.servicenet.service.impl;

import static org.benetech.servicenet.config.Constants.SERVICE_PROVIDER;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.benetech.servicenet.domain.ActivityFilter;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.Silo;
import org.benetech.servicenet.domain.Taxonomy;
import org.benetech.servicenet.domain.UserProfile;
import org.benetech.servicenet.repository.ActivityFilterRepository;
import org.benetech.servicenet.repository.GeocodingResultRepository;
import org.benetech.servicenet.repository.SiloRepository;
import org.benetech.servicenet.repository.TaxonomyRepository;
import org.benetech.servicenet.service.ActivityFilterService;
import org.benetech.servicenet.service.UserService;
import org.benetech.servicenet.service.dto.ActivityFilterDTO;
import org.benetech.servicenet.service.mapper.ActivityFilterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service Implementation for managing {@link ActivityFilter}.
 */
@Service
@Transactional
public class ActivityFilterServiceImpl implements ActivityFilterService {

    private final Logger log = LoggerFactory.getLogger(ActivityFilterServiceImpl.class);

    private final GeocodingResultRepository geocodingResultRepository;

    private final TaxonomyRepository taxonomyRepository;

    private final ActivityFilterRepository activityFilterRepository;

    private final ActivityFilterMapper activityFilterMapper;

    private final UserService userService;

    private final SiloRepository siloRepository;

    private static final String SILO_TAXONOMIES = "silo";

    public ActivityFilterServiceImpl(GeocodingResultRepository geocodingResultRepository, UserService userService,
        TaxonomyRepository taxonomyRepository, ActivityFilterRepository activityFilterRepository,
        ActivityFilterMapper activityFilterMapper,
        SiloRepository siloRepository) {
        this.geocodingResultRepository = geocodingResultRepository;
        this.taxonomyRepository = taxonomyRepository;
        this.activityFilterRepository = activityFilterRepository;
        this.activityFilterMapper = activityFilterMapper;
        this.userService = userService;
        this.siloRepository = siloRepository;
    }

    @Override
    public Set<String> getPostalCodes() {
        return geocodingResultRepository.getDistinctPostalCodesFromGeoResults();
    }

    @Override
    public Set<String> getRegions() {
        return geocodingResultRepository.getDistinctRegionsFromGeoResults();
    }

    @Override
    public Set<String> getCities() {
        return geocodingResultRepository.getDistinctCityFromGeoResults();
    }

    @Override
    public Set<String> getPostalCodesForServiceProviders(UserProfile currentUserProfile) {
        return geocodingResultRepository.getDistinctPostalCodesFromGeoResultsForServiceProviders(currentUserProfile);
    }

    @Override
    public Set<String> getPostalCodesForServiceProviders(Silo silo) {
        return geocodingResultRepository.getDistinctPostalCodesFromGeoResultsForServiceProviders(silo);
    }

    @Override
    public Set<String> getRegionsForServiceProviders(UserProfile currentUserProfile) {
        return geocodingResultRepository.getDistinctRegionsFromGeoResultsForServiceProviders(currentUserProfile);
    }

    @Override
    public Set<String> getRegionsForServiceProviders(Silo silo) {
        return geocodingResultRepository.getDistinctRegionsFromGeoResultsForServiceProviders(silo);
    }

    @Override
    public Set<String> getCitiesForServiceProviders(UserProfile currentUserProfile) {
        return geocodingResultRepository.getDistinctCityFromGeoResultsForServiceProviders(currentUserProfile);
    }

    @Override
    public Set<String> getCitiesForServiceProviders(Silo silo) {
        return geocodingResultRepository.getDistinctCityFromGeoResultsForServiceProviders(silo);
    }

    @Override
    public Map<String, Set<String>> getTaxonomies(UUID siloId, String providerName) {
        List<Taxonomy> taxonomies;
        Map<String, Set<String>> taxonomiesByProvider = new HashMap<>();
        if (StringUtils.isEmpty(providerName)) {
            taxonomies = taxonomyRepository.findAssociatedTaxonomies();
            for (Taxonomy taxonomy : taxonomies) {
                Set<String> providersTaxonomies = taxonomiesByProvider.getOrDefault(taxonomy.getProviderName(), new HashSet<>());
                providersTaxonomies.add(taxonomy.getName());
                taxonomiesByProvider.put(taxonomy.getProviderName(), providersTaxonomies);
            }
        } else {
            taxonomies = taxonomyRepository.findAssociatedTaxonomies(providerName);
            taxonomiesByProvider.put(SERVICE_PROVIDER,
                taxonomies.stream().map(Taxonomy::getName).collect(Collectors.toSet()));
        }
        if (siloId != null) {
            Silo silo = siloRepository.getOne(siloId);
            Set<Organization> organizations = silo.getOrganizations();
            Set<String> taxonomyNames = taxonomyRepository.findAssociatedTaxonomies(organizations).stream()
                .map(Taxonomy::getName).collect(Collectors.toSet());
            taxonomiesByProvider.put(SILO_TAXONOMIES, taxonomyNames);
        }
        return taxonomiesByProvider;
    }

    /**
     * Save a activityFilter.
     *
     * @param activityFilterDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ActivityFilterDTO save(ActivityFilterDTO activityFilterDTO) {
        log.debug("Request to save ActivityFilter : {}", activityFilterDTO);
        ActivityFilter activityFilter = activityFilterMapper.toEntity(activityFilterDTO);
        activityFilter = activityFilterRepository.save(activityFilter);
        return activityFilterMapper.toDto(activityFilter);
    }

    @Override
    public ActivityFilterDTO saveForCurrentUser(ActivityFilterDTO activityFilterDTO) {
        UserProfile currentUserProfile = userService.getCurrentUserProfile();
        activityFilterDTO.setUserId(currentUserProfile.getId());

        return save(activityFilterDTO);
    }

    /**
     * Get all the activityFilters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityFilterDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ActivityFilters");
        return activityFilterRepository.findAll(pageable)
            .map(activityFilterMapper::toDto);
    }


    /**
     * Get one activityFilter by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ActivityFilterDTO> findOne(UUID id) {
        log.debug("Request to get ActivityFilter : {}", id);
        return activityFilterRepository.findById(id)
            .map(activityFilterMapper::toDto);
    }

    /**
     * Delete the activityFilter by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete ActivityFilter : {}", id);
        activityFilterRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityFilterDTO> getAllForCurrentUser() {
        return activityFilterRepository.findByUserIsCurrentUser().stream()
            .map(activityFilterMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActivityFilterDTO> findByNameAndCurrentUser(String name) {
        return activityFilterRepository.findByNameAndCurrentUser(name).map(activityFilterMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityFilterDTO getCurrentUserActivityFilter() {
        return activityFilterMapper.toDto(userService.getCurrentUserProfile().getFilter());
    }

    @Override
    public void saveCurrentUserActivityFilter(ActivityFilterDTO activityFilterDTO) {
        UserProfile currentUserProfile = userService.getCurrentUserProfile();
        ActivityFilter activityFilter = activityFilterMapper.toEntity(activityFilterDTO);
        activityFilter.setId(null);
        activityFilter.setUserProfile(null);

        if (currentUserProfile.getFilter() != null) {
            activityFilter.setId(currentUserProfile.getFilter().getId());
        }

        activityFilterRepository.save(activityFilter);

        currentUserProfile.setFilter(activityFilter);
        userService.saveProfile(currentUserProfile);
    }
}
