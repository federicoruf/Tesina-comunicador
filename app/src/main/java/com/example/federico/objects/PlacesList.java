package com.example.federico.objects;

import java.io.Serializable;
import java.util.HashMap;

import com.google.api.client.util.Key;

public class PlacesList{
    private HashMap<String, Place> results;

    public void addPlace(Place place) {
        this.results.put(place.getName(), place);
    }
    public PlacesList() {
        this.results = new HashMap<String, Place>();
    }
    public Place getPlace(String name) {
        return this.results.get(name);
    }

    public HashMap<String, Place> getResults() {
        return results;
    }

    public void setResults(HashMap<String, Place> results) {
        this.results = results;
    }

}