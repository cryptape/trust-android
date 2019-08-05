package com.trustconnector.scdp.util;

public class DataFormatException extends RuntimeException
{
    private static final long serialVersionUID = -7069475250572665259L;
    public static final String DATA_FORMAT_ERROR = "Data Format Exception";
    
    public DataFormatException(final String message) {
        super(message);
    }
    
    public static void throwIt(final String message) {
        throw new DataFormatException(message);
    }
    
    public static void throwIt() {
        throw new DataFormatException("Data Format Exception");
    }
}
