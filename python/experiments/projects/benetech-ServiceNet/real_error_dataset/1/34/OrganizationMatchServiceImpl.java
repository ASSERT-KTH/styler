package org.benetech.servicenet.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.benetech.servicenet.conflict.ConflictDetectionService;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.OrganizationMatch;
import org.benetech.servicenet.domain.UserProfile;
import org.benetech.servicenet.matching.counter.OrganizationSimilarityCounter;
import org.benetech.servicenet.repository.LocationMatchRepository;
import org.benetech.servicenet.repository.MatchSimilarityRepository;
import org.benetech.servicenet.repository.OrganizationMatchRepository;
import org.benetech.servicenet.service.MatchSimilarityService;
import org.benetech.servicenet.service.OrganizationMatchService;
import org.benetech.servicenet.service.OrganizationService;
import org.benetech.servicenet.service.UserService;
import org.benetech.servicenet.service.dto.DismissMatchDTO;
import org.benetech.servicenet.service.dto.LocationMatchDto;
import org.benetech.servicenet.service.dto.MatchSimilarityDTO;
import org.benetech.servicenet.service.dto.OrganizationMatchDTO;
import org.benetech.servicenet.service.mapper.OrganizationMatchMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing OrganizationMatch.
 */
@Slf4j
@Service
@Transactional
public class OrganizationMatchServiceImpl implements OrganizationMatchService {

    private final OrganizationMatchRepository organizationMatchRepository;

    private final OrganizationMatchMapper organizationMatchMapper;

    private final OrganizationService organizationService;

    private final OrganizationSimilarityCounter organizationSimilarityCounter;

    private final ConflictDetectionService conflictDetectionService;

    private final UserService userService;

    private final MatchSimilarityService matchSimilarityService;

    private final MatchSimilarityRepository matchSimilarityRepository;

    private final BigDecimal orgMatchThreshold;

    private final LocationMatchRepository locationMatchRepository;

    private final EntityManager entityManager;

    private final DataSource dataSource;

    @SuppressWarnings({"checkstyle:ParameterNumber", "PMD.ExcessiveParameterList"})
    public OrganizationMatchServiceImpl(OrganizationMatchRepository organizationMatchRepository,
                                        OrganizationMatchMapper organizationMatchMapper,
                                        OrganizationService organizationService,
                                        OrganizationSimilarityCounter organizationSimilarityCounter,
                                        ConflictDetectionService conflictDetectionService,
                                        UserService userService,
                                        MatchSimilarityService matchSimilarityService,
                                        MatchSimilarityRepository matchSimilarityRepository,
                                        LocationMatchRepository locationMatchRepository,
                                        EntityManager entityManager,
                                        DataSource dataSource,
                                        @Value("${similarity-ratio.config.organization-match-threshold}")
                                            BigDecimal orgMatchThreshold) {
        this.organizationMatchRepository = organizationMatchRepository;
        this.organizationMatchMapper = organizationMatchMapper;
        this.organizationService = organizationService;
        this.organizationSimilarityCounter = organizationSimilarityCounter;
        this.conflictDetectionService = conflictDetectionService;
        this.userService = userService;
        this.orgMatchThreshold = orgMatchThreshold;
        this.matchSimilarityService = matchSimilarityService;
        this.matchSimilarityRepository = matchSimilarityRepository;
        this.locationMatchRepository = locationMatchRepository;
        this.entityManager = entityManager;
        this.dataSource = dataSource;
    }

    /**
     * Save a organizationMatch.
     *
     * @param organizationMatchDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public OrganizationMatchDTO save(OrganizationMatchDTO organizationMatchDTO) {
        log.debug("Request to save OrganizationMatch : {}", organizationMatchDTO);

        OrganizationMatch organizationMatch = organizationMatchMapper.toEntity(organizationMatchDTO);
        organizationMatch = saveOrUpdate(organizationMatch);
        return organizationMatchMapper.toDto(organizationMatch);
    }

    public OrganizationMatch saveOrUpdate(OrganizationMatch organizationMatch) {
        List<OrganizationMatch> existingMatches =
            organizationMatchRepository.findByOrganizationRecordAndPartnerVersion(
            organizationMatch.getOrganizationRecord(), organizationMatch.getPartnerVersion());
        if (existingMatches.size() > 0) {
            Iterator<OrganizationMatch> matchIterator = existingMatches.iterator();
            OrganizationMatch existingMatch = matchIterator.next();
            while (matchIterator.hasNext()) {
                OrganizationMatch duplicateMatch = matchIterator.next();
                matchSimilarityRepository.deleteAll(matchSimilarityRepository
                    .findByOrganizationMatchId(duplicateMatch.getId()));
                organizationMatchRepository.delete(duplicateMatch);
            }
            existingMatch.setTimestamp(organizationMatch.getTimestamp());
            existingMatch.setDismissed(organizationMatch.getDismissed());
            existingMatch.setDismissComment(organizationMatch.getDismissComment());
            existingMatch.setDismissDate(organizationMatch.getDismissDate());
            existingMatch.setDismissedBy(organizationMatch.getDismissedBy());
            existingMatch.setHidden(organizationMatch.getHidden());
            existingMatch.setHiddenBy(organizationMatch.getHiddenBy());
            existingMatch.setHiddenDate(organizationMatch.getHiddenDate());
            existingMatch.setSimilarity(organizationMatch.getSimilarity());
            return organizationMatchRepository.save(existingMatch);
        } else {
            return organizationMatchRepository.save(organizationMatch);
        }
    }

    /**
     * Get all the organizationMatches.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMatchDTO> findAll() {
        log.debug("Request to get all OrganizationMatches");
        return organizationMatchRepository.findAll().stream()
            .map(organizationMatchMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the organizationMatches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrganizationMatchDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrganizationMatches");
        return organizationMatchRepository.findAll(pageable)
            .map(organizationMatchMapper::toDto);
    }

    @Override
    public List<OrganizationMatchDTO> findAllForOrganization(UUID orgId) {
        return organizationMatchRepository.findAllByOrganizationRecordId(orgId).stream()
            .map(organizationMatchMapper::toDtoWithLocationMatches)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<OrganizationMatch> findAllMatchesForOrganization(UUID orgId) {
        return new LinkedList<>(organizationMatchRepository.findAllByOrganizationRecordId(orgId));
    }

    @Override
    public List<OrganizationMatchDTO> findAllDismissedForOrganization(UUID orgId) {
        return organizationMatchRepository.findAllByOrganizationRecordIdAndDismissed(orgId, true).stream()
            .map(organizationMatchMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<OrganizationMatchDTO> findAllNotDismissedForOrganization(UUID orgId) {
        return organizationMatchRepository.findAllByOrganizationRecordIdAndDismissed(orgId, false).stream()
            .map(organizationMatchMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<OrganizationMatchDTO> findAllHiddenForOrganization(UUID orgId) {
        return organizationMatchRepository.findAllByOrganizationRecordIdAndHidden(orgId, true).stream()
            .map(organizationMatchMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<OrganizationMatchDTO> findAllNotHiddenForOrganization(UUID orgId) {
        return organizationMatchRepository.findAllByOrganizationRecordIdAndHidden(orgId, false).stream()
            .map(organizationMatchMapper::toDtoWithLocationMatches)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<OrganizationMatchDTO> findCurrentUsersHiddenOrganizationMatches() {
        Optional<UserProfile> currentUser = userService.getCurrentUserProfileOptional();
        if (currentUser.isPresent()) {
            List<OrganizationMatch> matches;
            if (userService.isCurrentUserAdmin()) {
                matches = organizationMatchRepository.findAllByHidden(true);
            } else {
                matches = organizationMatchRepository.findAllByHiddenAndHiddenBy(true, currentUser.get());
            }
            return matches.stream()
                .map(organizationMatchMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
        } else {
            throw new IllegalStateException("No current user found");
        }
    }

    @Override
    public List<OrganizationMatchDTO> findAllNotHiddenOrganizationMatches() {
        return organizationMatchRepository.findAllByHidden(false).stream()
            .map(organizationMatchMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one organizationMatch by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationMatchDTO> findOne(UUID id) {
        log.debug("Request to get OrganizationMatch : {}", id);
        return organizationMatchRepository.findById(id)
            .map(organizationMatchMapper::toDto);
    }

    /**
     * Delete the organizationMatch by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete OrganizationMatch : {}", id);
        organizationMatchRepository.deleteById(id);
    }

    @Async
    @Override
    @Transactional
    // the connection is closed automatically at the end of transaction
    @SuppressWarnings("PMD.CloseResource")
    public void createOrUpdateOrganizationMatches() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            connection.setAutoCommit(false);
            Optional<Organization> organizationOptional = organizationService.findFirstThatNeedsMatching();
            while (organizationOptional.isPresent()) {
                Long total = organizationService.countOrganizationsByNeedsMatching();
                try {
                    entityManager.clear();
                    createOrUpdateOrganizationMatchesSynchronously(
                        organizationOptional.get().getId(),
                        total);
                    connection.commit();
                } catch (SQLException matchingException) {
                    log.error(matchingException.getMessage(), matchingException);
                }
                organizationOptional = organizationService.findFirstThatNeedsMatchingExcept(organizationOptional.get().getId());
            }
        } catch (SQLException sqlEx) {
            log.error(sqlEx.getMessage(), sqlEx);
        }
    }

    @Async
    @Override
    @Transactional
    public void createOrUpdateOrganizationMatches(UUID organizationId) {
        createOrUpdateOrganizationMatchesSynchronously(organizationId, null);
    }

    @Override
    public void createOrUpdateOrganizationMatchesSynchronously(UUID organizationId, Long total) {
        Organization organization = organizationService.findOneWithEagerAssociations(organizationId);
        if (total != null) {
            log.info(organization.getName() + ": Updating organization matches, " + total
                + " remaining.");
        } else {
            log.info(organization.getName() + ": Updating organization matches");
        }
        List<OrganizationMatch> matches = findCurrentMatches(organization);
        List<OrganizationMatch> partnerMatches = findCurrentPartnersMatches(organization);
        if (organization.getActive()) {
            List<UUID> hiddenMatchesIds = matches.stream()
                .filter(m -> BooleanUtils.isTrue((m.getHidden())))
                .map(OrganizationMatch::getId)
                .collect(Collectors.toList());

            for (UUID matchId : hiddenMatchesIds) {
                revertHideOrganizationMatch(matchId);
            }
            List<Organization> partnerOrganizations = findOrganizationsExcept(
                organization.getAccount().getName());

            List<OrganizationMatch> currentMatches = findAndPersistMatches(organization,
                partnerOrganizations);
            removeObsoleteMatches(currentMatches, matches);
            removeObsoleteMatches(currentMatches, partnerMatches);

            detectConflictsForCurrentMatches(organization);
        } else {
            removeMatches(matches);
            removeMatches(partnerMatches);
        }
        organization.setNeedsMatching(false);
        organizationService.save(organization);
        entityManager.flush();
    }

    private void removeObsoleteMatches(List<OrganizationMatch> matches, List<OrganizationMatch> previousMatches) {
        List<OrganizationMatch> matchesToRemove = new ArrayList<>();
        for (OrganizationMatch match : previousMatches) {
            if (!matches.contains(match)) {
                matchesToRemove.add(match);
            }
        }
        removeMatches(matchesToRemove);
    }

    @Override
    public void dismissOrganizationMatch(UUID id, DismissMatchDTO dismissMatchDTO) {
        organizationMatchRepository.findById(id).ifPresent(match -> {
            match.setDismissed(true);
            match.setDismissComment(dismissMatchDTO.getComment());
            match.setDismissDate(ZonedDateTime.now(ZoneId.systemDefault()));

            userService.getCurrentUserProfileOptional().ifPresentOrElse(
                match::setDismissedBy,
                () -> { throw new IllegalStateException("No current user found"); }
            );

            organizationMatchRepository.save(match);

            conflictDetectionService.remove(match);
        });
    }

    @Override
    public void revertDismissOrganizationMatch(UUID id) {
        organizationMatchRepository.findById(id).ifPresent(match -> {
            match.setDismissed(false);
            match.setDismissComment(null);
            match.setDismissedBy(null);
            match.setDismissDate(null);

            organizationMatchRepository.save(match);

            conflictDetectionService.detect(match.getOrganizationRecord(), Collections.singletonList(match));
        });
    }

    @Override
    public void hideOrganizationMatch(UUID id) {
        organizationMatchRepository.findById(id).ifPresent(match -> {
            match.setHidden(true);
            match.setHiddenDate(ZonedDateTime.now(ZoneId.systemDefault()));

            userService.getCurrentUserProfileOptional().ifPresentOrElse(
                match::setHiddenBy,
                () -> { throw new IllegalStateException("No current user found"); }
            );

            organizationMatchRepository.save(match);
        });
    }

    @Override
    public void hideOrganizationMatches(List<UUID> ids) {
        for (UUID id : ids) {
            hideOrganizationMatch(id);
        }
    }

    @Override
    public void revertHideOrganizationMatch(UUID id) {
        Optional<UserProfile> currentUser = userService.getCurrentUserProfileOptional();
        if (currentUser.isPresent()) {
            organizationMatchRepository.findById(id).ifPresent(match -> {
                if (userService.isCurrentUserAdmin() || match.getHiddenBy().equals(currentUser.get())) {
                    match.setHidden(false);
                    match.setHiddenBy(null);
                    match.setHiddenDate(null);

                    organizationMatchRepository.save(match);
                } else {
                    throw new AccessDeniedException("Cannot reinstate matches hidden by another user");
                }
            });
        } else {
            throw new IllegalStateException("No current user found");
        }
    }

    @Override
    public void deleteByOrganizationRecordOrPartnerVersionId(UUID organizationId) {
        organizationMatchRepository.deleteByOrganizationRecordIdOrPartnerVersionId(organizationId, organizationId);
    }

    private void detectConflictsForCurrentMatches(Organization organization) {
        List<OrganizationMatch> matches = findNotDismissedMatches(organization);
        matches.addAll(findNotDismissedPartnersMatches(organization));
        conflictDetectionService.detect(organization, matches);
    }

    private List<OrganizationMatch> findCurrentMatches(Organization organization) {
        return organizationMatchRepository
            .findAllByOrganizationRecordId(organization.getId());
    }

    private List<OrganizationMatch> findNotDismissedMatches(Organization organization) {
        return organizationMatchRepository
            .findAllByOrganizationRecordIdAndDismissed(organization.getId(), false);
    }

    private List<OrganizationMatch> findCurrentPartnersMatches(Organization organization) {
        return organizationMatchRepository
            .findAllByPartnerVersionId(organization.getId());
    }

    private List<OrganizationMatch> findNotDismissedPartnersMatches(Organization organization) {
        return organizationMatchRepository
            .findAllByPartnerVersionIdAndDismissed(organization.getId(), false);
    }

    private List<Organization> findOrganizationsExcept(String providerName) {
        return organizationService.findAllOthers(providerName);
    }

    private List<OrganizationMatch> findAndPersistMatches(Organization organization,
        List<Organization> partnerOrganizations) {
        List<OrganizationMatch> matches = new LinkedList<>();
        long startTime = System.currentTimeMillis();
        //TODO: Remove time counting logic (#264)
        log.debug("Searching for matches for " + organization.getAccount().getName() + "'s organization '" +
            organization.getName() + "' has started. There are "
            + partnerOrganizations.size() + " organizations to compare with");
        List<LocationMatchDto> locationMatchesToRemove = new ArrayList<>();
        for (Organization partner : partnerOrganizations) {
            List<MatchSimilarityDTO> similarityDTOs = organizationSimilarityCounter
                .getMatchSimilarityDTOs(organization, partner);
            similarityDTOs.stream().filter(dto -> dto.getMatchesToRemove() != null)
                .forEach(dto -> locationMatchesToRemove.addAll(dto.getMatchesToRemove()));
            if (isSimilar(similarityDTOs)) {
                matches.addAll(createOrganizationMatches(organization, partner, similarityDTOs));
            }
        }
        locationMatchRepository.deleteInBatchByLocationAndMatchingLocationIds(locationMatchesToRemove);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        //TODO: Remove time counting logic (#264)
        log.debug("Searching for matches for " +
            organization.getAccount().getName() + "'s organization '" +
            organization.getName() + "' took: " + elapsedTime + "ms, " + matches.size() + " matches found.");
        return matches;
    }

    private void removeMatches(List<OrganizationMatch> matches) {
        for (OrganizationMatch match : matches) {
            matchSimilarityRepository.deleteAll(
                matchSimilarityRepository.findByOrganizationMatchId(match.getId())
            );
            conflictDetectionService.remove(match);
            organizationMatchRepository.delete(match);
        }
    }

    public List<OrganizationMatch> createOrganizationMatches(Organization organization, Organization partner,
        List<MatchSimilarityDTO> similarityDTOS) {
        List<OrganizationMatch> matches = new LinkedList<>();

        BigDecimal similaritySum = similarityDTOS.stream()
            .map(MatchSimilarityDTO::getSimilarity)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(organizationSimilarityCounter.getTotalWeight(similarityDTOS), 2, RoundingMode.FLOOR);

        OrganizationMatch match = saveOrUpdate(
            new OrganizationMatch()
                .organizationRecord(organization)
                .partnerVersion(partner)
                .timestamp(ZonedDateTime.now())
                .similarity(similaritySum)
        );

        OrganizationMatch mirrorMatch = saveOrUpdate(
            new OrganizationMatch()
                .organizationRecord(partner)
                .partnerVersion(organization)
                .timestamp(ZonedDateTime.now())
                .similarity(similaritySum)
        );

        matches.add(match);
        matches.add(mirrorMatch);

        for (MatchSimilarityDTO similarityDTO : similarityDTOS) {
            if (similarityDTO.getSimilarity().compareTo(BigDecimal.ZERO) > 0) {
                similarityDTO.setOrganizationMatchId(match.getId());
                matchSimilarityService.saveOrUpdate(similarityDTO);
                similarityDTO.setOrganizationMatchId(mirrorMatch.getId());
                matchSimilarityService.saveOrUpdate(similarityDTO);
            }
        }
        return matches;
    }

    private boolean isSimilar(List<MatchSimilarityDTO> similarityDTOS) {
        return similarityDTOS.stream()
            .map(MatchSimilarityDTO::getSimilarity)
            .reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(orgMatchThreshold) >= 0;
    }
}
