package com.redhat.labs.omp.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.omp.models.gitlab.Hook;
import com.redhat.labs.omp.rest.client.GitLabService;

@ApplicationScoped
public class HookService {
    public static Logger LOGGER = LoggerFactory.getLogger(HookService.class);

    @Inject
    @RestClient
    GitLabService gitLabService;
    
    public Response createOrUpdateProjectHook(Integer projectId, Hook hook) {
        
        Response response;
        List<Hook> hooks = getProjectHooks(projectId);
        
        List<Hook> existingHook = hooks.stream().filter(h -> h.getUrl().equals(hook.getUrl())).collect(Collectors.toList());
        
        if(existingHook.isEmpty()) {
            response = createProjectHook(projectId, hook);
        } else {
            Hook exHook = existingHook.get(0);
            exHook.setToken(hook.getToken());
            exHook.setPushEvents(hook.getPushEvents());
            exHook.setPushEventsBranchFilter(hook.getPushEventsBranchFilter());
            response = updateProjectHook(projectId, exHook);
        }
        
        return response;
        
    }
    
    public Response createProjectHook(Integer projectId, Hook hook) {
   
        return gitLabService.createProjectHook(projectId, hook);
    }
    
    public Response updateProjectHook(Integer projectId, Hook hook) {              
      return gitLabService.updateProjectHook(projectId, hook.getId(), hook);
  }
    
    public List<Hook> getProjectHooks(int projectId) {
        return gitLabService.getProjectHooks(projectId);
    }
}
