package com.github.tototoshi.oauth.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.xerces.impl.dv.util.Base64;

public class RequestToken {

    private static final String REQUEST_TOKEN_URL = "http://api.twitter.com/oauth/request_token";
    private static final String OAUTH_VERSION = "1.0";
    private static final String SIGNATURE_METHOD = "HMAC-SHA1";
    private static String consumerKey;
    private static String consumerSecret;
    private static String oauthToken;
    private static String oauthTokenSecret;

    public RequestToken(){
        oauthToken = null;
        oauthTokenSecret = null;
        request();
    }

    public static void main(String[] args) {
        Properties prop = new Properties();

        try{
            prop.load(new FileInputStream(new File("oauth.properties")));
        } catch (IOException e){
            e.printStackTrace();
        }

        consumerKey = prop.getProperty("oauth.consumer.key");
        consumerSecret = prop.getProperty("oauth.consumer.secret");

        RequestToken requestToken = new RequestToken();
    }

    private void request() {
        String requestParameters = getRequestParameters();

        HttpClient httpClient = new DefaultHttpClient();

        String signatureBaseString = getSignatureBaseString(requestParameters);
        String keyString = getKeyString();
        String signature = getSignature(signatureBaseString, keyString);

        String req = REQUEST_TOKEN_URL + "?" + requestParameters + "&oauth_signature=" + signature;
        HttpGet httpGet = new HttpGet(req);

        HttpResponse response;
        BufferedReader br = null;
        try {
            response = httpClient.execute(httpGet);

            HttpEntity entity = response.getEntity();
            if(entity != null){
                InputStream in = entity.getContent();

                br = new BufferedReader(new InputStreamReader(in));
                String line = br.readLine();
                System.out.println(line);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getSignatureBaseString(String requestParameters) {
        return "GET&" + OAuthUtil.URLEncode(REQUEST_TOKEN_URL) + "&" + OAuthUtil.URLEncode(requestParameters);
    }

    private String getRequestParameters() {
        return
            "oauth_consumer_key=" + consumerKey + "&" +
            "oauth_nonce=" + OAuthUtil.URLEncode(OAuthUtil.getNonce()) + "&" +
            "oauth_signature_method=" + SIGNATURE_METHOD + "&" +
            "oauth_timestamp=" + OAuthUtil.getTimeStamp() + "&" +
            "oauth_version=" + OAUTH_VERSION ;
    }

    private String getKeyString(){
        if(oauthToken == null){
            return consumerSecret + "&";
        }else{
            return consumerSecret + "&" + oauthToken;
        }
    }

    private String getSignature(String signatureBaseString, String keyString){
        String signature = null;
        String algorithm = "HmacSHA1";
        try {
            Mac mac = Mac.getInstance(algorithm);
            Key key= new SecretKeySpec(keyString.getBytes(), algorithm);

            mac.init(key);
            byte[] digest = mac.doFinal(signatureBaseString.getBytes());
            signature = OAuthUtil.URLEncode(Base64.encode(digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return signature;
    }
}
