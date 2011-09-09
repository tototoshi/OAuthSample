package com.github.tototoshi.oauth.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class TestPost {

    private static String updateURL;
    private static String consumerKey;
    private static String consumerSecret;
    private static String oauthToken;
    private static String oauthTokenSecret;

    public static void main(String[] args) {

        Properties prop = new Properties();

        try{
            prop.load(new FileInputStream(new File("oauth.properties")));
        } catch (IOException e){
            e.printStackTrace();
        }

        updateURL = "http://twitter.com/statuses/update.xml";
        consumerKey = prop.getProperty("oauth.consumer.key");
        consumerSecret = prop.getProperty("oauth.consumer.secret");
        oauthToken = prop.getProperty("oauth.access.token");
        oauthTokenSecret = prop.getProperty("oauth.access.token.secret");

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
            "oauth_version=" + oAuthVersion + "&" +
            "status=" + OAuthUtil.URLEncode("てすと。")
            ;

        System.out.println(requestParameters);

        String signatureBaseString = "POST&" + OAuthUtil.URLEncode(updateURL) +
            "&" + OAuthUtil.URLEncode(requestParameters);
        String keyString = consumerSecret + "&" + oauthTokenSecret;
        String request = updateURL + "?" + requestParameters + "&oauth_signature="
            + OAuthUtil.getSignature(signatureBaseString, keyString);

        HttpPost httpPost = new HttpPost(request);
        httpPost.addHeader("Authorization", "OAuth");

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
