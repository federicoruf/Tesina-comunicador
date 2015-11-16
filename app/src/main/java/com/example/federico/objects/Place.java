package com.example.federico.objects;

import com.google.api.client.util.Key;
import java.io.Serializable;
import java.util.ArrayList;

public class Place {

    private String id;
    private String name;
    private Geometry geometry;
    private ArrayList<String> types;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Geometry implements Serializable {
        public Location location;
    }
    public static class Location implements Serializable {
        public double lat;
        public double lng;
    }

    public Place() {
        Location loc = new Location();
        this.geometry = new Geometry();
        this.geometry.location = loc;
        this.types = new ArrayList<String>();
    }
}