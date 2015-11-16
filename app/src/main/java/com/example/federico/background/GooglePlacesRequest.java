package com.example.federico.background;

import com.example.federico.objects.Place;
import com.example.federico.objects.PlacesList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by federico on 20/10/2015.
 */
public class GooglePlacesRequest extends UrlRequest {

    private PlacesList placesList;

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
                newPlace.getGeometry().location.lat = (Double.valueOf(loc.getString("lat")));
                newPlace.getGeometry().location.lng = (Double.valueOf(loc.getString("lng")));
                JSONArray types = placeObject.getJSONArray("types");
                for(int t=0; t<types.length(); t++){
                    String thisType = types.get(t).toString();
                    newPlace.getTypes().add(thisType);
                }
                newPlace.setName(placeObject.getString("name"));
                this.placesList.addPlace(newPlace);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return String.valueOf(this.placesList);
    }

    public PlacesList getPlacesList() {
        return placesList;
    }

    public void setPlacesList(PlacesList placesList) {
        this.placesList = placesList;
    }

}
