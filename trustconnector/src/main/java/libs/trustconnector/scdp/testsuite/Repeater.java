package libs.trustconnector.scdp.testsuite;

import libs.trustconnector.scdp.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.TestSuite;
import libs.trustconnector.scdp.TestSuiteEntry;

public class Repeater extends TestSuite
{
    @Override
    public boolean ExcTest(final String[] param) {
        if (param.length < 2) {
            SCDP.reportError("param error,usage:counter testclass[param1,param2,...]");
            return false;
        }
        final int counter = Integer.valueOf(param[0]);
        String[] p = null;
        if (param.length > 2) {
            p = new String[param.length - 2];
            System.arraycopy(param, 2, p, 0, param.length - 2);
        }
        for (int i = 1; i <= counter; ++i) {
            final int cookie = SCDP.beginGroup("Counter " + i);
            try {
                final Class<?> cls = Class.forName(param[1]);
                final TestSuiteEntry ts = (TestSuiteEntry)cls.newInstance();
                if (!ts.ExcTest(p)) {
                    SCDP.reportError("Test result is false");
                    return false;
                }
            }
            catch (ClassNotFoundException e) {
                SCDP.reportError("test case error:" + param[1] + " does not found!");
                e.printStackTrace();
                return false;
            }
            catch (ExceptionInInitializerError e2) {
                SCDP.reportError("test case init error");
                e2.printStackTrace();
                return false;
            }
            catch (Exception e3) {
                SCDP.reportError("test case name error:" + param[1] + " does not implements TestsuitEntry");
                e3.printStackTrace();
                return false;
            }
            SCDP.endGroup(cookie);
        }
        return true;
    }
}
