package com.redhat.labs.omp.models;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A simple class holding a results from a number of paged queries to gitlab
 * @author mcanoy
 *
 * @param <T>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResults<T> {
    public static final Logger LOGGER = LoggerFactory.getLogger(PagedResults.class);
            
    @Builder.Default private int number = 1;
    @Builder.Default private int total = 1;
    @Builder.Default private List<T> results = new ArrayList<>();
    
    
    public boolean hasMore() {
        return total >= number;
    }
    
    public void update(Response response, GenericType<List<T>> type) {
        
        if(number == 1) {
            String totalPageString = response.getHeaderString("X-Total-Pages");
            total = Integer.valueOf(totalPageString);
            LOGGER.trace("TOTAL PAGES {}", total);
        }
        List<T> p = response.readEntity(type);
        results.addAll(p);
        number++;
    }
    
    public int size() {
        return results.size();
    }
}
