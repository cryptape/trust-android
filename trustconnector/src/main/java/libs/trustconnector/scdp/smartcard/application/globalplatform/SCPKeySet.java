package libs.trustconnector.scdp.smartcard.application.globalplatform;

import libs.trustconnector.scdp.smartcard.application.*;

import libs.trustconnector.scdp.smartcard.application.KeySet;

public class SCPKeySet extends KeySet
{
    protected long counter;
    private static final long serialVersionUID = 1L;
    
    public SCPKeySet(final int cookie) {
        super((Object)cookie);
    }
    
    public void setCounter(final long c) {
        this.counter = c;
    }
    
    public void setCounter() {
    }
    
    public long getCounter() {
        return this.counter;
    }
    
    public void incCounter() {
        ++this.counter;
    }
    
    public void resetCounter() {
        this.counter = 0L;
    }
    
    public byte[] getCounterBytes(final int len) {
        final byte[] c = new byte[len];
        long t = this.counter;
        for (int i = len - 1; i > 0; --i) {
            c[i] = (byte)t;
            t >>= 8;
        }
        return c;
    }
}
