//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zx.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zx.client.param.CreateParam;
import com.zx.client.param.SendParam;
import com.zx.client.response.WalletAddress;
import com.zx.client.utils.HttpUtils;
import com.zx.client.utils.SignUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class Client {
    private  String url;
    private  String appId;
    private  String key;
    private static final String CREATE_PATH = "/create";

    public Client() {
    }

    public Client(String _url, String _appId, String _key) {
        if (_url.lastIndexOf("/") == _url.length() - 1) {
            _url = _url.substring(0, _url.length() - 1);
        }

        url = _url;
        appId = _appId;
        key = _key;
    }

    public  WalletAddress createAddress(String userId, String addressType) {
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

    public  boolean checkSign(JSONObject data) {
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
    public  String send(String coin, String series,String addressType,  String address, BigDecimal amount) throws Exception {
        String targetUrl = url + "/send";
        targetUrl = MessageFormat.format(targetUrl, coin);
        SendParam sendParam = new SendParam();
        sendParam.setAppId(appId);
        sendParam.setTime(System.currentTimeMillis()/ 1000);
        sendParam.setAddress(address);
        sendParam.setAddressType(addressType);
        sendParam.setSeries(series);
        sendParam.setAmount(amount);
        sendParam.setCoin(coin);
        String sign = SignUtils.getSign(JSON.parseObject(JSON.toJSONString(sendParam, new SerializerFeature[]{SerializerFeature.IgnoreNonFieldGetter})), key);
        sendParam.setSign(sign);
        String res = "";
        res = HttpUtils.getInstance().executePostWithJson(targetUrl, JSON.toJSONString(sendParam));
        JSONObject resObj = JSON.parseObject(res);
        if (resObj.getInteger("code") == 0) {
            return resObj.getString("data");
        } else {
            throw new RuntimeException("发送失败:" + resObj.getString("msg"));
        }
    }
}
