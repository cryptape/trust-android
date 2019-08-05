package com.trustconnector.scdp.smartcard.application.pboc;

import com.trustconnector.scdp.smartcard.application.*;
import com.trustconnector.scdp.smartcard.*;

public class QPBOC extends Application
{
    public QPBOC(final SmartCardReader reader, final AID aid) {
        super(reader, aid);
    }
}
