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
public class GetPlaces extends AsyncTask<String, Void, String> {

    public PlacesList placesList;

    @Override
    protected String doInBackground(String... placesURL) {
        //fetch places
        //build result as string
        StringBuilder placesBuilder = new StringBuilder();
        //process search parameter string(s)
        for (String placeSearchURL : placesURL) {
            try {
                //HTTP Get receives URL string
                URL requestUrl = new URL(placeSearchURL);
                HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection();
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
                        System.out.println("line: " + lineIn);
                        placesBuilder.append(lineIn);
                    }
                    try {
                        //create JSONObject, pass stinrg returned from doInBackground
                        JSONObject resultObject = new JSONObject(placesBuilder.toString());
                        //get "results" array
                        JSONArray placesArray = resultObject.getJSONArray("results");
                        //loop through places
                        placesList = new PlacesList();
                        for (int p=0; p<placesArray.length(); p++) {
                            //parse each place
                            //if any values are missing we won't show the marker
                            Place newPlace = new Place();
                            try {
                                //get place at this index
                                JSONObject placeObject = placesArray.getJSONObject(p);
                                //get location section - read lat lng
                                JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
                                newPlace.geometry.location.lat = (Double.valueOf(loc.getString("lat")));
                                newPlace.geometry.location.lng = (Double.valueOf(loc.getString("lng")));
                                //get types
                                JSONArray types = placeObject.getJSONArray("types");
                                //loop through types
                                for(int t=0; t<types.length(); t++){
                                    String thisType = types.get(t).toString();
                                    newPlace.types.add(thisType);
                                }
                                //name
                                newPlace.name = placeObject.getString("name");
                                this.placesList.addPlace(newPlace);
                            }
                            catch(JSONException jse){
                                Log.v("PLACES", "missing value");
                                jse.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("test", "Unsuccessful HTTP Response Code: " + responseCode);
                }
            } catch(IOException e) {
                Log.e("test", "Error connecting to Places API", e);
            }
        }
        return placesBuilder.toString();
    }
}
