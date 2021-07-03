package org.benetech.servicenet.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import org.benetech.servicenet.errors.BadRequestAlertException;
import org.benetech.servicenet.security.AuthoritiesConstants;
import org.benetech.servicenet.service.ServiceTaxonomiesDetailsFieldsValueService;
import org.benetech.servicenet.service.dto.ServiceTaxonomiesDetailsFieldsValueDTO;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@link org.benetech.servicenet.domain.ServiceTaxonomiesDetailsFieldsValue}.
 */
@RestController
@RequestMapping("/api")
public class ServiceTaxonomiesDetailsFieldsValueResource {

    private final Logger log = LoggerFactory.getLogger(ServiceTaxonomiesDetailsFieldsValueResource.class);

    private static final String ENTITY_NAME = "serviceTaxonomiesDetailsFieldsValue";

    private final ServiceTaxonomiesDetailsFieldsValueService serviceTaxonomiesDetailsFieldsValueService;

    public ServiceTaxonomiesDetailsFieldsValueResource(
        ServiceTaxonomiesDetailsFieldsValueService serviceTaxonomiesDetailsFieldsValueService
    ) {
        this.serviceTaxonomiesDetailsFieldsValueService = serviceTaxonomiesDetailsFieldsValueService;
    }

    /**
     * {@code POST  /service-taxonomies-details-fields-values} : Create a new serviceTaxonomiesDetailsFieldsValue.
     *
     * @param serviceTaxonomiesDetailsFieldsValueDTO the serviceTaxonomiesDetailsFieldsValueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new
     * serviceTaxonomiesDetailsFieldsValueDTO, or with status {@code 400 (Bad Request)} if the
     * serviceTaxonomiesDetailsFieldsValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
    @PostMapping("/service-taxonomies-details-fields-values")
    public ResponseEntity<ServiceTaxonomiesDetailsFieldsValueDTO> createServiceTaxonomiesDetailsFieldsValue(
        @Valid @RequestBody ServiceTaxonomiesDetailsFieldsValueDTO serviceTaxonomiesDetailsFieldsValueDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ServiceTaxonomiesDetailsFieldsValue : {}", serviceTaxonomiesDetailsFieldsValueDTO);
        if (serviceTaxonomiesDetailsFieldsValueDTO.getId() != null) {
            throw new BadRequestAlertException(
                "A new serviceTaxonomiesDetailsFieldsValue cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            );
        }
        ServiceTaxonomiesDetailsFieldsValueDTO result = serviceTaxonomiesDetailsFieldsValueService
            .save(serviceTaxonomiesDetailsFieldsValueDTO);
        return ResponseEntity.created(new URI("/api/service-taxonomies-details-fields-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /service-taxonomies-details-fields-values} : Updates an existing serviceTaxonomiesDetailsFieldsValue.
     *
     * @param serviceTaxonomiesDetailsFieldsValueDTO the serviceTaxonomiesDetailsFieldsValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated
     * serviceTaxonomiesDetailsFieldsValueDTO,
     * or with status {@code 400 (Bad Request)} if the serviceTaxonomiesDetailsFieldsValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the serviceTaxonomiesDetailsFieldsValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
    @PutMapping("/service-taxonomies-details-fields-values")
    public ResponseEntity<ServiceTaxonomiesDetailsFieldsValueDTO> updateServiceTaxonomiesDetailsFieldsValue(
        @Valid @RequestBody ServiceTaxonomiesDetailsFieldsValueDTO serviceTaxonomiesDetailsFieldsValueDTO
    ) throws URISyntaxException {
        log.debug(
            "REST request to update ServiceTaxonomiesDetailsFieldsValue : {}",
            serviceTaxonomiesDetailsFieldsValueDTO
        );
        if (serviceTaxonomiesDetailsFieldsValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ServiceTaxonomiesDetailsFieldsValueDTO result = serviceTaxonomiesDetailsFieldsValueService
            .save(serviceTaxonomiesDetailsFieldsValueDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, serviceTaxonomiesDetailsFieldsValueDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /service-taxonomies-details-fields-values} : get all the serviceTaxonomiesDetailsFieldsValues.
     *
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of serviceTaxonomiesDetailsFieldsValues
     * in body.
     */
    @GetMapping("/service-taxonomies-details-fields-values")
    public ResponseEntity<List<ServiceTaxonomiesDetailsFieldsValueDTO>> getAllServiceTaxonomiesDetailsFieldsValues(
        Pageable pageable) {
        log.debug("REST request to get all ServiceTaxonomiesDetailsFieldsValues");
        Page<ServiceTaxonomiesDetailsFieldsValueDTO> page = serviceTaxonomiesDetailsFieldsValueService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/service-taxonomies-details-fields-values");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * {@code GET  /service-taxonomies-details-fields-values/:id} : get the "id" serviceTaxonomiesDetailsFieldsValue.
     *
     * @param id the id of the serviceTaxonomiesDetailsFieldsValueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the
     * serviceTaxonomiesDetailsFieldsValueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/service-taxonomies-details-fields-values/{id}")
    public ResponseEntity<ServiceTaxonomiesDetailsFieldsValueDTO> getServiceTaxonomiesDetailsFieldsValue(
        @PathVariable UUID id
    ) {
        log.debug("REST request to get ServiceTaxonomiesDetailsFieldsValue : {}", id);
        Optional<ServiceTaxonomiesDetailsFieldsValueDTO> serviceTaxonomiesDetailsFieldsValueDTO =
            serviceTaxonomiesDetailsFieldsValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(serviceTaxonomiesDetailsFieldsValueDTO);
    }

    /**
     * {@code DELETE  /service-taxonomies-details-fields-values/:id} : delete the "id" serviceTaxonomiesDetailsFieldsValue.
     *
     * @param id the id of the serviceTaxonomiesDetailsFieldsValueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
    @DeleteMapping("/service-taxonomies-details-fields-values/{id}")
    public ResponseEntity<Void> deleteServiceTaxonomiesDetailsFieldsValue(@PathVariable UUID id) {
        log.debug("REST request to delete ServiceTaxonomiesDetailsFieldsValue : {}", id);
        serviceTaxonomiesDetailsFieldsValueService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
