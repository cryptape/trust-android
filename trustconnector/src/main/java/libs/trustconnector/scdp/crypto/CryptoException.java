package libs.trustconnector.scdp.crypto;

public class CryptoException extends RuntimeException
{
    private static final long serialVersionUID = -3695828147576215477L;
    private int reasonCode;
    public static int INVALID_KEY_LENGTH;
    public static int ILLEGAL_USE;
    public static int BUFFER_OVERFLOW;
    public static int INVALID_MODE;
    public static int INTERNAL_ERROR;
    
    public static void throwIt(final int reasonCode) {
        throw new CryptoException(reasonCode);
    }
    
    CryptoException(final int reasonCode) {
        this.reasonCode = reasonCode;
    }
    
    public int getReasonCode() {
        return this.reasonCode;
    }
    
    static {
        CryptoException.INVALID_KEY_LENGTH = 1;
        CryptoException.ILLEGAL_USE = 2;
        CryptoException.BUFFER_OVERFLOW = 3;
        CryptoException.INVALID_MODE = 4;
        CryptoException.INTERNAL_ERROR = 5;
    }
}
