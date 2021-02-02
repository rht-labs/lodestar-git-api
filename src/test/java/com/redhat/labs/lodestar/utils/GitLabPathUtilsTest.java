package com.redhat.labs.lodestar.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GitLabPathUtilsTest {

//    Groups Requirements:
//        Name can contain only letters, digits, emojis, '_', '.', dash, space, parenthesis. It must start with letter, digit, emoji or '_'.
//        Path can contain only letters, digits, '_', '-' and '.'. Cannot start with '-' or end in '.', '.git' or '.atom'

    @Test
    void testGenerateValidInputNull() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GitLabPathUtils.generateValidPath(null);
        });

    }

    @Test
    void testGenerateValidInputEmptyString() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GitLabPathUtils.generateValidPath("");
        });

    }

    @Test
    void testGenerateValidInputEmptyBlank() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GitLabPathUtils.generateValidPath("    ");
        });

    }

    @ParameterizedTest
    @CsvSource({ "something,something", "Some Name Here,some-name-here", "----Some Name Here--,some-name-here",
            "Some$1 Nameø 9-.Here!,some1-name-9-.here", "-Some$1 Nameø 9-Here!---,some1-name-9-here",
            "_My Favørite Proj.,my-favrite-proj", "_My Favørite Proj.git,my-favrite-proj",
            "_My Favørite Proj.atom,my-favrite-proj" })
    void testGenerateValidPath(String input, String expected) {
        Assertions.assertEquals(expected, GitLabPathUtils.generateValidPath(input));
    }

}
