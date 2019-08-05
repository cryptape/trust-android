//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.trustconnector.scdp.util.tlv.simpletlv;

import com.trustconnector.scdp.util.ByteArray;
import com.trustconnector.scdp.util.tlv.Length;
import com.trustconnector.scdp.util.tlv.TLV;
import com.trustconnector.scdp.util.tlv.Tag;
import com.trustconnector.scdp.util.tlv.TagList;
import com.trustconnector.scdp.util.tlv.bertlv.BERLength;
import com.trustconnector.scdp.util.tlv.bertlv.BERTag;

public class SimpleTLVBuilder {
    public SimpleTLVBuilder() {
    }

    public static TLV buildTLV(byte tag, byte[] value) {
        int length = 0;
        if (value != null) {
            length = value.length;
        }

        return buildTLV(tag, value, 0, length);
    }

    public static TLV buildTLV(byte tag, byte[] value, int offset, int length) {
        SimpleTag tagT = new SimpleTag(tag);
        Length len = new BERLength(length);
        return buildTLV(tagT, len, value, offset);
    }

    public static TLV buildTLV(SimpleTag tag, Length len, byte[] v, int vOff) {
        TLV tlv = null;
        if (tag.length() == 1) {
            byte[] tlvs = tag.toBytes();
            switch(tlvs[0] & 127) {
                case 1:
                    tlv = new CommandDetails(tag, len, v, vOff);
                    break;
                case 2:
                    tlv = new DeviceIdentities(tag, len, v, vOff);
                    break;
                case 3:
                    tlv = new Result(tag, len, v, vOff);
                    break;
                case 4:
                    tlv = new Duration(tag, len, v, vOff);
                    break;
                case 5:
                    tlv = new AlphaIdentifier(tag, len, v, vOff);
                    break;
                case 6:
                    tlv = new Address(tag, len, v, vOff);
                    break;
                case 7:
                    tlv = new CapbilityCfgParam(tag, len, v, vOff);
                    break;
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 18:
                case 22:
                case 23:
                case 24:
                case 26:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                default:
                    tlv = new SimpleTLV(tag, len, v, vOff);
                    break;
                case 11:
                    tlv = new TPDU(tag, len, v, vOff);
                    break;
                case 13:
                    tlv = new TextString(tag, len, v, vOff);
                    break;
                case 15:
                    tlv = new Item(tag, len, v, vOff);
                    break;
                case 16:
                    tlv = new ItemIdentifier(tag, len, v, vOff);
                    break;
                case 17:
                    tlv = new ResponseLength(tag, len, v, vOff);
                    break;
                case 19:
                    tlv = new LocationInfo(tag, len, v, vOff);
                    break;
                case 20:
                    tlv = new IMEI(tag, len, v, vOff);
                    break;
                case 21:
                    tlv = new HelpRequest(tag, len, v, vOff);
                    break;
                case 25:
                    tlv = new EventList(tag, len, v, vOff);
                    break;
                case 27:
                    tlv = new LocationStatus(tag, len, v, vOff);
                    break;
                case 28:
                    tlv = new TransactionIdentifier(tag, len, v, vOff);
                    break;
                case 36:
                    tlv = new TimerIdentifier(tag, len, v, vOff);
                    break;
                case 37:
                    tlv = new TimerValue(tag, len, v, vOff);
                    break;
                case 45:
                    tlv = new Language(tag, len, v, vOff);
                case 51:
                    break;
                case 52:
                    tlv = new BrowserTerminationCause(tag, len, v, vOff);
                    break;
                case 53:
                    tlv = new BearerDescription(tag, len, v, vOff);
                    break;
                case 54:
                    tlv = new ChannelData(tag, len, v, vOff);
                    break;
                case 55:
                    tlv = new ChannelDataLength(tag, len, v, vOff);
                    break;
                case 56:
                    tlv = new ChannelStatus(tag, len, v, vOff);
                    break;
                case 57:
                    tlv = new BufferSize(tag, len, v, vOff);
                    break;
                case 63:
                    tlv = new AccessTechnology(tag, len, v, vOff);
                    break;
                case 64:
                    tlv = new DisplayParameters(tag, len, v, vOff);
                    break;
                case 65:
                    tlv = new ServiceRecord(tag, len, v, vOff);
                    break;
                case 72:
                    tlv = new TPDU_CDMA(tag, len, v, vOff);
                    break;
                case 100:
                    tlv = new BrowsingStatus(tag, len, v, vOff);
                    break;
                case 101:
                    tlv = new NetworkSearchMode(tag, len, v, vOff);
            }
        } else {
            tlv = new SimpleTLV(tag, len, v, vOff);
        }

        return (TLV)tlv;
    }

    public static TagList buildTagList(byte[] tags, int offset, int tagsLen) {
        TagList list;
        int tagLenN;
        for(list = new TagList(); tagsLen > 0; offset += tagLenN) {
            Tag tag = new SimpleTag();
            tagLenN = tag.fromBytes(tags, offset, tagsLen);
            if (tagLenN == -1) {
                break;
            }

            list.add(tag);
            tagsLen -= tagLenN;
        }

        return list;
    }

    public static TagList buildTagList(String tagList) {
        byte[] ts = ByteArray.convert(tagList);
        return buildTagList(ts, 0, ts.length);
    }

    public static TagList buildBERTagList(int tag) {
        TagList list = new TagList();
        list.add(new BERTag(-48));
        list.add(new SimpleTag(tag));
        return list;
    }
}
