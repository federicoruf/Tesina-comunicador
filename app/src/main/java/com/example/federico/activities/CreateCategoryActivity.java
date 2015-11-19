package com.example.federico.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.example.federico.myapplication.R;
import com.example.federico.objects.Category;
import com.example.federico.objects.Phrase;
import com.example.federico.background.TranslatorRequest;
import com.example.federico.sqlite.DatabaseAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CreateCategoryActivity extends Activity {

    private static final int C_EDIT = 100;
    private static final int C_DELETE = 101;
    //kye translator
    private static final  String TRANSLATOR_KEY = "trnsl.1.1.20151106T142449Z.46961437432996eb.55c5739bdf302d75b53bf5ebc811d93f56884952";

    private ListView listView;
    private EditText inputCategoryName;
    private EditText inputNewPhrase;
    private Button buttonAddPhrase;
    private Button buttonAddCategory;

    private DatabaseAdapter dbAdapter;
    private TranslatorRequest translator;
    private CreateCategoryActivity context;
    private ArrayList<String> listPhrases;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        this.context = CreateCategoryActivity.this;
        this.dbAdapter = new DatabaseAdapter(context);

        //setea todo lo relacionado con la lista
        this.setListView();

        this.inputCategoryName = (EditText) findViewById(R.id.inputCategoryName);
        this.inputNewPhrase = (EditText) findViewById(R.id.inputNewPhrase);

        //setea el boton para agregar una frase
        this.setButtonAddPhrase();

        //setea el botón para agregar la categoría
        this.setButtonAddCategory();
    }

    private void setButtonAddCategory() {
        this.buttonAddCategory = (Button) findViewById(R.id.buttonAddCategory);
        this.buttonAddCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategoryName = inputCategoryName.getText().toString();
                try {
                    if (!dbAdapter.existsCategory(newCategoryName)) {
                        String tempURL ="https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                                "key=" + TRANSLATOR_KEY +
                                "&text=" + newCategoryName + "&lang=es-en";
                        translator = new TranslatorRequest();
                        translator.execute(tempURL);
                        translator.get();
                        System.out.println("TRADUCE: " + translator.getFinalTranslation());
                        Category newCategory = new Category();
                        newCategory.setName(newCategoryName);
                        newCategory.setEnglishName(translator.getFinalTranslation());
                        //da de alta la categoría
                        long id = dbAdapter.addCategoryDatabase(Category.toContentValues(newCategory));
                        for(String c: listPhrases){
                            dbAdapter.addPhraseToCategory(Phrase.toContentValues(new Phrase(c, id)));
                        }
                        showToastMessage(getResources().getString(R.string.category_added));
                        finish();
                    } else {
                        showToastMessage(getResources().getString(R.string.new_phrase_name_exist));
                    }
                } catch (InterruptedException | ExecutionException | SQLException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void setButtonAddPhrase() {
        this.buttonAddPhrase = (Button) findViewById(R.id.buttonAddNewPhrase);
        this.buttonAddPhrase.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPhrase = inputNewPhrase.getText().toString();
                if (!containsPhrase(newPhrase)) {
                    listPhrases.add(newPhrase);
                    listAdapter.notifyDataSetChanged();
                    closeKeyboard();
                } else {
                    showToastMessage(getResources().getString(R.string.phrase_repeated));
                }
            }
        });
    }

    private void setListView() {
        this.listPhrases = new ArrayList<String>();
        this.listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.listPhrases);
        this.listView = (ListView) findViewById(R.id.listViewPhrasesCategory);
        this.listView.setAdapter(listAdapter);
        this.registerForContextMenu(this.listView);
    }

    private boolean containsPhrase(String newPhrase) {
        System.out.println("tiene la frase? " + this.listPhrases.contains(newPhrase));
        return this.listPhrases.contains(newPhrase);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(listAdapter.getItem(((AdapterContextMenuInfo) menuInfo).position).toString());
        menu.add(Menu.NONE, C_EDIT, Menu.NONE, R.string.menu_edit);
        menu.add(Menu.NONE, C_DELETE, Menu.NONE, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case C_DELETE:
                this.deletePhrase(info.id);
                return true;
            case C_EDIT:
                //hacer un toast
                this.editPhrase(info.id);
                System.out.println("---------------------------SELECCIONA EDITAR");
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void deletePhrase(final long id) {
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);
        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(R.string.delete_title));
        dialogEliminar.setMessage(getResources().getString(R.string.delete_message));
        dialogEliminar.setCancelable(false);
        dialogEliminar.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listPhrases.remove((int) id);
                listAdapter.notifyDataSetChanged();
                Toast.makeText(CreateCategoryActivity.this, R.string.phrase_deleted, Toast.LENGTH_SHORT).show();
            }
        });
        dialogEliminar.setNegativeButton(android.R.string.no, null);
        dialogEliminar.show();
    }

    private void editPhrase(final long id){
        //get prompt.xml
        LayoutInflater li = LayoutInflater.from(this.context);
        View promptView = li.inflate(R.layout.prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        //set prompt to alertdialog builder
        alertDialogBuilder.setView(promptView);
        final EditText userInput = (EditText) promptView.findViewById(R.id.editTextDialogUserInput);
        //SETEAR LOS DATOS EN EL INPUT!!!!
        String oldPhrase = listAdapter.getItem((int)id).toString();
        userInput.setText(oldPhrase);
        System.out.println("vieja frase: " + oldPhrase);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //llama a la base de datos y actualiza la frase
                        String updatedPhrase = userInput.getText().toString();
                        if (!containsPhrase(updatedPhrase)) {
                            listPhrases.set((int) id, updatedPhrase);
                            listAdapter.notifyDataSetChanged();
                            context.closeKeyboard();
                        } else {
                            showToastMessage(getResources().getString(R.string.phrase_repeated));
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.create().show();
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //cierra el teclado en pantalla(al arracar la app tiene el foco en el campo de la busqueda)
    private void closeKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //ACTION BAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_category, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_download:
                System.out.println("clickeoooo");
                return true;
            case R.id.action_list_categories:
                Intent intent = new Intent(getBaseContext(), ListCategoriesActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.action_start:
                Intent intent1 = new Intent(getBaseContext(), StartActivity.class);
                context.startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}