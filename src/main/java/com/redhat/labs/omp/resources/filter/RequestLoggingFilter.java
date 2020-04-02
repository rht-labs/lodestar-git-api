package com.redhat.labs.omp.resources.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * ONLY WORKS ON SERVER SIDE - Q SUCKS
 */
@Logged
@Provider
public class RequestLoggingFilter implements ContainerRequestFilter {

    public static Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        logger.info("URI {}", requestContext.getUriInfo());

    }
}
