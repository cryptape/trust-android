package libs.trustconnector.scdp.util;

public final class TimeRecorder
{
    private boolean startRecord;
    private long timeStart;
    private long timeTotal;
    
    public TimeRecorder() {
        this.startRecord();
    }
    
    public void startRecord() {
        this.startRecord = false;
        this.timeTotal = 0L;
        this.timeStart = System.nanoTime();
    }
    
    public void stopRecord() {
        if (this.startRecord) {
            this.timeTotal = System.nanoTime() - this.timeStart;
            this.startRecord = false;
        }
    }
    
    public long getTime() {
        return this.timeTotal / 1000000L;
    }
    
    public long getTimeMS() {
        return this.timeTotal / 1000000L;
    }
    
    public long getTimeUS() {
        return this.timeTotal / 1000L;
    }
    
    public long getTimeNS() {
        return this.timeTotal;
    }
}
