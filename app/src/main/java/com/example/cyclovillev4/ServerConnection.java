package com.example.cyclovillev4;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.support.v4.content.ContextCompat.getSystemService;

public class ServerConnection {

    String email, passwrd;

    public static final int CONNECTION_TIMEOUT = 15000;
    public static final int READ_TIMEOUT = 15000;

    static String json = "";

    static HttpURLConnection conn;
    static URL url = null;

    public ServerConnection() {
        // Required empty public constructor
    }

    public static String makeHttpRequest(JSONObject postDataParams){

        try {

            Log.e("Connection Params",postDataParams.toString());
            String data = postDataParams.toString();

            url= new URL("http://shiftadmin.tamshi.co.ke/cyclovilleserver/index.php");

            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
            writer.write(encodeParams(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.e("Response Code",String.valueOf(responseCode));

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                Log.e("Code is HTTP_OK",String.valueOf(responseCode));

                try {
                    InputStream input = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader( new InputStreamReader(input, "iso-8859-1"), 8);
                    StringBuffer sb = new StringBuffer();
                    String line;

                    while ((line = reader.readLine()) != null) {

                        sb.append(line);
                        break;

                    }
                    reader.close();

                    json = sb.toString();
                    Log.e("JSON Data", json);

                    //conn.disconnect();
                    return json;

                } catch (IOException e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

            } else {

                return ("Connection Unsuccessful");
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return json;

    }

    private static String encodeParams(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        Log.e("String Params",result.toString());
        return result.toString();
    }

}
