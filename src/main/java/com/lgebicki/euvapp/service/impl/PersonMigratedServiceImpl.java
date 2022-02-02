package com.lgebicki.euvapp.service.impl;

import com.lgebicki.euvapp.domain.PersonMigrated;
import com.lgebicki.euvapp.repository.PersonMigratedRepository;
import com.lgebicki.euvapp.service.PersonMigratedService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PersonMigrated}.
 */
@Service
@Transactional
public class PersonMigratedServiceImpl implements PersonMigratedService {

    private final Logger log = LoggerFactory.getLogger(PersonMigratedServiceImpl.class);

    private final PersonMigratedRepository personMigratedRepository;

    public PersonMigratedServiceImpl(PersonMigratedRepository personMigratedRepository) {
        this.personMigratedRepository = personMigratedRepository;
    }

    @Override
    public PersonMigrated save(PersonMigrated personMigrated) {
        log.debug("Request to save PersonMigrated : {}", personMigrated);
        return personMigratedRepository.save(personMigrated);
    }

    @Override
    public Optional<PersonMigrated> partialUpdate(PersonMigrated personMigrated) {
        log.debug("Request to partially update PersonMigrated : {}", personMigrated);

        return personMigratedRepository
            .findById(personMigrated.getId())
            .map(existingPersonMigrated -> {
                if (personMigrated.getFirstName() != null) {
                    existingPersonMigrated.setFirstName(personMigrated.getFirstName());
                }
                if (personMigrated.getLastName() != null) {
                    existingPersonMigrated.setLastName(personMigrated.getLastName());
                }

                return existingPersonMigrated;
            })
            .map(personMigratedRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonMigrated> findAll() {
        log.debug("Request to get all PersonMigrateds");
        return personMigratedRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonMigrated> findOne(Long id) {
        log.debug("Request to get PersonMigrated : {}", id);
        return personMigratedRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PersonMigrated : {}", id);
        personMigratedRepository.deleteById(id);
    }
}
