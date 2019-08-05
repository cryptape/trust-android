package libs.trustconnector.scdp;

import java.util.*;

public abstract class TestSuite implements TestSuiteEntry
{
    protected Stack<Integer> stepCookie;
    
    public TestSuite() {
        this.stepCookie = new Stack<Integer>();
    }
    
    public final int beginTestGroup(final String groupName) {
        return SCDP.beginGroup(groupName);
    }
    
    public final void endTestGroup(final int groupCookie) {
        SCDP.endGroup(groupCookie);
    }
    
    public final void testStep(final String step) {
        if (this.stepCookie.size() > 0) {
            final int lastStepCookie = this.stepCookie.pop();
            SCDP.endGroup(lastStepCookie);
        }
        final int lastStepCookie = SCDP.beginGroup(step);
        this.stepCookie.push(lastStepCookie);
    }
    
    public final void addLog(final String log) {
        SCDP.addLog(log);
    }
    
    public final void reportError(final String errorMessage) {
        SCDP.reportError(errorMessage);
    }
    
    public final void reportError(final String errorMessage, final boolean bThrowException) {
        SCDP.reportError(errorMessage);
        if (bThrowException) {
            throw new SCDPException(errorMessage);
        }
    }
    
    public static void reportWarning(final String warningMessage) {
        SCDP.reportWarning(warningMessage);
    }
    
    public static void reportExtResult(final String extResultName, final String extResult) {
        SCDP.reportExtResult(extResultName, extResult);
    }
    
    public String getParamValue(final String name) {
        return SCDP.getParamValue(name);
    }
    
    public static boolean getParamValueBool(final String name) {
        return SCDP.getParamValueBool(name);
    }
    
    public static int getParamValueInt(final String name) {
        return SCDP.getParamValueInt(name);
    }
    
    public static byte[] getParamValueBytes(final String name) {
        return SCDP.getParamValueBytes(name);
    }
    
    @Override
    public boolean checkCondition() {
        return true;
    }
    
    @Override
    public void onException(final Exception e) {
        final Iterator<Integer> ite = this.stepCookie.iterator();
        while (ite.hasNext()) {
            SCDP.endGroup(ite.next());
        }
        this.stepCookie.removeAllElements();
    }
    
    @Override
    public void onEnd() {
        final Iterator<Integer> ite = this.stepCookie.iterator();
        while (ite.hasNext()) {
            SCDP.endGroup(ite.next());
        }
        this.stepCookie.removeAllElements();
    }
    
    public void requiredOption(final String optionName) {
        if (!SCDP.getParamValueBool(optionName)) {
            throw new SCDPException("option=[" + optionName + "] value is not true");
        }
    }
    
    public void requireOption(final String optionName, final int value) {
        final int valueT = SCDP.getParamValueInt(optionName);
        if (value != valueT) {
            throw new SCDPException("option=[" + optionName + "] value is not [" + value + "]");
        }
    }
    
    public void requireOptionRange(final String optionName, final int min, final int max) {
        final int value = SCDP.getParamValueInt(optionName);
        if (value < min || value > max) {
            throw new SCDPException("option=[" + optionName + "] value is not in the range of [" + min + "-" + max + "]");
        }
    }
    
    public void requireOption(final String optionName, final String expValue) {
        final String value = SCDP.getParamValue(optionName);
        final String[] exp = expValue.split("\\|");
        boolean bRes = false;
        for (int i = 0; i < exp.length; ++i) {
            if (value.compareTo(exp[i]) == 0) {
                bRes = true;
                break;
            }
        }
        if (!bRes) {
            throw new SCDPException("option=[" + optionName + "] value is not match " + expValue);
        }
    }
}
