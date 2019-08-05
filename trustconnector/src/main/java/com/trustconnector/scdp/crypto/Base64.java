//package com.broadthinking.scdp.crypto;
//
//import sun.misc.*;
//import java.io.*;
//
//public class Base64
//{
//    private Base64() {
//    }
//
//    public static String encode(final byte[] data) {
//        if (System.getProperty("java.version").startsWith("1.8")) {
//            final java.util.Base64.Encoder c = java.util.Base64.getEncoder();
//            return c.encodeToString(data);
//        }
//        final BASE64Encoder encoder = new BASE64Encoder();
//        return encoder.encode(data);
//    }
//
//    public static byte[] decode(final String base64) {
//        try {
//            if (System.getProperty("java.version").startsWith("1.8")) {
//                final java.util.Base64.Decoder c = java.util.Base64.getDecoder();
//                return c.decode(base64);
//            }
//            final BASE64Decoder decoder = new BASE64Decoder();
//            return decoder.decodeBuffer(base64);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
