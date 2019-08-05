package libs.trustconnector.scdp.crypto;

public class SHA_1 extends MessageDigestAlg
{
    public static final String ALG = "SHA-1";
    
    public SHA_1() {
        super("SHA-1");
    }
    
    public static byte[] calc(final byte[] msg) {
        return calc(msg, 0, msg.length);
    }
    
    public static byte[] calc(final byte[] msg, final int offset, final int length) {
        return MessageDigestAlg.calc("SHA-1", msg, offset, length);
    }
}
