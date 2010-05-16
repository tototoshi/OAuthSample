package com.toshi.twitter.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.RandomStringUtils;

import org.apache.xerces.impl.dv.util.Base64;

public class OAuthUtil {
    public static String getTimeStamp(){
        return Long.toString(System.currentTimeMillis());
    }

    public static String getSignature(String signatureBaseString, String keyString){
        String signature = null;
        String algorithm = "HmacSHA1";
        try {
            Mac mac = Mac.getInstance(algorithm);
            Key key= new SecretKeySpec(keyString.getBytes(), algorithm);

            mac.init(key);
            byte[] digest = mac.doFinal(signatureBaseString.getBytes());
            signature = URLEncode(Base64.encode(digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return signature;
    }

    public static String URLEncode(String beforeEncode) {
        String afterEncode;
        try {
            beforeEncode = beforeEncode.replace(" ", "&nbsp;");
            afterEncode = URLEncoder.encode(beforeEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return afterEncode;
    }

    public static String getNonce(){
        return RandomStringUtils.randomAscii(8);
    }

}
