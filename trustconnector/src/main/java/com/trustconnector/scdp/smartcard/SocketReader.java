package com.trustconnector.scdp.smartcard;

public class SocketReader extends SmartCardReader
{
    private static final int MAGIC_CODE = 1397466458;
    private static final int ATTR_TYPE_MAGIC = 1397424128;
    private static final int ATTR_TYPE_IP = 1397424129;
    private static final int ATTR_TYPE_PORT = 1397424130;
    private static final int ATTR_TYPE_PROTOCOL = 1397424131;
    
    SocketReader(final int index) {
        super(index);
    }
    
    static SocketReader checkReader(final String name, final int index) {
        if (name.startsWith("Socket Reader")) {
            final SocketReader r = new SocketReader(index);
            final int a = r.getAttributeInt(1397424128);
            if (a == 1397466458) {
                return r;
            }
        }
        return null;
    }
    
    public boolean setPort(final int port) {
        return this.setAttributeInt(1397424130, port);
    }
    
    public int getPort() {
        return this.getAttributeInt(1397424130);
    }
    
    public boolean setAddr(final String addr) {
        return this.setAttributeString(1397424129, addr);
    }
    
    public String getIP() {
        return this.getAttributeString(1397424129);
    }
    
    public boolean setProtocol(final int protocol) {
        return this.setAttributeInt(1397424131, protocol);
    }
}
