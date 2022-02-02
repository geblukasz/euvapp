package com.lgebicki.euvapp.service;

import com.lgebicki.euvapp.domain.PersonMigrated;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link PersonMigrated}.
 */
public interface PersonMigratedService {
    /**
     * Save a personMigrated.
     *
     * @param personMigrated the entity to save.
     * @return the persisted entity.
     */
    PersonMigrated save(PersonMigrated personMigrated);

    /**
     * Partially updates a personMigrated.
     *
     * @param personMigrated the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PersonMigrated> partialUpdate(PersonMigrated personMigrated);

    /**
     * Get all the personMigrateds.
     *
     * @return the list of entities.
     */
    List<PersonMigrated> findAll();

    /**
     * Get the "id" personMigrated.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PersonMigrated> findOne(Long id);

    /**
     * Delete the "id" personMigrated.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
