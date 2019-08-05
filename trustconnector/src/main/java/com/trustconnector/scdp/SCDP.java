package com.trustconnector.scdp;

import org.jdom2.input.*;
import org.jdom2.*;
import org.jdom2.filter.*;
import org.jdom2.xpath.*;
import java.net.*;
import java.io.*;
import com.trustconnector.scdp.util.*;

public final class SCDP
{
    static boolean terminated;
    private static final String TEST_SUITE_ROOT_NAME = "testsuit";
    public static final int VERSION = 256;
    public static final int PROPERTY_LIB_VERSION = 0;
    public static final int PROPERTY_LIB_PATH = 1;
    private static Element scdpRoot;
    private static Element configRoot;
    private static final String CONFIG_XML_FILE = ".project";
    private static JavaEntry javaEntry;
    
    private SCDP() {
    }
    
    static void init(final String[] args) {
        final int port = Integer.valueOf(args[0]);
        (SCDP.javaEntry = new SCDP().new JavaEntry()).connect(port);
        final SAXBuilder builder = new SAXBuilder();
        try {
            final Document profileDoc = builder.build(".\\.project");
            final Element root = profileDoc.getRootElement();
            SCDP.scdpRoot = root.getChild("scdp");
            if (SCDP.scdpRoot == null) {
                System.out.println("project file format error!");
                return;
            }
            final String config = SCDP.scdpRoot.getAttributeValue("config");
            if (config == null || config.length() == 0) {
                SCDP.configRoot = SCDP.scdpRoot;
            }
            else {
                final Document confidDoc = builder.build(config);
                SCDP.configRoot = confidDoc.getRootElement();
            }
        }
        catch (Exception ex) {}
    }
    
    static boolean isTerminated() {
        return SCDP.terminated;
    }
    
    public static Element getCfgSubNode(final String subNode) {
        if (SCDP.configRoot != null) {
            return SCDP.configRoot.getChild(subNode);
        }
        return null;
    }
    
    public static Element getTestSuiteRoot() {
        if (SCDP.scdpRoot != null) {
            return SCDP.scdpRoot.getChild("testsuit");
        }
        return null;
    }
    
    public static String getParamValue(final String name) {
        if (SCDP.configRoot != null) {
            final XPathExpression<Element> xpath = (XPathExpression<Element>)XPathFactory.instance().compile("./parameters/parameter[@name='" + name + "']", Filters.element());
            final Element paramNode = (Element)xpath.evaluateFirst((Object)SCDP.configRoot);
            if (paramNode != null) {
                return paramNode.getAttributeValue("value");
            }
        }
        return null;
    }
    
    public static boolean getParamValueBool(final String name) {
        final String value = getParamValue(name);
        return value != null && value.compareToIgnoreCase("true") == 0;
    }
    
    public static int getParamValueInt(final String name) {
        final String value = getParamValue(name);
        if (value == null) {
            throw new NumberFormatException();
        }
        if (value.startsWith("0x")) {
            return Integer.valueOf(value.substring(2), 16);
        }
        return Integer.valueOf(value, 10);
    }
    
    public static byte[] getParamValueBytes(final String name) {
        final String value = getParamValue(name);
        if (value != null) {
            return ByteArray.convert(value);
        }
        throw new NumberFormatException();
    }
    
    static int beginTestGroup(final String groupName) {
        return SCDP.javaEntry.beginTestGroup(groupName);
    }
    
    static void endTestGroup(final int cookit) {
        SCDP.javaEntry.endTestGroup(cookit);
    }
    
    static int beginTestCase(final String caseName) {
        return SCDP.javaEntry.beginTestCase(caseName);
    }
    
    static void endTestCase(final int cookit) {
        SCDP.javaEntry.endTestCase(cookit);
    }
    
    public static int beginGroup(final String groupName) {
        return SCDP.javaEntry.beginGroup(groupName);
    }
    
    public static void endGroup(final int cookit) {
        SCDP.javaEntry.endGroup(cookit);
    }
    
    public static void addLog(final String log) {
        final String[] logs = log.split("\\\n");
        for (int i = 0; i < logs.length; ++i) {
            SCDP.javaEntry.addLog(logs[i]);
        }
    }
    
    public static void addSublog(final String log) {
        SCDP.javaEntry.addSublog(log);
    }
    
    public static void reportError(final String errorMessage) {
        SCDP.javaEntry.reportError(errorMessage);
    }
    
    public static void reportError(final String errorMessage, final boolean bThrowException) {
        SCDP.javaEntry.reportError(errorMessage);
        if (bThrowException) {
            throw new SCDPException(errorMessage);
        }
    }
    
    public static void reportSubError(final String errorMessage) {
        SCDP.javaEntry.reportSubError(errorMessage);
    }
    
    public static void reportWarning(final String warningMessage) {
        SCDP.javaEntry.reportWarning(warningMessage);
    }
    
    public static void reportExtResult(final String extResultName, final String extResult) {
        SCDP.javaEntry.reportExtResult(extResultName, extResult);
    }
    
    public static int readerCount() {
        return SCDP.javaEntry.readerCount();
    }
    
    public static int readerGetDefault() {
        return SCDP.javaEntry.readerGetDefault();
    }
    
    public static int readerGetNextAvailable(final int index) {
        return SCDP.javaEntry.readerGetNextReader(index);
    }
    
    public static String readerGetName(final int index) {
        return SCDP.javaEntry.readerGetName(index);
    }
    
    public static byte[] readerConnect(final int index) {
        final byte[] rsp = SCDP.javaEntry.readerConnect(index);
        if (rsp == null || rsp.length <= 4) {
            return null;
        }
        final byte[] atr = new byte[rsp.length - 4];
        System.arraycopy(rsp, 4, atr, 0, rsp.length - 4);
        return atr;
    }
    
    public static boolean readerDisConnect(final int index) {
        return SCDP.javaEntry.readerDisConnect(index);
    }
    
    public static byte[] readerTransmit(final int index, final String name, final byte[] apdu, final int offset, final int length) {
        return SCDP.javaEntry.readerTransmit(index, name, apdu, offset, length);
    }
    
    public static double readerGetLastCmdTime(final int index) {
        return SCDP.javaEntry.readerGetLastCmdTime(index);
    }
    
    public static byte[] readerReset(final int index, final boolean bColdReset) {
        return SCDP.javaEntry.readerReset(index, bColdReset);
    }
    
    public static int readerGetProtocol(final int index) {
        return SCDP.javaEntry.readerGetProtocol(index);
    }
    
    public static byte[] readerGetAttr(final int index, final int attrType) {
        return SCDP.javaEntry.readerGetAttr(index, attrType);
    }
    
    public static boolean readerSetAttr(final int index, final int attrType, final byte[] newAttr) {
        return SCDP.javaEntry.readerSetAttr(index, attrType, newAttr);
    }
    
    public static void addAPDUInfo(final String message) {
        final String[] logs = message.split("\\\n");
        for (int i = 0; i < logs.length; ++i) {
            SCDP.javaEntry.addAPDUInfo(logs[i]);
        }
    }
    
    public static void addAPDUExpInfo(final String message) {
        addSublog(message);
    }
    
    public static void reportAPDUExpErr(final String message) {
        SCDP.javaEntry.reportAPDUError(message);
    }
    
    public static void startRecordAPDUTime() {
        SCDP.javaEntry.startRecordTime(false);
    }
    
    public static void startRecordAPDUTime(final boolean bAddConnectTime) {
        SCDP.javaEntry.startRecordTime(bAddConnectTime);
    }
    
    public static void stopRecordAPDUTime() {
        SCDP.javaEntry.stopRecordTime();
    }
    
    public static void reportRecordAPDUTime(final String msgBeforeTime) {
        SCDP.javaEntry.reportRecordTime(msgBeforeTime);
    }
    
    public static long getRecordAPDUTime() {
        return SCDP.javaEntry.getRecordTime();
    }
    
    static void OnEnd() {
        SCDP.javaEntry.OnEnd();
    }
    
    public static String promptInput(final String title, final String desc) {
        return SCDP.javaEntry.promptInput(title, desc);
    }
    
    public static boolean promptConfirm(final String title, final String desc) {
        return SCDP.javaEntry.promptConfirm(title, desc);
    }
    
    public static void promptInfo(final String title, final String info) {
        SCDP.javaEntry.promptInfo(title, info);
    }
    
    public static String getProperty(final int propertyType) {
        if (0 == propertyType) {
            return String.format("%d.%d", 1, 0);
        }
        return SCDP.javaEntry.getProperty(propertyType);
    }
    
    class JavaEntry
    {
        static final int ENTRY_BEGIN_GROUP = 0;
        static final int ENTRY_END_GROUP = 1;
        static final int ENTRY_ADD_LOG = 2;
        static final int ENTRY_ADD_SUB_LOG = 3;
        static final int ENTRY_END_SUB_LOG = 4;
        static final int ENTRY_REPORT_ERR = 5;
        static final int ENTRY_REPORT_SUB_ERR = 6;
        static final int ENTRY_END_SUB_ERR = 7;
        static final int ENTRY_BEGIN_TEST_GROUP = 8;
        static final int ENTRY_BEGIN_TEST_CASE = 9;
        static final int ENTRY_REPORT_APDU_ERR = 10;
        static final int ENTRY_END_TEST_GROUP = 11;
        static final int ENTRY_END_TEST_CASE = 12;
        static final int ENTRY_ADD_APDU_INFO = 13;
        static final int ENTRY_REPORT_WARNING = 14;
        static final int ENTRY_REPORT_EXT_RESULT = 16;
        static final int ENTRY_READER_GET_COUNT = 32;
        static final int ENTRY_READER_GET_NAME = 33;
        static final int ENTRY_READER_CONNECT = 34;
        static final int ENTRY_READER_DISCONN = 35;
        static final int ENTRY_READER_TRANSMIT = 36;
        static final int ENTRY_READER_RESET = 37;
        static final int ENTRY_READER_GET_PROTOCOL = 39;
        static final int ENTRY_READER_GET_NEXT_READER = 40;
        static final int ENTRY_READER_GET_DEFAULT_READER = 41;
        static final int ENTRY_START_RECORD_TIME = 48;
        static final int ENTRY_STOP_RECORD_TIME = 49;
        static final int ENTRY_REPORT_RECORD_TIME = 50;
        static final int ENTRY_GET_RECORD_TIME = 51;
        static final int ENTRY_GET_LAST_APDU_TIME = 52;
        static final int ENTRY_PROMPT_INPUT = 64;
        static final int ENTRY_PROMPT_CONFIRM = 65;
        static final int ENTRY_PROMPT_INFO = 66;
        static final int ENTRY_READER_GET_ATTR = 80;
        static final int ENTRY_READER_SET_ATTR = 81;
        static final int ENTRY_GET_PROPERTY = 96;
        static final int ENTRY_EXC_END = 255;
        static final int MESSAGE_MAX_LEN = 1024;
        static final byte RESULT_OK = 0;
        static final byte RESULT_ERR = -1;
        private Socket client;
        private DataOutputStream writer;
        private DataInputStream reader;
        private static final int CPKG_OFF_LEN = 0;
        private static final int CPKG_OFF_OPCODE = 4;
        private static final int CPKG_OFF_PARAM = 8;
        private static final int RPKG_OFF_RESULT = 0;
        private static final int RPKG_OFF_RESPONSE = 4;
        private static final int PKG_MAX_LEN = 800;
        
        int beginTestGroup(final String groupName) {
            final byte[] res = this.sendMessage(8, groupName);
            return this.getRPkgResult(res);
        }
        
        void endTestGroup(final int cookit) {
            this.sendMessage(11, cookit);
        }
        
        int beginTestCase(final String groupName) {
            final byte[] res = this.sendMessage(9, groupName);
            return this.getRPkgResult(res);
        }
        
        void endTestCase(final int cookit) {
            this.sendMessage(12, cookit);
        }
        
        int beginGroup(final String groupName) {
            final byte[] res = this.sendMessage(0, groupName);
            return this.getRPkgResult(res);
        }
        
        void endGroup(final int cookit) {
            this.sendMessage(1, cookit);
        }
        
        void addLog(final String log) {
            this.sendMessage(2, log);
        }
        
        void addSublog(final String log) {
            this.sendMessage(3, log);
        }
        
        void addAPDUInfo(final String info) {
            this.sendMessage(13, info);
        }
        
        void reportError(final String errorMessage) {
            this.sendMessage(5, errorMessage);
        }
        
        void reportSubError(final String errorMessage) {
            this.sendMessage(6, errorMessage);
        }
        
        void reportAPDUError(final String errorMessage) {
            this.sendMessage(10, errorMessage);
        }
        
        void reportWarning(final String warningMessage) {
            this.sendMessage(14, warningMessage);
        }
        
        void reportExtResult(final String extResultName, final String extResult) {
            final byte[] msgContent = this.strToMessage(extResultName);
            final byte[] msgContentT = this.strToMessage(extResult);
            final byte[] msg = new byte[msgContent.length + msgContentT.length];
            System.arraycopy(msgContent, 0, msg, 0, msgContent.length);
            System.arraycopy(msgContentT, 0, msg, msgContent.length, msgContentT.length);
            this.sendMessage(16, msg);
        }
        
        int readerCount() {
            final byte[] res = this.sendMessage(32);
            if (res == null) {
                return 0;
            }
            return this.getRPkgResult(res);
        }
        
        int readerGetDefault() {
            final byte[] res = this.sendMessage(41);
            if (res == null) {
                return 0;
            }
            return this.getRPkgResult(res);
        }
        
        int readerGetNextReader(final int index) {
            final byte[] res = this.sendMessage(40, index);
            if (res == null) {
                return 0;
            }
            return this.getRPkgResult(res);
        }
        
        String readerGetName(final int index) {
            final byte[] res = this.sendMessage(33, index);
            if (res == null || this.getRPkgResult(res) != 0) {
                return null;
            }
            final StringBuilder name = new StringBuilder();
            this.messageReadStr(res, 4, name);
            return name.toString();
        }
        
        byte[] readerConnect(final int index) {
            return this.sendMessage(34, index);
        }
        
        boolean readerDisConnect(final int index) {
            final byte[] res = this.sendMessage(35, index);
            return res != null && this.getRPkgResult(res) == 0;
        }
        
        byte[] readerTransmit(final int index, final String name, final byte[] apdu, final int offset, final int length) {
            final byte[] nameBuf = this.strToMessage(name);
            final byte[] message = new byte[8 + length + nameBuf.length];
            this.messageAppendInt(index, message, 0);
            this.messageAppendInt(length, message, 4);
            System.arraycopy(apdu, offset, message, 8, length);
            System.arraycopy(nameBuf, 0, message, 8 + length, nameBuf.length);
            final byte[] rsp = this.sendMessage(36, message);
            if (this.getRPkgResult(rsp) == 0) {
                return this.getRPkgResponse(rsp);
            }
            return null;
        }
        
        public double bytes2Double(final byte[] arr, final int offset) {
            long value = 0L;
            for (int i = 0; i < 8; ++i) {
                value |= (long)(arr[i + offset] & 0xFF) << 8 * i;
            }
            return Double.longBitsToDouble(value);
        }
        
        double readerGetLastCmdTime(final int index) {
            final byte[] message = new byte[4];
            this.messageAppendInt(index, message, 0);
            final byte[] rsp = this.sendMessage(52, message);
            if (this.getRPkgResult(rsp) == 0) {
                return this.bytes2Double(rsp, 4);
            }
            return 0.0;
        }
        
        byte[] readerReset(final int index, final boolean bColdReset) {
            final byte[] msg = new byte[8];
            this.messageAppendInt(index, msg, 0);
            if (bColdReset) {
                this.messageAppendInt(0, msg, 4);
            }
            else {
                this.messageAppendInt(1, msg, 4);
            }
            final byte[] res = this.sendMessage(37, msg);
            if (this.getRPkgResult(res) == 0) {
                return this.getRPkgResponse(res);
            }
            return null;
        }
        
        int readerGetProtocol(final int index) {
            final byte[] protocol = this.sendMessage(39, index);
            if (this.getRPkgResult(protocol) == 0) {
                return this.messageReadInt(protocol, 4);
            }
            return 0;
        }
        
        byte[] readerGetAttr(final int index, final int attrType) {
            final byte[] msg = new byte[8];
            this.messageAppendInt(index, msg, 0);
            this.messageAppendInt(attrType, msg, 4);
            final byte[] res = this.sendMessage(80, msg);
            if (this.getRPkgResult(res) == 0) {
                return this.getRPkgResponse(res);
            }
            return null;
        }
        
        boolean readerSetAttr(final int index, final int attrType, final byte[] newAttr) {
            final byte[] msg = new byte[12 + newAttr.length];
            this.messageAppendInt(index, msg, 0);
            this.messageAppendInt(attrType, msg, 4);
            this.messageAppendInt(newAttr.length, msg, 8);
            System.arraycopy(newAttr, 0, msg, 12, newAttr.length);
            final byte[] res = this.sendMessage(81, msg);
            return this.getRPkgResult(res) == 0;
        }
        
        void startRecordTime(final boolean b) {
            final int bConn = b ? 1 : 0;
            this.sendMessage(48, bConn);
        }
        
        void stopRecordTime() {
            this.sendMessage(49);
        }
        
        void reportRecordTime(final String msg) {
            this.sendMessage(50, msg);
        }
        
        long getRecordTime() {
            final byte[] res = this.sendMessage(51);
            return this.getRPkgResult(res);
        }
        
        String promptInput(final String title, final String desc) {
            final byte[] res = this.sendMessage(64, title, desc);
            if (res == null || this.getRPkgResult(res) != 0) {
                return null;
            }
            final StringBuilder name = new StringBuilder();
            this.messageReadStr(res, 4, name);
            return name.toString();
        }
        
        boolean promptConfirm(final String title, final String desc) {
            final byte[] res = this.sendMessage(65, title, desc);
            return res != null && this.getRPkgResult(res) != 0;
        }
        
        void promptInfo(final String title, final String desc) {
            this.sendMessage(66, title, desc);
        }
        
        String getProperty(final int propertyType) {
            final byte[] res = this.sendMessage(96, propertyType);
            if (res == null || this.getRPkgResult(res) != 0) {
                return null;
            }
            final StringBuilder name = new StringBuilder();
            this.messageReadStr(res, 4, name);
            return name.toString();
        }
        
        void addAPDUExpInfo(final String message) {
            this.addSublog(message);
        }
        
        void reportAPDUExpErr(final String message) {
            this.reportSubError(message);
        }
        
        void OnEnd() {
            this.sendMessage(255);
        }
        
        private byte[] sendMessage(final int messageType) {
            return this.sendMessage(messageType, null, 0, 0);
        }
        
        private byte[] sendMessage(final int messageType, final byte[] content) {
            if (content != null && content.length > 1024) {
                System.out.println("CJBridger error! message length too long!");
                return null;
            }
            return this.sendMessage(messageType, content, 0, (content == null) ? 0 : content.length);
        }
        
        private byte[] sendMessage(final int messageType, final int value) {
            final byte[] content = new byte[4];
            this.messageAppendInt(value, content, 0);
            return this.sendMessage(messageType, content, 0, content.length);
        }
        
        private byte[] sendMessage(final int messageType, final String msg) {
            final byte[] msgContent = this.strToMessage(msg);
            final byte[] res = this.sendMessage(messageType, msgContent, 0, msgContent.length);
            return res;
        }
        
        private byte[] sendMessage(final int messageType, final String title, final String desc) {
            final byte[] titleC = this.strToMessage(title);
            final byte[] descC = this.strToMessage(desc);
            final byte[] msgContent = new byte[titleC.length + descC.length];
            System.arraycopy(titleC, 0, msgContent, 0, titleC.length);
            System.arraycopy(descC, 0, msgContent, titleC.length, descC.length);
            final byte[] res = this.sendMessage(messageType, msgContent, 0, msgContent.length);
            return res;
        }
        
        private byte[] sendMessage(final int messageType, final byte[] content, final int offset, final int length) {
            return this.sendAndRecvPkg(messageType, content, offset, length);
        }
        
        private byte[] strToMessage(final String message) {
            final int messageLen = this.getStrLenInMessage(message);
            final byte[] result = new byte[messageLen];
            this.messageAppendStr(message, result, 0);
            return result;
        }
        
        private int messageAppendStr(final String content, final byte[] destBuf, int offset) {
            if (content != null && content.length() != 0) {
                final byte[] message = content.getBytes();
                offset += this.messageAppendInt(message.length, destBuf, offset);
                System.arraycopy(message, 0, destBuf, offset, message.length);
                return message.length + 4;
            }
            return this.messageAppendInt(0, destBuf, offset);
        }
        
        private int messageAppendInt(final int value, final byte[] destBuf, final int offset) {
            destBuf[offset] = (byte)(value >> 24);
            destBuf[offset + 1] = (byte)(value >> 16);
            destBuf[offset + 2] = (byte)(value >> 8);
            destBuf[offset + 3] = (byte)value;
            return 4;
        }
        
        private int messageReadInt(final byte[] buf, final int offset) {
            int iRes = 0;
            iRes |= (buf[offset] & 0xFF);
            iRes <<= 8;
            iRes |= (buf[offset + 1] & 0xFF);
            iRes <<= 8;
            iRes |= (buf[offset + 2] & 0xFF);
            iRes <<= 8;
            iRes |= (buf[offset + 3] & 0xFF);
            return iRes;
        }
        
        private int messageReadStr(final byte[] buf, int offset, final StringBuilder builder) {
            final int length = this.messageReadInt(buf, offset);
            offset += 4;
            builder.delete(0, builder.capacity());
            for (int j = 0; j < length; ++j) {
                builder.append((char)buf[offset + j]);
            }
            return 4 + length;
        }
        
        private int getStrLenInMessage(final String str) {
            if (str == null || str.length() == 0) {
                return 4;
            }
            return str.getBytes().length + 4;
        }
        
        public boolean connect(final String server, final int port) {
            try {
                this.client = new Socket(server, port);
                this.writer = new DataOutputStream(this.client.getOutputStream());
                this.reader = new DataInputStream(this.client.getInputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        
        public boolean connect(final int port) {
            return this.connect("127.0.0.1", port);
        }
        
        private byte[] sendAndRecvPkg(final int opCode, final byte[] param, final int off, final int paramLen) {
            if (paramLen < 0 || paramLen > 800) {
                System.out.println("Error send message, param length=" + paramLen);
                return null;
            }
            final int pkgLen = paramLen + 4;
            final byte[] rawPkg = new byte[pkgLen + 4];
            if (param != null) {
                System.arraycopy(param, 0, rawPkg, 8, paramLen);
            }
            this.messageAppendInt(pkgLen, rawPkg, 0);
            this.messageAppendInt(opCode, rawPkg, 4);
            try {
                this.writer.write(rawPkg);
                this.writer.flush();
                final byte[] pkgLenBuf = new byte[4];
                this.reader.readFully(pkgLenBuf);
                final int retPkgLen = Util.bytesToInt(pkgLenBuf);
                final byte[] retPkg = new byte[retPkgLen];
                this.reader.readFully(retPkg);
                return retPkg;
            }
            catch (Exception e) {
                SCDP.terminated = true;
                return null;
            }
        }
        
        int getRPkgResult(final byte[] rpkg) {
            return this.messageReadInt(rpkg, 0);
        }
        
        byte[] getRPkgResponse(final byte[] rpkg) {
            if (rpkg != null && rpkg.length > 4) {
                final byte[] response = new byte[rpkg.length - 4];
                System.arraycopy(rpkg, 4, response, 0, response.length);
                return response;
            }
            return null;
        }
    }
}
