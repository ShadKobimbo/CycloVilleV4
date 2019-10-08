package com.example.cyclovillev4;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ImageUploadConnection {

    public static JSONObject jsonObject;
    public static String encodedImage;
    public static String status = "";

    static URL url = null;
    static HttpURLConnection connection;

    public ImageUploadConnection() {
        // Required empty public constructor
    }

    public static String makeUploadRequest(JSONObject imageDataParams){

        jsonObject = imageDataParams;
        encodedImage = jsonObject.toString();

        try {

            String data = jsonObject.toString();
            Log.e("JSON String Data", data);

            url = new URL("http://shiftadmin.tamshi.co.ke/cyclovilleserver/image_upload.php");
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setFixedLengthStreamingMode(data.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);

            Log.d("Vicky", "Data to php = " + data);

            writer.flush();
            writer.close();
            out.close();
            //connection.connect();

            int responseCode = connection.getResponseCode();
            Log.e("Vicky","Response Code = " + String.valueOf(responseCode));

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                try {

                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            in, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    in.close();

                    String result = sb.toString();

                    Log.d("Vicky", "Response from php = " + result);

                    if (result != null) {

                        JSONObject json_data = new JSONObject(result);

                        String codeString = json_data.getString("success");

                        Integer codeInt = Integer.parseInt(codeString);

                        Integer codeGood = 1;

                        if (codeInt == codeGood) {

                            String imageURL = json_data.getString("url");
                            Log.d("Vicky", "Image Url = " + imageURL);

                            status = imageURL;


                        } else {

                            status = null;
                        }
                    } else {

                        status = null;
                    }
                } catch (IOException e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

            } else {

                return null;
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (
                JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("Vicky", "Error Encountered");
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return status;
    }

}
