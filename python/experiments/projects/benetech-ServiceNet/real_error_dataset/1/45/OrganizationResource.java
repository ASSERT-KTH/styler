package org.benetech.servicenet.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.UserProfile;
import org.benetech.servicenet.security.AuthoritiesConstants;
import org.benetech.servicenet.service.ActivityService;
import org.benetech.servicenet.service.OrganizationService;
import org.benetech.servicenet.service.UserService;
import org.benetech.servicenet.errors.BadRequestAlertException;
import org.benetech.servicenet.service.dto.OrganizationDTO;
import org.benetech.servicenet.service.dto.OrganizationOptionDTO;
import org.benetech.servicenet.service.dto.provider.DeactivatedOrganizationDTO;
import org.benetech.servicenet.service.dto.provider.ProviderOrganizationDTO;
import org.benetech.servicenet.web.rest.util.HeaderUtil;
import org.benetech.servicenet.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing Organization.
 */
@RestController
@RequestMapping("/api")
public class OrganizationResource {

    private static final String ENTITY_NAME = "organization";
    private final Logger log = LoggerFactory.getLogger(OrganizationResource.class);
    private final OrganizationService organizationService;
    private final UserService userService;
    private final ActivityService activityService;

    public OrganizationResource(OrganizationService organizationService, UserService userService,
        ActivityService activityService) {
        this.organizationService = organizationService;
        this.userService = userService;
        this.activityService = activityService;
    }

    /**
     * POST  /organizations : Create a new organization.
     *
     * @param organizationDTO the organizationDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new organizationDTO,
     * or with status 400 (Bad Request) if the organization has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
    @PostMapping("/organizations")
    @Timed
    public ResponseEntity<OrganizationDTO> createOrganization(
        @Valid @RequestBody OrganizationDTO organizationDTO) throws URISyntaxException {
        log.debug("REST request to save Organization : {}", organizationDTO);
        if (organizationDTO.getId() != null) {
            throw new BadRequestAlertException("A new organization cannot already have an ID", ENTITY_NAME, "idexists");
        }
        org.benetech.servicenet.service.dto.OrganizationDTO result = organizationService.save(organizationDTO);
        return ResponseEntity.created(new URI("/api/organizations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * POST  /organizations/user-owned : Create a new organization owned by current user.
     *
     * @param organizationDTO the organizationRecordDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new organizationDTO,
     * or with status 400 (Bad Request) if the organization has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/organizations/user-owned")
    @Timed
    public ResponseEntity<OrganizationDTO> createOrganizationOwnedByUser(
        @Valid @RequestBody ProviderOrganizationDTO organizationDTO) throws URISyntaxException {
        log.debug("REST request to save Organization : {}", organizationDTO);
        if (organizationDTO.getId() != null) {
            throw new BadRequestAlertException("A new organization cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrganizationDTO result = organizationService.saveWithUser(organizationDTO);
        return ResponseEntity.created(new URI("/api/organizations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /organizations : Updates an existing organization.
     *
     * @param organizationDTO the organizationDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated organizationDTO,
     * or with status 400 (Bad Request) if the organizationDTO is not valid,
     * or with status 500 (Internal Server Error) if the organizationDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
    @PutMapping("/organizations")
    @Timed
    public ResponseEntity<OrganizationDTO> updateOrganization(
        @Valid @RequestBody OrganizationDTO organizationDTO) throws URISyntaxException {
        log.debug("REST request to update Organization : {}", organizationDTO);
        if (organizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        OrganizationDTO result = organizationService.save(organizationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, organizationDTO.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /organizations/user-owned : Updates current users existing organization.
     *
     * @param organizationDTO the organizationDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated organizationDTO,
     * or with status 400 (Bad Request) if the organizationDTO is not valid,
     * or with status 500 (Internal Server Error) if the organizationDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/organizations/user-owned")
    @Timed
    public ResponseEntity<OrganizationDTO> updateOrganizationOwnedByUser(
        @Valid @RequestBody ProviderOrganizationDTO organizationDTO) throws URISyntaxException {
        log.debug("REST request to update Organization : {}", organizationDTO);
        if (organizationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<Organization> existingOrganization;
        UserProfile userProfile = userService.getCurrentUserProfile();
        if (userProfile.getUserGroups().isEmpty()) {
            existingOrganization = organizationService
                .findOneWithIdAndUserProfile(organizationDTO.getId(), userProfile);
        } else {
            existingOrganization = organizationService
                .findOneWithIdAndUserProfileInUserGroups(organizationDTO.getId(), userProfile);
        }
        if (existingOrganization.isEmpty()) {
            throw new BadRequestAlertException(
                "You are not allowed to edit this organization",
                ENTITY_NAME,
                "cantEditRecord"
            );
        }
        OrganizationDTO result = organizationService.saveWithUser(organizationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, organizationDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /organizations : get all the organizations.
     *
     * @param filter the filter of the request
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of organizations in body
     */
    @GetMapping("/organizations")
    @Timed
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations(@RequestParam(required = false) String filter,
    Pageable pageable) {
        Page<OrganizationDTO> page;
        if ("funding-is-null".equals(filter)) {
            log.debug("REST request to get all Organizations where funding is null");
            page = organizationService.findAllWhereFundingIsNull(pageable);
        } else {
            log.debug("REST request to get all Organizations");
            page = organizationService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/organizations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /organization-options : get all the organization options.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of organizations in body
     */
    @GetMapping("/organization-options")
    @Timed
    public ResponseEntity<List<OrganizationOptionDTO>> getOrganizationOptions() {
        return ResponseEntity.ok().body(
            organizationService.findAllOptions()
        );
    }

    /**
     * GET  /organizations/search : search organizations.
     *
     * @param name name of the organization
     * @param systemAccount the system account
     * @return the ResponseEntity with status 200 (OK) and the list of organizations
     */
    @GetMapping("/organizations/search")
    @Timed
    public ResponseEntity<List<OrganizationDTO>> searchOrganizations(
        @RequestParam(required = false) String name, @RequestParam(required = false) String systemAccount,
        Pageable pageable) {
        Page<OrganizationDTO> page = organizationService.findAllByNameLikeAndAccountNameWithUserProfile(
            name, systemAccount, pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/organizations/search");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /organizations/:id : get the "id" organization.
     *
     * @param id the id of the organizationDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the organizationDTO, or with status 404 (Not Found)
     */
    @GetMapping("/organizations/{id}")
    @Timed
    public ResponseEntity<OrganizationDTO> getOrganization(@PathVariable UUID id) {
        log.debug("REST request to get Organization : {}", id);
        Optional<OrganizationDTO> organizationDTO = organizationService.findOneDTO(id);
        return ResponseUtil.wrapOrNotFound(organizationDTO);
    }

    /**
     * GET  /provider-organization/:id : get the organization with given id for service provider.
     *
     * @param id the id of the organization to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the SimpleOrganizationDTO,
     * or with status 404 (Not Found)
     */
    @GetMapping("/provider-organization/{id}")
    @Timed
    public ResponseEntity<ProviderOrganizationDTO> getOrganizationForProvider(@PathVariable UUID id) {
        log.debug("REST request to get Organization : {}", id);
        Optional<ProviderOrganizationDTO> organizationDTO = organizationService.findOneDTOForProvider(id);
        return ResponseUtil.wrapOrNotFound(organizationDTO);
    }

    /**
     * DELETE  /organizations/:id : delete the "id" organization.
     *
     * @param id the id of the organizationDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
    @DeleteMapping("/organizations/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID id) {
        log.debug("REST request to delete Organization : {}", id);
        organizationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * DELETE  /organizations/user-owned/:id : delete the "id" organization from current user.
     *
     * @param id the id of the organizationDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/organizations/user-owned/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrganizationUserOwned(@PathVariable UUID id) {
        log.debug("REST request to delete Organization : {}", id);
        Optional<Organization> existingOrganization;
        UserProfile userProfile = userService.getCurrentUserProfile();
        if (userProfile.getUserGroups().isEmpty()) {
            existingOrganization = organizationService
                .findOneWithIdAndUserProfile(id, userProfile);
        } else {
            existingOrganization = organizationService
                .findOneWithIdAndUserProfileInUserGroups(id, userProfile);
        }
        if (existingOrganization.isEmpty()) {
            throw new BadRequestAlertException("You are not allowed to delete this organization", ENTITY_NAME, "cantdelete");
        }
        organizationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * POST  /organizations/deactivate/:id : deactivate the "id" organization from current user.
     *
     * @param id the id of the organizationDTO to deactivate
     * @return the ResponseEntity with status 200 (OK)
     */
    @PostMapping("/organizations/deactivate/{id}")
    @Timed
    public ResponseEntity<Void> deactivateOrganization(@PathVariable UUID id) {
        log.debug("REST request to deactivate Organization : {}", id);
        Optional<Organization> existingOrganization;
        UserProfile userProfile = userService.getCurrentUserProfile();
        if (userProfile.getUserGroups().isEmpty()) {
            existingOrganization = organizationService
                .findOneWithIdAndUserProfile(id, userProfile);
        } else {
            existingOrganization = organizationService
                .findOneWithIdAndUserProfileInUserGroups(id, userProfile);
        }
        if (existingOrganization.isEmpty()) {
            throw new BadRequestAlertException(
                "You are not allowed to deactivate this organization",
                ENTITY_NAME,
                "cantEditRecord"
            );
        }
        organizationService.deactivate(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * POST  /organizations/deactivate/:id : reactivate the "id" organization from current user.
     *
     * @param id the id of the organizationDTO to reactivate
     * @return List of all not active organizations
     */
    @PostMapping("/organizations/reactivate/{id}")
    @Timed
    public ResponseEntity<List<DeactivatedOrganizationDTO>> reactivateOrganization(@PathVariable UUID id) {
        log.debug("REST request to reactivate Organization : {}", id);
        Optional<Organization> existingOrganization;
        UserProfile userProfile = userService.getCurrentUserProfile();
        if (userProfile.getUserGroups().isEmpty()) {
            existingOrganization = organizationService
                .findOneWithIdAndUserProfileAndNotActive(id, userProfile);
        } else {
            existingOrganization = organizationService
                .findOneWithIdAndUserProfileInUserGroupsAndNotActive(id, userProfile);
        }
        if (existingOrganization.isEmpty()) {
            throw new BadRequestAlertException(
                "You are not allowed to deactivate this organization",
                ENTITY_NAME,
                "cantEditRecord"
            );
        }
        organizationService.reactivate(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, id.toString()))
            .body(activityService.getAllDeactivatedRecords());
    }

    @PostMapping("/claim-records")
    @Timed
    public ResponseEntity<Void> claimRecords(@RequestBody List<UUID> recordsToClaim) {
        log.debug("REST request to clone and claim records : {}", recordsToClaim);

        organizationService.claimRecords(recordsToClaim);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unclaim-record")
    @Timed
    public ResponseEntity<Void> unclaimRecord(@RequestParam(required = false) UUID recordId) {
        log.debug("REST request to delete and unclaim records : {}", recordId);

        organizationService.unclaimRecord(recordId);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert(HeaderUtil.APPLICATION_NAME + "." + ENTITY_NAME + ".unclaimed", "")).build();
    }
}
