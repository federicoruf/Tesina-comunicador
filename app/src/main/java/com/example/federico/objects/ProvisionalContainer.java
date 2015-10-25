package com.example.federico.objects;

import java.util.ArrayList;

/**
 * Created by federico on 06/10/2015.
 */
public class ProvisionalContainer {

    private static final ArrayList<Category> categories;
    static{
        //DENTRO DEL ALCANCE
        //businesses = new ArrayList<>();
        categories = new ArrayList<>();
        Category c = new Category("cine");
        c.setEnglishName("cinema");
        c.addPhrase("la película es el 3D?");
        c.addPhrase("cuánto dura la película?");
        c.addPhrase("la película tiene subtitulos?");
        c.addPhrase("la película es apta para menores de edad?");
        c.addPhrase("cuánto cuesta?");
        c.addPhrase("quisiera comprar X entradas");
        c.addPhrase("a que hora es la próxima función?");
        c.addPhrase("quiero una entrada");
        categories.add(c);
        /*Business b = new Business("cinema 8", c);
        b.setLatitude(-35.570656);
        b.setLongitude(-58.006893);
        businesses.add(b);*/

        //FUERA DEL ALCANCE, MUY LEJOS DE MI CASA
        c = new Category("tinda de ropas");
        c.setEnglishName("clothes store");
        c.addPhrase("tendría un talle más chico?");
        c.addPhrase("tendría un talle más grande?");
        c.addPhrase("qué precio tiene X?");
        c.addPhrase("tiene remeras manga largas?");
        /*b = new Business("fenix", c);
        b.setLatitude(-35.572004);
        b.setLongitude(-58.010165);
        businesses.add(b);*/

        //FUERA DEL ALCANCE
        c = new Category("bar");
        c.setEnglishName("bar");
        c.addPhrase("podría traerme la cuenta?");
        c.addPhrase("quisiera un tostado");
        c.addPhrase("quisiera una medialuna");
        c.addPhrase("quisiera tomar un café");
        c.addPhrase("podría traerme edulcorante?");
        c.addPhrase("quisiera tomar un té");
        categories.add(c);
        /*b = new Business("unbar", c);
        b.setLatitude(-35.569562);
        b.setLongitude(-58.004825);
        businesses.add(b);*/

        //DENTRO DEL ALCANCE
        c = new Category("biblioteca");
        c.setEnglishName("library");
        c.addPhrase("quisiera obtener el carnet de socio");
        c.addPhrase("necesito una copia de ");
        c.addPhrase("quiero el libro ");
        c.addPhrase("quiero devolver este libro");
        categories.add(c);
        /*b = new Business("Faustino Sarmiento", c);
        b.setLatitude(-35.570416);
        b.setLongitude(-58.006903);
        businesses.add(b);*/

        c = new Category("hospital");
        c.setEnglishName("hospital");
        c.addPhrase("quisiera sacar un turno para el doctor");
        c.addPhrase("qué días atiende el doctor ?");
        c.addPhrase("el doctor  atiende por la obra social");
        categories.add(c);
        /*b = new Business("Hospital San Vicente de Paul", c);
        b.setLatitude(-35.5706316);
        b.setLongitude(-58.006903);
        businesses.add(b);*/

        c = new Category("alojamiento");
        c.setEnglishName("lodging");
        c.addPhrase("quisiera reservar una habitacion");
        c.addPhrase("incluye desayuno?");
        c.addPhrase("cuanto cuesta la noche?");
        categories.add(c);
    }

    //devuelve los lugares q coincidan con el string ingresado
    public final static ArrayList<String> businessNames(String match){
        ArrayList<String> finded = new ArrayList<String>();
        //Location location = tracker.getLocation();
        //System.out.println("lat: " + location.getLatitude() + " - long: " + location.getLongitude());

        for (Category b: categories) {
            if (b.getName().matches("(?i).*" + match + ".*")) {
                finded.add(b.getName());
                //float[] results = new float[1];
                //Location.distanceBetween(location.getLatitude(), location.getLongitude(), b.getLatitude(), b.getLongitude(), results);
                //float distanceInMeters = results[0];
                /*if (distanceInMeters < GPSTraker.MAX_DISTANCE_FROM_LOCATION) {
                    System.out.println("Esta dentro de alcance " + b.getName());
                    //finded.add(b.getName());
                } else {
                    System.out.println("Esta fuera de alcance " + b.getName());
                }*/
            }
        }
        return  finded;
    }

    public final static ArrayList<Category> businessEnglishNames(String match){
        ArrayList<Category> finded = new ArrayList<Category>();
        for (Category b: categories) {
            if (b.getName().matches("(?i).*" + match + ".*")) {
                finded.add(b);
            }
        }
        return  finded;
    }

    //devuelve la categoría y por ende sus frases
    public final static Category getPhrasesFrom(String place) {
        for (Category b: categories) {
            if(b.getName().equals(place)) {
                return b;
            }
        }
        return null;
    }

    public static Category businessNamesByPlace(Place place) {
        for (Category b : categories) {
            for (String type : place.types) {
                if (b.getEnglishName().equals(type)) {
                    return b;
                }
            }
        }
        return null;
    }
}
