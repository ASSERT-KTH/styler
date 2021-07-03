package org.benetech.servicenet.web.rest.unauthorized;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.benetech.servicenet.config.Constants;
import org.benetech.servicenet.domain.Silo;
import org.benetech.servicenet.errors.BadRequestAlertException;
import org.benetech.servicenet.service.ActivityFilterService;
import org.benetech.servicenet.service.ActivityService;
import org.benetech.servicenet.service.OrganizationService;
import org.benetech.servicenet.service.SiloService;
import org.benetech.servicenet.service.SystemAccountService;
import org.benetech.servicenet.service.TaxonomyService;
import org.benetech.servicenet.service.dto.SiloDTO;
import org.benetech.servicenet.service.dto.provider.ProviderRecordDTO;
import org.benetech.servicenet.service.dto.provider.ProviderRecordForMapDTO;
import org.benetech.servicenet.service.dto.SystemAccountDTO;
import org.benetech.servicenet.service.dto.TaxonomyDTO;
import org.benetech.servicenet.service.dto.provider.ProviderFilterDTO;
import org.benetech.servicenet.service.dto.provider.ProviderOrganizationDTO;
import org.benetech.servicenet.web.rest.TaxonomyFilterDTO;
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
 * REST controller for managing public record resource.
 */
@RestController
@RequestMapping("/public-api")
public class PublicRecordResource {

    private final Logger log = LoggerFactory.getLogger(PublicRecordResource.class);

    @Autowired
    private ActivityService activityService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private SiloService siloService;

    @Autowired
    private TaxonomyService taxonomyService;

    @Autowired
    private ActivityFilterService activityFilterService;

    @Autowired
    private SystemAccountService systemAccountService;

    @PostMapping("/all-provider-records/{silo}")
    @Timed
    public ResponseEntity<List<ProviderRecordDTO>> getProviderActivitiesPublic(
        @RequestBody ProviderFilterDTO providerFilterDTO, @RequestParam(required = false) String search, Pageable pageable,
        @PathVariable String silo) {
        Optional<Silo> optSilo = siloService.getOneByName(silo);
        this.checkSilo(optSilo);
        Page<ProviderRecordDTO> page = activityService.getAllPartnerActivitiesPublic(providerFilterDTO,
            optSilo.get(), search, pageable);
        HttpHeaders headers = PaginationUtil
            .generatePaginationHttpHeaders(page, "/public-api/all-provider-records");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
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
    public ResponseEntity<ProviderOrganizationDTO> getOrganizationForProvider(@PathVariable UUID id,
        @RequestParam String siloName) {
        Optional<Silo> optSilo = siloService.getOneByName(siloName);
        this.checkSilo(optSilo);
        log.debug("REST request to get Organization : {}", id);
        Optional<ProviderOrganizationDTO> organizationDTO = organizationService
            .findOneDTOForProviderAndSilo(id, optSilo.get());
        return ResponseUtil.wrapOrNotFound(organizationDTO);
    }

    /**
     * GET  /provider-taxonomies/:provider : get provider's taxonomies.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of taxonomies in body
     */
    @GetMapping("/provider-taxonomies/{provider}")
    @Timed
    public ResponseEntity<List<TaxonomyDTO>> getProviderTaxonomies(@PathVariable String provider,
        @RequestParam String siloName) {
        log.debug("REST request to get {} provider's Taxonomies", provider);
        Optional<Silo> optSilo = siloService.getOneByName(siloName);
        this.checkSilo(optSilo);
        List<TaxonomyDTO> taxonomies = taxonomyService.findByProvider(provider);
        return new ResponseEntity<>(taxonomies, HttpStatus.OK);
    }

    @PostMapping("/all-provider-records-map")
    @Timed
    public ResponseEntity<List<ProviderRecordForMapDTO>> getAllProviderActivitiesForMap(
        @RequestParam String siloName, @RequestBody ProviderFilterDTO providerFilterDTO,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) List<Double> boundaries, Pageable pageable
    ) {
        Optional<Silo> optSilo = siloService.getOneByName(siloName);
        this.checkSilo(optSilo);
        Page<ProviderRecordForMapDTO> page = activityService.getAllPartnerActivitiesForMap(
            pageable, providerFilterDTO, search, optSilo.get(), boundaries);
        HttpHeaders headers = PaginationUtil
            .generatePaginationHttpHeaders(page, "/public-api/all-provider-records-map");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/select-record/{orgId}")
    @Timed
    public ResponseEntity<ProviderRecordDTO> getSelectedRecord(@PathVariable UUID orgId,
        @RequestParam String siloName) {
        Optional<Silo> optSilo = siloService.getOneByName(siloName);
        this.checkSilo(optSilo);
        return ResponseEntity.ok().body(activityService.getPartnerActivityById(orgId, optSilo.get()));
    }

    /**
     * GET getPostalCodesForServiceProviders
     */
    @GetMapping("/activity-filter/service-providers/get-postal-codes")
    public Set<String> getPostalCodesForServiceProviders(@RequestParam String siloName) {
        Optional<Silo> optSilo = siloService.getOneByName(siloName);
        this.checkSilo(optSilo);
        return activityFilterService.getPostalCodesForServiceProviders(optSilo.get());
    }

    /**
     * GET getRegionsForServiceProviders
     */
    @GetMapping("/activity-filter/service-providers/get-regions")
    public Set<String> getRegionsForServiceProviders(@RequestParam String siloName) {
        Optional<Silo> optSilo = siloService.getOneByName(siloName);
        this.checkSilo(optSilo);
        return activityFilterService.getRegionsForServiceProviders(optSilo.get());
    }

    /**
     * GET getCitiesForServiceProviders
     */
    @GetMapping("/activity-filter/service-providers/get-cities")
    public Set<String> getCitiesForServiceProviders(@RequestParam String siloName) {
        Optional<Silo> optSilo = siloService.getOneByName(siloName);
        this.checkSilo(optSilo);
        return activityFilterService.getCitiesForServiceProviders(optSilo.get());
    }

    /**
     * GET  /system-accounts : get all the systemAccounts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of systemAccounts in body
     */
    @GetMapping("/system-accounts")
    @Timed
    public List<SystemAccountDTO> getAllSystemAccounts() {
        log.debug("REST request to get all SystemAccounts");
        return systemAccountService.findAll();
    }

    /**
     * GET getTaxonomies
     */
    @GetMapping("/activity-filter/get-taxonomies")
    public TaxonomyFilterDTO getTaxonomies(@RequestParam(required = false) UUID siloId, @RequestParam(required = false) String siloName) {
        TaxonomyFilterDTO taxonomyFilterDTO = new TaxonomyFilterDTO();
        if (siloId != null) {
            taxonomyFilterDTO.setTaxonomiesByProvider(
                activityFilterService.getTaxonomies(siloId)
            );
        } else {
            Optional<SiloDTO> silo = siloService.findOneByName(siloName);
            if (silo.isPresent()) {
                taxonomyFilterDTO.setTaxonomiesByProvider(
                    activityFilterService.getTaxonomies(silo.get().getId())
                );
            } else {
                throw new BadRequestAlertException("There is no silo with such name", "providerRecord", "idnull");
            }
        }
        taxonomyFilterDTO.setCurrentProvider(
            Constants.SERVICE_PROVIDER
        );
        return taxonomyFilterDTO;
    }

    private void checkSilo(Optional<Silo> optSilo) {
        if (optSilo.isEmpty()) {
            throw new BadRequestAlertException("There is no silo with such name", "providerRecord", "idnull");
        }
        Silo silo = optSilo.get();
        if (!silo.isPublic()) {
            throw new BadRequestAlertException("Silo is not public", "providerRecord", "idnull");
        }
    }
}
