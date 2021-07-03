package org.benetech.servicenet.service.factory.records;

import lombok.extern.slf4j.Slf4j;
import org.benetech.servicenet.domain.ExclusionsConfig;
import org.benetech.servicenet.domain.FieldExclusion;
import org.benetech.servicenet.domain.LocationExclusion;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.OrganizationMatch;
import org.benetech.servicenet.domain.SystemAccount;
import org.benetech.servicenet.domain.view.ActivityInfo;
import org.benetech.servicenet.service.ConflictService;
import org.benetech.servicenet.service.ExclusionsConfigService;
import org.benetech.servicenet.service.UserService;
import org.benetech.servicenet.service.dto.ActivityDTO;
import org.benetech.servicenet.service.dto.ActivityRecordDTO;
import org.benetech.servicenet.service.dto.ConflictDTO;
import org.benetech.servicenet.service.dto.ProviderRecordDTO;
import org.benetech.servicenet.service.factory.records.builder.RecordBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RecordFactory {

    @Autowired
    private ExclusionsConfigService exclusionsConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConflictService conflictService;

    @Autowired
    private RecordBuilder recordBuilder;

    public Optional<ActivityRecordDTO> getFilteredRecord(Organization organization) {
        List<ConflictDTO> conflicts = getBaseConflicts(organization.getId());

        Map<UUID, ExclusionsConfig> exclusionsMap = exclusionsConfigService.getAllBySystemAccountId();

        Set<ExclusionsConfig> baseExclusions = getBaseExclusions(organization.getAccount().getId(), exclusionsMap);
        Set<FieldExclusion> fieldExclusions = baseExclusions.stream()
            .flatMap(e -> e.getExclusions().stream())
            .collect(Collectors.toSet());

        Set<LocationExclusion> locationExclusions = baseExclusions.stream()
            .flatMap(e -> e.getLocationExclusions().stream())
            .collect(Collectors.toSet());

        List<ConflictDTO> filteredConflicts = filterConflicts(conflicts, fieldExclusions, exclusionsMap);

        return filterRecord(organization, filteredConflicts, fieldExclusions, locationExclusions);
    }


    public Optional<ProviderRecordDTO> getFilteredProviderRecord(Organization organization) {

        Map<UUID, ExclusionsConfig> exclusionsMap = exclusionsConfigService.getAllBySystemAccountId();

        Set<ExclusionsConfig> baseExclusions = getBaseExclusions(organization.getAccount().getId(), exclusionsMap);
        Set<FieldExclusion> fieldExclusions = baseExclusions.stream()
            .flatMap(e -> e.getExclusions().stream())
            .collect(Collectors.toSet());

        Set<LocationExclusion> locationExclusions = baseExclusions.stream()
            .flatMap(e -> e.getLocationExclusions().stream())
            .collect(Collectors.toSet());

        return filterProviderRecord(organization, fieldExclusions, locationExclusions);
    }

    public ActivityDTO getFilteredResult(ActivityInfo info, Map<UUID, ExclusionsConfig> exclusionsMap) {
        Set<FieldExclusion> baseExclusions = Optional.ofNullable(exclusionsMap.get(info.getAccountId()))
            .map(ExclusionsConfig::getExclusions).orElse(new HashSet<>());

        List<ConflictDTO> conflicts = getBaseConflicts(info.getId());
        List<ConflictDTO> filteredConflicts = filterConflicts(conflicts, baseExclusions, exclusionsMap);
        List<UUID> matches = info.getOrganizationMatches().stream().map(OrganizationMatch::getId)
            .collect(Collectors.toList());
        SystemAccount systemAccount = info.getOrganization().getAccount();

        return ActivityDTO.builder()
            .accountName(systemAccount.getName())
            .organizationId(info.getId())
            .organizationName(info.getName())
            .organizationMatches(matches)
            .lastUpdated(info.getRecent())
            .conflicts(filteredConflicts)
            .build();
    }

    private List<ConflictDTO> getBaseConflicts(UUID resourceId) {
        return conflictService.findAllPendingWithResourceId(resourceId);
    }

    private List<ConflictDTO> filterConflicts(List<ConflictDTO> conflicts, Set<FieldExclusion> baseExclusions,
        Map<UUID, ExclusionsConfig> exclusionsMap) {

        return conflicts.stream()
            .filter(conf -> isNotExcluded(conf, baseExclusions)
                && isNotExcluded(conf, Optional.ofNullable(exclusionsMap.get(conf.getPartnerId()))
                .map(ExclusionsConfig::getExclusions).orElse(new HashSet<>())))
            .collect(Collectors.toList());
    }

    private boolean isNotExcluded(ConflictDTO conflict, Set<FieldExclusion> exclusions) {
        return exclusions.stream().noneMatch(x ->
            x.getEntity().equals(conflict.getEntityPath())
                && x.getExcludedFields().contains(conflict.getFieldName()));
    }

    private Set<ExclusionsConfig> getBaseExclusions(UUID accountId, Map<UUID, ExclusionsConfig> exclusionsMap) {
        Set<ExclusionsConfig> exclusions = new HashSet<>();

        userService.getCurrentSystemAccount()
            .map(systemAccount -> Optional.ofNullable(exclusionsMap.get(systemAccount.getId())))
            .ifPresent(exclusionsConfig -> exclusionsConfig.ifPresent(exclusions::add));

        Optional.ofNullable(exclusionsMap.get(accountId))
            .ifPresent(exclusions::add);

        return exclusions;
    }

    private Optional<ActivityRecordDTO> filterRecord(Organization organization, List<ConflictDTO> conflicts,
        Set<FieldExclusion> exclusions, Set<LocationExclusion> locationExclusions) {
        try {
            return Optional.of(buildRecord(organization, conflicts, exclusions, locationExclusions));
        } catch (IllegalAccessException e) {
            log.error("Unable to filter record.");
            return Optional.empty();
        }
    }

    private Optional<ProviderRecordDTO> filterProviderRecord(Organization organization,
        Set<FieldExclusion> exclusions, Set<LocationExclusion> locationExclusions) {
        try {
            return Optional.of(buildProviderRecord(organization, exclusions, locationExclusions));
        } catch (IllegalAccessException e) {
            log.error("Unable to filter record.");
            return Optional.empty();
        }
    }

    private ActivityRecordDTO buildRecord(Organization organization, List<ConflictDTO> conflictDTOS,
        Set<FieldExclusion> exclusions, Set<LocationExclusion> locationExclusions) throws IllegalAccessException {

        if (exclusions.isEmpty()) {
            return recordBuilder.buildBasicRecord(
                organization, organization.getUpdatedAt(), conflictDTOS, locationExclusions);
        } else {
            return recordBuilder.buildFilteredRecord(organization, organization.getUpdatedAt(),
                conflictDTOS, exclusions, locationExclusions);
        }
    }

    private ProviderRecordDTO buildProviderRecord(Organization organization,
        Set<FieldExclusion> exclusions, Set<LocationExclusion> locationExclusions) throws IllegalAccessException {

        if (exclusions.isEmpty()) {
            return recordBuilder.buildBasicProviderRecord(
                organization, organization.getUpdatedAt(), locationExclusions);
        } else {
            return recordBuilder.buildFilteredProviderRecord(organization, organization.getUpdatedAt(),
                 exclusions, locationExclusions);
        }
    }
}
