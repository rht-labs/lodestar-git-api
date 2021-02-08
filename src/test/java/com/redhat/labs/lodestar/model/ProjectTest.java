package com.redhat.labs.lodestar.model;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.redhat.labs.lodestar.models.gitlab.Project;

class ProjectTest {

    @Test
    void testPreserve() {

        Project project = new Project();
        Assertions.assertNull(project.getTagList());
        
        project.setTagList(new ArrayList<>());
        Assertions.assertEquals(0, project.getTagList().size());

        project.preserve();

        Assertions.assertEquals(1, project.getTagList().size());
        Assertions.assertEquals("DO_NOT_DELETE", project.getTagList().get(0));
        
        //Handle 2nd call properly
        project.preserve();

        Assertions.assertEquals(1, project.getTagList().size());
        Assertions.assertEquals("DO_NOT_DELETE", project.getTagList().get(0));
    }
}
