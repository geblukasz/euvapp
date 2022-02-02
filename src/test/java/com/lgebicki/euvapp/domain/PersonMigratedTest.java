package com.lgebicki.euvapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.lgebicki.euvapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PersonMigratedTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PersonMigrated.class);
        PersonMigrated personMigrated1 = new PersonMigrated();
        personMigrated1.setId(1L);
        PersonMigrated personMigrated2 = new PersonMigrated();
        personMigrated2.setId(personMigrated1.getId());
        assertThat(personMigrated1).isEqualTo(personMigrated2);
        personMigrated2.setId(2L);
        assertThat(personMigrated1).isNotEqualTo(personMigrated2);
        personMigrated1.setId(null);
        assertThat(personMigrated1).isNotEqualTo(personMigrated2);
    }
}
