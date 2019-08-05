package com.trustconnector.scdp.smartcard.application.telecom.checkrule.cat;

import com.trustconnector.scdp.smartcard.application.telecom.cat.*;
import com.trustconnector.scdp.smartcard.*;
import com.trustconnector.scdp.*;
import com.trustconnector.scdp.util.tlv.simpletlv.*;

public abstract class ProactiveCommandChecker implements APDUChecker
{
    protected ProactiveCommand command;
    protected byte expCmdType;
    protected byte expCmdNum;
    protected byte[] expCmdQualifier;
    protected byte expSrc;
    protected byte expDst;
    protected byte expCmdDetailMask;
    protected byte expCmdDevIDMask;
    
    public ProactiveCommandChecker(final int cmdType) {
        this.setMatchCmdType(cmdType);
    }
    
    public ProactiveCommandChecker(final int cmdType, final int qulifier) {
        this.setMatchCmdType(cmdType);
        this.setMatchCmdQualifier(qulifier);
    }
    
    public ProactiveCommandChecker(final int cmdType, final int srcDevID, final int dstDevID) {
        this.setMatchCmdType(cmdType);
        this.setMatchDevID(srcDevID, dstDevID);
    }
    
    public ProactiveCommandChecker(final int cmdType, final int srcDevID, final int dstDevID, final int qualifier) {
        this.setMatchCmdType(cmdType);
        this.setMatchDevID(srcDevID, dstDevID);
        this.setMatchCmdQualifier(qualifier);
    }
    
    public ProactiveCommandChecker(final int cmdType, final int srcDevID, final int dstDevID, final String cmdQualifier) {
        this.setMatchCmdType(cmdType);
        this.setMatchDevID(srcDevID, dstDevID);
        this.setMatchCmdQualifier(cmdQualifier);
    }
    
    public ProactiveCommand getCommand() {
        return this.command;
    }
    
    @Override
    public void check(final APDU apdu) throws APDUCheckException {
        final byte[] rdata = apdu.getRData();
        if (rdata[0] != -48) {
            return;
        }
        this.command = ProactiveCommand.buildCommand(rdata);
        boolean bCheckRes = this.checkBasic();
        bCheckRes &= this.check();
        if (!bCheckRes) {
            APDUCheckException.throwIt(2);
        }
    }
    
    public abstract boolean check();
    
    public boolean checkBasic() {
        boolean bCheckRes = true;
        if ((this.expCmdDetailMask & 0x1) == 0x1) {
            SCDP.addAPDUExpInfo("Command Number=" + String.format("%02X", this.command.getCmdNumber()));
        }
        else {
            SCDP.addAPDUInfo("Command Number=" + String.format("%02X", this.command.getCmdNumber()));
        }
        if ((this.expCmdDetailMask & 0x2) == 0x2) {
            if (this.command.getCmdType() != this.expCmdType) {
                bCheckRes = false;
            }
            SCDP.addAPDUExpInfo("Command Type=" + this.command.getCmdTypeName());
        }
        else {
            SCDP.addAPDUInfo("Command Type=" + this.command.getCmdTypeName());
        }
        if ((this.expCmdDetailMask & 0x4) == 0x4) {
            SCDP.addAPDUInfo("Command Qualifier=" + String.format("%02X", this.command.getCmdQualifier()));
        }
        else {
            SCDP.addAPDUInfo("Command Qualifier=" + String.format("%02X", this.command.getCmdQualifier()));
        }
        if ((this.expCmdDevIDMask & 0x1) == 0x1) {
            SCDP.addAPDUInfo("Source Device=" + DeviceIdentities.getDeviceName(this.command.getSrcDevID()));
        }
        else {
            SCDP.addAPDUInfo("Destination Device=" + String.format("%02X", this.command.getCmdQualifier()));
        }
        return bCheckRes;
    }
    
    public void setMatchCmdNumber(final int cmdNumber) {
        this.expCmdNum = (byte)cmdNumber;
        this.expCmdDetailMask |= 0x1;
    }
    
    public void setMatchCmdType(final int cmdType) {
        this.expCmdType = (byte)cmdType;
        this.expCmdDetailMask |= 0x2;
    }
    
    public void setMatchCmdQualifier(final int cmdQualifier) {
        (this.expCmdQualifier = new byte[1])[0] = (byte)cmdQualifier;
        this.expCmdDetailMask |= 0x4;
    }
    
    public void setMatchCmdQualifier(final String cmdQualifier) {
        final String[] qualifiers = cmdQualifier.split("\\|");
        final int c = qualifiers.length;
        this.expCmdQualifier = new byte[c];
        int v = 0;
        for (int i = 0; i < c; ++i) {
            v = Integer.valueOf(qualifiers[i], 16);
            this.expCmdQualifier[i] = (byte)v;
        }
    }
    
    public byte getCmdType() {
        return this.command.getCmdType();
    }
    
    public byte getCmdNumber() {
        return this.command.getCmdNumber();
    }
    
    public byte getCmdQualifier() {
        return this.command.getCmdQualifier();
    }
    
    public void setMatchSrc(final int srcDevID) {
        this.expCmdDevIDMask |= 0x1;
        this.expSrc = (byte)srcDevID;
    }
    
    public void setMatchDst(final int dstDevID) {
        this.expCmdDevIDMask |= 0x2;
        this.expDst = (byte)dstDevID;
    }
    
    public void setMatchDevID(final int srcDevID, final int dstDevID) {
        this.setMatchSrc(srcDevID);
        this.setMatchDst(dstDevID);
    }
    
    public byte getSrcDevID() {
        return this.command.getSrcDevID();
    }
    
    public byte getDstDevID() {
        return this.command.getDstDevID();
    }
    
    public void clearMatch() {
        this.expCmdDetailMask = 0;
        this.expCmdDevIDMask = 0;
    }
}
