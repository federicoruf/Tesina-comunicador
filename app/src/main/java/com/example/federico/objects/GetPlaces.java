package com.example.federico.objects;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by federico on 20/10/2015.
 */
public class GetPlaces extends UrlRequest {

    public PlacesList placesList;

    @Override
    protected String doInBackground(String... placesURL) {
        return this.generateJSON(super.doInBackground(placesURL));
    }

    public String generateJSON(String stringJson) {
        JSONObject resultObject = null;
        try {
            resultObject = new JSONObject(stringJson);
            JSONArray placesArray = resultObject.getJSONArray("results");
            placesList = new PlacesList();
            for (int p=0; p<placesArray.length(); p++) {
                Place newPlace = new Place();
                JSONObject placeObject = placesArray.getJSONObject(p);
                JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
                newPlace.geometry.location.lat = (Double.valueOf(loc.getString("lat")));
                newPlace.geometry.location.lng = (Double.valueOf(loc.getString("lng")));
                JSONArray types = placeObject.getJSONArray("types");
                for(int t=0; t<types.length(); t++){
                    String thisType = types.get(t).toString();
                    newPlace.types.add(thisType);
                }
                newPlace.name = placeObject.getString("name");
                this.placesList.addPlace(newPlace);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return String.valueOf(this.placesList);
    }
}
