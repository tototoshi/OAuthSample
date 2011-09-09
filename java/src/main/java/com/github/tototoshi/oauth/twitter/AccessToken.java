package com.github.tototoshi.oauth.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class AccessToken {
    private static String accessTokenURL;
    private static String consumerKey;
    private static String consumerSecret;
    private static String oauthToken;
    private static String oauthTokenSecret;
    private static String PIN;

    public static void main(String[] args) {

        Properties prop = new Properties();

        try{
            prop.load(new FileInputStream(new File("oauth.properties")));
        } catch (IOException e){
            e.printStackTrace();
        }
            
        accessTokenURL = "http://twitter.com/oauth/access_token";
        consumerKey = prop.getProperty("oauth.consumer.key");
        consumerSecret = prop.getProperty("oauth.consumer.secret");
        oauthToken = prop.getProperty("oauth.token");
        oauthTokenSecret = prop.getProperty("oauth.token.secret");
        PIN = prop.getProperty("oauth.pin");

        String signatureMethod = "HMAC-SHA1";
        String oAuthVersion = "1.0";
        String oauthTimeStamp = OAuthUtil.getTimeStamp();
        String oauthNonce = OAuthUtil.getNonce();

        String requestParameters = 
            "oauth_consumer_key=" + consumerKey + "&" +
            "oauth_nonce=" + OAuthUtil.URLEncode(oauthNonce) + "&" +
            "oauth_signature_method=" + signatureMethod + "&" +
            "oauth_timestamp=" + oauthTimeStamp + "&" +
            "oauth_token=" + oauthToken + "&" +
            "oauth_version=" + oAuthVersion
            ;

        String signatureBaseString = "POST&" + OAuthUtil.URLEncode(accessTokenURL)
            + "&" + OAuthUtil.URLEncode(requestParameters); 
        String keyString = consumerSecret + "&" + oauthTokenSecret;
        String request = accessTokenURL + "?" + requestParameters + "&oauth_verifier=" 
            + PIN + "&oauth_signature=" + OAuthUtil.getSignature(signatureBaseString, keyString);

        HttpPost httpPost = new HttpPost(request);
        httpPost.addHeader("Authorization", "OAuth");

        System.out.println(request);
        HttpClient httpClient = new DefaultHttpClient();
        BufferedReader br = null;
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(entity != null){
                InputStream in = entity.getContent();


                br = new BufferedReader(new InputStreamReader(in));
                String line = br.readLine();
                System.out.println(response.getStatusLine());
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

}
