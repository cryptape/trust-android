package com.trustconnector.scdp.smartcard.application.telecom.cat;

import com.trustconnector.scdp.util.tlv.simpletlv.*;

public class SetupEventList extends ProactiveCommand
{
    protected EventList eventList;
    
    public SetupEventList(final byte[] cmd) {
        super(cmd);
        this.eventList = (EventList)this.findTLV((byte)25);
    }
    
    public byte[] getEventList() {
        return this.eventList.getEventList();
    }
    
    public boolean isEventExist(final byte event) {
        final byte[] eventList = this.getEventList();
        if (eventList == null) {
            return false;
        }
        for (int eventCount = eventList.length, i = 0; i < eventCount; ++i) {
            if (event == eventList[i]) {
                return true;
            }
        }
        return false;
    }
}
