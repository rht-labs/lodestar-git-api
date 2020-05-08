package com.redhat.labs.omp.utils;

public class GitLabPathUtils {

    public static String generateValidPath(String input) {

        if (null == input || input.trim().length() == 0) {
            throw new IllegalArgumentException("input string cannot be blank.");
        }

        // trim
        String path = input.trim();

        // remove leading or trailing hyphens
        path = path.replaceFirst("^-*", "").replaceFirst("-*$", "");

        // turn to lowercase
        path = path.toLowerCase();

        // replace whitespace with a '-'
        path = path.replaceAll("\\s", "-");

        // remove any characters other than A-Z, a-z, 0-9, ., -
        path = path.replaceAll("[^A-Za-z0-9-\\.]", "");

        return path;

    }

}
