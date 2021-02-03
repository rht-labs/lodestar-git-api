package com.redhat.labs.lodestar.resources.filter;

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

    public static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info("URI {}", requestContext.getUriInfo());

    }
}
