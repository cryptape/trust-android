package libs.trustconnector.scdp.smartcard;

import libs.trustconnector.scdp.*;

import libs.trustconnector.scdp.SCDP;

public final class ReaderManager
{
    private static SmartCardReader defReader;
    
    private ReaderManager() {
    }
    
    private static SmartCardReader getReader(final int index) {
        if (index == -1) {
            return null;
        }
        final String name = SCDP.readerGetName(index);
        SmartCardReader r = MP300Reader.checkReader(name, index);
        if (r != null) {
            return r;
        }
        r = SocketReader.checkReader(name, index);
        if (r != null) {
            return r;
        }
        r = SDI01XReader.checkReader(name, index);
        if (r != null) {
            return r;
        }
        return new PCSCReader(index);
    }
    
    public static SmartCardReader getReader() {
        if (SCDP.readerCount() == 0) {
            SmartCardReaderException.throwIt("Reader Not Found");
        }
        if (ReaderManager.defReader == null) {
            ReaderManager.defReader = getReader(SCDP.readerGetDefault());
        }
        return ReaderManager.defReader;
    }
    
    public static SmartCardReader getNextAvailableReader(final SmartCardReader reader) {
        int lastReaderIndex;
        if (reader != null) {
            lastReaderIndex = reader.index;
        }
        else {
            lastReaderIndex = -1;
        }
        final int index = SCDP.readerGetNextAvailable(lastReaderIndex);
        if (index == -1) {
            return null;
        }
        return getReader(index);
    }
}
