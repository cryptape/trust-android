package com.trustconnector.scdp.util;

import java.util.*;
import org.jdom2.*;

public final class XMLUtil
{
    public static final String BOOL_TURE = "true";
    public static final String BOOL_FALSE = "false";
    
    public static int getNodeAttrInt(final Element node, final String attrName) {
        final String v = node.getAttributeValue(attrName);
        if (v == null) {
            throw new DataFormatException("attr not found:" + attrName);
        }
        if (v.startsWith("0x")) {
            return Integer.valueOf(v.substring(2), 16);
        }
        return Integer.valueOf(v);
    }
    
    public static void setNodeAttrInt(final Element node, final String attrName, final int v) {
        node.setAttribute(attrName, String.valueOf(v));
    }
    
    public static int getNodeAttrHex(final Element node, final String attrName) {
        String v = node.getAttributeValue(attrName);
        if (v.startsWith("0x")) {
            v = v.substring(2);
        }
        return Integer.valueOf(v, 16);
    }
    
    public static void setNodeAttrHex(final Element node, final String attrName, final int v, final boolean add0x) {
        final String vS = Util.intToString(v, add0x);
        node.setAttribute(attrName, vS);
    }
    
    public static void setNodeAttrHex(final Element node, final String attrName, final int v, final int expLen, final boolean add0x) {
        final byte[] t = Util.intToBytes(v, expLen);
        final String vS = ByteArray.convert(t);
        node.setAttribute(attrName, vS);
    }
    
    public static void setNodeAttrHex1(final Element node, final String attrName, final int v, final boolean add0x) {
        setNodeAttrHex(node, attrName, v, 1, add0x);
    }
    
    public static void setNodeAttrHex2(final Element node, final String attrName, final int v, final boolean add0x) {
        setNodeAttrHex(node, attrName, v, 2, add0x);
    }
    
    public static void setNodeAttrHex3(final Element node, final String attrName, final int v, final boolean add0x) {
        setNodeAttrHex(node, attrName, v, 3, add0x);
    }
    
    public static void setNodeAttrHex4(final Element node, final String attrName, final int v, final boolean add0x) {
        setNodeAttrHex(node, attrName, v, 4, add0x);
    }
    
    public static boolean getNodeAttrBool(final Element node, final String attrName) {
        final String v = node.getAttributeValue(attrName);
        return v != null && v.compareToIgnoreCase("true") == 0;
    }
    
    public static void setNodeAttrBool(final Element node, final String attrName, final boolean bV) {
        if (bV) {
            node.setAttribute(attrName, "true");
        }
        else {
            node.setAttribute(attrName, "false");
        }
    }
    
    public static byte[] getNodeAttrBytes(final Element node, final String attrName) {
        final String vs = node.getAttributeValue(attrName);
        return ByteArray.convert(vs);
    }
    
    public static int getNodeAttrBytes(final Element node, final String attrName, final byte[] buf, final int offset) {
        final String vs = node.getAttributeValue(attrName);
        if (vs == null || vs.length() == 0) {
            return 0;
        }
        final byte[] a = ByteArray.convert(vs);
        System.arraycopy(a, 0, buf, offset, a.length);
        return a.length;
    }
    
    public static void setNodeAttrBytes(final Element node, final String attrName, final byte[] bV) {
        final String vs = ByteArray.convert(bV);
        node.setAttribute(attrName, vs);
    }
    
    public static Element findNodeWithAttrValue(final Element root, final String attrName, final String strValue) {
        final List<Element> infos = (List<Element>)root.getChildren();
        final Iterator<Element> it = infos.iterator();
        String value = null;
        Element info = null;
        boolean isFind = false;
        while (it.hasNext()) {
            info = it.next();
            final Attribute attr = info.getAttribute(attrName);
            if (attr != null) {
                value = attr.getValue();
                if (value != null && value.contentEquals(strValue)) {
                    isFind = true;
                    break;
                }
                continue;
            }
        }
        if (!isFind) {
            return null;
        }
        return info;
    }
}
