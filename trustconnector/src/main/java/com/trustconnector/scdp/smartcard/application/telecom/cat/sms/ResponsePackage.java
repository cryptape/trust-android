package com.trustconnector.scdp.smartcard.application.telecom.cat.sms;

import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.*;

public class ResponsePackage
{
    byte[] data;
    byte[][] tpdus;
    public static final byte RESPONSE_STATUS_CODE_PoR_OK = 0;
    public static final byte RESPONSE_STATUS_CODE_CHECKSUM_FAIL = 1;
    public static final byte RESPONSE_STATUS_CODE_COUNT_LOW = 2;
    public static final byte RESPONSE_STATUS_CODE_COUNT_HIGH = 3;
    public static final byte RESPONSE_STATUS_CODE_COUNT_BLOCKED = 4;
    public static final byte RESPONSE_STATUS_CODE_CIPHER_ERROR = 5;
    public static final byte RESPONSE_STATUS_CODE_UNIDENTIFIED_SEC_ERROR = 6;
    public static final byte RESPONSE_STATUS_CODE_INSUFFICIENT_MEMORY = 7;
    public static final byte RESPONSE_STATUS_CODE_NEED_MORE_TIME = 8;
    public static final byte RESPONSE_STATUS_CODE_TAR_UNKNOWN = 9;
    public static final byte RESPONSE_STATUS_CODE_INSUFFICIENT_SEC_LEVEL = 10;
    public static final byte RESPONSE_STATUS_CODE_ACTUAL_RSP_DATA_USE_SMSSUBMIT = 11;
    public static final String[] desc;
    
    public ResponsePackage() {
    }
    
    public ResponsePackage(final byte[] userdata) {
        this.data = userdata.clone();
    }
    
    public boolean isDataComplete() {
        return this.data != null;
    }
    
    public boolean checkTAR(final byte[] TAR) {
        final byte[] tar = this.getTAR();
        return Util.arrayCompare(TAR, 0, tar, 0, 3);
    }
    
    public byte[] getTAR() {
        final byte[] tar = new byte[3];
        System.arraycopy(this.data, 3, tar, 0, 3);
        return tar;
    }
    
    public boolean checkCount(final byte[] count) {
        final byte[] c = this.getCount();
        return Util.arrayCompare(count, 0, c, 0, 5);
    }
    
    public byte[] getCount() {
        final byte[] count = new byte[5];
        System.arraycopy(this.data, 6, count, 0, 5);
        return count;
    }
    
    public byte getResponseStatusCode() {
        return this.data[12];
    }
    
    public String getResponseStatusCodeDecs() {
        final int a = this.data[12] & 0xFF;
        if (a < ResponsePackage.desc.length) {
            return ResponsePackage.desc[a];
        }
        return "Unkonwn Error";
    }
    
    public boolean checkResponseStatusCode(final byte statusCode) {
        return statusCode == this.getResponseStatusCode();
    }
    
    public byte[] getChecksum() {
        final int checksumLen = this.data[2] - 3 - 5 - 1 - 1;
        if (checksumLen < 0) {
            return null;
        }
        final byte[] res = new byte[checksumLen];
        System.arraycopy(this.data, 13, res, 0, checksumLen);
        return res;
    }
    
    public boolean checkAdditionResponseData(final byte[] response) {
        final byte[] data = this.getAdditionResponseData();
        return data.length == response.length && Util.arrayCompare(data, 0, response, 0, data.length);
    }
    
    public boolean checkAdditionResponseData(final String response) {
        final byte[] data = this.getAdditionResponseData();
        return Util.compareHexStrWithX(data, response);
    }
    
    public byte[] getAdditionResponseData() {
        final byte[] res = new byte[this.data.length - 2 - 1 - this.data[2] - this.data[11]];
        if (res.length > 0) {
            System.arraycopy(this.data, 3 + this.data[2], res, 0, res.length);
            return res;
        }
        return null;
    }
    
    public boolean procUserData(final int tpduCount, final int tpduIndex, final byte[] userDataContent) {
        if (this.tpdus == null) {
            this.tpdus = new byte[tpduCount][];
            this.data = null;
        }
        this.tpdus[tpduIndex - 1] = userDataContent.clone();
        for (int i = 0; i < tpduCount; ++i) {
            if (this.tpdus[i] == null) {
                return false;
            }
        }
        final ByteArray t = new ByteArray();
        for (int j = 0; j < tpduCount; ++j) {
            t.append(this.tpdus[j]);
        }
        this.data = t.toBytes();
        this.tpdus = null;
        return true;
    }
    
    public void procUserData(final byte[] userDataContent) {
        this.data = userDataContent.clone();
        this.tpdus = null;
    }
    
    public boolean check(final int rspStatusCode) {
        SCDP.addLog("Return Status Code=" + String.format("%02X", rspStatusCode));
        SCDP.addLog("Expect Status Code=" + String.format("%02X", this.getResponseStatusCode()));
        if (rspStatusCode != this.getResponseStatusCode()) {
            SCDP.reportError("Status Code Check Failed!");
            return false;
        }
        if (rspStatusCode == 0 && this.getAdditionResponseData() == null) {
            SCDP.reportError("Addition data not found!");
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        String a = "PoR=" + ByteArray.convert(this.data);
        a = a + "\nTAR=" + ByteArray.convert(this.getTAR());
        a = a + "\nCount=" + ByteArray.convert(this.getCount());
        final byte[] c = this.getChecksum();
        if (c != null) {
            a = a + "\nChecksum=" + ByteArray.convert(c);
        }
        a = a + "\nRespose Code=" + this.getResponseStatusCode();
        a = a + "\nRespose Code Desc=" + this.getResponseStatusCodeDecs();
        a = a + "\nResponse Addition Data=" + ByteArray.convert(this.getAdditionResponseData());
        return a;
    }
    
    byte[] getEncData() {
        final int encLen = this.data.length - 2 - 1 - 3;
        final byte[] a = new byte[encLen];
        System.arraycopy(this.data, 6, a, 0, encLen);
        return a;
    }
    
    void setDecData(final byte[] rawData) {
        System.arraycopy(rawData, 0, this.data, 6, rawData.length);
    }
    
    byte[] getChecksumData(final int checksumLen) {
        final ByteArray a = new ByteArray();
        a.append((byte)2);
        a.append((byte)113);
        a.append((byte)0);
        a.append(this.data, 0, 13);
        a.append(this.data, 13 + checksumLen, this.data.length - (13 + checksumLen));
        return a.toBytes();
    }
    
    static {
        desc = new String[] { "PoR OK", "Checksum Fail", "Count Low", "Count Higth", "Count Blocked", "Cipher Error", "Unidentified Security Error", "Insufficient Memory", "Need More Time", "TAR Unknown", "Insufficient Security Level" };
    }
}
