//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.trustconnector.scdp.smartcard.application.telecom.cat;

import com.trustconnector.scdp.util.tlv.simpletlv.AlphaIdentifier;
import com.trustconnector.scdp.util.tlv.simpletlv.Item;
import com.trustconnector.scdp.util.tlv.simpletlv.SimpleTag;
import java.util.Iterator;
import java.util.Vector;

public class SelectItem extends ProactiveCommand {
    protected AlphaIdentifier alpha = (AlphaIdentifier)this.findTLV((byte)5);
    protected Vector<Item> items = new Vector();

    public SelectItem(byte[] cmd) {
        super(cmd);

        for(int index = this.cmdTLVList.find(new SimpleTag(15), 0); index != -1; index = this.cmdTLVList.find(new SimpleTag(15), index)) {
            Item a = (Item)this.cmdTLVList.get(index);
            if (!a.isEmpty()) {
                this.items.add(a);
            }

            ++index;
        }

    }

    public Vector<Item> getItems() {
        return this.items;
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

    public byte getItemIDByName(String itemText) {
        Iterator ite = this.items.iterator();

        Item a;
        String text;
        do {
            if (!ite.hasNext()) {
                return 0;
            }

            a = (Item)ite.next();
            text = a.getItemText();
        } while(!text.contentEquals(itemText));

        return a.getItemID();
    }

    public Item getItemByText(String itemText) {
        Iterator ite = this.items.iterator();

        Item a;
        String text;
        do {
            if (!ite.hasNext()) {
                return null;
            }

            a = (Item)ite.next();
            text = a.getItemText();
        } while(!text.contentEquals(itemText));

        return a;
    }
}
