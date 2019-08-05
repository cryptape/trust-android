package com.trustconnector.scdp.testsuite;

import com.trustconnector.scdp.*;
import java.io.*;
import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.smartcard.*;
import com.trustconnector.scdp.smartcard.application.globalplatform.*;
import com.trustconnector.scdp.smartcard.application.*;
import java.util.*;

public class JCSHRunner extends TestSuite
{
    public static final int[] expSW;
    public static final String CMD_CONNECT = "connect";
    public static final String CMD_AUTH = "auth";
    public static final String CMD_SEND = "send";
    public static final String CMD_SELECT_ISD = "select";
    public static final String CMD_SELECT_APP = "/select";
    public static final String CMD_SEND_RAW = "/send";
    public static final String CMD_UPLOAD = "upload";
    public static final String CMD_PUT_KEY = "put-key";
    public static final String CMD_SET_KEY = "set-key";
    public static final String CMD_INIT_UPDATE = "init-update";
    public static final String CMD_EXT_AUTH = "ext-auth";
    public static final String CMD_INSTALL = "install";
    public static final String CMD_DELETE = "delete";
    
    @Override
    public boolean ExcTest(final String[] param) {
        final SmartCardReader reader = ReaderManager.getReader();
        final APDU apdu = new APDU();
        final NormalAPDUChecker checker = new NormalAPDUChecker(JCSHRunner.expSW);
        apdu.setRAPDUChecker(checker);
        SD.setDefaultReader(reader);
        reader.reset();
        SD curSD;
        final ISD isd = (ISD)(curSD = SD.getISD());
        for (int i = 0; i < param.length; ++i) {
            final int cookie = SCDP.beginGroup(param[i]);
            int lineNo = 0;
            try {
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(param[i]));
                String strLine;
                while ((strLine = bufferedReader.readLine()) != null) {
                    ++lineNo;
                    strLine = strLine.trim();
                    if (strLine.length() != 0) {
                        final String[] cmdParam = strLine.split(" ");
                        SCDP.addLog(strLine);
                        if (cmdParam[0].compareToIgnoreCase("connect") == 0) {
                            reader.reset();
                            curSD.closeSCP02();
                        }
                        else if (cmdParam[0].compareToIgnoreCase("auth") == 0) {
                            curSD.openSCP02();
                        }
                        else if (cmdParam[0].compareToIgnoreCase("send") == 0) {
                            apdu.setCAPDU(cmdParam[1]);
                            curSD.transmit(apdu);
                        }
                        else if (cmdParam[0].compareToIgnoreCase("/send") == 0) {
                            apdu.setCAPDU(cmdParam[1]);
                            reader.transmit(apdu);
                        }
                        else if (cmdParam[0].compareToIgnoreCase("select") == 0) {
                            curSD.closeSCP02();
                            isd.select();
                            curSD = isd;
                        }
                        else if (cmdParam[0].compareToIgnoreCase("/select") == 0) {
                            curSD.closeSCP02();
                            apdu.setCAPDU("Select APP", "00A4040000");
                            apdu.setCData(ByteArray.convert(cmdParam[1]));
                            curSD.transmit(apdu);
                            curSD = SD.getSD(new AID(cmdParam[1]));
                        }
                        else if (cmdParam[0].compareToIgnoreCase("upload") == 0) {
                            String path;
                            if (cmdParam[1].compareTo("-d") == 0) {
                                path = cmdParam[2];
                            }
                            else {
                                path = cmdParam[1];
                            }
                            path = path.replaceAll("\"", "");
                            curSD.loadPackage(path);
                        }
                        else if (cmdParam[0].compareToIgnoreCase("set-key") == 0) {
                            for (int j = 1; j < cmdParam.length; ++j) {
                                final String[] keyParam = cmdParam[j].split("/");
                                final int kVer = Integer.valueOf(keyParam[0]);
                                final int kID = Integer.valueOf(keyParam[1]);
                                final byte[] keyValue = ByteArray.convert(keyParam[3]);
                                final Key key = curSD.getKey(kVer, kID);
                                if (key == null) {
                                    curSD.addSCP02Key(kVer, kID, keyValue);
                                }
                                else {
                                    final GPKey k = (GPKey)key;
                                    k.updateValue(keyValue);
                                }
                            }
                        }
                        else if (cmdParam[0].compareToIgnoreCase("put-key") == 0) {
                            KeySet ks = null;
                            for (int l = 1; l < cmdParam.length; ++l) {
                                final String[] keyParam2 = cmdParam[l].split("/");
                                final Integer ver = Integer.valueOf(keyParam2[0]);
                                if (ks == null) {
                                    ks = new SCPKeySet((int)ver);
                                }
                                final GPKey key2 = new GPKey(ver, 128, Integer.valueOf(keyParam2[1]), ByteArray.convert(keyParam2[3]));
                                ks.addKey(key2);
                            }
                            curSD.putKey(ks);
                        }
                        else if (cmdParam[0].compareToIgnoreCase("init-update") == 0) {
                            if (curSD.getKeySets().isEmpty() && curSD.getAssociateSD() == null) {
                                curSD.setAssociateSD(isd);
                            }
                            curSD.getSCP02Service().initUpdate(Integer.valueOf(cmdParam[1]));
                        }
                        else if (cmdParam[0].compareToIgnoreCase("ext-auth") == 0) {
                            int scLevel = 0;
                            if (cmdParam[1].compareTo("mac") == 0) {
                                scLevel = 1;
                            }
                            else if (cmdParam[1].compareTo("plain") == 0) {
                                scLevel = 0;
                            }
                            else if (cmdParam[1].compareTo("enc") == 0) {
                                scLevel = 3;
                            }
                            curSD.getSCP02Service().ExtAuth(scLevel);
                        }
                        else if (cmdParam[0].compareToIgnoreCase("install") == 0) {
                            final int pCount = cmdParam.length - 1 - 2;
                            if (pCount < 0) {
                                SCDP.reportError("install param count error");
                                return false;
                            }
                            final Privilege privilege = new Privilege();
                            String instanceAID = null;
                            final ByteArray paramApp = new ByteArray();
                            boolean bInstallAndMakeSelectable = true;
                            for (int p = 1; p <= pCount; ++p) {
                                if (cmdParam[p].compareToIgnoreCase("-i") == 0) {
                                    ++p;
                                    instanceAID = cmdParam[p];
                                }
                                else if (cmdParam[p].compareToIgnoreCase("-q") == 0) {
                                    ++p;
                                    this.convertParamToBytes(cmdParam[p], paramApp);
                                }
                                else if (cmdParam[p].compareToIgnoreCase("-s") == 0) {
                                    privilege.setPrivilege(128);
                                }
                                else if (cmdParam[p].compareToIgnoreCase("-e") == 0) {
                                    privilege.setPrivilege(32);
                                }
                                else if (cmdParam[p].compareToIgnoreCase("-b") == 0) {
                                    privilege.setPrivilege(64);
                                }
                                else if (cmdParam[p].compareToIgnoreCase("-d") == 0) {
                                    privilege.setPrivilege(4);
                                }
                                else if (cmdParam[p].compareToIgnoreCase("-t") == 0) {
                                    privilege.setPrivilege(8);
                                }
                                else if (cmdParam[p].compareToIgnoreCase("-l") == 0) {
                                    privilege.setPrivilege(16);
                                }
                                else if (cmdParam[p].compareToIgnoreCase("-p") == 0) {
                                    privilege.setPrivilege(2);
                                }
                                else {
                                    if (cmdParam[p].compareToIgnoreCase("-o") != 0) {
                                        SCDP.reportError("install param unknow:" + cmdParam[p]);
                                        return false;
                                    }
                                    bInstallAndMakeSelectable = false;
                                }
                            }
                            if (bInstallAndMakeSelectable) {
                                curSD.installForInstallAndMakeSel(cmdParam[cmdParam.length - 2], cmdParam[cmdParam.length - 1], instanceAID, privilege.toBytes(), paramApp.toBytes());
                            }
                            else {
                                curSD.installForInstall(cmdParam[cmdParam.length - 2], cmdParam[cmdParam.length - 1], instanceAID, privilege.toBytes(), paramApp.toBytes());
                            }
                        }
                        else if (cmdParam[0].compareToIgnoreCase("delete") == 0) {
                            if (cmdParam[1].compareTo("-r") == 0) {
                                curSD.delete(new AID(cmdParam[2]), true);
                            }
                            else {
                                curSD.delete(new AID(cmdParam[1]), false);
                            }
                        }
                        else {
                            final char firstChar = strLine.charAt(0);
                            switch (firstChar) {
                                case '#': {
                                    continue;
                                }
                                default: {
                                    SCDP.reportError("line no.=" + lineNo + "/ exception=unknown command:" + strLine);
                                    SCDP.endGroup(cookie);
                                    return false;
                                }
                            }
                        }
                    }
                }
                bufferedReader.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                SCDP.reportError("line no.=" + lineNo + "/ exception=" + e.getMessage());
                SCDP.endGroup(cookie);
                return false;
            }
            SCDP.endGroup(cookie);
        }
        return true;
    }
    
    private boolean convertParamToBytes(final String param, final ByteArray paramValue) {
        paramValue.reinit();
        int index = param.indexOf(35);
        if (index == -1) {
            paramValue.append(ByteArray.convert(param));
        }
        else {
            final int paramStrLen = param.length();
            final Stack<ParamInfo> ss = new Stack<ParamInfo>();
            final Queue<ParamInfo> procQueue = new LinkedList<ParamInfo>();
            ss.push(new ParamInfo(index));
            ++index;
            final char quotoBegin = param.charAt(index);
            final char quotoEnd = this.getQuotoEnd(quotoBegin);
            for (int i = ++index; i < paramStrLen; ++i) {
                final char a = param.charAt(i);
                if (a == '#') {
                    final ParamInfo t = new ParamInfo(i);
                    if (!ss.isEmpty()) {
                        t.parent = ss.peek();
                    }
                    ss.push(t);
                }
                else if (a == quotoEnd) {
                    if (ss.isEmpty()) {
                        return false;
                    }
                    final ParamInfo t = ss.pop();
                    t.endIndex = i;
                    if (t.parent != null) {
                        t.parent.increseChildCount();
                    }
                    procQueue.offer(t);
                }
            }
            if (!ss.isEmpty()) {
                return false;
            }
            final StringBuilder s = new StringBuilder(param);
            for (final ParamInfo c : procQueue) {
                int lengthOfChar = this.getParamstrLen(param, c, quotoEnd);
                if (lengthOfChar % 2 != 0) {
                    return false;
                }
                lengthOfChar /= 2;
                if (lengthOfChar > 255) {
                    return false;
                }
                s.setCharAt(c.beginIndex, ByteArray.intToChar(lengthOfChar >> 4));
                s.setCharAt(c.beginIndex + 1, ByteArray.intToChar(lengthOfChar & 0xF));
            }
            String paramV = s.toString();
            final String endS = "\\" + String.valueOf(quotoEnd);
            paramV = paramV.replaceAll(endS, "");
            paramValue.append(ByteArray.convert(paramV));
        }
        return true;
    }
    
    private char getQuotoEnd(final char begin) {
        if (begin == '(') {
            return ')';
        }
        if (begin == '{') {
            return '}';
        }
        if (begin == '[') {
            return ']';
        }
        return '#';
    }
    
    private int getParamstrLen(final String param, final ParamInfo c, final char endChar) {
        int length = c.endIndex - c.beginIndex - 2;
        length -= c.childCount;
        return length;
    }
    
    static {
        expSW = new int[] { 36864 };
    }
}
