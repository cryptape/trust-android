package libs.trustconnector.scdp.smartcard.application.globalplatform;

import org.jdom2.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.util.XMLUtil;

public class SCP02Key extends GPKey
{
    private int id;
    private int seqCount;
    
    public SCP02Key(final int id, final byte[] value) {
        this.seqCount = -1;
        this.id = id;
        this.value = value.clone();
    }
    
    public SCP02Key(final Element keyNode) {
        super(keyNode);
        this.seqCount = -1;
        this.id = XMLUtil.getNodeAttrHex(keyNode, "id");
    }
    
    @Override
    public Object getCookie() {
        return new Integer(this.id);
    }
    
    @Override
    public int getID() {
        return this.id;
    }
    
    @Override
    public void updateValue(final byte[] value) {
        this.value = value.clone();
    }
    
    public int getCount() {
        return this.seqCount;
    }
    
    public void setCount(final int count) {
        this.seqCount = count;
    }
    
    public boolean isCountInit() {
        return this.seqCount != -1;
    }
    
    public void initCount(final int count) {
        if (this.seqCount == -1) {
            this.seqCount = count;
        }
    }
    
    public void incCount() {
        ++this.seqCount;
    }
}
