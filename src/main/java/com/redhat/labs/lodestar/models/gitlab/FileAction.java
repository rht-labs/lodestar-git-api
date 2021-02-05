package com.redhat.labs.lodestar.models.gitlab;

public enum FileAction {
    //refer to https://docs.gitlab.com/ee/api/commits.html#create-a-commit-with-multiple-files-and-actions

    CREATE,
    UPDATE,
    DELETE;
    
    @Override public String toString() {
        return name().toLowerCase();
    }

}
