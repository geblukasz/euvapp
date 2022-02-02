package com.lgebicki.euvapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lgebicki.euvapp.IntegrationTest;
import com.lgebicki.euvapp.domain.PersonMigrated;
import com.lgebicki.euvapp.repository.PersonMigratedRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PersonMigratedResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PersonMigratedResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/person-migrateds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PersonMigratedRepository personMigratedRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPersonMigratedMockMvc;

    private PersonMigrated personMigrated;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PersonMigrated createEntity(EntityManager em) {
        PersonMigrated personMigrated = new PersonMigrated().firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME);
        return personMigrated;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PersonMigrated createUpdatedEntity(EntityManager em) {
        PersonMigrated personMigrated = new PersonMigrated().firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);
        return personMigrated;
    }

    @BeforeEach
    public void initTest() {
        personMigrated = createEntity(em);
    }

    @Test
    @Transactional
    void createPersonMigrated() throws Exception {
        int databaseSizeBeforeCreate = personMigratedRepository.findAll().size();
        // Create the PersonMigrated
        restPersonMigratedMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personMigrated))
            )
            .andExpect(status().isCreated());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeCreate + 1);
        PersonMigrated testPersonMigrated = personMigratedList.get(personMigratedList.size() - 1);
        assertThat(testPersonMigrated.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testPersonMigrated.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void createPersonMigratedWithExistingId() throws Exception {
        // Create the PersonMigrated with an existing ID
        personMigrated.setId(1L);

        int databaseSizeBeforeCreate = personMigratedRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonMigratedMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personMigrated))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPersonMigrateds() throws Exception {
        // Initialize the database
        personMigratedRepository.saveAndFlush(personMigrated);

        // Get all the personMigratedList
        restPersonMigratedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(personMigrated.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)));
    }

    @Test
    @Transactional
    void getPersonMigrated() throws Exception {
        // Initialize the database
        personMigratedRepository.saveAndFlush(personMigrated);

        // Get the personMigrated
        restPersonMigratedMockMvc
            .perform(get(ENTITY_API_URL_ID, personMigrated.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(personMigrated.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME));
    }

    @Test
    @Transactional
    void getNonExistingPersonMigrated() throws Exception {
        // Get the personMigrated
        restPersonMigratedMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPersonMigrated() throws Exception {
        // Initialize the database
        personMigratedRepository.saveAndFlush(personMigrated);

        int databaseSizeBeforeUpdate = personMigratedRepository.findAll().size();

        // Update the personMigrated
        PersonMigrated updatedPersonMigrated = personMigratedRepository.findById(personMigrated.getId()).get();
        // Disconnect from session so that the updates on updatedPersonMigrated are not directly saved in db
        em.detach(updatedPersonMigrated);
        updatedPersonMigrated.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        restPersonMigratedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPersonMigrated.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPersonMigrated))
            )
            .andExpect(status().isOk());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeUpdate);
        PersonMigrated testPersonMigrated = personMigratedList.get(personMigratedList.size() - 1);
        assertThat(testPersonMigrated.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testPersonMigrated.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void putNonExistingPersonMigrated() throws Exception {
        int databaseSizeBeforeUpdate = personMigratedRepository.findAll().size();
        personMigrated.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonMigratedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personMigrated.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personMigrated))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPersonMigrated() throws Exception {
        int databaseSizeBeforeUpdate = personMigratedRepository.findAll().size();
        personMigrated.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMigratedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personMigrated))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPersonMigrated() throws Exception {
        int databaseSizeBeforeUpdate = personMigratedRepository.findAll().size();
        personMigrated.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMigratedMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personMigrated)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePersonMigratedWithPatch() throws Exception {
        // Initialize the database
        personMigratedRepository.saveAndFlush(personMigrated);

        int databaseSizeBeforeUpdate = personMigratedRepository.findAll().size();

        // Update the personMigrated using partial update
        PersonMigrated partialUpdatedPersonMigrated = new PersonMigrated();
        partialUpdatedPersonMigrated.setId(personMigrated.getId());

        restPersonMigratedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonMigrated.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPersonMigrated))
            )
            .andExpect(status().isOk());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeUpdate);
        PersonMigrated testPersonMigrated = personMigratedList.get(personMigratedList.size() - 1);
        assertThat(testPersonMigrated.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testPersonMigrated.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void fullUpdatePersonMigratedWithPatch() throws Exception {
        // Initialize the database
        personMigratedRepository.saveAndFlush(personMigrated);

        int databaseSizeBeforeUpdate = personMigratedRepository.findAll().size();

        // Update the personMigrated using partial update
        PersonMigrated partialUpdatedPersonMigrated = new PersonMigrated();
        partialUpdatedPersonMigrated.setId(personMigrated.getId());

        partialUpdatedPersonMigrated.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        restPersonMigratedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonMigrated.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPersonMigrated))
            )
            .andExpect(status().isOk());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeUpdate);
        PersonMigrated testPersonMigrated = personMigratedList.get(personMigratedList.size() - 1);
        assertThat(testPersonMigrated.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testPersonMigrated.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingPersonMigrated() throws Exception {
        int databaseSizeBeforeUpdate = personMigratedRepository.findAll().size();
        personMigrated.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonMigratedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, personMigrated.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personMigrated))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPersonMigrated() throws Exception {
        int databaseSizeBeforeUpdate = personMigratedRepository.findAll().size();
        personMigrated.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMigratedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personMigrated))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPersonMigrated() throws Exception {
        int databaseSizeBeforeUpdate = personMigratedRepository.findAll().size();
        personMigrated.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMigratedMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(personMigrated))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PersonMigrated in the database
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePersonMigrated() throws Exception {
        // Initialize the database
        personMigratedRepository.saveAndFlush(personMigrated);

        int databaseSizeBeforeDelete = personMigratedRepository.findAll().size();

        // Delete the personMigrated
        restPersonMigratedMockMvc
            .perform(delete(ENTITY_API_URL_ID, personMigrated.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PersonMigrated> personMigratedList = personMigratedRepository.findAll();
        assertThat(personMigratedList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
