package libs.trustconnector.scdp;

public interface TestSuiteEntry
{
    boolean checkCondition();
    
    boolean ExcTest(final String[] p0);
    
    void onException(final Exception p0);
    
    void onEnd();
}
