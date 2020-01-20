package com.redhat.labs.omp;

import com.redhat.labs.omp.resources.TemplateCombobulator;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@QuarkusTest
public class TemplateCombobulatorTest {


    @Inject
    TemplateCombobulator templateCombobulator;

    @Test
    void templateCombobulator(){
        Map<String, String> myMap = new HashMap();
        myMap.put("RESIDENCY_CUSTOMER_NAME", "mickey-mouse");
        myMap.put("RESIDENCY_TECHNICAL_LEAD_EMAIL", "michael.mouse@disney.com");
        myMap.put("RESIDENCY_LOCATION", "Magic Kingdom");

        TemplateInstance templateInstance = templateCombobulator.combobulateTemplateInstance("biscuits", myMap);
        Assertions.assertNotNull(templateInstance);
        String result = templateInstance.render();

        Assertions.assertTrue(result.contains("mickey-mouse"));
        Assertions.assertFalse(result.contains("RESIDENCY_CUSTOMER_NAME"));

        Assertions.assertTrue(result.contains("michael.mouse@disney.com"));
        Assertions.assertFalse(result.contains("RESIDENCY_TECHNICAL_LEAD_EMAIL"));

        Assertions.assertTrue(result.contains("Magic Kingdom"));
        Assertions.assertFalse(result.contains("RESIDENCY_LOCATION"));

    }
}