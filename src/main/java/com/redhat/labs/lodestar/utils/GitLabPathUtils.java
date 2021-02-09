package com.redhat.labs.lodestar.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitLabPathUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitLabPathUtils.class);

    private GitLabPathUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateValidPath(String input) {

        if (null == input || input.trim().length() == 0) {
            throw new IllegalArgumentException("input string cannot be blank.");
        }

        // trim
        String path = input.trim();

        // turn to lowercase
        path = path.toLowerCase();

        // replace whitespace with a '-'
        path = path.replaceAll("\\s", "-");

        // remove any characters other than A-Z, a-z, 0-9, ., -
        path = path.replaceAll("[^A-Za-z0-9-\\.]", "");

        // remove leading or trailing hyphens
        path = path.replaceFirst("^-*", "").replaceFirst("-*$", "");

        // remove ending '.', '.git', or '.atom'
        path = path.replaceAll("(\\.|\\.git|\\.atom)$", "");

        LOGGER.debug("input name {}, converted to path {}", input, path);

        return path;

    }

    public static String getPath(String engagementPathPrefix, String customerName, String engagementName) {
        return new StringBuilder(engagementPathPrefix == null ? "" : engagementPathPrefix)
                .append("/")
                .append(customerName)
                .append("/")
                .append(engagementName)
                .append("/iac").toString();
    }

    public static String getValidPath(String engagementPathPrefix, String customerName, String engagementName) {

        String customerPath = GitLabPathUtils.generateValidPath(customerName);
        String projectPath = GitLabPathUtils.generateValidPath(engagementName);
        return GitLabPathUtils.getPath(engagementPathPrefix, customerPath, projectPath);

    }

}
