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
    Map<String, Object> myMap;
    String exampleTemplate;

    @BeforeEach
    public void setupTestData(){
        myMap = new HashMap();
        myMap.put("customer_name", "mickey-mouse");
        myMap.put("engagement_lead_email", "michael.mouse@disney.com");
        myMap.put("location", "Magic Kingdom");
        exampleTemplate = "residency:\n" +
                "  id: \"{id}\"\n" +
                "  customer_name: \"{customer_name}\"\n" +
                "  project_name: \"{project_name}\"\n" +
                "  description: \"{description}\"\n" +
                "  location: \"{location}\"\n" +
                "  start_date: \"{start_date}\"\n" +
                "  end_date: \"{end_date}\"\n" +
                "  archive_date: \"{archive_date}\"\n" +
                "  contacts:\n" +
                "    engagement_lead:\n" +
                "      name: \"{engagement_lead_name}\"\n" +
                "      email: \"{engagement_lead_email}\"\n" +
                "    technical_lead:\n" +
                "      name: \"{technical_lead_name}\"\n" +
                "      email: \"{technical_lead_email}\"\n" +
                "    customer_contact:\n" +
                "      name: \"{customer_contact_name}\"\n" +
                "      email: \"{customer_contact_email}\"\n" +
                "  openshift_cluster:\n" +
                "    cloud_provider:\n" +
                "      name: \"{ocp_cloud_provider_name}\"\n" +
                "      region: \"{ocp_cloud_provider_region}\"\n" +
                "    version: \"{ocp_version}\"\n" +
                "    sub_domain: \"{ocp_sub_domain}\"\n" +
                "    persistent_storage_size: \"{ocp_persistent_storage_size}\"\n" +
                "    cluster_size: \"{ocp_cluster_size}\"";
    }

    @Inject
    TemplateCombobulator templateCombobulator;



    @Test
    void templateCombobulator(){

        TemplateInstance templateInstance = templateCombobulator.combobulateTemplateInstance(exampleTemplate, myMap);
        Assertions.assertNotNull(templateInstance);
        String result = templateInstance.render();

        Assertions.assertTrue(result.contains("mickey-mouse"));

        Assertions.assertTrue(result.contains("michael.mouse@disney.com"));

        Assertions.assertTrue(result.contains("Magic Kingdom"));

    }

    @Test
    void processTemplate() {
        GetMultipleFilesResponse processedFiles = templateCombobulator.process(myMap);
        //      🤠 Dirty hack to get the first index of the files array 🤠
        Assertions.assertTrue(processedFiles.files.get(0).fileContent.contains("mickey-mouse"));

        Assertions.assertTrue(processedFiles.files.get(0).fileContent.contains("michael.mouse@disney.com"));

        Assertions.assertTrue(processedFiles.files.get(0).fileContent.contains("Magic Kingdom"));
    }
}