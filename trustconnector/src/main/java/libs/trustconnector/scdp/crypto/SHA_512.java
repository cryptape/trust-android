package libs.trustconnector.scdp.crypto;

public class SHA_512 extends MessageDigestAlg
{
    public static final String ALG = "SHA-512";
    
    public SHA_512() {
        super("SHA-512");
    }
    
    public static byte[] calc(final byte[] msg) {
        return calc(msg, 0, msg.length);
    }
    
    public static byte[] calc(final byte[] msg, final int offset, final int length) {
        return calc("SHA-512", msg, offset, length);
    }
}
