package com.example.federico.myapplication;

//import com.google.api.translate.Language;
//import com.google.api.translate.Translate;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.federico.objects.Phrase;
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
import java.util.Locale;

import com.example.federico.objects.GPSTraker;

public class StartActivity extends Activity{


    private static final String WIKI = "Matecat";
    //Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    //Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    //Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    //para cargar el valor del extra así sabe que tipo de busqueda debe hacer, las dejo publicas así las accede otra activity
    public static final String TYPE_SEARCH = "TYPE_SEARCH";
    public static final String VALUE_TO_SEARCH = "VALUE_TO_SEARCH";
    public static final String GPS_ENABLE = "GPS_ENABLE";
    public static final String LAT_LONG = "LAT_LONG";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    //hashmap que tiene las traducciones de los lugares buscados, para así puedo
    private Button buttonSearchCategory;
    private CheckBox checkBoxGps;
    private Button buttonSearchPlaceName;
    private EditText inputSearchPlace;
    private TextView textViewChat;
    private EditText editPhrase;
    private ImageButton setImgButtonSpeech;
    private ImageButton imageButton;
    private Button buttonSpeak;
    private TextToSpeech textToSpeech;
    private Button buttonChoosePhrase;
    private ScrollView scrollViewChat;


    private String finalTranslation;
    private GPSTraker tracker;
    private String categorySpanish;
    private HashMap<String, String> translation;
    private ArrayList<String> result3;
    private StartActivity context;
    private DatabaseAdapter dbAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        this.context = StartActivity.this;
        this.tracker = new GPSTraker(this.context);
        this.dbAdapter = new DatabaseAdapter(context);

        this.translation = new HashMap<String, String>();

        // input del texto a buscar
        this.inputSearchPlace = (EditText) findViewById(R.id.editTextSearchPlace);

        //botón de busqueda por CATEGORÍA
        this.setButtonSearchCategory();

        //checkbox para habilitar el gps
        this.setCheckBoxGPS();

        //Botón para realizar la búsqueda a partir de NOMBRE DEL LUGAR
        this.setButtonSearchPlaceName();

        //chequea si esta halitado el gps y actualiza el cehckbox según como este.
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);;
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        this.checkBoxGps.setChecked(enabled);

        //reconocedor de voz
        checkVoiceRecognitionIsPresent();

        this.textViewChat = (TextView) findViewById(R.id.textOutput);
        this.editPhrase = (EditText) findViewById(R.id.speakPhrase);

        //botón que abre el activitity con todas las frases de la categoría
        this.setButtonChoosePhrase();

        //objeto para poder escuchar la frase elegida
        this.setTextToSpeech();

        //botón para que el celular haga sonar la frase elegida
        this.setButtonSpeak();

        //botón para agregar frase a la categoría actualmente seleccionada
        this.setImageButton();

        //botón para el micrófono
        this.setImgButtonSpeech();

        Intent intent = getIntent();
        this.categorySpanish = intent.getStringExtra("categorySpanish");
        System.out.println("result 2, hizo ya la busqueda " + this.categorySpanish);

        ActionBar ab = null;
        if ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) && (this.categorySpanish !=  null)) {
            ab = getActionBar();
            ab.setTitle("Categoría elegida: " + this.categorySpanish);
        }
    }

    private void setImgButtonSpeech() {
        this.setImgButtonSpeech = (ImageButton) findViewById(R.id.imageButtonMicrophone);
        this.setImgButtonSpeech.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                // Specify the calling package to identify your application
                //CON ESTA LINEA PUEDO USAR SIN TENER CONEXIÓN A INTERNET, PERO Q PASA, PRIMERO TIENE Q HABER BAJADO AL CELULAR EL PAQUETE DE ESPAÑOL.
                //NO FUCIONA, NO ENTIENDO XQ.
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es");

                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
                // Display an hint to the user about what he should say.
                //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, metTextHint.getText().toString());
                // Given an hint to the recognizer about what the user is going to say
                //There are two form of language model available
                //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
                //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                // If number of Matches is not selected then return show toast message
                /*if (msTextMatches.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
                    Toast.makeText(this, "Please select No. of Matches from spinner", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                //int noOfMatches = Integer.parseInt(msTextMatches.getSelectedItem().toString());
                // Specify how many results you want to receive. The results will be
                // sorted where the first result is the one with higher confidence.
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                //Start the Voice recognizer activity for the result.
                startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
            }
        });
    }

    private void setImageButton() {
        this.imageButton = (ImageButton)findViewById(R.id.imageButtonPlus);
        this.imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //agrega la frase a la BD tomando en cuenta la categoría
                String phrase = editPhrase.getText().toString();
                try {
                    Category category = dbAdapter.getCategoryFromSpanishName(categorySpanish);
                    Phrase newPhrase = new Phrase(phrase, category.getId());
                    long newId = dbAdapter.addPhraseToCategory(Phrase.toContentValues(newPhrase));
                    if ( newId != -1) {
                        showToastMessage(getResources().getString(R.string.new_phrase));
                    } else {
                        showToastMessage(getResources().getString(R.string.phrase_repeated));
                    }
                    newPhrase.setId(newId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setButtonSpeak() {
        this.buttonSpeak = (Button) findViewById(R.id.buttonSpeak);
        this.buttonSpeak.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollViewChat = (ScrollView) findViewById(R.id.scrollViewChat);
                Editable phrase = editPhrase.getText();
                addText("yo: " + phrase);
                //esta deprecado, pero si agrego un null al final no lo estará, pero esa versión no esta disponible para APIs antiguas
                textToSpeech.speak(String.valueOf(phrase), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    private void setTextToSpeech() {
        this.textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    Locale locSpanish = new Locale("spa", "ARG");
                    textToSpeech.setLanguage(locSpanish);
                }
            }
        });
    }

    private void addText(String text) {
        this.textViewChat.invalidate();
        this.textViewChat.requestLayout();
        this.textViewChat.append(text + "\n");
    }

    private void setButtonChoosePhrase() {
        this.buttonChoosePhrase = (Button) findViewById(R.id.buttonChoosePhrase);
        this.buttonChoosePhrase.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (categorySpanish == null) {
                    categorySpanish = "default";
                }
                Intent i = new Intent(getBaseContext(), ListPhrasesActivity.class);
                i.putExtra("categorySpanish", categorySpanish);
                startActivityForResult(i, 1);
            }
        });
    }

    private void setCheckBoxGPS() {
        this.checkBoxGps = (CheckBox) findViewById(R.id.checkBoxGps);
        this.checkBoxGps.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivityForResult(intent, i);
                context.onActivityResult(i, 0, intent);
            }
        });
    }

    private void setButtonSearchPlaceName() {
        this.buttonSearchPlaceName = (Button) findViewById(R.id.buttonSearchPlaceName);
        buttonSearchPlaceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tracker.isCanGetLocation()){
                    createIntentSearch("nameSearch");
                } else {
                    Toast.makeText(context, "Debe tener habilitado el GPS para realizar la busqueda por lugar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setButtonSearchCategory() {
        this.buttonSearchCategory = (Button) findViewById(R.id.buttonSearchPlace);
        buttonSearchCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createIntentSearch("categorySearch");
            }
        });
    }

    private void checkVoiceRecognitionIsPresent() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            this.setImgButtonSpeech.setEnabled(false);
            this.showToastMessage(getResources().getString(R.string.no_voice_recognition));
        }
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void createIntentSearch(String typeSearch){
        Intent intent = new Intent(getBaseContext(), ResultsSearchActivity.class);
        intent.putExtra(TYPE_SEARCH, typeSearch);
        intent.putExtra(VALUE_TO_SEARCH, this.inputSearchPlace.getText().toString());
        System.out.println("gps enable: " + tracker.isCanGetLocation());
        intent.putExtra(GPS_ENABLE, tracker.isCanGetLocation());
        double[] latLog = {tracker.getLatitude(), tracker.getLongitude()};
        intent.putExtra(LAT_LONG, latLog);
        startActivityForResult(intent, 2);
        context.onActivityResult(2, 2, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("result code " + resultCode);
        if(resultCode == 0) {
            //reinicia el tracker para que tome la ubicación correctamente
            this.tracker = new GPSTraker(this.context);
            System.out.println("enabled: " + this.tracker.isCanGetLocation());
        }
        if (resultCode == 2) {
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String val = data.getStringExtra("Phrase");
                this.editPhrase.setText(val);
            }
        }
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
            //If Voice recognition is successful then it returns RESULT_OK
            if (resultCode == RESULT_OK) {
                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (!textMatchList.isEmpty()) {
                    addText("el otro: " + textMatchList.get(0));
                }
                //Result code for various error.
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        if (id == R.id.action_download) {
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
