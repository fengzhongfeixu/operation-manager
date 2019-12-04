package com.sugon.gsq.om.proxy.utils;

public class HexUtils {

    public static String printHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            Integer i = Integer.valueOf(b);
            if ((i&0xff) < 16) {
                sb.append("0" + Integer.toHexString(i & 0xff));
            }else{
                sb.append(Integer.toHexString(i & 0xff));
            }
            sb.append(" ");
        }
        return sb.toString();
    }

}
