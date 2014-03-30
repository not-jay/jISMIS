package com.xtouchme.http.client.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

//Thanks to MattC @ StackOverFlow
//http://stackoverflow.com/questions/2253061/secure-http-post-in-android
public class HttpRequest {

    DefaultHttpClient httpClient;
    HttpContext localContext;
    private String ret;

    HttpResponse response = null;
    HttpPost httpPost = null;
    HttpGet httpGet = null;

    public HttpRequest(){
        HttpParams myParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        HttpConnectionParams.setSoTimeout(myParams, 10000);
        httpClient = new DefaultHttpClient(myParams);
        localContext = new BasicHttpContext();
    }

    public void clearCookies() {
        httpClient.getCookieStore().clear();
    }

    public void abort() {
        try {
            if (httpClient != null) {
                System.out.println("Abort.");
                httpPost.abort();
            }
        } catch (Exception e) {
//            System.out.println("Your App Name Here" + e);
        }
    }

    public String sendPost(String url, String data) {
        return sendPost(url, data, null);
    }

//    public String sendJSONPost(String url, JSONObject data) {
//        return sendPost(url, data.toString(), "application/json");
//    }

    public String sendJSONPost(String url, String data) {
        ret = null;

        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

        httpPost = new HttpPost(url);
        response = null;

        StringEntity tmp = null;

        //Log.d("Your App Name Here", "Setting httpPost headers"); //TODO: uncomment on android

        /** User agents are what browsers send to servers/whatever that polls for them
         *  to detect what browser you are using. For our case, just a simple HTTP POST,
         *  something as trivial as this doesn't really matter. So I'll just set it to my
         *  favorite text-based, on-terminal browser, Lynx :P
         *  Read up about lynx on http://en.wikipedia.org/wiki/Lynx_%28web_browser%29 */
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
        httpPost.setHeader("User-Agent", "Lynx/2.8.8dev.3 libwww-FM/2.14 SSL-MM/1.4.1");
        httpPost.setHeader("Referer", "http://ismis.usc.edu.ph/Home/Student");
        httpPost.setHeader("Pragma", "no-cache");
        httpPost.setHeader("Host", "ismis.usc.edu.ph");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//      httpPost.setHeader("Content-Length", "14");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Cache-Control", "no-cache");
        httpPost.setHeader("Accept-Language", "en-US,en;q=0.5");
        httpPost.setHeader("Accept-Encoding", "gzip, deflate");
        httpPost.setHeader("Accept", "text/plain, */*; q=0.01");

        try {
            tmp = new StringEntity(data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            //Log.e("Your App Name Here", "HttpUtils : UnsupportedEncodingException : "+e); //TODO: uncomment on android
        }

        httpPost.setEntity(tmp);

        //Log.d("Your App Name Here", url + "?" + data); //TODO: android

        try {
            response = httpClient.execute(httpPost,localContext);

            if (response != null) {
                ret = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("Your App Name Here", "HttpUtils: " + e); //TODO: android
        }

        //Log.d("Your App Name Here", "Returning value:" + ret); //TODO: android

        return ret;
    }

    public String sendPost(String url, String data, String contentType) {
        ret = null;

        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

        httpPost = new HttpPost(url);
        response = null;

        StringEntity tmp = null;

        //Log.d("Your App Name Here", "Setting httpPost headers"); //TODO: uncomment on android

        /** User agents are what browsers send to servers/whatever that polls for them
         *  to detect what browser you are using. For our case, just a simple HTTP POST,
         *  something as trivial as this doesn't really matter. So I'll just set it to my
         *  favorite text-based, on-terminal browser, Lynx :P
         *  Read up about lynx on http://en.wikipedia.org/wiki/Lynx_%28web_browser%29 */
        httpPost.setHeader("User-Agent", "Lynx/2.8.8dev.3 libwww-FM/2.14 SSL-MM/1.4.1");
        httpPost.setHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");

        if (contentType != null) {
            httpPost.setHeader("Content-Type", contentType);
        } else {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        }

        try {
            tmp = new StringEntity(data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            //Log.e("Your App Name Here", "HttpUtils : UnsupportedEncodingException : "+e); //TODO: uncomment on android
        }

        httpPost.setEntity(tmp);

        //Log.d("Your App Name Here", url + "?" + data); //TODO: android

        try {
            response = httpClient.execute(httpPost,localContext);

            if (response != null) {
                ret = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            //Log.e("Your App Name Here", "HttpUtils: " + e); //TODO: android
        }

        //Log.d("Your App Name Here", "Returning value:" + ret); //TODO: android

        return ret;
    }

    public String sendGet(String url) {
        httpGet = new HttpGet(url);

        try {
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
            //Log.e("Your App Name Here", e.getMessage()); //TODO: android
        }

        //int status = response.getStatusLine().getStatusCode();

        // we assume that the response body contains the error message
        try {
            ret = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            //Log.e("Your App Name Here", e.getMessage()); //TODO: android
        }

        return ret;
    }

    public InputStream getHttpStream(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            response = httpConn.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception e) {
            throw new IOException("Error connecting");
        } // end try-catch

        return in;
    }

    public DefaultHttpClient getClient() {
        return httpClient;
    }
}
