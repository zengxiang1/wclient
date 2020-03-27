//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zx.client.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
    private int timeout = 20000;
    private int waitForBackgroundJavaScript = 20000;
    private Map<String, String> cookieMap = new HashMap();
    private String charset = "UTF-8";
    private static HttpUtils httpUtils;

    private HttpUtils() {
    }

    public static HttpUtils getInstance() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
        }

        return httpUtils;
    }

    public void invalidCookieMap() {
        this.cookieMap.clear();
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getWaitForBackgroundJavaScript() {
        return this.waitForBackgroundJavaScript;
    }

    public void setWaitForBackgroundJavaScript(int waitForBackgroundJavaScript) {
        this.waitForBackgroundJavaScript = waitForBackgroundJavaScript;
    }

    public String executeGet(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Cookie", this.convertCookieMapToString(this.cookieMap));
        httpGet.setConfig(RequestConfig.custom().setSocketTimeout(this.timeout).setConnectTimeout(this.timeout).build());
        CloseableHttpClient httpClient = null;
        String str = "";

        try {
            httpClient = HttpClientBuilder.create().build();
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            this.getCookiesFromCookieStore(context.getCookieStore(), this.cookieMap);
            int state = response.getStatusLine().getStatusCode();
            if (state == 404) {
                str = "";
            }

            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    str = EntityUtils.toString(entity, this.charset);
                }
            } finally {
                response.close();
            }
        } catch (IOException var23) {
            throw var23;
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException var21) {
                throw var21;
            }

        }

        return str;
    }

    public String executeGetWithSSL(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Cookie", this.convertCookieMapToString(this.cookieMap));
        httpGet.setConfig(RequestConfig.custom().setSocketTimeout(this.timeout).setConnectTimeout(this.timeout).build());
        CloseableHttpClient httpClient = null;
        String str = "";

        try {
            httpClient = createSSLInsecureClient();
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            this.getCookiesFromCookieStore(context.getCookieStore(), this.cookieMap);
            int state = response.getStatusLine().getStatusCode();
            if (state == 404) {
                str = "";
            }

            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    str = EntityUtils.toString(entity, this.charset);
                }
            } finally {
                response.close();
            }
        } catch (IOException var25) {
            throw var25;
        } catch (GeneralSecurityException var26) {
            throw var26;
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException var23) {
                throw var23;
            }

        }

        return str;
    }

    public String executePost(String url, Map<String, String> params) throws Exception {
        String reStr = "";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(RequestConfig.custom().setSocketTimeout(this.timeout).setConnectTimeout(this.timeout).build());
        httpPost.setHeader("Cookie", this.convertCookieMapToString(this.cookieMap));
        List<NameValuePair> paramsRe = new ArrayList();
        Iterator var6 = params.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, String> entry = (Entry)var6.next();
            paramsRe.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
        }

        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(paramsRe));
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpResponse response = httpclient.execute(httpPost, context);
            this.getCookiesFromCookieStore(context.getCookieStore(), this.cookieMap);
            HttpEntity entity = response.getEntity();
            reStr = EntityUtils.toString(entity, this.charset);
        } catch (IOException var13) {
            throw var13;
        } finally {
            httpPost.releaseConnection();
        }

        return reStr;
    }

    public String executePostWithSSL(String url, Map<String, String> params) throws Exception {
        String re = "";
        HttpPost post = new HttpPost(url);
        List<NameValuePair> paramsRe = new ArrayList();
        Iterator var6 = params.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, String> entry = (Entry)var6.next();
            paramsRe.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
        }

        post.setHeader("Cookie", this.convertCookieMapToString(this.cookieMap));
        post.setConfig(RequestConfig.custom().setSocketTimeout(this.timeout).setConnectTimeout(this.timeout).build());

        try {
            CloseableHttpClient httpClientRe = createSSLInsecureClient();
            HttpClientContext contextRe = HttpClientContext.create();
            post.setEntity(new UrlEncodedFormEntity(paramsRe));
            CloseableHttpResponse response = httpClientRe.execute(post, contextRe);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                re = EntityUtils.toString(entity, this.charset);
            }

            this.getCookiesFromCookieStore(contextRe.getCookieStore(), this.cookieMap);
            return re;
        } catch (Exception var10) {
            throw var10;
        }
    }

    public String executePostWithJson(String url, String jsonBody) throws Exception {
        String reStr = "";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(RequestConfig.custom().setSocketTimeout(this.timeout).setConnectTimeout(this.timeout).build());
        httpPost.setHeader("Cookie", this.convertCookieMapToString(this.cookieMap));
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        try {
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpResponse response = httpclient.execute(httpPost, context);
            this.getCookiesFromCookieStore(context.getCookieStore(), this.cookieMap);
            HttpEntity entity = response.getEntity();
            reStr = EntityUtils.toString(entity, this.charset);
        } catch (IOException var12) {
            throw var12;
        } finally {
            httpPost.releaseConnection();
        }

        return reStr;
    }

    public String executePostWithJsonAndSSL(String url, String jsonBody) throws Exception {
        String re = "";
        HttpPost post = new HttpPost(url);
        post.setHeader("Cookie", this.convertCookieMapToString(this.cookieMap));
        post.setConfig(RequestConfig.custom().setSocketTimeout(this.timeout).setConnectTimeout(this.timeout).build());

        try {
            CloseableHttpClient httpClientRe = createSSLInsecureClient();
            HttpClientContext contextRe = HttpClientContext.create();
            post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = httpClientRe.execute(post, contextRe);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                re = EntityUtils.toString(entity, this.charset);
            }

            this.getCookiesFromCookieStore(contextRe.getCookieStore(), this.cookieMap);
            return re;
        } catch (Exception var9) {
            throw var9;
        }
    }

    private void getCookiesFromCookieStore(CookieStore cookieStore, Map<String, String> cookieMap) {
        List<Cookie> cookies = cookieStore.getCookies();
        Iterator var4 = cookies.iterator();

        while(var4.hasNext()) {
            Cookie cookie = (Cookie)var4.next();
            cookieMap.put(cookie.getName(), cookie.getValue());
        }

    }

    private String convertCookieMapToString(Map<String, String> map) {
        String cookie = "";

        Entry entry;
        for(Iterator var3 = map.entrySet().iterator(); var3.hasNext(); cookie = cookie + (String)entry.getKey() + "=" + (String)entry.getValue() + "; ") {
            entry = (Entry)var3.next();
        }

        if (map.size() > 0) {
            cookie = cookie.substring(0, cookie.length() - 2);
        }

        return cookie;
    }

    private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        try {
            SSLContext sslContext = (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, (chain, authType) -> {
                return true;
            }).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, (s, sslContextL) -> {
                return true;
            });
            return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        } catch (GeneralSecurityException var2) {
            throw var2;
        }
    }
}
