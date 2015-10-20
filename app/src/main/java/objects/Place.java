package objects;

import android.location.Location;

import com.google.api.client.util.Key;
import java.io.Serializable;
import java.util.ArrayList;

public class Place implements Serializable {
    @Key
    public String id;
    @Key
    public String name;
    @Key
    public String reference;
    @Key
    public String icon;
    @Key
    public String vicinity;
    @Key
    public Geometry geometry;
    @Key
    public ArrayList<String> types;
    @Key
    public String formatted_address;
    @Key
    public String formatted_phone_number;
    @Override
    public String toString() {
        return name + " - " + id + " - " + reference;
    }
    public static class Geometry implements Serializable {
        @Key
        public Location location;
    }
    public static class Location implements Serializable {
        @Key
        public double lat;
        @Key
        public double lng;
    }

    public Place() {
        Location loc = new Location();
        this.geometry = new Geometry();
        this.geometry.location = loc;
        this.types = new ArrayList<String>();
    }
}