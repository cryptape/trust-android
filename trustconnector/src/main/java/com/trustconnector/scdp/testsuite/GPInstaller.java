package com.trustconnector.scdp.testsuite;

import com.trustconnector.scdp.*;
import com.trustconnector.scdp.util.*;
import com.trustconnector.scdp.util.tlv.bertlv.*;
import com.trustconnector.scdp.smartcard.application.globalplatform.*;
import com.trustconnector.scdp.smartcard.*;
import com.trustconnector.scdp.util.tlv.*;

public class GPInstaller extends TestSuite
{
    @Override
    public boolean ExcTest(final String[] param) {
        final String cmd = param[0];
        final SmartCardReader reader = ReaderManager.getReader();
        reader.reset();
        final ISD isd = SD.getISD(reader);
        final APDU apdu = isd.getAPDU();
        final NormalAPDUChecker checker = new NormalAPDUChecker(36864);
        apdu.setRAPDUChecker(checker);
        isd.select();
        isd.openSCP02();
        if (cmd.compareToIgnoreCase("delete") == 0) {
            boolean bDeleteRefObj;
            if (param.length == 2) {
                bDeleteRefObj = false;
            }
            else {
                if (param.length != 3) {
                    SCDP.reportError("delete param error: delete (-r) AID");
                    return false;
                }
                bDeleteRefObj = true;
            }
            final AID aid = new AID(param[param.length - 1]);
            isd.delete(aid, bDeleteRefObj, null, "009000|6A88");
            return true;
        }
        if (cmd.compareToIgnoreCase("upload") == 0) {
            if (param.length != 2) {
                SCDP.reportError("upload param error: upload cappath");
                return false;
            }
            isd.loadPackage(param[1]);
            return true;
        }
        else {
            if (cmd.compareToIgnoreCase("install") == 0) {
                final AID pkgAID = new AID(param[1]);
                final AID clsAID = new AID(param[2]);
                final AID app = new AID(param[3]);
                Privilege privilege = null;
                if (param.length > 4) {
                    privilege = new Privilege(ByteArray.convert(param[4]));
                }
                LV iparam = null;
                if (param.length > 5) {
                    iparam = BERLVBuilder.buildLV(ByteArray.convert(param[5]));
                }
                isd.installForInstallAndMakeSel(pkgAID, clsAID, app, privilege, iparam);
                return true;
            }
            return false;
        }
    }
}
