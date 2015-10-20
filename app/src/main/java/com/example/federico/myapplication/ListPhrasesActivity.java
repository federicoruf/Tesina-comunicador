package com.example.federico.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import objects.Category;
import objects.ProvisionalContainer;

public class ListPhrasesActivity extends Activity {

    String[] countryArray = { "India", "Pakistan", "USA", "UK", "combo número uno", "cuánto cuesta?",
    "qué hora es?", "hasta qué hora está abierto el local?", "qué talle es?", "este talle me queda chico",
    "se puede pagar con tarjeta de crédito?", "cuántas cuotas?"};
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_phrases);

        Intent intent = getIntent();
        String place = intent.getStringExtra("Place");

        this.category = ProvisionalContainer.getPhrasesFrom(place);
        if (category != null) {
            TextView textViewFracesCategoria = (TextView) findViewById(R.id.textViewCategoryPhrases);
            textViewFracesCategoria.setText("Frases de la categoría: " + category.getName());

            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_list_view_phrases, category.getPhrases());
            ListView listView = (ListView) findViewById(R.id.list_phrases);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = category.getPhrases().get(position);
                    // String val = data.getStringExtra("Phrase");
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Phrase", item);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
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
}
