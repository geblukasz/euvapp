package com.lgebicki.euvapp;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.lgebicki.euvapp");

        noClasses()
            .that()
            .resideInAnyPackage("com.lgebicki.euvapp.service..")
            .or()
            .resideInAnyPackage("com.lgebicki.euvapp.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.lgebicki.euvapp.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
