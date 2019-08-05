package com.trustconnector.scdp.smartcard;

import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.*;

public class NormalAPDUChecker implements APDUChecker
{
    protected int[] expSWs;
    protected int[] expSWMasks;
    protected byte[][] expRData;
    protected byte[][] expRDataMask;
    protected int retSW;
    protected byte[] retData;
    protected String checkRule;
    private static final int[] defSW;
    private static final int[] defSWMask;
    private static final String defRule = "9000|91XX|9FXX|61XX|6CXX";
    
    public NormalAPDUChecker() {
        this.setSWCheck(NormalAPDUChecker.defSW, NormalAPDUChecker.defSWMask);
        this.checkRule = "9000|91XX|9FXX|61XX|6CXX";
    }
    
    public NormalAPDUChecker(final String checkRule) {
        this.setCheck(checkRule);
    }
    
    public NormalAPDUChecker(final int sw) {
        this.setCheck(sw);
    }
    
    public NormalAPDUChecker(final int[] sws) {
        this.setCheck(sws);
    }
    
    public NormalAPDUChecker(final int[] sws, final int[] swsmask) {
        this.setCheck(sws, swsmask);
    }
    
    public NormalAPDUChecker(final byte[] rdata, final int sw) {
        this.setCheck(rdata, sw);
    }
    
    protected void setSWCheck(final int sw) {
        (this.expSWs = new int[1])[0] = sw;
        (this.expSWMasks = new int[1])[0] = -1;
    }
    
    protected void setSWCheck(final int[] sws) {
        this.expSWs = sws.clone();
        this.expSWMasks = new int[sws.length];
        for (int i = 0; i < sws.length; ++i) {
            this.expSWMasks[i] = -1;
        }
    }
    
    protected void setSWCheck(final int[] sws, final int[] swsMask) {
        this.expSWs = sws;
        this.expSWMasks = swsMask;
    }
    
    protected void setRDataCheck(final byte[] data) {
        this.setRDataCheck(data, null);
    }
    
    protected void setRDataCheck(final byte[] data, final int off, final int length) {
        this.setRDataCheck(data, off, length, null);
    }
    
    protected void setRDataCheck(final byte[] data, byte[] dataMask) {
        this.expRData = new byte[1][];
        this.expRDataMask = new byte[1][];
        this.expRData[0] = data;
        if (data != null && dataMask == null) {
            dataMask = new byte[data.length];
            Util.arrayFill(dataMask, 0, dataMask.length, (byte)(-1));
        }
        this.expRDataMask[0] = dataMask;
    }
    
    protected void setRDataCheck(final byte[] data, final int off, final int length, byte[] dataMask) {
        this.expRData = new byte[1][];
        this.expRDataMask = new byte[1][];
        System.arraycopy(data, off, this.expRData[0] = new byte[length], 0, length);
        if (data != null && dataMask == null) {
            dataMask = new byte[data.length];
            Util.arrayFill(dataMask, 0, dataMask.length, (byte)(-1));
        }
        this.expRDataMask[0] = dataMask;
    }
    
    private String buildMask(final String maskWithX) {
        final StringBuilder tBuilder = new StringBuilder();
        for (int l = maskWithX.length(), i = 0; i < l; ++i) {
            if (maskWithX.charAt(i) == 'X') {
                tBuilder.append('0');
            }
            else {
                tBuilder.append('F');
            }
        }
        return tBuilder.toString();
    }
    
    public void setCheck(final String checkRule) {
        if (checkRule == null || checkRule.length() == 0) {
            this.checkRule = "";
            this.expSWs = null;
            this.expSWMasks = null;
            this.expRData = null;
            this.expRDataMask = null;
            return;
        }
        this.checkRule = checkRule;
        final String[] exps = checkRule.split("\\|");
        final int checkCount = exps.length;
        this.expSWs = new int[checkCount];
        this.expSWMasks = new int[checkCount];
        this.expRData = new byte[checkCount][];
        this.expRDataMask = new byte[checkCount][];
        for (int i = 0; i < checkCount; ++i) {
            final String checkRuleT = exps[i];
            final int tatalLen = checkRuleT.length();
            final String expSW = checkRuleT.substring(tatalLen - 4);
            if (expSW.indexOf(88) == -1) {
                this.expSWs[i] = Util.HexStringToInt(expSW);
                this.expSWMasks[i] = -1;
            }
            else {
                final String t = expSW.replace('X', '0');
                this.expSWs[i] = Util.HexStringToInt(t);
                final String mask = this.buildMask(expSW);
                this.expSWMasks[i] = Util.HexStringToInt(mask);
            }
            if (tatalLen > 4) {
                final String expData = checkRuleT.substring(0, tatalLen - 4);
                if (expData.indexOf(88) == -1) {
                    this.expRData[i] = ByteArray.convert(expData);
                    Util.arrayFill(this.expRDataMask[i] = new byte[this.expRData[i].length], 0, this.expRDataMask[i].length, (byte)(-1));
                }
                else {
                    final String t2 = expData.replace('X', '0');
                    this.expRData[i] = ByteArray.convert(t2);
                    final String mask2 = this.buildMask(expData);
                    this.expRDataMask[i] = ByteArray.convert(mask2);
                }
            }
        }
    }
    
    public void setCheck(final int sw) {
        this.expRData = new byte[1][];
        this.expRDataMask = new byte[1][];
        this.setSWCheck(sw);
        this.checkRule = String.format("%04X", sw);
    }
    
    public void setCheck(final int[] sws) {
        this.expRData = new byte[sws.length][];
        this.expRDataMask = new byte[sws.length][];
        this.setSWCheck(sws);
        this.checkRule = String.format("%04X", sws[0]);
        for (int i = 1; i < sws.length; ++i) {
            this.checkRule += String.format("|%04X", sws[i]);
        }
    }
    
    public void setCheck(final int[] sws, final int[] swsmask) {
        this.expRData = new byte[sws.length][];
        this.expRDataMask = new byte[swsmask.length][];
        this.setSWCheck(sws, swsmask);
        this.checkRule = String.format("%04X", sws[0]);
        this.checkRule += String.format("(%04X)", swsmask[0]);
        for (int i = 1; i < sws.length; ++i) {
            this.checkRule += String.format("|%04X", sws[i]);
            this.checkRule += String.format("(%04X)", swsmask[i]);
        }
    }
    
    public void setCheck(final byte[] data) {
        this.expRData = new byte[1][];
        this.expRDataMask = new byte[1][];
        this.setSWCheck(null);
        this.checkRule = ByteArray.convert(data);
    }
    
    public void setCheck(final byte[] data, final int sw) {
        this.setRDataCheck(data);
        this.setSWCheck(sw);
        this.checkRule = ByteArray.convert(data);
        this.checkRule += String.format("%04X", sw);
    }
    
    public void setCheck(final byte[] data, final int off, final int length, final int sw) {
        this.setRDataCheck(data, off, length);
        this.setSWCheck(sw);
        this.checkRule = ByteArray.convert(data, off, length);
        this.checkRule += String.format("%04X", sw);
    }
    
    public void setCheck(final byte[] data, final byte[] dataMask, final int sw) {
        this.setRDataCheck(data, dataMask);
        this.setSWCheck(sw);
        this.checkRule = ByteArray.convert(data);
        this.checkRule += String.format("%04X", sw);
    }
    
    public void setCheck(final byte[] data, final int off, final int length, final byte[] dataMask, final int[] sws, final int[] swsMask) {
        this.setRDataCheck(data, off, length, dataMask);
        this.setSWCheck(sws, swsMask);
    }
    
    public void clear() {
        this.expSWs = null;
        this.expSWMasks = null;
        this.expRData = null;
        this.expRDataMask = null;
    }
    
    public void setDefaultCheck() {
        this.clear();
        this.setSWCheck(NormalAPDUChecker.defSW, NormalAPDUChecker.defSWMask);
    }
    
    public byte[] getReturnData() {
        return this.retData;
    }
    
    public int getReturnSW() {
        return this.retSW;
    }
    
    @Override
    public void check(final APDU apdu) {
        this.retData = apdu.getRData();
        this.retSW = apdu.getSW();
        if (this.expSWs != null) {
            boolean bCheckRDPass = false;
            boolean bSWCheckPass = false;
            for (int expCount = this.expSWs.length, i = 0; i < expCount; ++i) {
                bCheckRDPass = false;
                if (this.expRData != null) {
                    if (this.expRData[i] != null && this.retData != null) {
                        if (this.expRData[i].length == this.retData.length) {
                            bCheckRDPass = true;
                            for (int j = 0; j < this.expRData[i].length; ++j) {
                                if ((byte)(this.retData[j] & this.expRDataMask[i][j]) != this.expRData[i][j]) {
                                    bCheckRDPass = false;
                                    break;
                                }
                            }
                        }
                    }
                    else if (this.expRData[i] == null) {
                        bCheckRDPass = true;
                    }
                }
                else {
                    bCheckRDPass = true;
                }
                bSWCheckPass = ((this.retSW & this.expSWMasks[i]) == this.expSWs[i]);
                if (bCheckRDPass && bSWCheckPass) {
                    break;
                }
            }
            if (!bCheckRDPass || !bSWCheckPass) {
                SCDP.reportAPDUExpErr("Faile to Check Expect:" + this.checkRule);
                APDUCheckException.throwIt(4);
            }
            else {
                SCDP.addAPDUExpInfo("Expect:" + this.checkRule);
            }
        }
    }
    
    static {
        defSW = new int[] { 36864, 37120, 40704, 24832, 27648, 25360 };
        defSWMask = new int[] { 65535, 65280, 65280, 65280, 65280, 65535 };
    }
}
