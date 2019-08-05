package libs.trustconnector.scdp.smartcard.application.telecom.checkrule.cat;

public class ProvideLocalInfomationChecker extends ProactiveCommandChecker
{
    public ProvideLocalInfomationChecker(final int qulifier) {
        super(38, qulifier, -127, -126);
    }
    
    @Override
    public boolean check() {
        return true;
    }
}
