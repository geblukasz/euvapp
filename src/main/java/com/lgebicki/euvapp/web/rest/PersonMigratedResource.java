package com.lgebicki.euvapp.web.rest;

import com.lgebicki.euvapp.domain.PersonMigrated;
import com.lgebicki.euvapp.repository.PersonMigratedRepository;
import com.lgebicki.euvapp.service.PersonMigratedService;
import com.lgebicki.euvapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.lgebicki.euvapp.domain.PersonMigrated}.
 */
@RestController
@RequestMapping("/api")
public class PersonMigratedResource {

    private final Logger log = LoggerFactory.getLogger(PersonMigratedResource.class);

    private static final String ENTITY_NAME = "personMigrated";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PersonMigratedService personMigratedService;

    private final PersonMigratedRepository personMigratedRepository;

    public PersonMigratedResource(PersonMigratedService personMigratedService, PersonMigratedRepository personMigratedRepository) {
        this.personMigratedService = personMigratedService;
        this.personMigratedRepository = personMigratedRepository;
    }

    /**
     * {@code POST  /person-migrateds} : Create a new personMigrated.
     *
     * @param personMigrated the personMigrated to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new personMigrated, or with status {@code 400 (Bad Request)} if the personMigrated has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/person-migrateds")
    public ResponseEntity<PersonMigrated> createPersonMigrated(@RequestBody PersonMigrated personMigrated) throws URISyntaxException {
        log.debug("REST request to save PersonMigrated : {}", personMigrated);
        if (personMigrated.getId() != null) {
            throw new BadRequestAlertException("A new personMigrated cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PersonMigrated result = personMigratedService.save(personMigrated);
        return ResponseEntity
            .created(new URI("/api/person-migrateds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /person-migrateds/:id} : Updates an existing personMigrated.
     *
     * @param id the id of the personMigrated to save.
     * @param personMigrated the personMigrated to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personMigrated,
     * or with status {@code 400 (Bad Request)} if the personMigrated is not valid,
     * or with status {@code 500 (Internal Server Error)} if the personMigrated couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/person-migrateds/{id}")
    public ResponseEntity<PersonMigrated> updatePersonMigrated(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PersonMigrated personMigrated
    ) throws URISyntaxException {
        log.debug("REST request to update PersonMigrated : {}, {}", id, personMigrated);
        if (personMigrated.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personMigrated.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personMigratedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PersonMigrated result = personMigratedService.save(personMigrated);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, personMigrated.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /person-migrateds/:id} : Partial updates given fields of an existing personMigrated, field will ignore if it is null
     *
     * @param id the id of the personMigrated to save.
     * @param personMigrated the personMigrated to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personMigrated,
     * or with status {@code 400 (Bad Request)} if the personMigrated is not valid,
     * or with status {@code 404 (Not Found)} if the personMigrated is not found,
     * or with status {@code 500 (Internal Server Error)} if the personMigrated couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/person-migrateds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PersonMigrated> partialUpdatePersonMigrated(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PersonMigrated personMigrated
    ) throws URISyntaxException {
        log.debug("REST request to partial update PersonMigrated partially : {}, {}", id, personMigrated);
        if (personMigrated.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personMigrated.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personMigratedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PersonMigrated> result = personMigratedService.partialUpdate(personMigrated);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, personMigrated.getId().toString())
        );
    }

    /**
     * {@code GET  /person-migrateds} : get all the personMigrateds.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of personMigrateds in body.
     */
    @GetMapping("/person-migrateds")
    public List<PersonMigrated> getAllPersonMigrateds() {
        log.debug("REST request to get all PersonMigrateds");
        return personMigratedService.findAll();
    }

    /**
     * {@code GET  /person-migrateds/:id} : get the "id" personMigrated.
     *
     * @param id the id of the personMigrated to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personMigrated, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/person-migrateds/{id}")
    public ResponseEntity<PersonMigrated> getPersonMigrated(@PathVariable Long id) {
        log.debug("REST request to get PersonMigrated : {}", id);
        Optional<PersonMigrated> personMigrated = personMigratedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(personMigrated);
    }

    /**
     * {@code DELETE  /person-migrateds/:id} : delete the "id" personMigrated.
     *
     * @param id the id of the personMigrated to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/person-migrateds/{id}")
    public ResponseEntity<Void> deletePersonMigrated(@PathVariable Long id) {
        log.debug("REST request to delete PersonMigrated : {}", id);
        personMigratedService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
