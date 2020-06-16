package com.redhat.labs.omp.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodingUtils {
    
    private EncodingUtils() {}

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
