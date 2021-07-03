package org.benetech.servicenet.conflict;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.benetech.servicenet.config.Constants;
import org.benetech.servicenet.conflict.detector.ConflictDetector;
import org.benetech.servicenet.domain.AbstractEntity;
import org.benetech.servicenet.domain.Conflict;
import org.benetech.servicenet.domain.Metadata;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.OrganizationMatch;
import org.benetech.servicenet.domain.SystemAccount;
import org.benetech.servicenet.domain.enumeration.ConflictStateEnum;
import org.benetech.servicenet.matching.model.EntityEquivalent;
import org.benetech.servicenet.matching.model.OrganizationEquivalent;
import org.benetech.servicenet.matching.service.impl.OrganizationEquivalentsService;
import org.benetech.servicenet.service.ConflictService;
import org.benetech.servicenet.service.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ConflictDetectionServiceImpl implements ConflictDetectionService {

    private final Logger log = LoggerFactory.getLogger(ConflictDetectionServiceImpl.class);

    private final ApplicationContext context;

    private final EntityManager em;

    private final OrganizationEquivalentsService organizationEquivalentsService;

    private final ConflictService conflictService;

    private final MetadataService metadataService;

    public ConflictDetectionServiceImpl(ApplicationContext context,
        EntityManager em,
        OrganizationEquivalentsService organizationEquivalentsService,
        ConflictService conflictService,
        MetadataService metadataService) {
        this.context = context;
        this.em = em;
        this.organizationEquivalentsService = organizationEquivalentsService;
        this.conflictService = conflictService;
        this.metadataService = metadataService;
    }

    @Async
    @Override
    @Transactional
    public void detectAsynchronously(Organization organization, List<OrganizationMatch> matches) {
        detect(organization, matches);
    }

    @Override
    @Transactional
    public void detect(Organization organization, List<OrganizationMatch> matches) {
        log.info(organization.getName() + ": Searching for conflicts");
        long detectionStartTime = System.currentTimeMillis();
        for (OrganizationMatch match : matches) {
            long startTime = System.currentTimeMillis();
            OrganizationEquivalent orgEquivalent = organizationEquivalentsService
                .generateEquivalent(
                    match.getOrganizationRecord(), match.getPartnerVersion());

            List<EntityEquivalent> equivalents = gatherAllEquivalents(orgEquivalent);

            detect(equivalents, match.getOrganizationRecord().getAccount(),
                match.getPartnerVersion().getAccount());

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            //TODO: Remove time counting logic (#264)
            log.debug("Searching for conflicts between " +
                match.getOrganizationRecord().getAccount().getName() + "'s organization '" +
                match.getOrganizationRecord().getName() + "' and " +
                match.getPartnerVersion().getAccount().getName() + "'s organization '" +
                match.getPartnerVersion().getName() + "' took: " + elapsedTime + "ms");
        }
        if (organization.getReplacedBy() != null) {
            Organization replacement = organization.getReplacedBy();
            OrganizationEquivalent orgEquivalent = organizationEquivalentsService
                .generateEquivalent(organization, replacement);

            List<EntityEquivalent> equivalents = gatherAllEquivalents(orgEquivalent);

            List<Conflict> conflicts = detect(equivalents, organization.getAccount(), replacement.getAccount());
            replacement.setHasUpdates(!conflicts.isEmpty());
            em.persist(replacement);
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - detectionStartTime;
        //TODO: Remove time counting logic (#264)
        log.info(organization.getName() + ": Searching for conflicts took " + elapsedTime + "ms");
    }

    @Override
    @Transactional
    public void remove(OrganizationMatch match) {
        OrganizationEquivalent orgEquivalent = organizationEquivalentsService.generateEquivalent(
            match.getOrganizationRecord(), match.getPartnerVersion());

        List<EntityEquivalent> equivalents = gatherAllEquivalents(orgEquivalent);

        for (EntityEquivalent eq : equivalents) {
            List<Conflict> conflicts = conflictService.findAllPendingWithResourceIdAndPartnerResourceId(
                eq.getBaseResourceId(), eq.getPartnerResourceId());

            conflicts.forEach(this::removeConflict);
        }
    }

    private void removeConflict(Conflict outdated) {
        outdated.setState(ConflictStateEnum.REMOVED);
        outdated.setStateDate(ZonedDateTime.now());
        em.persist(outdated);
    }

    private List<EntityEquivalent> gatherAllEquivalents(OrganizationEquivalent orgEquivalent) {
        List<EntityEquivalent> equivalents = new LinkedList<>(orgEquivalent.getUnwrappedEntities());
        equivalents.add(orgEquivalent);
        return equivalents;
    }

    private List<Conflict> detect(List<EntityEquivalent> equivalents, SystemAccount owner, SystemAccount accepted) {
        List<Conflict> conflicts = new ArrayList<>();
        for (EntityEquivalent eq : equivalents) {
            AbstractEntity current = (AbstractEntity) em.find(eq.getClazz(), eq.getBaseResourceId());
            AbstractEntity mirror = (AbstractEntity) em.find(eq.getClazz(), eq.getPartnerResourceId());

            try {
                ConflictDetector detector = context.getBean(
                    eq.getClazz().getSimpleName() + Constants.CONFLICT_DETECTOR_SUFFIX, ConflictDetector.class);
                List<Conflict> equivalentConflicts = detector.detectConflicts(current, mirror);

                List<Conflict> existingConflicts = conflictService.findAllPendingWithResourceIdAndPartnerResourceId(current.getId(), mirror.getId());
                removeDuplicatesAndRejectOutdatedConflicts(equivalentConflicts, existingConflicts);

                equivalentConflicts.forEach(c -> addAccounts(c, owner, accepted));
                equivalentConflicts.forEach(c -> addConflictingDates(eq, c));

                persistConflicts(equivalentConflicts);
                conflicts.addAll(equivalentConflicts);
                conflicts.addAll(existingConflicts);
            } catch (NoSuchBeanDefinitionException ex) {
                log.warn("There is no conflict detector for {}", eq.getClazz().getSimpleName());
            }
        }
        return conflicts;
    }

    private void addConflictingDates(EntityEquivalent eq, Conflict conflict) {
        Optional<Metadata> currentMetadata = metadataService.findMetadataForConflict(
            eq.getBaseResourceId(), conflict.getFieldName(), conflict.getCurrentValue());
        currentMetadata.ifPresent(m -> conflict.setCurrentValueDate(m.getLastActionDate()));

        Optional<Metadata> offeredMetadata = metadataService.findMetadataForConflict(
            eq.getPartnerResourceId(), conflict.getFieldName(), conflict.getOfferedValue());
        offeredMetadata.ifPresent(m -> conflict.setOfferedValueDate(m.getLastActionDate()));
    }

    private void addAccounts(Conflict conflict, SystemAccount owner, SystemAccount partner) {
        conflict.setOwner(owner);
        conflict.setPartner(partner);
    }

    private void persistConflicts(List<Conflict> conflicts) {
        conflicts.forEach(em::persist);
    }

    private void removeDuplicatesAndRejectOutdatedConflicts(List<Conflict> conflicts, List<Conflict> existingConflicts) {
        conflicts.removeIf(conflict -> StringUtils.isBlank(conflict.getOfferedValue()));

        existingConflicts.forEach(conflict -> conflicts.stream()
            .filter(c -> c.getFieldName().equals(conflict.getFieldName()))
            .findFirst().ifPresentOrElse(
                conf -> {
                    if (!StringUtils.equals(conf.getCurrentValue(), conflict.getCurrentValue())) {
                        rejectOutdatedConflict(conflict);
                    } else if (!StringUtils.equals(conf.getOfferedValue(), conflict.getOfferedValue())) {
                        rejectOutdatedConflict(conflict);
                    } else {
                        conflicts.remove(conf);
                    }
                },
                () -> rejectOutdatedConflict(conflict)
            ));
    }

    private void rejectOutdatedConflict(Conflict outdated) {
        outdated.setState(ConflictStateEnum.REJECTED);
        outdated.setStateDate(ZonedDateTime.now());
        em.persist(outdated);
    }

}
