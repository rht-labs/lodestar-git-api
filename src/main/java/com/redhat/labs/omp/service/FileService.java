package com.redhat.labs.omp.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.redhat.labs.omp.models.gitlab.response.RepositoryFile;
import com.redhat.labs.omp.rest.client.GitLabService;

@ApplicationScoped
public class FileService {

	@Inject
	@RestClient
	GitLabService gitLabService;

	// create a file
	
	// create multiple files

	// update a file

	// delete a file

	// get a file
	public RepositoryFile getFileFromRespository(String repositoryId, String fileName) {
	    return null;
	}

}
