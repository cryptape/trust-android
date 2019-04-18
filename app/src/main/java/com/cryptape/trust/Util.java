package com.cryptape.trust;

/**
 * util functions for BTT
 * @author Caesar
 *
 */
public class Util {

	/**
	 * transmit a Hex-byte to Hex-character in upper case
	 * example: (0x0a - 'A')
	 * @param   b			Hex-byte(0x00-0x0F)
	 * @return  char		Hex-character: '0' - '9', 'A' - 'F'
	 */
	public static char byteToHexChar(byte b) {
		if(b > 0x0F) {
			throw new Error("Not a Hex Byte: " + b);
		}

		if((b>=0) && (b<=9)) {
			return (char)(b+'0');
		}
		else {
			return (char)((b-0x0A)+'A');
		}
	}
	
	/**
	 * transmit a Hex-character to a Hex-byte
	 * example:('A' - 0x0a)
	 * @param 	c			Hex-character: '0' - '9', 'A' - 'F', 'a' - 'f'
	 * @return  byte		Hex-byte: 0x00 - 0x0F
	 */
	public static byte hexCharToByte(char c) {
		if((c>='a') && (c<='f')) {
			return (byte)(0x0A + (c-'a'));
		}
		
		if((c>='A') && (c<='F')) {
			return (byte)(0x0A + (c-'A'));
		}
		
		if((c>='0') && (c<='9')) {
			return (byte)(0x00 + (c-'0'));
		}
		
		throw new Error("unsupport charset: " + c);
		
	}
	
	/**
	 * check whether this char is hex char(0-9 a-f A-F)
	 * @param ch
	 * @return 
	 */
	public static boolean isHexChar(char ch){
		if( (ch>='0' && ch<='9') ||
			(ch>='a' && ch<='f') ||
			(ch>='A' && ch<='F') ){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * judge if a char is a separate char (' ', '\t', '\r', '\n')
	 * @param ch
	 * @return
	 */
	public static boolean isSeparateChar(char ch){
		if(ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n'){
			return true;
		}
		return false;
	}
	
	/**
	 * trans the Hex-byte in BCD mode to Dec value
	 * example (0x56 --> 0x38(56))
	 * @param hexByte: byte in BCD 
	 * @return
	 */
	public static byte hexToDec(byte hexByte) {
		
		int hi, lo;
		
		hi = ((hexByte >> 4) & 0x0F);
		lo = (hexByte & 0x0F);
		
		return (byte)(hi * 10 + lo);
	}
	
	/**
	 * trans the Dec value to Hex-byte in BCD mode 
	 * example (56 --> 0x56)
	 * @param decByte
	 * @return
	 */
	public static byte decToHex(short decByte) {
		
		int hi, lo;
		
		lo = (decByte % 10);
		hi = (decByte / 10);
		
		return (byte)((hi << 4) | lo);
	}
	
	/**
	 * transmit a hex-character string to byte array
	 * example:("1A02" - {0x1A,0x02}
	 * @param  str			Hex-string build by Hex-characters
	 * @return byte[]		Hex-byte array
	 */
	public static byte[] stringToBytes(String str) {
		if((str.length()%2)!=0) {
			throw new Error("Not a Hex String: " + str);
		}
		
		byte [] ret = new byte [str.length()/2];
		
		for(int i=0;i<ret.length;i++) {
			ret[i] = hexCharToByte(str.charAt(i*2));
			ret[i] = (byte)(ret[i]<<4);
			ret[i] |= hexCharToByte(str.charAt(i*2+1));
		}
		return ret;
	}
	
	/**
	 * delete all separate char in the String
	 * @param value
	 * @return
	 */
	public static String stringTrimSeparates(String value){
		StringBuilder builder = new StringBuilder();
		
		int iLen = value.length();
		for(int i=0; i<iLen; i++){
			char data = value.charAt(i);
			if(!Util.isSeparateChar(data)){
				builder.append(data);
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * reverse string with Hex mode
	 * example: "12FE8D" -- "8DFE12"
	 * @param src
	 * @return
	 */
	public static String stringReverse(String src) {
		if((src.length()%2)!=0) {
			return null;
		}
		StringBuffer dst = new StringBuffer();
		for(int i=0;i<src.length();i+=2) {
			if(!isHexChar(src.charAt(i)) || !isHexChar(src.charAt(i+1))) {
				return null;
			}
			dst.insert(0, src.substring(i, i+2));
		}
		return dst.toString();
	}
	
	/**
	 * transmit a hex-bytes array to a hex-charater string
	 * example:({0x1A,0x02} - "1A02"
	 * @param	bts			Hex-byte array  
	 * @return  String		Hex-string build by Hex-characters
	 * @throws Exception 	when input is not build by hex-bytes
	 */
	public static String bytesToString(byte[] bts) {
		return bytesToString(bts, 0, bts.length);
	}
	
	/**
	 * transmit a hex-bytes array to a hex-charater string
	 * example:({0x1A,0x02} - "1A02"
	 * @param bts
	 * @param off
	 * @param length
	 * @return
	 */
	public static String bytesToString(byte[] bts, int off, int length){
		StringBuffer str = new StringBuffer(length*2);
		for(int i=0;i<length;i++) {			
			str.append(byteToHexChar((byte)((bts[i+off]>>4)&0x0F)));
			str.append(byteToHexChar((byte)(bts[i+off]&0x0F)));
		}
		
		return str.toString();
	}
	
	/**
	 * make a int with bytes in big endian (max 4 bytes because int is 32-bit in java)
	 * example: {0x12, 0x34 , 0x56} - 0x00123456
	 * @param 	bts			bytes array
	 * @return	int			value
	 * @throws  Exception 	when input is null or error length.
	 */
	public static int bytesToInt(byte[] bts) throws Exception {
		
		return bytesToInt(bts,0,bts.length);
	}
	
	/**
	 * make a int with bytes in big endian (max 4 bytes because int is 32-bit in java)
	 * example: {0x12, 0x34 , 0x56} - 0x00123456
	 * @param  bts			bytes array
	 * @param  iOff			offset in bytes array
	 * @param  iExpLen		len in bytes array
	 * @return int			value
	 * @throws Exception 	when input is null or error length.
	 */
	public static int bytesToInt(byte[] bts, int iOff, int iExpLen)  {
	
		if(iExpLen > 4) {
			throw new Error("unsupport bytes array length");
		}
		
		int iTmp = 0;
		for(int i=0;i<iExpLen;i++) {
			iTmp = iTmp << 8;
			iTmp = iTmp | (bts[iOff++]&0x00FF);
		}
		
		return iTmp;
	}
	
	/**
	 * make a byte array with int in big endian
	 * example 0xFFFF1234 - {0xFF,0xFF,0x12,0x34}
	 * @param iValue	
	 * @param iExpLen
	 * @return
	 */
	public static byte[] intToBytes(int iValue, int iExpLen) {
		
		byte[] bts = new byte[iExpLen];
		
		intToBytes(iValue, iExpLen, bts, 0);
		
		return bts;
	}
	
	/**
	 * make a byte array with int in big endian
	 * example 0xFFFF1234 - {0xFF,0xFF,0x12,0x34}
	 * @param iValue		IN	value
	 * @param iExpLen		length of bytes
	 * @param buf			OUT byte array
	 * @param iOff			byte array off
	 * @return				length of bytes
	 */
	public static int intToBytes(int iValue, int iExpLen, byte[] buf, int iOff) {
		
		for(int iLen=iExpLen;iLen>0;iLen--) {
			int iTmp = iValue;
			for(int i=0;i<(iLen-1);i++) {
				iTmp = iTmp>>8;
			}
			buf[iOff++] = (byte)(iTmp & 0xFF);
		}
		return iExpLen;
	}
	
	/**
	 * make a short to bytes in big endian
	 * example 0x1234 - {0x12,0x34}
	 * @param sValue
	 * @param buf
	 * @param iOff
	 */
	public static void shortToBytes(short sValue, byte[] buf, int iOff) {
		buf[iOff++] = (byte) (sValue>>8);
		buf[iOff] = (byte) sValue;
	}
	
	/**
	 * make a bytes to short in big endian
	 * example {0x12,0x34} - 0x1234
	 * @param buf
	 * @param iOff
	 * @return
	 */
	public static short bytesToShort(byte[] buf, int iOff) {
		short sValue;
		
		sValue = (short)(buf[iOff++]&0x00FF);
		sValue <<= 8;
		sValue |= (short)(buf[iOff]&0x00FF);
		
		return sValue;
	}
	
	/**
	 * compare two bytes array part. 
	 * example: bytes {1,2,3,4} equal bytes{2,3,4} with offset1=1,offset2=0,len=3
	 * @param   buf1		array 1
	 * @param	buf2		array 2
	 * @param	offset1		the data offset of array 1
	 * @param	offset2		the data offset of array 2
	 * @param	len			the compare size
	 * @return  boolean		true if equal
	 */
	public static boolean bytesEqual(byte [] buf1, int offset1, byte [] buf2, int offset2, int len) {
		
		for(int i=0;i<len;i++) {
			if(buf1[i+offset1] != buf2[i+offset2]) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * judge the if byte array is build all by one value
	 * @param buf		byte array
	 * @param value		spec value
	 * @return			true if all spec value in byte array
	 */
	public static boolean isBytesFilled(byte [] buf, byte value) {
		
		for(int i=0;i<buf.length;i++) {
			if(buf[i]!=value) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * byte arrays add in Hex with carry
	 * @param augend
	 * @param augOff
	 * @param addend
	 * @param addOff
	 * @param out
	 * @param oOff
	 * @param sDataLen
	 */
	public static void bytesAddHex(byte[] augend, int augOff, byte[] addend, int addOff, byte[] out, int oOff, int sDataLen) {
		short sf, st;
		byte c = 0x00;
		
		int i = sDataLen-1;
		augOff += i;
		addOff += i;
		oOff += i;
		
		for(; i >= 0; i--) {
			
			sf = augend[augOff--];			
			st = addend[addOff--];
			st += c;
			
			c = 0x00;
			
			st += sf;
			
			if(st > 0xFF) {
				
				st -= 0x100;
				c = 0x01;
			}
			
			out[oOff--] = (byte) st;
		}
	}

	/**
	 * byte arrays add in DEC with carry
	 * EXP: {0x00,0x00,0x09,0x99,0x99} + {0x00,0x00,0x00,0x00,0x01} = {0x00,0x00,0x10,0x00,0x00}
	 * @param from
	 * @param fOff
	 * @param to
	 * @param tOff
	 * @param out
	 * @param oOff
	 * @throws UtilitiesException
	 */
	public static void bytesAddDec(byte[] augend, int augOff, byte[] addend, int addOff, byte[] out, int oOff) {
		
		short sf, st;
		byte c = 0x00;
		
		byte i = 0x05;
		augOff += i;
		addOff += i;
		oOff += i;
		
		for(; i >= 0; i--) {
			
			sf = hexToDec(augend[augOff--]);			
			st = hexToDec(addend[addOff--]);
			st += c;
			
			c = 0x00;
			
			st += sf;
			
			if(st > 99) {
				
				st -= 100;
				c = 0x01;
			}
			
			out[oOff--] = decToHex(st);
		}
	}
	
	/**
	 * byte arrays xor operation
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static byte[] bytesXor(byte[] data1, byte[] data2) {
		byte[] data = new byte[data1.length];
		
		for (int i=0; i<data.length; i++) {
			data[i] = (byte) (data1[i] ^ data2[i]);
		}
		
		return data;
	}
	
	
  /**
   * byte arrays not operation
   * @param abyData
   * @param nLength
   */
  public static void bytesNot(byte[] abyData, int nLength){
	 for(; nLength>0; nLength--)
	    {
		  abyData[nLength-1] = (byte)(~abyData[nLength-1]);
	    }
	}

	/**
	 * 
	 * @param iValue
	 * @param iExpLen
	 * @return
	 */
	public static String int2String(int iValue, int iExpLen) throws Exception{
		byte[] buf = new byte[iExpLen];
		Util.intToBytes(iValue, iExpLen, buf, 0);		
		return Util.bytesToString(buf);
	}
}