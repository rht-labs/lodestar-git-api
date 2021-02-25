package com.redhat.labs.lodestar.models.pagination;

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
    
    private static final int UNDEFINED_PAGE_COUNT = 10000;
    private int pageSize;
    @Builder.Default private int number = 1;
    @Builder.Default private int total = 1;
    @Builder.Default private List<T> results = new ArrayList<>();
    
    public PagedResults(int pageSize) {
        this();
        this.pageSize = pageSize;
    }
    
    
    public boolean hasMore() {
        return total >= number;
    }
    
    public void update(Response response, GenericType<List<T>> type) {
        LOGGER.trace("page result update");
        
        if(number == 1) {
            String totalPageString = response.getHeaderString("X-Total-Pages");
            
            if(totalPageString == null) {
                total = UNDEFINED_PAGE_COUNT; //Should not be able to get this high
                LOGGER.trace("X-Total-Pages header is missing");
            } else {
                total = Integer.valueOf(totalPageString);
            }
            
            LOGGER.trace("TOTAL PAGES {}", total);
        }
        List<T> responseSet = response.readEntity(type);
        
        //There weren't enough  results to fetch another page
        if(responseSet.size() < pageSize && total == UNDEFINED_PAGE_COUNT) {
            total = 1;
        }
        
        results.addAll(responseSet);
        number++;
    }
    
    public int size() {
        return results.size();
    }
  
}
