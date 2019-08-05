package libs.trustconnector.scdp.smartcard.application.telecom.cat.sms;

import libs.trustconnector.scdp.*;
import libs.trustconnector.scdp.util.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;

public class RemoteResponseCompact
{
    public static final int CHECK_RES_ERROR_EXE_NUM = 1;
    public static final int CHECK_RES_ERROR_SW = 2;
    public static final int CHECK_RES_ERROR_RESPONSE_DATA = 4;
    byte[] response;
    
    public RemoteResponseCompact(final byte[] response) {
        this.response = response;
    }
    
    public int getNumberOfCmdExc() {
        return this.response[0] & 0xFF;
    }
    
    public int getSW() {
        return (this.response[1] << 8 & 0xFF00) | (this.response[2] & 0xFF);
    }
    
    public byte[] getResponseData() {
        if (this.response.length > 3) {
            final int l = this.response.length - 3;
            final byte[] r = new byte[l];
            System.arraycopy(this.response, 3, r, 0, l);
            return r;
        }
        return null;
    }
    
    public int check(final int expExeNum, final int expSW, final byte[] expRsp) {
        int res = 0;
        SCDP.addLog("Return Execute Number=" + String.format("%02X", this.getNumberOfCmdExc()));
        if (expExeNum != -1) {
            SCDP.addLog("Expect Execute Number=" + String.format("%02X", expExeNum));
            if (expExeNum != this.getNumberOfCmdExc()) {
                SCDP.reportError("Execute Number Check Failed!");
                res |= 0x1;
            }
        }
        SCDP.addLog("Return SW=" + String.format("%04X", this.getSW()));
        if (expSW != -1) {
            SCDP.addLog("Expect SW=" + String.format("%04X", expSW));
            if ((expSW & 0xFFFF) != (this.getSW() & 0xFFFF)) {
                SCDP.reportError("SW Check Failed!");
                res |= 0x2;
            }
        }
        final byte[] ret = this.getResponseData();
        SCDP.addLog("Return Response Data=" + ByteArray.convert(ret));
        if (expRsp != null) {
            SCDP.addLog("Expect Response Data=" + ByteArray.convert(expRsp));
            if (!Util.arrayCompare(ret, expRsp)) {
                SCDP.reportError("Response Data Check Failed!");
                res |= 0x4;
            }
        }
        return res;
    }
    
    @Override
    public String toString() {
        String a = "Excute Command Number=";
        a += this.getNumberOfCmdExc();
        a += "\nSW=";
        a += String.format("%04X", this.getSW());
        if (this.response.length > 3) {
            a += "\nResponse Data=";
            a += ByteArray.convert(this.getResponseData());
        }
        return a;
    }
}
