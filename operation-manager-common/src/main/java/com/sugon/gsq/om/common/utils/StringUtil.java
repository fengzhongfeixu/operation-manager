package com.sugon.gsq.om.common.utils;

import java.util.UUID;

public class StringUtil {
    public static boolean isEmpty(String str) {
        if (str != null) {
            str = str.trim();
        }
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static String getUiid(){
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
    }
}
