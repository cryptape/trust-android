package libs.trustconnector.scdp.smartcard.application.telecom.checkrule.cat;

import java.util.*;
import libs.trustconnector.scdp.smartcard.application.telecom.cat.*;
import libs.trustconnector.scdp.*;
import libs.trustconnector.scdp.util.tlv.simpletlv.*;

import libs.trustconnector.scdp.SCDP;
import libs.trustconnector.scdp.smartcard.application.telecom.cat.SelectItem;
import libs.trustconnector.scdp.util.tlv.simpletlv.Item;

public class SelectItemChecker extends ProactiveCommandChecker
{
    protected Vector<String> expItemText;
    protected Vector<Integer> expItemUSC2;
    protected Vector<Integer> expItemID;
    protected String retAlpha;
    protected String expAlpha;
    protected boolean expAlphaUCS2;
    protected int expMask;
    protected static final int EXP_MASK_ALPHA_TEXT = 1;
    protected static final int EXP_MASK_ALPHA_DCS = 2;
    protected static final int EXP_MASK_ITEM = 4;
    protected static final int EXP_MASK_ITEM_EXSIT = 8;
    protected static final int INVALID_EXP = -1;
    
    public SelectItemChecker() {
        super(36, -127, -126, "00|01|02|03|04|05|06|07|80|81|82|83|84|85|86|87");
        this.expItemText = new Vector<String>();
        this.expItemUSC2 = new Vector<Integer>();
        this.expItemID = new Vector<Integer>();
    }
    
    public SelectItemChecker(final String alpha) {
        super(36, -127, -126, "00|01|02|03|04|05|06|07|80|81|82|83|84|85|86|87");
        this.expItemText = new Vector<String>();
        this.expItemUSC2 = new Vector<Integer>();
        this.expItemID = new Vector<Integer>();
        this.setMatchAlpha(alpha);
    }
    
    public SelectItemChecker(final String alpha, final boolean bAlpahTextUCS2) {
        super(36, -127, -126, "00|01|02|03|04|05|06|07|80|81|82|83|84|85|86|87");
        this.expItemText = new Vector<String>();
        this.expItemUSC2 = new Vector<Integer>();
        this.expItemID = new Vector<Integer>();
        this.setMatchAlpha(alpha, bAlpahTextUCS2);
    }
    
    public SelectItemChecker(final String alpha, final boolean bAlpahTextUCS2, final int Qualifier) {
        super(36, -127, -126, Qualifier);
        this.expItemText = new Vector<String>();
        this.expItemUSC2 = new Vector<Integer>();
        this.expItemID = new Vector<Integer>();
        this.setMatchAlpha(alpha, bAlpahTextUCS2);
    }
    
    @Override
    public boolean check() {
        boolean bCheckRes = true;
        final SelectItem selectItem = (SelectItem)this.command;
        if ((this.expMask & 0x2) == 0x2) {
            final boolean ret = selectItem.isAlphaUCS2();
            if (ret != this.expAlphaUCS2) {
                bCheckRes = false;
                SCDP.reportAPDUExpErr("Alpha DCS Check Failed, UCS2 Ret=" + ret + ",UCS2 exp=" + this.expAlphaUCS2);
            }
            else {
                SCDP.addAPDUExpInfo("Alpha DCS USC2=" + ret);
            }
        }
        if ((this.expMask & 0x1) == 0x1) {
            this.retAlpha = selectItem.getAlphaText();
            if (!this.retAlpha.contentEquals(this.expAlpha)) {
                bCheckRes = false;
                SCDP.reportAPDUExpErr("Alpha Check Failed, UCS2 Ret=" + this.retAlpha + ",UCS2 exp=" + this.expAlpha);
            }
            else {
                SCDP.addAPDUExpInfo("Alpha =" + this.retAlpha);
            }
        }
        final Vector<Item> items = selectItem.getItems();
        if ((this.expMask & 0x4) == 0x4) {
            final int size = this.expItemText.size();
            if (items.size() != size) {
                bCheckRes = false;
            }
            else {
                for (int i = 0; i < size; ++i) {
                    final Item item = items.get(i);
                    if (!item.getItemText().contentEquals(this.expItemText.get(i))) {
                        bCheckRes = false;
                    }
                    int exp = this.expItemUSC2.get(i);
                    if (exp != -1) {
                        if (exp == 0) {
                            if (item.isUCS2()) {
                                bCheckRes = false;
                            }
                        }
                        else if (!item.isUCS2()) {
                            bCheckRes = false;
                        }
                    }
                    exp = this.expItemUSC2.get(i);
                    if (exp != -1 && exp != item.getItemID()) {
                        bCheckRes = false;
                    }
                }
            }
        }
        if ((this.expMask & 0x8) == 0x8) {
            for (int size = this.expItemText.size(), i = 0; i < size; ++i) {
                final Item item = selectItem.getItemByText(this.expItemText.get(i));
                if (item != null) {
                    int exp = this.expItemUSC2.get(i);
                    if (exp != -1) {
                        if (exp == 0) {
                            if (item.isUCS2()) {
                                bCheckRes = false;
                            }
                        }
                        else if (!item.isUCS2()) {
                            bCheckRes = false;
                        }
                    }
                    exp = this.expItemUSC2.get(i);
                    if (exp != -1 && exp != item.getItemID()) {
                        bCheckRes = false;
                    }
                }
                else {
                    bCheckRes = false;
                }
            }
        }
        return bCheckRes;
    }
    
    public void setMatchAlpha(final String alpha) {
        this.expAlpha = alpha;
        this.expMask |= 0x1;
    }
    
    public void setMatchAlpha(final String alpha, final boolean bUCS2) {
        this.expAlpha = alpha;
        this.expAlphaUCS2 = bUCS2;
        this.expMask |= 0x1;
        this.expMask |= 0x2;
    }
    
    public void setMatchItems(final String menuText, final char split) {
        final String[] menus = menuText.split("\\" + split);
        for (int c = menus.length, i = 0; i < c; ++i) {
            this.setMatchItem(menus[i], -1, -1);
        }
    }
    
    public void setMatchItem(final String menuItemText) {
        this.setMatchItem(menuItemText, -1, -1);
    }
    
    public void setMatchItem(final String menuItemText, final int menuID) {
        this.setMatchItem(menuItemText, -1, menuID);
    }
    
    public void setMatchItem(final String menuItemText, final boolean bUSC2, final int menuID) {
        this.setMatchItem(menuItemText, bUSC2 ? 0 : 1, menuID);
    }
    
    protected void setMatchItem(final String menuItemText, final int bUSC2, final int menuID) {
        this.expItemText.addElement(menuItemText);
        this.expItemID.addElement(menuID);
        this.expItemUSC2.addElement(bUSC2);
        this.expMask |= 0x4;
    }
    
    public void setMenuItemExsit(final String menuText, final char split) {
        final String[] menus = menuText.split("\\" + split);
        for (int c = menus.length, i = 0; i < c; ++i) {
            this.setItemExsit(menus[i], -1, -1);
        }
    }
    
    public void setItemExsit(final String menuItemText) {
        this.setItemExsit(menuItemText, -1, -1);
    }
    
    public void setItemExsit(final String menuItemText, final int menuID) {
        this.setItemExsit(menuItemText, -1, menuID);
    }
    
    public void setItemExsit(final String menuItemText, final boolean bUSC2, final int menuID) {
        this.setItemExsit(menuItemText, bUSC2 ? 0 : 1, menuID);
    }
    
    public void setItemExsit(final String menuItemText, final int bUSC2, final int menuID) {
        this.expItemText.addElement(menuItemText);
        this.expItemID.addElement(menuID);
        this.expItemUSC2.addElement(bUSC2);
        this.expMask |= 0x8;
    }
}
