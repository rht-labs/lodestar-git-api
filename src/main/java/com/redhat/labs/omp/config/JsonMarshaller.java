package com.redhat.labs.omp.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

import io.quarkus.runtime.StartupEvent;

/**
 * Used converting String to Objects (non-request, non-response)
 * @author mcanoy
 *
 */
@ApplicationScoped
public class JsonMarshaller {

    private Jsonb jsonb;
    
    void onStart(@Observes StartupEvent event) { 
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true)
                .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        jsonb = JsonbBuilder.create(config);
    }
    
    public <T> String toJson(T object) {
        return jsonb.toJson(object);
    }
    
    public <T> T fromJson(String json, Class<T> type) {
        
        return jsonb.fromJson(json, type);
    }
    
    
}
