package libs.trustconnector.scdp.testsuite;

import libs.trustconnector.scdp.*;

import libs.trustconnector.scdp.TestSuite;

public class GSMTest extends TestSuite
{
    @Override
    public boolean ExcTest(final String[] param) {
        try {
            if (param != null) {
                for (int i = 0; i < param.length; ++i) {
                    System.out.println("param" + i + ":" + param[i]);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
