//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zx.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zx.client.param.CreateParam;
import com.zx.client.response.WalletAddress;
import com.zx.client.utils.HttpUtils;
import com.zx.client.utils.SignUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class Client {
    private static String url;
    private static String appId;
    private static String key;
    private static final String CREATE_PATH = "/create";

    public Client() {
    }

    public static void init(String _url, String _appId, String _key) {
        if (_url.lastIndexOf("/") == _url.length() - 1) {
            _url = _url.substring(0, _url.length() - 1);
        }

        url = _url;
        appId = _appId;
        key = _key;
    }

    public static WalletAddress createAddress(String userId, String addressType) {
        String targetUrl = url + "/" + addressType + "/create";
        CreateParam createParam = new CreateParam();
        createParam.setAppId(appId);
        createParam.setUserId(userId);
        createParam.setTime(System.currentTimeMillis() / 1000L);
        String sign = SignUtils.getSign(JSON.parseObject(JSON.toJSONString(createParam, new SerializerFeature[]{SerializerFeature.IgnoreNonFieldGetter})), key);
        createParam.setSign(sign);
        String res = "";

        try {
            res = HttpUtils.getInstance().executePostWithJson(targetUrl, JSON.toJSONString(createParam));
            JSONObject resObj = JSON.parseObject(res);
            if (resObj.getInteger("code") == 0) {
                return (WalletAddress)resObj.getObject("data", WalletAddress.class);
            } else {
                throw new RuntimeException("获取地址失败:" + resObj.getString("msg"));
            }
        } catch (Exception var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static boolean checkSign(JSONObject data) {
        Long time = data.getLong("time");
        Long gap = System.currentTimeMillis() / 1000L - time;
        gap = Math.abs(gap);
        if (gap > 120L) {
            throw new RuntimeException("时间错误");
        } else {
            String paramSign = data.getString("sign");
            data.remove("sign");
            String needSignStr = SignUtils.compareParam(data);
            needSignStr = needSignStr + "&key=" + key;
            System.out.println("最终验签参数:" + needSignStr);
            String sign = DigestUtils.md5Hex(needSignStr);
            System.out.println("最终签名:" + sign);
            return sign.equalsIgnoreCase(paramSign);
        }
    }
}
