package com.example.federico.background;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by federico on 06/11/2015.
 */
public class TranslatorRequest extends UrlRequest{

    private String finalTranslation;

    @Override
    protected String doInBackground(String... placesURL) {
         return this.generateJSON(super.doInBackground(placesURL));
    }

        public String generateJSON(String stringJson) {
        try {
            JSONObject resultObject = new JSONObject(stringJson);
            JSONArray matches = resultObject.getJSONArray("text");
            this.setFinalTranslation((String) matches.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.getFinalTranslation();
    }

    public String getFinalTranslation() {
        return finalTranslation;
    }

    public void setFinalTranslation(String finalTranslation) {
        this.finalTranslation = finalTranslation;
    }
}
