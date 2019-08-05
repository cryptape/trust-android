package libs.trustconnector.scdp.smartcard.javacard.cap;

import libs.trustconnector.scdp.smartcard.*;
import java.util.zip.*;
import libs.trustconnector.scdp.util.*;
import java.io.*;
import java.util.*;

import libs.trustconnector.scdp.smartcard.AID;
import libs.trustconnector.scdp.util.ByteArray;

public final class CAP
{
    private Component[] components;
    private String pkgName;
    private static Map<AID, String> mapPkgAIDName;
    
    private CAP() {
        this.init();
    }
    
    public CAP(final String capfile) throws Exception {
        if (!this.load(capfile)) {
            throw new Exception("cap file not found:" + capfile);
        }
    }
    
    private void init() {
        this.pkgName = null;
        this.components = new Component[13];
    }
    
    public boolean loadIJC(final String ijcfile) {
        this.init();
        DataInputStream in = null;
        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(ijcfile)));
            final int size = in.available();
            final byte[] content = new byte[size];
            in.readFully(content);
            in.close();
            int comLen;
            for (int i = 0; i < size; i += comLen + 3) {
                comLen = ((content[i + 1] & 0xFF) << 8 | (content[i + 2] & 0xFF));
                final byte[] data = new byte[comLen + 3];
                System.arraycopy(content, i, data, 0, comLen + 3);
                final Component com = Component.buildComponent(data);
                this.components[data[0]] = com;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean load(final String capfile) {
        this.init();
        try {
            final ZipFile capFile = new ZipFile(capfile);
            final Enumeration<?> emu = capFile.entries();
            while (emu.hasMoreElements()) {
                final ZipEntry entry = (ZipEntry)emu.nextElement();
                final String name = entry.getName();
                if (name.lastIndexOf(".cap") == name.length() - 4) {
                    if (this.pkgName == null) {
                        this.pkgName = capEntryPath2PkgName(name);
                    }
                    final DataInputStream dis = new DataInputStream(capFile.getInputStream(entry));
                    final byte[] data = new byte[dis.available()];
                    dis.readFully(data);
                    final Component com = Component.buildComponent(data);
                    this.components[data[0]] = com;
                }
            }
            capFile.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public int getVersion() {
        final ComponentHeader header = (ComponentHeader)this.components[1];
        if (header == null) {
            return 0;
        }
        return header.getCapVersion();
    }
    
    public boolean isLib() {
        final ComponentHeader header = (ComponentHeader)this.components[1];
        return header != null && !header.hasAppletComponent();
    }
    
    public AID getAID() {
        final ComponentHeader header = (ComponentHeader)this.components[1];
        if (header == null) {
            return null;
        }
        return header.getAID();
    }
    
    public String getPkgName() {
        if (this.pkgName != null) {
            return this.pkgName;
        }
        final ComponentHeader header = (ComponentHeader)this.components[1];
        if (header == null) {
            return null;
        }
        return header.getPkgName();
    }
    
    public AID[] getRefPkg() {
        final ComponentImport importC = (ComponentImport)this.components[4];
        if (importC == null) {
            return null;
        }
        return importC.getImportPkgs();
    }
    
    public boolean hasDebugComponent() {
        return this.components[12] != null;
    }
    
    public AID[] getAppletAIDs() {
        if (this.components[3] != null) {
            final ComponentApplet c = (ComponentApplet)this.components[3];
            return c.getAppletsAID();
        }
        return null;
    }
    
    public int getDownloadSize(final boolean bHasDescriptor, final boolean bHasDebug) {
        int size = 0;
        for (int i = 0; i <= 10; ++i) {
            if (this.components[i] != null) {
                size += this.components[i].getTotalLen();
            }
        }
        if (bHasDescriptor && this.components[11] != null) {
            size += this.components[11].getTotalLen();
        }
        if (bHasDebug && this.components[12] != null) {
            size += this.components[12].getTotalLen();
        }
        return size;
    }
    
    public ByteArray getDownloadBytes(final boolean bHasDescriptor, final boolean bHasDebug) {
        final ByteArray a = this.components[1].getBytes();
        final ByteArray dir = this.components[2].getBytes();
        if (!bHasDescriptor && this.components[2] != null) {
            dir.setInt2(5, 0);
        }
        if (!bHasDebug && this.components[12] != null) {
            dir.setInt2(25, 0);
        }
        a.append(dir);
        a.append(this.components[4].getBytes());
        if (this.components[3] != null) {
            a.append(this.components[3].getBytes());
        }
        a.append(this.components[6].getBytes());
        a.append(this.components[7].getBytes());
        if (this.components[8] != null) {
            a.append(this.components[8].getBytes());
        }
        if (this.components[10] != null) {
            a.append(this.components[10].getBytes());
        }
        a.append(this.components[5].getBytes());
        a.append(this.components[9].getBytes());
        if (bHasDescriptor && this.components[11] != null) {
            a.append(this.components[11].getBytes());
        }
        if (bHasDebug && this.components[12] != null) {
            a.append(this.components[12].getBytes());
        }
        return a;
    }
    
    public boolean toJCSH(final String file, final int blockLen, final boolean bHasDecs, final boolean bHasDebug) {
        if (blockLen > 255) {
            return false;
        }
        try {
            final BufferedWriter outScript = new BufferedWriter(new FileWriter(file));
            final AID aid = this.getAID();
            String installForLoad = String.format("80E60200%02X%02X", aid.length() + 5, aid.length());
            installForLoad += aid;
            installForLoad += "00000000";
            outScript.write("send ");
            outScript.write(installForLoad);
            outScript.newLine();
            final ByteArray capContent = this.getDownloadBytes(bHasDecs, bHasDebug);
            final ByteArray loadConent = new ByteArray("C4");
            loadConent.append(capContent.toBERLV());
            final byte[][] blocks = loadConent.split(blockLen);
            final int blockCount = blocks.length - 1;
            String loadAPDU = null;
            for (int i = 0; i < blockCount; ++i) {
                loadAPDU = String.format("80E800%02XF0", (byte)i);
                loadAPDU += ByteArray.convert(blocks[i], 0, 240);
                outScript.write("send ");
                outScript.write(loadAPDU);
                outScript.newLine();
            }
            loadAPDU = String.format("80E880%02X%02X", (byte)(blocks.length - 1), blocks[blockCount].length);
            loadAPDU += ByteArray.convert(blocks[blockCount], 0, blocks[blockCount].length);
            outScript.write("send ");
            outScript.write(loadAPDU);
            outScript.newLine();
            outScript.flush();
            outScript.close();
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
    private static String capEntryPath2PkgName(final String strPath) {
        int index = 0;
        final StringBuilder pkgBuilder = new StringBuilder(strPath);
        index = pkgBuilder.lastIndexOf("/");
        pkgBuilder.delete(index, pkgBuilder.length());
        index = pkgBuilder.lastIndexOf("/");
        pkgBuilder.delete(index, pkgBuilder.length());
        return pkgBuilder.toString();
    }
    
    public static String getPkgName(final AID aid) {
        return CAP.mapPkgAIDName.get(aid);
    }
    
    public static CAP loadFromFile(final String capfile) {
        final CAP cap = new CAP();
        if (!cap.load(capfile)) {
            return null;
        }
        return cap;
    }
    
    public static CAP loadFromIJC(final String ijcFile) {
        final CAP cap = new CAP();
        if (!cap.loadIJC(ijcFile)) {
            return null;
        }
        return cap;
    }
    
    public static CAP parseFromLoadAPDU(final String[] apdus) {
        return null;
    }
    
    static {
        CAP.mapPkgAIDName = new HashMap<AID, String>();
    }
}
