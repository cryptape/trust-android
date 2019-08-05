package com.trustconnector.scdp;

import org.jdom2.*;
import java.util.*;

public final class Main
{
    private static final String suit = "testsuit";
    private static final String test = "test";
    
    private Main() {
    }
    
    public static void main(final String[] args) throws Exception {
        try {
            SCDP.init(args);
            final Element testSuit = SCDP.getTestSuiteRoot();
            if (testSuit == null) {
                System.out.println("project file format error!");
                return;
            }
            final String disable = testSuit.getAttributeValue("disable");
            if (disable != null && disable.compareToIgnoreCase("true") == 0) {
                return;
            }
            final List<Element> listTestEntry = (List<Element>)testSuit.getChildren();
            final Iterator<Element> IteEntry = listTestEntry.iterator();
            while (IteEntry.hasNext()) {
                if (SCDP.isTerminated()) {
                    return;
                }
                final Element entry = IteEntry.next();
                final String nodeName = entry.getName();
                if (nodeName.compareToIgnoreCase("testsuit") == 0) {
                    onSuit(entry);
                }
                else {
                    if (nodeName.compareToIgnoreCase("test") != 0) {
                        continue;
                    }
                    onTest(entry);
                }
            }
            SCDP.OnEnd();
        }
        catch (Exception e) {
            e.printStackTrace();
            SCDP.reportError("Exception:" + e);
            SCDP.OnEnd();
            throw e;
        }
        catch (Error e2) {
            e2.printStackTrace();
            SCDP.reportError("Error:" + e2);
            SCDP.OnEnd();
            throw e2;
        }
    }
    
    private static void onSuit(final Element suitEntry) {
        if (SCDP.isTerminated()) {
            return;
        }
        final String disable = suitEntry.getAttributeValue("disable");
        if (disable != null && disable.compareToIgnoreCase("true") == 0) {
            return;
        }
        final String suitName = suitEntry.getAttributeValue("name");
        final int bCookie = SCDP.beginTestGroup(suitName);
        final List<Element> listTestEntry = (List<Element>)suitEntry.getChildren();
        for (final Element entry : listTestEntry) {
            final String nodeName = entry.getName();
            if (nodeName.compareToIgnoreCase("testsuit") == 0) {
                onSuit(entry);
            }
            else {
                if (nodeName.compareToIgnoreCase("test") != 0) {
                    continue;
                }
                onTest(entry);
            }
        }
        SCDP.endTestGroup(bCookie);
    }

    private static void onTest(final Element testEntry) {
        if (SCDP.isTerminated()) {
            return;
        }
        final String disable = testEntry.getAttributeValue("disable");
        if (disable != null && disable.compareToIgnoreCase("true") == 0) {
            return;
        }
        final String testName = testEntry.getAttributeValue("name");
        final String paramCfg = testEntry.getAttributeValue("param");
        String[] param = null;
        if (paramCfg != null && paramCfg.length() > 0) {
            final List<String> listParam = new ArrayList<String>();
            param = paramCfg.split(",");
            for (final String p : param) {
                if (p.startsWith("${") && p.endsWith("}")) {
                    final int pLen = p.length();
                    final String name = p.substring(2, pLen - 1);
                    final String value = SCDP.getParamValue(name);
                    if (value == null) {
                        listParam.add(p);
                    }
                    else if (value.indexOf(44) != -1) {
                        final String[] split;
                        final String[] newPs = split = value.split(",");
                        for (final String newP : split) {
                            listParam.add(newP);
                        }
                    }
                    else {
                        listParam.add(value);
                    }
                }
                else {
                    listParam.add(p);
                }
            }
            param = new String[listParam.size()];
            listParam.toArray(param);
        }
        final int bCookie = SCDP.beginTestCase(testName);
        String testClassName = testEntry.getAttributeValue("testsuite");
        if (testClassName == null || testClassName.length() == 0) {
            testClassName = testEntry.getText().trim();
        }
        else {
            testClassName.trim();
        }
        try {
            final Class<?> cls = Class.forName(testClassName);
            final TestSuiteEntry ts = (TestSuiteEntry)cls.newInstance();
            String msg = "";
            boolean bCondition;
            try {
                bCondition = ts.checkCondition();
            }
            catch (Exception e) {
                bCondition = false;
                msg = e.getMessage();
            }
            boolean bResult = false;
            try {
                if (bCondition) {
                    bResult = ts.ExcTest(param);
                }
                else {
                    if (msg.length() > 0) {
                        SCDP.reportWarning(msg);
                    }
                    SCDP.reportWarning("Test Case is not applicable!");
                }
            }
            catch (Exception e2) {
                ts.onException(e2);
                SCDP.reportError(e2.getClass().getName() + ":" + e2.getMessage());
                e2.printStackTrace();
                try {
                    ts.onEnd();
                }
                catch (Exception e22) {
                    SCDP.reportError(e22.getClass().getName() + ":" + e22.getMessage());
                    e2.printStackTrace();
                }
            }
            finally {
                try {
                    ts.onEnd();
                }
                catch (Exception e3) {
                    SCDP.reportError(e3.getClass().getName() + ":" + e3.getMessage());
                    e3.printStackTrace();
                }
            }
            if (bCondition && !bResult) {
                SCDP.reportError("Test result is false");
            }
        }
        catch (ClassNotFoundException e4) {
            SCDP.reportError("test case error:" + testClassName + " does not found!");
            e4.printStackTrace();
        }
        catch (ExceptionInInitializerError e5) {
            SCDP.reportError("test case init error");
            e5.printStackTrace();
        }
        catch (Exception e6) {
            SCDP.reportError("test case name error:" + testClassName + " does not implements TestsuitEntry");
            e6.printStackTrace();
        }
        SCDP.endTestCase(bCookie);
    }
}
