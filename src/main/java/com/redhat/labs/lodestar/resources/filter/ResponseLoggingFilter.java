package com.redhat.labs.lodestar.resources.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Logged
@Provider
public class ResponseLoggingFilter implements ContainerResponseFilter {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResponseLoggingFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOGGER.info("Request {}", requestContext.getUriInfo());
        LOGGER.info("Response {}", responseContext.getEntity());
    }
}
