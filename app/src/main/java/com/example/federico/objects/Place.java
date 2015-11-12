package com.example.federico.objects;

import com.google.api.client.util.Key;
import java.io.Serializable;
import java.util.ArrayList;

public class Place {

    public String id;

    public String name;

    public Geometry geometry;

    public ArrayList<String> types;

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