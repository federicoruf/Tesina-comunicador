package com.example.federico.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.federico.myapplication.R;
import com.example.federico.sqlite.DatabaseAdapter;
import com.google.android.gms.common.GoogleApiAvailability;

import com.example.federico.objects.Category;
import com.example.federico.background.GooglePlacesRequest;
import com.example.federico.objects.Place;
import com.example.federico.objects.PlacesList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.example.federico.objects.GPSTraker;

public class StartActivity extends Activity{

    //Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    //Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    //Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    //key de la aplicación
    private static final String API_KEY = "AIzaSyAQcafXWJo0QxU46U16EbmWSogIYklYbRc";

    //hashmap que tiene las traducciones de los lugares buscados, para así puedo
    private ListView listView;
    private Button buttonSearchCategory;
    private Button buttonSearchPlaceName;
    private TextView textViewResultsSearch;
    private EditText inputSearchPlace;
    private Button buttonChatNow;
    //es para modificar el estado  en el menu según si el gps esta activado o no
    private Menu menu;

    private StartActivity context;
    private GPSTraker tracker;
    private PlacesList placesList;
    private DatabaseAdapter dbAdapter;
    private boolean wifiActive;

    private GooglePlacesRequest googlePlacesRequest;

    private void activateInternerConnection() {
        String[] options = new String[] {getResources().getString(R.string.activate_wifi)
                , getResources().getString(R.string.activate_movile_network)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.network_option_select));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //valor de retorno para cuando finaliza el intent y vuelve a la app original, que es lo q debe hacer?
                //onactivityresult creo q se llama el método
                int i = 1;
                switch (which) {
                    case 0:
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        context.startActivityForResult(intent, i);
                        context.onActivityResult(i, 2, intent);
                        if (getParent() == null) {
                            setResult(Activity.RESULT_OK);
                        } else {
                            getParent().setResult(Activity.RESULT_OK);
                        }
                        break;
                    case 1:
                        intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                        context.startActivityForResult(intent, i);
                        context.onActivityResult(i, 2, intent);
                        break;
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void isWifiActive(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        this.wifiActive = mWifi.isConnected();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = StartActivity.this;
        this.tracker = new GPSTraker(this.context);
        this.isWifiActive();
        if (!this.wifiActive) {
            System.out.println("wifi descativado!!");
            this.activateInternerConnection();
        } else {
            this.startApplicationElements();
        }
    }

    private void startApplicationElements(){
        setContentView(R.layout.activity_start);

        this.placesList = new PlacesList();

        //creación del comunicador con la base de datos
        this.dbAdapter = new DatabaseAdapter(context);

        // input del texto a buscar
        this.inputSearchPlace = (EditText) findViewById(R.id.editTextSearchPlace);

        //texto de resultados de la busqueda
        this.textViewResultsSearch = (TextView) findViewById(R.id.textViewResultsSearch);

        //lista de los resultados encontrados
        try {
            this.setListView();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //botón de busqueda por CATEGORÍA
        this.setButtonSearchCategory();


        //Botón para realizar la búsqueda a partir de NOMBRE DEL LUGAR
        this.setButtonSearchPlaceName();

        //Botón para hablar ahora
        this.setButtonChatNow();

        //chequea si esta halitado el gps y actualiza el cehckbox según como este.
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void setButtonChatNow() {
        this.buttonChatNow = (Button) findViewById(R.id.buttonChatNow);
        this.buttonChatNow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatActivity("default");
            }
        });
    }

    private void setListView() throws SQLException {
        this.listView = (ListView) findViewById(R.id.listViewResultsSearch);
        if (tracker.getLocation() != null) {
            String concatTypes = concatTypes((ArrayList<Category>) dbAdapter.getAllCategories());
            this.setResultSearchWithGPSByCategory(concatTypes);
        }
    }

    private void setResultSearchWithGPSByCategory(String placeToSearch) throws SQLException {
        String TempSQL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + tracker.getLatitude() + "," + tracker.getLongitude()
                + "&types=" + placeToSearch
                + "&rankby=distance"
                + "&key=" + API_KEY;
        System.out.println(TempSQL);
        try {
            this.googlePlacesRequest = new GooglePlacesRequest();
            this.googlePlacesRequest.execute(TempSQL);
            //esto hace que la app espere a que termine el proceso en background así se carga la variable placesList
            this.googlePlacesRequest.get();
            this.placesList = this.googlePlacesRequest.getPlacesList();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.textViewResultsSearch.setText("Resultados de la busqueda: " + this.placesList.getResults().size());
        createListByPlacesNames();
    }

    //cierra el teclado en pantalla(al arracar la app tiene el foco en el campo de la busqueda)
    private void closeKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setButtonSearchPlaceName() {
        this.buttonSearchPlaceName = (Button) findViewById(R.id.buttonSearchPlaceName);
        this.buttonSearchPlaceName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //PREGUNTAR SI ESTA HABILITADO EL GPS, PARA ASÍ BUSCA POR LUGAR
                //ES NECESARIO CONOCER SU UBICACIÓN PARA OBTENER LOS NOMBRE DE LUGARES CERCA DE DONDE SE ENCUENTRA, DESEA HABILITAR EL GPS?
                if (tracker.getLocation() != null) {
                    //toma el valor ingresado en el input
                    String placeToSearch = deleteWhiteSpace(String.valueOf(inputSearchPlace.getText()));
                    closeKeyboard();
                    try {
                        String TempSQL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                                + "location=" + tracker.getLatitude() + "," + tracker.getLongitude()
                                + "&name=" + placeToSearch
                                + "&rankby=distance"
                                + "&key=" + API_KEY;
                        System.out.println(TempSQL);
                        googlePlacesRequest = new GooglePlacesRequest();
                        AsyncTask<String, Void, String> execute1 = googlePlacesRequest.execute(TempSQL);
                        //esto hace que la app espere a que termine el proceso en background así se carga la variable placesList
                        googlePlacesRequest.get();
                        placesList = googlePlacesRequest.getPlacesList();
                        textViewResultsSearch.setText("Resultados de la busqueda: " + placesList.getResults().size());
                        if (placesList.getResults().size() != 0) {
                            createListByPlacesNames();
                        } else {
                            textViewResultsSearch.setText("Resultados de la busqueda: 0");
                            listView.setAdapter(null);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(StartActivity.this, "Debe tener habilitado el GPS para realizar la busqueda por lugar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("result code " + resultCode);
        if(resultCode == 0) {
            //reinicia el tracker para que tome la ubicación correctamente
            this.tracker = new GPSTraker(this.context);
            System.out.println("enabled: " + this.tracker.isCanGetLocation());
            //si es null es xq entro devido a q no hay señal de internet
            if (this.dbAdapter != null) {
                if (this.tracker.isCanGetLocation()) {
                      try {
                        System.out.println("lat: " + this.tracker.getLatitude() + "long: " + this.tracker.getLongitude());
                        String concatTypes = concatTypes((ArrayList<Category>) this.dbAdapter.getAllCategories());
                        setResultSearchWithGPSByCategory(concatTypes);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                   this.emptyResultSearch();
                    try {
                        ArrayList<Category> categories = dbAdapter.getAllCategoriesThatMatchWith("");
                        this.loadCategoriesInrResultList(categories);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                this.setActionBarGpsStatus();
            } else {
                this.startApplicationElements();
                this.isWifiActive();
                MenuBuilder builder = new MenuBuilder(this);
                this.onCreateOptionsMenu(builder);
            }
        }
    }

    private void emptyResultSearch() {
        this.textViewResultsSearch.setText("Resultados de la busqueda: 0");
        this.listView.setAdapter(null);
    }

    public String deleteWhiteSpace(String placeToSearch){

            if (placeToSearch.length() != 0 && Character.isWhitespace(placeToSearch.charAt(placeToSearch.length() - 1))) {
                System.out.println("tiene espacio en blanco");
                return placeToSearch.trim();
            }

        return placeToSearch;
    }

    private void setButtonSearchCategory() {
        this.buttonSearchCategory = (Button) findViewById(R.id.buttonSearchPlace);
        this.buttonSearchCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //toma el valor ingresado en el input
                String placeToSearch = deleteWhiteSpace(String.valueOf(inputSearchPlace.getText()));
                //ArrayList<Category> categories = ProvisionalContainer.businessEnglishNames(placeToSearch);
                ArrayList<Category> categories = null;
                try {
                    categories = dbAdapter.getAllCategoriesThatMatchWith(placeToSearch);
                    closeKeyboard();
                    //a partir del string buscado, este puede coincidir con varias categorias, si
                    // da como resultado 1 o +, entonces las concateno para realizar la busqueda con
                    //google places.
                    if (categories.size() > 0) {
                        //pregunta si el gps esta activado, si lo esta usa google places para mostrar los lugares
                        //si no esta, usa directamente las categorias almacenadas
                        if (tracker.getLocation() != null) {
                            placeToSearch = concatTypes(categories);
                            setResultSearchWithGPSByCategory(placeToSearch);
                        } else {
                            loadCategoriesInrResultList(categories);
                            /*ArrayList<String> catNames = new ArrayList<String>();
                            for (Category c : categories) {
                                catNames.add(c.getName());
                            }
                            textViewResultsSearch.setText("Resultados de la busqueda: " + catNames.size());
                            ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, catNames);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String item = parent.getAdapter().getItem(position).toString();
                                    startChatActivity(item);
                                }
                            });*/
                        }
                    } else {
                        emptyResultSearch();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadCategoriesInrResultList(ArrayList<Category> categories){
        ArrayList<String> catNames = new ArrayList<String>();
        for (Category c : categories) {
            catNames.add(c.getName());
        }
        textViewResultsSearch.setText("Resultados de la busqueda: " + catNames.size());
        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, catNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getAdapter().getItem(position).toString();
                startChatActivity(item);
            }
        });
    }

    //genera la vista de los resultado del tipo "nombre del lugar"
    private void createListByPlacesNames(){
        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, getPlacesName());
        this.listView.setAdapter(adapter);
        //listener para los elementos encontrados
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getAdapter().getItem(position).toString();
                try {
                    //obtiene de la lista de lugares el que fue seleccionado
                    //obtiene el objeto category correspondiente a uno de los tipos del place
                    Category cat = dbAdapter.getCategoryLikeFromPlace(placesList.getPlace(item));
                    startChatActivity(cat.getName());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
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

    private void startChatActivity(String category){
        Intent intent = new Intent(getBaseContext(), ChatActivity.class);
        intent.putExtra("categorySpanish", category);
        startActivity(intent);
    }

    private ArrayList<String> getPlacesName() {
        ArrayList<String> namesPlaces = new ArrayList<String>();
        for(String key: this.placesList.getResults().keySet()){
            namesPlaces.add(key);
        }
        return namesPlaces;
    }

    private void setActionBarGpsStatus() {
        if (!this.tracker.isCanGetLocation()) {
            this.menu.getItem(1).setTitle(getResources().getString(R.string.activate_GPS));
        } else {
            this.menu.getItem(1).setTitle(getResources().getString(R.string.deactivate_GPS));
        }
    }

    //setea el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("SETEA EL MENU");
        //if (wifiActive) {
            this.menu = menu;
            getMenuInflater().inflate(R.menu.menu_start, menu);
            this.setActionBarGpsStatus();
            return true;
        //}
       // return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_download:
                Intent intent =  new Intent(getBaseContext(), TabsCategoriesActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.action_activate_GPS:
                int i = 0;
                intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivityForResult(intent, i);
                context.onActivityResult(i, 0, intent);
                return true;
            case R.id.action_create_category:
                intent = new Intent(getBaseContext(), CreateCategoryActivity.class);
                context.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
}
