package com.redhat.labs.omp;

import javax.inject.Inject;

import org.infinispan.server.hotrod.HotRodServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.redhat.labs.omp.mocks.MockHotRodServer;
import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.filesmanagement.GetMultipleFilesResponse;
import com.redhat.labs.omp.utils.TemplateCombobulator;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TemplateCombobulatorTest {
    private static HotRodServer hs;

    @Inject
    TemplateCombobulator templateCombobulator;

    @BeforeAll
    public static void init() {
        hs = MockHotRodServer.getHotRod();
    }

    @AfterAll
    public static void stop() {
        hs.stop();
    }

    @Test
    void processTemplate() {
        Engagement eng = new Engagement();
        eng.id = 1;
        eng.archiveDate = "20201225";
        eng.startDate = "20200101";
        eng.endDate = "20200404";
        eng.customerContactEmail = "jim@redhat.com";
        eng.customerContactName = "mickey-mouse";
        eng.customerName = "Santa";
        eng.description = "North Pole Inventroy";
        eng.engagementLeadEmail = "michael.mouse@disney.com";
        eng.engagementLeadEmail = "joe@redhat.com";
        eng.location = "Magic Kingdom";
        eng.ocpCloudProviderName = "GCP";
        eng.ocpCloudProviderRegion = "west";
        eng.ocpClusterSize = "medium";
        eng.ocpPersistentStorageSize = "50TB";
        eng.ocpSubDomain = "claws";
        eng.ocpVersion = "1.0.0";
        eng.projectName = "Gift gifts";
        eng.technicalLeadEmail = "michael.mouse@disney.com";
        eng.technicalLeadName = "Mitch";
        GetMultipleFilesResponse processedFiles = templateCombobulator.process(eng);

        String expected = "---\n" +
                "\n" +
                "residency:\n" +
                "  id: \"1\"\n" +
                "  customer_name: \"Santa\"\n" +
                "  project_name: \"Gift gifts\"\n" +
                "  description: \"North Pole Inventroy\"\n" +
                "  location: \"Magic Kingdom\"\n" +
                "  start_date: \"20200101\"\n" +
                "  end_date: \"20200404\"\n" +
                "  archive_date: \"20201225\"\n" +
                "  contacts:\n" +
                "    engagement_lead:\n" +
                "      name: \"\"\n" +
                "      email: \"joe@redhat.com\"\n" +
                "    technical_lead:\n" +
                "      name: \"Mitch\"\n" +
                "      email: \"michael.mouse@disney.com\"\n" +
                "    customer_contact:\n" +
                "      name: \"mickey-mouse\"\n" +
                "      email: \"jim@redhat.com\"\n" +
                "  openshift_cluster:\n" +
                "    cloud_provider:\n" +
                "      name: \"GCP\"\n" +
                "      region: \"west\"\n" +
                "    version: \"1.0.0\"\n" +
                "    sub_domain: \"claws\"\n" +
                "    persistent_storage_size: \"50TB\"\n" +
                "    cluster_size: \"medium\"\n";

        String content = processedFiles.files.get(0).fileContent;

        Assertions.assertEquals(expected, content);
        //      🤠 Dirty hack to get the first index of the files array 🤠
        Assertions.assertTrue(content.contains("mickey-mouse"));
        Assertions.assertTrue(content.contains("michael.mouse@disney.com"));
        Assertions.assertTrue(content.contains("Magic Kingdom"));
    }
}