package com.redhat.labs.omp.resources.filter;

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

    public static Logger logger = LoggerFactory.getLogger(ContainerResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        logger.info("Request {}", requestContext.getUriInfo());
        logger.info("Response {}", responseContext.getEntity());
    }
}
