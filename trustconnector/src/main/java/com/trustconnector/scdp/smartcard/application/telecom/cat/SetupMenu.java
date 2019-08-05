package com.trustconnector.scdp.smartcard.application.telecom.cat;

import com.trustconnector.scdp.util.tlv.simpletlv.*;

import java.util.*;

public class SetupMenu extends ProactiveCommand
{
    protected AlphaIdentifier alpha;
    protected Vector<Item> items;

    public SetupMenu(byte[] cmd) {
        super(cmd);

        for(int index = this.cmdTLVList.find(new SimpleTag(15), 0); index != -1; index = this.cmdTLVList.find(new SimpleTag(15), index)) {
            Item a = (Item)this.cmdTLVList.get(index);
            if (!a.isEmpty()) {
                this.items.add(a);
            }

            ++index;
        }

    }
    
    public boolean isAlphaUCS2() {
        return this.alpha.isUCS2();
    }
    
    public String getAlphaText() {
        return this.alpha.getAlphaText();
    }
    
    public AlphaIdentifier getAlpha() {
        return this.alpha;
    }
    
    public Vector<Item> getMenuItems() {
        return this.items;
    }
    
    public byte getMenuIDByName(final String itemText) {
        for (final Item a : this.items) {
            final String text = a.getItemText();
            if (text.contentEquals(itemText)) {
                return a.getItemID();
            }
        }
        return 0;
    }
    
    public Item getItemByText(final String itemText) {
        for (final Item a : this.items) {
            final String text = a.getItemText();
            if (text.contentEquals(itemText)) {
                return a;
            }
        }
        return null;
    }
}
