package com.example.federico.background;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by federico on 06/11/2015.
 */
public class UrlRequest extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... url) {
        StringBuilder placesBuilder = new StringBuilder();
        for (String smallUrl : url) {
            try {
                URL requestUrl = new URL(smallUrl);
                HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = null;
                    InputStream inputStream = connection.getInputStream();
                    if (inputStream == null) {
                        return "";
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String lineIn;
                    while ((lineIn = reader.readLine()) != null) {
                        placesBuilder.append(lineIn);
                    }
                    System.out.println(placesBuilder);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }  catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return placesBuilder.toString();
    }
}
