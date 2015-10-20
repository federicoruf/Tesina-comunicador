package com.example.federico.myapplication;

//import com.google.api.translate.Language;
//import com.google.api.translate.Translate;
import com.google.api.GoogleAPI;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import objects.Category;
import objects.GetPlaces;
import objects.Place;
import objects.PlacesList;
import android.view.View.OnKeyListener;

//import com.google.api.client.http.json.JsonHttpParser;

// ESTA LA TENGO import com.google.api.client.json.jackson.JacksonFactory;


import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import im.delight.android.webrequest.WebRequest;
import im.delight.android.webrequest.WebRequest.Callback;
import objects.GPSTraker;
import objects.ProvisionalContainer;

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
        if (this.tracker.getLocation() == null) {
           Toast.makeText(StartActivity.this, "Por favor habilite el GPS", Toast.LENGTH_LONG).show();
        }
        this.placesList = new PlacesList();

        this.translation = new HashMap<String, String>();

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
                ArrayList<Category> categories = ProvisionalContainer.businessEnglishNames(placeToSearch);

                //cierra el teclado en pantalla(al arracar la app tiene el foco en el campo de la busqueda)
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                //listener para los resultados de la busqueda y abre una nueva activity
                if (categories.size()>0) {
                    if (tracker.getLocation() != null) {
                        try {
                            placeToSearch = "";
                            for (Category c : categories) {
                                placeToSearch = placeToSearch.concat(c.getEnglishName() + "|");
                            }
                            System.out.println("plasdsdad: " + placeToSearch);
                            placeToSearch = placeToSearch.substring(0, (placeToSearch.length() - 1));

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
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String item = parent.getAdapter().getItem(position).toString();
                                Place place = placesList.getPlace(item);
                                Category cat = ProvisionalContainer.businessNamesByPlace(place);
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                intent.putExtra("Place", cat.getName());
                                startActivity(intent);
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
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                intent.putExtra("Place", item);
                                startActivity(intent);
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
