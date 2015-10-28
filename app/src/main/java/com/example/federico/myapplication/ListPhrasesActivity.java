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

import com.example.federico.objects.Category;
import com.example.federico.objects.Phrase;
import com.example.federico.objects.ProvisionalContainer;
import com.example.federico.sqlite.DatabaseAdapter;

import java.sql.SQLException;
import java.util.ArrayList;

public class ListPhrasesActivity extends Activity {

    private TextView textViewFracesCategoria;
    private ListView listView;

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
        ArrayList<String> phrasesFromCategory = dbAdapter.getPhrasesFromCategory(this.category.getId());
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_list_view_phrases, phrasesFromCategory);
        this.listView = (ListView) findViewById(R.id.list_phrases);
        listView.setAdapter(adapter);
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
