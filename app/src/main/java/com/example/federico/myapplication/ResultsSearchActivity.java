package com.example.federico.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.federico.objects.Category;
import com.example.federico.objects.GPSTraker;
import com.example.federico.objects.GetPlaces;
import com.example.federico.objects.Place;
import com.example.federico.objects.PlacesList;
import com.example.federico.sqlite.DatabaseAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ResultsSearchActivity extends Activity {

    //key de la aplicación sordos
    private static final String API_KEY = "AIzaSyAQcafXWJo0QxU46U16EbmWSogIYklYbRc";
    //sordos2
    //private static final String API_KEY = "AIzaSyAnxgybZFXw4rXrAqUT8g3w4BYBcJm4SMI";

    private TextView textViewResultsSearch;
    private ListView listView;

    private DatabaseAdapter dbAdapter;
    private ResultsSearchActivity context;
    private PlacesList placesList;
    private GetPlaces getPlaces;
    private String valueToSearch;
    private String typeSearch;
    private Boolean GPSEnable;
    private double[] latLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_search);
        this.context = ResultsSearchActivity.this;
        this.placesList = new PlacesList();

        //recoge los 3 valores para hacer la busqueda
        Intent intent = getIntent();
        this.typeSearch = intent.getStringExtra(StartActivity.TYPE_SEARCH);
        //this.valueToSearch = intent.getExtras().getString("VALUE_TO_SEARCH");
        this.valueToSearch = intent.getStringExtra(StartActivity.VALUE_TO_SEARCH);
        //System.out.println("intent.getStringExtra(StartActivity.GPS_ENABLE): " + intent.getStringExtra(StartActivity.GPS_ENABLE));
        this.GPSEnable = intent.getExtras().getBoolean(StartActivity.GPS_ENABLE);
        this.latLong = intent.getDoubleArrayExtra(StartActivity.LAT_LONG);
        System.out.println("lat: " + this.latLong[0] + " long: " + this.latLong[1]);

        //creación del comunicador con la base de datos
        this.dbAdapter = new DatabaseAdapter(context);

        //texto de resultados de la busqueda
        this.textViewResultsSearch = (TextView) findViewById(R.id.textViewResultsSearch);

        //lista de los resultados encontrados
        try {
            this.setListView();
        } catch (InterruptedException | ExecutionException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void setListView() throws SQLException, ExecutionException, InterruptedException {
        this.listView = (ListView) findViewById(R.id.listViewResultsSearch);
        //que tipo de busqueda eligió para hacer?
        if (this.typeSearch.equals("categorySearch")) {
            ArrayList<Category> allCategoriesThatMatch = (ArrayList<Category>) dbAdapter.getAllCategoriesThatMatchWith(this.valueToSearch);
            closeKeyboard();
            if (allCategoriesThatMatch.size()>0) {
                //la buqueda es por categorías y con el GPS activado
                if (this.GPSEnable) {
                    String concatTypes = concatTypes(allCategoriesThatMatch);
                    this.setResultSearchWithGPSByCategory(concatTypes);
                } else {
                    this.setResutlsSearchWithoutGPSByCategory(allCategoriesThatMatch);
                }
            } else {
                this.textViewResultsSearch.setText("Resultados de la busqueda: 0");
                this.listView.setAdapter(null);
            }
        } else {
            if (this.GPSEnable) {
                this.setResutlsSearchByName(this.valueToSearch);
            }
        }
    }

    //busqueda por categorias con el gps activado
    private void setResultSearchWithGPSByCategory(String placeToSearch) throws SQLException, ExecutionException, InterruptedException {
        String TempSQL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + latLong[0] + "," + latLong[1]
                + "&types=" + placeToSearch
                + "&rankby=distance"
                + "&key=" + API_KEY;
        System.out.println(TempSQL);
        getPlaces = new GetPlaces();
        AsyncTask<String, Void, String> execute1 = getPlaces.execute(TempSQL);
        //esto hace que la app espere a que termine el proceso en background así se carga la variable placesList
        getPlaces.get();
        placesList = getPlaces.placesList;
        textViewResultsSearch.setText("Resultados de la busqueda: " + placesList.results.size());
        createListByPlacesNames();
    }

    //busqueda por categorías con el gps desactivado
    public void setResutlsSearchWithoutGPSByCategory(ArrayList<Category> allCategoriesThatMatch) {
        ArrayList<String> catNames = new ArrayList<String>();
        for (Category c: allCategoriesThatMatch) {
            catNames.add(c.getName());
        }
        textViewResultsSearch.setText("Resultados de la busqueda: " + catNames.size());
        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, catNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getAdapter().getItem(position).toString();
                startMainActivity(item);
            }
        });
    }

    public void setResutlsSearchByName(String placeToSearch) throws ExecutionException, InterruptedException {
        closeKeyboard();
        String TempSQL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + latLong[0] + "," + latLong[1]
                + "&name=" + placeToSearch
                + "&rankby=distance"
                + "&key=" + API_KEY;
        System.out.println(TempSQL);
        getPlaces = new GetPlaces();
        AsyncTask<String, Void, String> execute1 = getPlaces.execute(TempSQL);
        //esto hace que la app espere a que termine el proceso en background así se carga la variable placesList
        getPlaces.get();
        placesList = getPlaces.placesList;
        textViewResultsSearch.setText("Resultados de la busqueda: " + placesList.results.size());
        if (placesList.results.size() != 0) {
            createListByPlacesNames();
        } else {
            textViewResultsSearch.setText("Resultados de la busqueda: 0");
            listView.setAdapter(null);
        }
    }

    //concatena las categorias encontradas para ser usadas en el url de googleplace y devolver los lugares q coincidan
    private String concatTypes(ArrayList<Category> categories) {
        String placeToSearch = "";
        for (Category c : categories) {
            placeToSearch = placeToSearch.concat(c.getEnglishName() + "|");
        }
        placeToSearch = placeToSearch.replace(" ", "%20");
        placeToSearch = placeToSearch.substring(0, (placeToSearch.length() - 1));
        return placeToSearch;
    }

    //genera la vista de los resultado del tipo "nombre del lugar"
    private void createListByPlacesNames(){
        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, getPlacesName());
        listView.setAdapter(adapter);
        //listener para los elementos encontrados
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getAdapter().getItem(position).toString();
                //obtiene de la lista de lugares el que fue seleccionado
                Place place = placesList.getPlace(item);
                try {
                    //obtiene el objeto category correspondiente a uno de los tipos del place
                    Category cat = dbAdapter.getCategoryLikeFromPlace(place);
                    startMainActivity(cat.getName());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startMainActivity(String category){
        Intent intent = new Intent(getBaseContext(), StartActivity.class);
        intent.putExtra("categorySpanish", category);
        startActivity(intent);
    }


    private ArrayList<String> getPlacesName() {
        ArrayList<String> namesPlaces = new ArrayList<String>();
        for(String key: this.placesList.results.keySet()){
            namesPlaces.add(key);
        }
        return namesPlaces;
    }

    //cierra el teclado en pantalla(al arracar la app tiene el foco en el campo de la busqueda)
    private void closeKeyboard(){
        //InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
