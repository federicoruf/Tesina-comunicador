package com.example.federico.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.federico.objects.Category;
import com.example.federico.objects.Phrase;
import com.example.federico.sqlite.DatabaseAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView textViewChat;
    private TextToSpeech textToSpeech;
    private ImageButton setImgButtonSpeech;
    private Button buttonChoosePhrase;
    private Button buttonSpeak;
    private ImageButton imageButton;
    private EditText editPhrase;
    private ScrollView scrollViewChat;

    private String categorySpanish;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private DatabaseAdapter dbAdapter;
    private MainActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = MainActivity.this;
        this.dbAdapter = new DatabaseAdapter(context);
        Intent intent = getIntent();
        this.categorySpanish = intent.getStringExtra("categorySpanish");

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

    private void setButtonChoosePhrase() {
        this.buttonChoosePhrase = (Button) findViewById(R.id.buttonChoosePhrase);
        this.buttonChoosePhrase.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), ListPhrasesActivity.class);
                i.putExtra("categorySpanish", categorySpanish);
                startActivityForResult(i, 1);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //for the speech of the "talk" person
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

        //for the phrase selected by the user
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String val = data.getStringExtra("Phrase");
                this.editPhrase.setText(val);
            }
        }
    }

    private void addText(String text) {
        this.textViewChat.invalidate();
        this.textViewChat.requestLayout();
        this.textViewChat.append(text + "\n");
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}