package com.redhat.labs.lodestar.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.rest.client.GitLabService;

@ApplicationScoped
public class HookService {
    public static final Logger LOGGER = LoggerFactory.getLogger(HookService.class);

    @Inject
    @RestClient
    GitLabService gitLabService;

    /**
     * Uses the provided {@link Hook} to update a web hook in GitLab for the given
     * project ID. If no existing web hook is found, one will be created.
     * 
     * @param projectId
     * @param hook
     * @return
     */
    public Response createOrUpdateProjectHook(Integer projectId, Hook hook) {

        Response response;
        List<Hook> hooks = getProjectHooks(projectId);
        Optional<Hook> existingHook = hooks.stream().filter(h -> hookMatches(hook, h)).findFirst();

        if (existingHook.isEmpty()) {
            response = createProjectHook(projectId, hook);
        } else {
            Hook exHook = existingHook.get();
            exHook.setUrl(hook.getUrl());
            exHook.setToken(hook.getToken());
            exHook.setPushEvents(hook.getPushEvents());
            exHook.setPushEventsBranchFilter(hook.getPushEventsBranchFilter());
            response = updateProjectHook(projectId, exHook);
        }

        return response;

    }

    /**
     * Calls the GitLab API to create a web hook for the given project ID.
     * 
     * @param projectId
     * @param hook
     * @return
     */
    public Response createProjectHook(Integer projectId, Hook hook) {
        return gitLabService.createProjectHook(projectId, hook);
    }

    /**
     * Calls the GitLab API to update an existing web hook for the given project ID.
     * 
     * @param projectId
     * @param hook
     * @return
     */
    public Response updateProjectHook(Integer projectId, Hook hook) {
        return gitLabService.updateProjectHook(projectId, hook.getId(), hook);
    }

    /**
     * Returns a {@link List} of {@link Hook} for the given project ID.
     * 
     * @param projectId
     * @return
     */
    public List<Hook> getProjectHooks(int projectId) {
        return gitLabService.getProjectHooks(projectId);
    }

    /**
     * Removes all hooks from the given project ID.
     * 
     * @param projectId
     */
    public void deleteProjectHooks(Integer projectId) {

        List<Hook> hooks = getProjectHooks(projectId);
        hooks.stream().forEach(hook -> {
            LOGGER.debug("project {} - removing hook {}", projectId, hook);
            gitLabService.deleteProjectHook(projectId, hook.getId());
        });

    }

    /**
     * Returns true if the paths of the two {@link Hook} URLs match. Note this
     * ignores the protocol and host name.
     * 
     * @param incoming
     * @param existing
     * @return
     */
    private boolean hookMatches(Hook incoming, Hook existing) {

        Optional<URI> incomingUri = getUri(incoming.getUrl());
        Optional<URI> existingUri = getUri(existing.getUrl());

        if (incomingUri.isPresent() && existingUri.isPresent()) {
            return incomingUri.get().getPath().equals(existingUri.get().getPath());
        }

        return false;

    }

    /**
     * Returns an {@link Optional} containing the {@link URI} for the given value.
     * Otherwise, an empty {@link Optional} is returned.
     * 
     * @param uri
     * @return
     */
    private Optional<URI> getUri(String uri) {

        try {
            return Optional.of(new URI(uri));
        } catch (URISyntaxException e) {
            LOGGER.warn("provided uri has incorrect syntax, {}", uri);
            return Optional.empty();
        }

    }

}
