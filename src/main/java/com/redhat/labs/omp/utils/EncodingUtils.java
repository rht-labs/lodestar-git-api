package com.redhat.labs.omp.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.redhat.labs.omp.models.gitlab.File;

public class EncodingUtils {

    public static boolean isDecodable(String val) {

        try {
            Base64.getDecoder().decode(val.getBytes());
        } catch (IllegalArgumentException iae) {
            return false;
        }

        return true;

    }

    public static void encodeFile(File file) throws UnsupportedEncodingException {

        // encode file path
        String encodedFilePath = urlEncode(file.getFilePath());
        file.setFilePath(encodedFilePath);

        // encode contents
        byte[] encodedContents = base64Encode(file.getContent().getBytes());
        file.setContent(new String(encodedContents, StandardCharsets.UTF_8));

    }

    public static void decodeFile(File file) throws UnsupportedEncodingException {

        // decode file path
        String decodedFilePath = urlDecode(file.getFilePath());
        file.setFilePath(decodedFilePath);

        // decode contents if it exists
        if (null != file.getContent()) {
            byte[] decodedContents = base64Decode(file.getContent());
            file.setContent(new String(decodedContents, StandardCharsets.UTF_8));
        }

    }

    public static byte[] base64Encode(byte[] src) {
        return Base64.getEncoder().encode(src);
    }

    public static byte[] base64Decode(String src) {
        return Base64.getDecoder().decode(src);
    }

    public static String urlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, StandardCharsets.UTF_8.toString());
    }

    public static String urlDecode(String src) throws UnsupportedEncodingException {
        return URLDecoder.decode(src, StandardCharsets.UTF_8.toString());
    }

}
