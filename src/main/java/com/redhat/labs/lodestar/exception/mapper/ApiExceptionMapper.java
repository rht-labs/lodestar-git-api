package com.redhat.labs.lodestar.exception.mapper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {

        int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;

        JsonObject model = Json.createObjectBuilder().add("error", exception.getMessage()).add("code", status).build();

        // TODO: may need to be different response for each type of runtime exception
        return Response.status(status).entity(model.toString()).build();

    }

}
