package com.example.federico.myapplication;

//import com.google.api.translate.Language;
//import com.google.api.translate.Translate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.federico.sqlite.DatabaseAdapter;
import com.google.android.gms.common.GoogleApiAvailability;

import com.example.federico.objects.Category;
import com.example.federico.objects.GetPlaces;
import com.example.federico.objects.Place;
import com.example.federico.objects.PlacesList;

//import com.google.api.client.http.json.JsonHttpParser;

// ESTA LA TENGO import com.google.api.client.json.jackson.JacksonFactory;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.example.federico.objects.GPSTraker;
import com.example.federico.objects.ProvisionalContainer;

public class StartActivity extends Activity{


    private static final String WIKI = "Matecat";
    //Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    //Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    //Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    //key de la aplicación
    private static final String API_KEY = "AIzaSyAQcafXWJo0QxU46U16EbmWSogIYklYbRc";

    //hashmap que tiene las traducciones de los lugares buscados, para así puedo
    private HashMap<String, String> translation;
    private ArrayList<String> result3;
    private ListView listView;
    private CheckBox checkBoxGps;
    private StartActivity context;
    private GPSTraker tracker;
    private EditText inputSearchPlace;
    private PlacesList placesList;
    private DatabaseAdapter dbAdapter;
    private String finalTranslation;

    /*Creates a dialog for an error message*/
    private void showErrorDialog(int errorCode) {
        // create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        //lo configura como un dialog, pero usa un metodo desconocido "getSupportFragmentManager()"
        Toast.makeText(StartActivity.this, args.getString("DIALOG_ERROR"), Toast.LENGTH_LONG).show();
    }

    /*called from ErrorDialogFragment when the dialog is dismissed*/
    public void onDialogDismissed(){
        mResolvingError = false;
    }

    /*A fragment to display an error dialog*/
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment(){   }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((StartActivity) getActivity()).onDialogDismissed();
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        this.context = StartActivity.this;
        this.tracker = new GPSTraker(this.context);
        /*
        la app es capaz de funcionar ahora con gps, lo dejo el código xq tal vez puede sacar algo de aca.
        if (this.tracker.getLocation() == null) {
           Toast.makeText(StartActivity.this, "Por favor habilite el GPS", Toast.LENGTH_LONG).show();
        }
        */
        this.placesList = new PlacesList();

        this.translation = new HashMap<String, String>();

        //creación del comunicador con la base de datos
        this.dbAdapter = new DatabaseAdapter(context);

        //lista de los resultados encontrados
        this.listView = (ListView) findViewById(R.id.listViewResultsSearch);
        //checkbox para habilitar el gps
        this.checkBoxGps = (CheckBox) findViewById(R.id.checkBoxGps);
        // input del texto a buscar
        this.inputSearchPlace = (EditText) findViewById(R.id.editTextSearchPlace);
        //texto de resultados de la busqueda
        final TextView textViewResultsSearch = (TextView) findViewById(R.id.textViewResultsSearch);


        //chequea si esta halitado el gps y actualiza el cehckbox según como este.
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);;
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        checkBoxGps.setChecked(enabled);
        //botón del check
        Button buttonSearch = (Button) findViewById(R.id.buttonSearchPlace);
        buttonSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                /*location=latitud y long de la posición actual (obligatorio)
                rankby=orden de los elementos que son retornados
                types=el tipo de lugar buscado
                radious=radio en metros que se quiere buscar
                key=clave de la aplicación (obligatorio)
                name=nombre del lugar buscado
                 */

                //toma el valor ingresado en el input
                String placeToSearch = String.valueOf(inputSearchPlace.getText());
                //ArrayList<Category> categories = ProvisionalContainer.businessEnglishNames(placeToSearch);
                ArrayList<Category> categories = null;
                try {
                    categories = dbAdapter.getAllCategoriesThatMatchWith(placeToSearch);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //cierra el teclado en pantalla(al arracar la app tiene el foco en el campo de la busqueda)
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                //para q sirve este if?
                if (categories.size()>0) {
                    //pregunta si el gps esta activado, si lo esta usa google places para mostrar los lugares
                    //si no esta, usa directamente las categorias almacenadas
                    if (tracker.getLocation() != null) {
                        try {
                            placeToSearch = concatTypes(categories);


                            String TempSQL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                                    + "location=" + tracker.getLatitude() + "," + tracker.getLongitude()
                                    //ACA CONFIGURAS EL RADIO!, ESTA EN METROS, EN ESTE CASO ESTARÍA SETEADO 2000M
                                    //SI NO SE ESPECIFICA, EL MAX ES DE 50KM
                                    //+ "&radious=2000&types=hospital"
                                    + "&types=" + placeToSearch
                                    //ESTO ES Q ORDENE LOS LUGARES A MOSTRAR DE MENOS A MAS LEJOS ESTA DE MI UBICACIÓN ACTUAL
                                    + "&rankby=distance"
                                    + "&key=" + API_KEY;
                            System.out.println(TempSQL);
                            GetPlaces getPlaces = new GetPlaces();
                            AsyncTask<String, Void, String> execute1 = getPlaces.execute(TempSQL);
                            //esto hace que la app espere a que termine el proceso en background así se carga la variable placesList

                            getPlaces.get();
                            placesList = getPlaces.placesList;
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        textViewResultsSearch.setText("Resultados de la busqueda: " + placesList.results.size());
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
                    } else {
                        ArrayList<String> catNames = new ArrayList<String>();
                        for (Category c: categories) {
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
                } else {
                    textViewResultsSearch.setText("Resultados de la busqueda: 0");
                    listView.setAdapter(null);
                }
            }
        });

        //para activar/desactivar el gps
        this.checkBoxGps.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //reinicia el tracker para que tome la ubicación correctamente
                tracker = new GPSTraker(context);
                tracker.showSettingsAlert(checkBoxGps);
            }
        });

        Button buttonDownload = (Button) findViewById(R.id.buttonDownload);
        buttonDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.settings", "com.android.settings.LanguageSettings");
                startActivity(intent);

            }
        });
        //CÓDIGO DE PRUEBA PARA EL FUNCIONAMIENTO DE LA bd
        try {
            List<Category> allCategories = dbAdapter.getAllCategories();
            for (Category ca: allCategories) {
                System.out.println("nombre: " + ca.getName());
                System.out.println("nombre ingles: " + ca.getEnglishName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void startMainActivity(String category){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
