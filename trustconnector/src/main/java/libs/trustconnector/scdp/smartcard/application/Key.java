package libs.trustconnector.scdp.smartcard.application;

public interface Key
{
    int getLength();
    
    byte[] getValue();
    
    int getType();
    
    Object getCookie();
}
