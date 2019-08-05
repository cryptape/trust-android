package libs.trustconnector.scdp.testsuite;

import libs.trustconnector.scdp.*;
import java.io.*;
import libs.trustconnector.scdp.util.*;
import libs.trustconnector.scdp.smartcard.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.TestSuite;
import libs.trustconnector.scdp.smartcard.APDU;
import libs.trustconnector.scdp.smartcard.NormalAPDUChecker;
import libs.trustconnector.scdp.smartcard.ReaderManager;
import libs.trustconnector.scdp.smartcard.SmartCardReader;
import libs.trustconnector.scdp.util.ByteArray;
import libs.trustconnector.scdp.util.Util;

public class APDURunner extends TestSuite
{
    static final String NOTE_1 = "//";
    static final String NOTE_2 = "\\\\";
    static final String NOTE_3 = "#";
    static final String NOTE_4 = ";";
    static final String RESET = "$0";
    static final String DELAY = "$D[";
    static final String ATR = "$A[";
    static final String MESSAGE = "$M[";
    static final String ENABLE_AUTO_GR = "$EG";
    static final String DISABLE_AUTO_GR = "$NG";
    public static final String PARAM_NAME_CHECK_DEFAULT = "CheckDefaultEnable";
    public static final String PARAM_NAME_NORMAL_SW = "NormalSW";
    public static final String PARAM_NAME_NORMAL_SWMASK = "NormalSWMask";
    
    @Override
    public boolean ExcTest(final String[] param) {
        final SmartCardReader reader = ReaderManager.getReader();
        reader.reset();
        final APDU apdu = new APDU();
        final NormalAPDUChecker checker = new NormalAPDUChecker(36864);
        boolean bAuto616C = false;
        final String expDefSW = SCDP.getParamValue("NormalSW");
        for (int i = 0; i < param.length; ++i) {
            final int cookie = SCDP.beginGroup(param[i]);
            try {
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(param[i]));
                int lineNo = 0;
                String strLine;
                while ((strLine = bufferedReader.readLine()) != null) {
                    ++lineNo;
                    strLine = Util.stringTrimAll(strLine);
                    if (strLine.length() != 0) {
                        final char firstChar = strLine.charAt(0);
                        switch (firstChar) {
                            case '$': {
                                if (strLine.compareTo("$0") == 0) {
                                    reader.reset();
                                    continue;
                                }
                                if (strLine.indexOf("$D[") == 0) {
                                    try {
                                        final int end = strLine.indexOf(93);
                                        final String duration = strLine.substring(3, end);
                                        final int t = Integer.parseInt(duration);
                                        SCDP.addLog("sleep " + t + "ms");
                                        Thread.sleep(t);
                                        continue;
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                        SCDP.reportError(e.getMessage());
                                        return false;
                                    }
                                }
                                if (strLine.indexOf("$A[") == 0) {
                                    final byte[] atr = reader.getATR().getValue();
                                    final String expATR = strLine.substring(3, strLine.length() - 1);
                                    final byte[] expA = ByteArray.convert(expATR);
                                    if (atr.length != expA.length || ByteArray.compare(atr, 0, expA, 0, atr.length) != 0) {
                                        SCDP.reportError("ATR not match:exp=" + expA + ",ret=" + ByteArray.convert(atr));
                                    }
                                    else {
                                        SCDP.addLog("ATR Excpect: " + expATR + "match ATR retrun:" + reader.getATRString());
                                    }
                                    continue;
                                }
                                if (strLine.indexOf("$M[") == 0) {
                                    final String msg = strLine.substring(3, strLine.length() - 1);
                                    SCDP.addLog(msg);
                                    continue;
                                }
                                if (strLine.compareTo("$EG") == 0) {
                                    bAuto616C = true;
                                    continue;
                                }
                                if (strLine.compareTo("$NG") == 0) {
                                    bAuto616C = false;
                                    continue;
                                }
                                continue;
                            }
                            case '\\': {
                                final String msg = strLine.substring(2);
                                SCDP.addLog(msg);
                                continue;
                            }
                            case '/': {
                                final String msgt = strLine.substring(2);
                                SCDP.addLog(msgt);
                                continue;
                            }
                            case '#': {
                                final String msga = strLine.substring(1);
                                SCDP.addLog(msga);
                                continue;
                            }
                            default: {
                                String strExpRAPDU = null;
                                String strExpSW = null;
                                int apduEndIndex = strLine.length() - 1;
                                final int expRindex = strLine.indexOf(91);
                                if (expRindex != -1) {
                                    final int expREndIndex = strLine.indexOf(93);
                                    if (expRindex > expREndIndex) {
                                        SCDP.reportError("\u6587\u6863\u683c\u5f0f\u9519\u8bef:rdata[]\u671f\u5f85\u683c\u5f0f\u9519\u8bef\uff01\u884c\u53f7\uff1a" + lineNo);
                                        return false;
                                    }
                                    if (expRindex + 1 < expREndIndex) {
                                        strExpRAPDU = strLine.substring(expRindex + 1, expREndIndex);
                                    }
                                    apduEndIndex = expRindex - 1;
                                }
                                final int expSWIndex = strLine.indexOf(40);
                                if (expSWIndex != -1) {
                                    final int expSWEndIndex = strLine.indexOf(41);
                                    if (expSWIndex > expSWEndIndex) {
                                        SCDP.reportError("\u6587\u6863\u683c\u5f0f\u9519\u8bef:sw()\u671f\u5f85\u683c\u5f0f\u9519\u8bef\uff01\u884c\u53f7\uff1a" + lineNo);
                                        return false;
                                    }
                                    if (expSWIndex + 1 < expSWEndIndex) {
                                        strExpSW = strLine.substring(expSWIndex + 1, expSWEndIndex);
                                    }
                                    if (expRindex == -1) {
                                        apduEndIndex = expSWIndex - 1;
                                    }
                                }
                                if (strExpRAPDU != null && strExpSW != null) {
                                    checker.setCheck(strExpRAPDU + strExpSW);
                                }
                                else if (strExpRAPDU != null && strExpSW == null) {
                                    checker.setCheck(ByteArray.convert(strExpRAPDU));
                                }
                                else if (strExpRAPDU == null && strExpSW != null) {
                                    checker.setCheck(strExpSW);
                                }
                                else if (expDefSW != null) {
                                    checker.setCheck(expDefSW);
                                }
                                else {
                                    checker.setDefaultCheck();
                                }
                                apdu.setCAPDU(strLine.substring(0, apduEndIndex + 1));
                                apdu.setRAPDUChecker(checker);
                                reader.transmit(apdu, bAuto616C);
                                continue;
                            }
                        }
                    }
                }
                bufferedReader.close();
            }
            catch (Exception e2) {
                e2.printStackTrace();
                SCDP.reportError(e2.getMessage());
                SCDP.endGroup(cookie);
                return false;
            }
            SCDP.endGroup(cookie);
        }
        return true;
    }
}
