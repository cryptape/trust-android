package libs.trustconnector.scdp.crypto;

public class MD5 extends MessageDigestAlg
{
    public static final String ALG = "MD5";
    
    public MD5() {
        super("MD5");
    }
    
    public static byte[] calc(final byte[] msg) {
        return calc(msg, 0, msg.length);
    }
    
    public static byte[] calc(final byte[] msg, final int offset, final int length) {
        return MessageDigestAlg.calc("MD5", msg, offset, length);
    }
}
