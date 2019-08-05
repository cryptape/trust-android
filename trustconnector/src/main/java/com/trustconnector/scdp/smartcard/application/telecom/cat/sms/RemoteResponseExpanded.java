package com.trustconnector.scdp.smartcard.application.telecom.cat.sms;

import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.util.tlv.*;
import com.trustconnector.scdp.*;
import com.trustconnector.scdp.util.*;

public class RemoteResponseExpanded
{
    public static final int CHECK_RES_ERROR_EXE_NUM = 1;
    public static final int CHECK_RES_ERROR_SW = 2;
    public static final int CHECK_RES_ERROR_RESPONSE_DATA = 4;
    protected TLVList responseTLVs;
    protected boolean isIndefinite;
    public static final int TAG_RESPONSE_SCRIPTING_TEMPLATE = 171;
    public static final int TAG_RESPONSE_SCRIPTING_TEMPLATE_INDEFINITE_LENGTH = 175;
    public static final int TAG_VALUE_RAPDU = 35;
    public static final int TAG_VALUE_NUM_OF_EXECUTED = 128;
    public static final int TAG_VALUE_IMM_ACT_RESPONSE = 129;
    public static final int TAG_VALUE_SCRIPT_CHANINNING_RESPONSE = 131;
    public static final int TAG_VALUE_BAD_FORMAT_TAG = 131;
    public static final byte INDEFINITE_FLAG_START = Byte.MIN_VALUE;
    public static final byte INDEFINITE_FLAG_END = 0;
    public static final BERTag TAG_RAPDU;
    public static final BERTag TAG_NUM_OF_EXECUTED;
    public static final BERTag TAG_IMM_ACT_RESPONSE;
    public static final BERTag TAG_SCRIPT_CHANINNING_RESPONSE;
    public static final BERTag BAD_FORMAT_TAG;
    
    public RemoteResponseExpanded(final byte[] response) {
        this.responseTLVs = new TLVList();
        if ((response[0] & 0xFF) == 0xAB) {
            final TLV tlv = new BERTLV(response);
            final byte[] v = tlv.getValue();
            this.responseTLVs.fromBytes(v, 0, v.length, new BERTLVParser());
            this.isIndefinite = false;
        }
        else {
            if ((response[0] & 0xFF) != 0xAF) {
                throw new SCDPException("RemoteResponseExpanded format error, unknown type");
            }
            if (response[1] != -128) {
                throw new SCDPException("RemoteResponseExpanded indefinite format error,start of 80 not found");
            }
            final int length = response.length;
            if (response[length - 1] != 0 || response[length - 2] != 0) {
                throw new SCDPException("RemoteResponseExpanded indefinite format error, endof 00 not found");
            }
            this.responseTLVs.fromBytes(response, 2, length - 4, new BERTLVParser());
            this.isIndefinite = true;
        }
    }
    
    public TLVList getResponseTLV() {
        return this.responseTLVs;
    }
    
    public int getNumberOfCmdExc() {
        if (this.responseTLVs != null) {
            if (this.isIndefinite) {
                return this.responseTLVs.size();
            }
            final int i = this.responseTLVs.find(RemoteResponseExpanded.TAG_NUM_OF_EXECUTED);
            if (i != -1) {
                final TLV t = this.responseTLVs.get(i);
                final byte[] c = t.getValue();
                return (c[0] << 8 & 0xFF00) | (c[1] & 0xFF);
            }
        }
        return -1;
    }
    
    public byte[] getResponse() {
        return this.getResponse(0);
    }
    
    public byte[] getResponseData() {
        return this.getResponseData(0);
    }
    
    public int getResponseSW() {
        return this.getResponseSW(0);
    }
    
    public byte[] getResponse(int index) {
        if (this.responseTLVs != null) {
            int i = 0;
            do {
                i = this.responseTLVs.find(RemoteResponseExpanded.TAG_RAPDU, i);
                if (i != -1) {
                    if (index == 0) {
                        final TLV t = this.responseTLVs.get(i);
                        return t.getValue();
                    }
                    --index;
                    ++i;
                }
            } while (i != -1);
        }
        return null;
    }
    
    public byte[] getResponseData(final int index) {
        final byte[] rsp = this.getResponse(index);
        if (rsp != null && rsp.length > 2) {
            final byte[] data = new byte[rsp.length - 2];
            System.arraycopy(rsp, 0, data, 0, rsp.length - 2);
            return data;
        }
        return null;
    }
    
    public int getResponseSW(final int index) {
        final byte[] rsp = this.getResponse(index);
        if (rsp != null && rsp.length >= 2) {
            return (rsp[rsp.length - 2] << 8 & 0xFF00) | (rsp[rsp.length - 1] & 0xFF);
        }
        return -1;
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
        SCDP.addLog("Return SW=" + String.format("%04X", this.getResponseSW(0)));
        if (expSW != -1) {
            SCDP.addLog("Expect SW=" + String.format("%04X", expSW));
            if ((expSW & 0xFFFF) != (this.getResponseSW(0) & 0xFFFF)) {
                SCDP.reportError("SW Check Failed!");
                res |= 0x2;
            }
        }
        final byte[] ret = this.getResponseData(0);
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
        String res = "";
        if (this.responseTLVs != null) {
            for (int c = this.responseTLVs.length(), i = 0; i < c; ++i) {
                final TLV tlv = this.responseTLVs.get(i);
                final Tag t = tlv.getTag();
                final byte[] v = tlv.getValue();
                if (t == RemoteResponseExpanded.TAG_NUM_OF_EXECUTED) {
                    final int num = (v[0] << 8 & 0xFF00) | (v[1] & 0xFF);
                    res = res + "Excute Command Number=" + num + "\r\n";
                }
                else if (t == RemoteResponseExpanded.TAG_RAPDU) {
                    res = res + "RAPDU=" + ByteArray.convert(v) + "\r\n";
                }
            }
        }
        return res;
    }
    
    static {
        TAG_RAPDU = new BERTag(35);
        TAG_NUM_OF_EXECUTED = new BERTag(128);
        TAG_IMM_ACT_RESPONSE = new BERTag(129);
        TAG_SCRIPT_CHANINNING_RESPONSE = new BERTag(131);
        BAD_FORMAT_TAG = new BERTag(131);
    }
}
