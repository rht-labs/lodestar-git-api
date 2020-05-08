package com.redhat.labs.utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.redhat.labs.omp.utils.GitLabPathUtils;

public class GitLabPathUtilsTest {

    @Test
    public void testGenerateValidInputNull() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GitLabPathUtils.generateValidPath(null);
          });

    }

    @Test
    public void testGenerateValidInputEmptyString() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GitLabPathUtils.generateValidPath("");
          });

    }

    @Test
    public void testGenerateValidInputEmptyBlank() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GitLabPathUtils.generateValidPath("    ");
          });

    }

    @Test
    public void testGenerateValidPathDoNothing() {

        // given
        String input = "something";

        // when
        String output = GitLabPathUtils.generateValidPath(input);

        Assertions.assertEquals("something", output);

    }

    @Test
    public void testGenerateValidPathReplaceSpaces() {

        // given
        String input = "Some Name Here";

        // when
        String output = GitLabPathUtils.generateValidPath(input);

        Assertions.assertEquals("some-name-here", output);

    }

    @Test
    public void testGenerateValidPathRemoveLeadingOrTrailingHyphens() {

        // given
        String input = "----Some Name Here--";

        // when
        String output = GitLabPathUtils.generateValidPath(input);

        Assertions.assertEquals("some-name-here", output);

    }

    @Test
    public void testGenerateValidPathReplaceSpacesAndRemoveSpecialCharacters() {

        // given
        String input = "Some$1 Nameø 9-.Here!";

        // when
        String output = GitLabPathUtils.generateValidPath(input);

        Assertions.assertEquals("some1-name-9-.here", output);

    }

    @Test
    public void testGenerateValidPathFullReplace() {

        // given
        String input = "-Some$1 Nameø 9-Here!---";

        // when
        String output = GitLabPathUtils.generateValidPath(input);

        Assertions.assertEquals("some1-name-9-here", output);

    }

}
