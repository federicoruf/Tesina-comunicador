package objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import com.google.api.client.util.Key;

public class PlacesList implements Serializable {
    @Key
    public String status;
    @Key
    public HashMap<String, Place> results;

    public void addPlace(Place place) {
        this.results.put(place.name, place);
    }
    public PlacesList() {
        this.results = new HashMap<String, Place>();
    }
    public Place getPlace(String name) {
        return this.results.get(name);
    }
}