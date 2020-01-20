package com.redhat.labs.omp.resources;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Template;

@ApplicationScoped
public class TemplateCombobulator  {

    @Inject
    Engine engine;

    public TemplateInstance combobulateTemplateInstance(String templateName, Map<String, String> templateVariables) {
        // String should be the template
        Template fetchedTemplate = engine.parse(this.getTemplate(templateName));
        TemplateInstance processedTemplate = null;
        for (Map.Entry<String, String> entry : templateVariables.entrySet()) {
            if (processedTemplate == null) {
                processedTemplate = fetchedTemplate.data(entry.getKey(), entry.getValue());
            } else {
                processedTemplate = processedTemplate.data(entry.getKey(), entry.getValue());
            }
        }
        return processedTemplate;
    }

    public String combobulateTemplateInstanceAsString(String templateName, Map<String, String> templateVariables) {
        return this.combobulateTemplateInstance(templateName, templateVariables).render();
    }

    // Get templates from location (wherever that is) based on name from meta.dat
    private String getTemplate(String name) {
        // TODO - implement the lookup based on Faisal and Gabriel's work
        return "---\n"+
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
}