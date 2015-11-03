package com.example.federico.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.federico.objects.Category;
import com.example.federico.objects.Phrase;
import com.example.federico.sqlite.DatabaseAdapter;

import java.sql.SQLException;
import java.util.ArrayList;

public class ListPhrasesActivity extends Activity {

    private static final int C_EDIT = 100;
    private static final int C_DELETE = 101;

    private TextView textViewFracesCategoria;
    private ListView listView;
    private ArrayAdapter listAdapter;

    private Category category;
    private DatabaseAdapter dbAdapter;
    private ListPhrasesActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_phrases);
        this.context = ListPhrasesActivity.this;
        this.dbAdapter = new DatabaseAdapter(context);

        Intent intent = getIntent();
        String categoryName = intent.getStringExtra("categorySpanish");
        try {
            this.category = dbAdapter.getCategoryFromSpanishName(categoryName);
            //this.category = ProvisionalContainer.getPhrasesFrom(place);
            if (category != null) {
                System.out.println("resultado: " + category.getName());
                this.textViewFracesCategoria = (TextView) findViewById(R.id.textViewCategoryPhrases);
                textViewFracesCategoria.setText("Frases de la categoría: " + category.getName());

                //setea la lista de frases
                this.setListView();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setListView() throws SQLException {
        this.listView = (ListView) findViewById(R.id.list_phrases);
        this.loadPhrases();
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getAdapter().getItem(position).toString();
                // String val = data.getStringExtra("Phrase");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Phrase", item);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        this.registerForContextMenu(this.listView);
    }

    private void loadPhrases() throws SQLException {
        ArrayList<String> phrasesFromCategory = dbAdapter.getPhrasesFromCategory(this.category.getId());
        this.listAdapter = new ArrayAdapter<String>(this, R.layout.activity_list_view_phrases, phrasesFromCategory);
        listView.setAdapter(listAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_phrases, menu);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(listAdapter.getItem(((AdapterView.AdapterContextMenuInfo) menuInfo).position).toString());
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
                try {
                    dbAdapter.deletePhrase(listAdapter.getItem((int) id).toString());
                    Toast.makeText(ListPhrasesActivity.this, R.string.phrase_deleted, Toast.LENGTH_SHORT).show();
                    loadPhrases();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        dialogEliminar.setNegativeButton(android.R.string.no, null);
        dialogEliminar.show();
    }

    private void editPhrase(final long id){
        //get prompt.xml
        LayoutInflater li = LayoutInflater.from(context);
        View promptView = li.inflate(R.layout.prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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
                        try {
                            long resu = dbAdapter.updatePhrase(listAdapter.getItem((int) id).toString(), updatedPhrase, category);
                            if (resu != -1) {
                                showToastMessage(getResources().getString(R.string.phrase_updated));
                            } else {
                                showToastMessage(getResources().getString(R.string.phrase_repeated));
                            }
                            loadPhrases();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
