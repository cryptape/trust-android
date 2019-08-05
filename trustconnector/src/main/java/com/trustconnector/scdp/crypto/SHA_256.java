package com.trustconnector.scdp.crypto;

public class SHA_256 extends MessageDigestAlg
{
    public static final String ALG = "SHA-256";
    
    public SHA_256() {
        super("SHA-256");
    }
    
    public static byte[] calc(final byte[] msg) {
        return calc(msg, 0, msg.length);
    }
    
    public static byte[] calc(final byte[] msg, final int offset, final int length) {
        return MessageDigestAlg.calc("SHA-256", msg, offset, length);
    }
}
