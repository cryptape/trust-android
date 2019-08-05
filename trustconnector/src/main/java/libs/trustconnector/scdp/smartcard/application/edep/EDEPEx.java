package libs.trustconnector.scdp.smartcard.application.edep;

import libs.trustconnector.scdp.smartcard.*;

import libs.trustconnector.scdp.smartcard.AID;
import libs.trustconnector.scdp.smartcard.SmartCardReader;

public class EDEPEx extends EDEP
{
    public EDEPEx(final SmartCardReader reader, final AID aid) {
        super(reader, aid);
    }
}
