package com.trustconnector.scdp.crypto;

public class SHA_384 extends MessageDigestAlg
{
    public static final String ALG = "SHA-384";
    
    public SHA_384() {
        super("SHA-384");
    }
    
    public static byte[] calc(final byte[] msg) {
        return calc(msg, 0, msg.length);
    }
    
    public static byte[] calc(final byte[] msg, final int offset, final int length) {
        return MessageDigestAlg.calc("SHA-384", msg, offset, length);
    }
}
