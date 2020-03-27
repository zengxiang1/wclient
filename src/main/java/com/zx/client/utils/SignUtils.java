//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zx.client.utils;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;

public class SignUtils {
    public SignUtils() {
    }

    public static String getSign(JSONObject params, String key) {
        String needSignStr = compareParam(params);
        System.out.println("======>签名字符串<======: " + needSignStr);
        needSignStr = needSignStr + "&key=" + key;
        String result = DigestUtils.md5Hex(needSignStr);
        System.out.println("======>签名结果<======:" + result);
        return result;
    }

    public static String compareParam(JSONObject param) {
        StringBuffer result = new StringBuffer();
        if (param == null) {
            return result.toString();
        } else {
            Set<String> keySet = param.keySet();
            List<String> keys = new ArrayList(keySet);
            keys.sort(Comparator.naturalOrder());
            if (keys.size() > 0) {
                keys.forEach((key) -> {
                    result.append(key + "=" + param.get(key));
                    result.append("&");
                });
                result.replace(result.length() - 1, result.length(), "");
            }

            return result.toString();
        }
    }
}
