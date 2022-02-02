package com.lgebicki.euvapp.repository;

import com.lgebicki.euvapp.domain.PersonMigrated;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the PersonMigrated entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PersonMigratedRepository extends JpaRepository<PersonMigrated, Long> {}
