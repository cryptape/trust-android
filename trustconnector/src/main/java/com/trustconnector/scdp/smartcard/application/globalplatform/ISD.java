package com.trustconnector.scdp.smartcard.application.globalplatform;

import com.trustconnector.scdp.smartcard.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.tlv.*;

public class ISD extends SD
{
    public static final String ISD_CFG_AID = "ISD";
    
    protected ISD(final SmartCardReader reader) {
        super(reader, null);
    }
    
    protected ISD(final SmartCardReader reader, final AID aid) {
        super(reader, aid);
    }
    
    public byte getCardLifeCycle() {
        final byte[] rsp = this.getStatusISD(false);
        if (rsp == null) {
            return 0;
        }
        return rsp[rsp[0] + 1];
    }
    
    public Privilege getISDPrivilege() {
        byte[] rsp = this.getStatusISD(true);
        if (rsp != null) {
            return new Privilege(rsp, rsp[0] + 2, 1);
        }
        rsp = this.getStatusISD(false);
        final TLV rspTLV = BERTLVParser.parseTLV(rsp);
        if (rspTLV == null) {
            return null;
        }
        final TLVList rspList = TLVList.parseFromBytes(rspTLV.getValue(), new BERTLVParser());
        if (rspList == null) {
            return null;
        }
        final TLV privilegeTLV = rspList.findTLV(new BERTag(197));
        if (privilegeTLV == null) {
            return null;
        }
        return new Privilege(privilegeTLV.getValue());
    }
}
