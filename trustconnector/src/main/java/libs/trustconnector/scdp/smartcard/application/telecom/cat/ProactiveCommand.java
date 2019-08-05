package libs.trustconnector.scdp.smartcard.application.telecom.cat;

import libs.trustconnector.scdp.util.tlv.bertlv.*;
import libs.trustconnector.scdp.util.tlv.simpletlv.*;
import libs.trustconnector.scdp.util.tlv.*;
import java.util.*;

import libs.trustconnector.scdp.util.tlv.TLV;
import libs.trustconnector.scdp.util.tlv.TLVList;
import libs.trustconnector.scdp.util.tlv.Tag;
import libs.trustconnector.scdp.util.tlv.bertlv.BERTLV;
import libs.trustconnector.scdp.util.tlv.simpletlv.CommandDetails;
import libs.trustconnector.scdp.util.tlv.simpletlv.DeviceIdentities;
import libs.trustconnector.scdp.util.tlv.simpletlv.SimpleTLV;
import libs.trustconnector.scdp.util.tlv.simpletlv.SimpleTLVParser;
import libs.trustconnector.scdp.util.tlv.simpletlv.SimpleTag;

public class ProactiveCommand
{
    protected byte[] orgCmd;
    protected CommandDetails commandDetails;
    protected DeviceIdentities devID;
    protected TLVList cmdTLVList;
    
    public ProactiveCommand(final byte[] cmd) {
        this.formByetes(cmd);
    }
    
    public static ProactiveCommand buildCommand(final byte[] cmd) {
        final BERTLV tlv = new BERTLV(cmd);
        final TLVList cmdTLVList = SimpleTLVParser.parseTLVList(tlv.getValue(), 0, tlv.getValueLen());
        if (cmdTLVList == null) {
            return null;
        }
        final CommandDetails details = (CommandDetails)cmdTLVList.findTLV(SimpleTag.TAG_COMMAND_DETAILS);
        if (details == null) {
            return null;
        }
        final byte cmdType = details.getCmdType();
        switch (cmdType & 0x7F) {
            case 1: {
                return new Refresh(cmd);
            }
            case 2: {
                return new MoreTime(cmd);
            }
            case 3: {
                return new PollInterval(cmd);
            }
            case 4: {
                return new PollingOff(cmd);
            }
            case 5: {
                return new SetupEventList(cmd);
            }
            case 16: {}
            case 17: {}
            case 19: {
                return new SendSMS(cmd);
            }
            case 20: {}
            case 21: {}
            case 22: {}
            case 33: {
                return new DisplayText(cmd);
            }
            case 35: {
                return new GetInput(cmd);
            }
            case 36: {
                return new SelectItem(cmd);
            }
            case 37: {
                return new SetupMenu(cmd);
            }
            case 38: {}
            case 39: {}
            case 40: {}
            case 48: {}
            case 49: {}
            case 50: {}
            case 51: {}
            case 52: {}
            case 53: {}
            case 64: {}
            case 65: {}
            case 66: {}
            case 67: {}
            case 68: {}
            case 69: {}
            case 70: {}
            case 71: {}
            case 80: {}
            case 81: {}
            case 96: {}
            case 97: {}
            case 98: {}
            case 112: {}
            case 113: {}
        }
        return new ProactiveCommand(cmd);
    }
    
    public boolean formByetes(final byte[] cmd) {
        this.orgCmd = null;
        final BERTLV tlv = new BERTLV(cmd);
        this.cmdTLVList = SimpleTLVParser.parseTLVList(tlv.getValue(), 0, tlv.getValueLen());
        if (this.cmdTLVList == null) {
            return false;
        }
        this.commandDetails = (CommandDetails)this.findTLV(SimpleTag.TAG_COMMAND_DETAILS);
        this.devID = (DeviceIdentities)this.findTLV((byte)2);
        this.orgCmd = cmd.clone();
        return true;
    }
    
    public byte[] toBytes() {
        return this.orgCmd.clone();
    }
    
    public SimpleTLV findTLV(final Tag tag) {
        return (SimpleTLV)this.cmdTLVList.findTLV(tag);
    }
    
    public SimpleTLV findTLV(final byte tag) {
        return (SimpleTLV)this.cmdTLVList.findTLV(new SimpleTag(tag));
    }
    
    public TLVList getTLVList() {
        return this.cmdTLVList;
    }
    
    public byte getCmdType() {
        return this.commandDetails.getCmdType();
    }
    
    public byte getCmdQualifier() {
        return this.commandDetails.getCmdQualifier();
    }
    
    public byte getCmdNumber() {
        return this.commandDetails.getCmdNumber();
    }
    
    public String getCmdTypeName() {
        return this.commandDetails.getCmdName();
    }
    
    public byte getSrcDevID() {
        return this.devID.getSrcDevID();
    }
    
    public byte getDstDevID() {
        return this.devID.getDstDevID();
    }
    
    @Override
    public String toString() {
        String res = "";
        final Iterator<TLV> ite = this.cmdTLVList.iterator();
        while (ite.hasNext()) {
            final TLV t = ite.next();
            res += t.toString();
            if (ite.hasNext()) {
                res += "\n";
            }
        }
        return res;
    }
}
