package org.benetech.servicenet.web.rest;

import com.codahale.metrics.annotation.Timed;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import org.benetech.servicenet.domain.SystemAccount;
import org.benetech.servicenet.repository.ActivityRepository;
import org.benetech.servicenet.service.ActivityService;
import org.benetech.servicenet.service.OrganizationService;
import org.benetech.servicenet.service.ServiceService;
import org.benetech.servicenet.service.UserService;
import org.benetech.servicenet.service.dto.ActivityDTO;
import org.benetech.servicenet.service.dto.ActivityFilterDTO;
import org.benetech.servicenet.service.dto.ActivityRecordDTO;
import org.benetech.servicenet.service.dto.ProviderRecordDTO;
import org.benetech.servicenet.service.dto.ProviderRecordForMapDTO;
import org.benetech.servicenet.service.dto.Suggestions;
import org.benetech.servicenet.service.dto.provider.DeactivatedOrganizationDTO;
import org.benetech.servicenet.service.dto.provider.ProviderFilterDTO;
import org.benetech.servicenet.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Activity.
 */
@RestController
@RequestMapping("/api")
public class ActivityResource {

    private final Logger log = LoggerFactory.getLogger(ActivityResource.class);

    private final ActivityService activityService;

    private final UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ActivityRepository activityRepository;

    public ActivityResource(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }

    /**
     * GET  /activities : get all the activities.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of activities in body
     */
    @PostMapping("/activities")
    @Timed
    public ResponseEntity<List<ActivityDTO>> getAllActivities(@Valid @RequestBody ActivityFilterDTO activityFilterDTO,
        @PathParam("search") String search, Pageable pageable) {
        Optional<SystemAccount> accountOpt = userService.getCurrentSystemAccount();
        UUID systemAccountId = accountOpt.map(SystemAccount::getId).orElse(null);

        Page<ActivityDTO> page = activityService.getAllOrganizationActivities(pageable, systemAccountId,
            search, activityFilterDTO);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/activities");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/activities/{orgId}")
    @Timed
    public ResponseEntity<ActivityRecordDTO> getActivityDetails(@PathVariable UUID orgId) {
        return activityService.getOneByOrganizationId(orgId)
            .map(r -> ResponseEntity.ok().body(r))
            .orElse(ResponseEntity.badRequest()
                .build());
    }

    @GetMapping("/partner-activities/{orgId}")
    @Timed
    public ResponseEntity<List<ActivityRecordDTO>> getPartnerActivities(@PathVariable UUID orgId) {
        return ResponseEntity.ok().body(
            activityService.getPartnerActivitiesByOrganizationId(orgId)
        );
    }

    @GetMapping("/provider-records")
    @Timed
    public ResponseEntity<List<ProviderRecordDTO>> getProviderActivities() {
        return ResponseEntity.ok().body(
            activityService.getPartnerActivitiesForCurrentUser()
        );
    }

    @PostMapping("/all-provider-records")
    @Timed
    public ResponseEntity<List<ProviderRecordDTO>> getAllProviderActivities(
        @RequestBody ProviderFilterDTO providerFilterDTO, @RequestParam(required = false) String search, Pageable pageable) {
        Page<ProviderRecordDTO> page = activityService.getAllPartnerActivities(providerFilterDTO, search, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/all-provider-records");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/all-provider-records-map")
    @Timed
    public ResponseEntity<List<ProviderRecordForMapDTO>> getAllProviderActivitiesForMap(
        @RequestBody ProviderFilterDTO providerFilterDTO, @RequestParam(required = false) String search, Pageable pageable) {
        Page<ProviderRecordForMapDTO> page = activityService.getAllPartnerActivitiesForMap(providerFilterDTO, search, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/all-provider-records-map");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/select-record/{orgId}")
    @Timed
    public ResponseEntity<ProviderRecordDTO> getSelectedRecord(@PathVariable UUID orgId) {
        return ResponseEntity.ok().body(activityService.getPartnerActivityById(orgId));
    }

    @PostMapping("/activity-suggestions")
    @Timed
    public ResponseEntity<Suggestions> getSuggestions(@Valid @RequestBody ActivityFilterDTO activityFilterDTO,
        @PathParam("search") String search) {
        Optional<SystemAccount> accountOpt = userService.getCurrentSystemAccount();
        UUID systemAccountId = accountOpt.map(SystemAccount::getId).orElse(null);

        Suggestions suggestions = activityService.getNameSuggestions(activityFilterDTO, systemAccountId, search);
        return ResponseEntity.ok().body(suggestions);
    }

    @GetMapping("/deactivated-provider-records")
    @Timed
    public ResponseEntity<List<DeactivatedOrganizationDTO>> getDeactivatedProviderActivities() {
        return ResponseEntity.ok().body(
            activityService.getAllDeactivatedRecords()
        );
    }
}
