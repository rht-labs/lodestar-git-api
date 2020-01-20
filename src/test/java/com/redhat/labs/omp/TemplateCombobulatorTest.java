package com.redhat.labs.omp;

import com.redhat.labs.omp.models.filesmanagement.GetMultipleFilesResponse;
import com.redhat.labs.omp.resources.TemplateCombobulator;
import com.redhat.labs.omp.resources.TemplateResource;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuarkusTest
public class TemplateCombobulatorTest {
    Map<String, String> myMap;
    String exampleTemplate;

    @BeforeEach
    public void setupTestData(){
        myMap = new HashMap();
        myMap.put("RESIDENCY_CUSTOMER_NAME", "mickey-mouse");
        myMap.put("RESIDENCY_TECHNICAL_LEAD_EMAIL", "michael.mouse@disney.com");
        myMap.put("RESIDENCY_LOCATION", "Magic Kingdom");
        exampleTemplate = "---\n"+
                "\n"+
                "residency:\n"+
                "  customer_name: \"{RESIDENCY_CUSTOMER_NAME}\"\n"+
                "  project_name: \"{RESIDENCY_PROJECT_NAME}\"\n"+
                "  description: \"{RESIDENCY_DESCRIPTION}\"\n"+
                "  location: \"{RESIDENCY_LOCATION}\"\n"+
                "  start_date: \"{RESIDENCY_START_DATE}\"\n"+
                "  end_date: \"{RESIDENCY_END_DATE}\"\n"+
                "  archive_date: \"{RESIDENCY_ARCHIVE_DATE}\"\n"+
                "  contacts:\n"+
                "    engagement_lead:\n"+
                "      name: \"{RESIDENCY_ENGAGEMENT_LEAD_NAME}\"\n"+
                "      email: \"{RESIDENCY_ENGAGEMENT_LEAD_EMAIL}\"\n"+
                "    technical_lead:\n"+
                "      name: \"{RESIDENCY_TECHNICAL_LEAD_NAME}\"\n"+
                "      email: \"{RESIDENCY_TECHNICAL_LEAD_EMAIL}\"\n"+
                "    customer_contact:\n"+
                "      name: \"{CUSTOMER_CONTACT_NAME}\"\n"+
                "      email: \"{CUSTOMER_CONTACT_EMAIL}\"\n"+
                "  openshift_cluster:\n"+
                "    cloud-provider:\n"+
                "      name: \"{OCP_CLOUD_PROVIDER_NAME}\"\n"+
                "      region: \"{OCP_CLOUD_PROVIDER_REGION}\"\n"+
                "    version: \"{OCP_VERSION}\"\n"+
                "    sub-domain: \"{OCP_SUB_DOMAIN}\"\n"+
                "    persistent-storage-size: \"{OCP_PV_SIZE}\"\n"+
                "    cluster-size: \"{OCP_CLUSTER_SIZE}\"\n"+
                "     \n";
    }

    @Inject
    TemplateCombobulator templateCombobulator;



    @Test
    void templateCombobulator(){

        TemplateInstance templateInstance = templateCombobulator.combobulateTemplateInstance(exampleTemplate, myMap);
        Assertions.assertNotNull(templateInstance);
        String result = templateInstance.render();

        Assertions.assertTrue(result.contains("mickey-mouse"));
        Assertions.assertFalse(result.contains("RESIDENCY_CUSTOMER_NAME"));

        Assertions.assertTrue(result.contains("michael.mouse@disney.com"));
        Assertions.assertFalse(result.contains("RESIDENCY_TECHNICAL_LEAD_EMAIL"));

        Assertions.assertTrue(result.contains("Magic Kingdom"));
        Assertions.assertFalse(result.contains("RESIDENCY_LOCATION"));

    }

    @Test
    void processTemplate() {
        GetMultipleFilesResponse processedFiles = templateCombobulator.process(myMap);
//      🤠 Dirty hack to get the first index of the files array 🤠
        Assertions.assertTrue(processedFiles.files.get(0).fileContent.contains("mickey-mouse"));
        Assertions.assertFalse(processedFiles.files.get(0).fileContent.contains("RESIDENCY_CUSTOMER_NAME"));

    }
}