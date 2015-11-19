package com.example.federico.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.federico.myapplication.R;
import com.example.federico.objects.Category;
import com.example.federico.sqlite.DatabaseAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ListCategoriesActivity extends Activity {

    private static final int C_DELETE = 101;

    private DatabaseAdapter dbAdapter;
    private ListCategoriesActivity context;
    private ListView listView;
    private ArrayAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categories);

        this.context = ListCategoriesActivity.this;
        this.dbAdapter = new DatabaseAdapter(this.context);
        try {
            this.setListView();
        } catch (SQLException e) {
            e.printStackTrace();
        };
    }

    private void setListView() throws SQLException {
        this.listView = (ListView) findViewById(R.id.listViewLocalCategories);
        this.loadCategories();
        this.registerForContextMenu(this.listView);
        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteCategory(id);
                return false;
            }
        });
    }

    private void deleteCategory(final long id) {
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);
        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(listAdapter.getItem((int) id).toString());
        dialogEliminar.setMessage(getResources().getString(R.string.delete_message_category));
        dialogEliminar.setCancelable(false);
        dialogEliminar.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dbAdapter.deleteCategory(listAdapter.getItem((int) id).toString());
                    Toast.makeText(ListCategoriesActivity.this, R.string.category_deleted, Toast.LENGTH_SHORT).show();
                    loadCategories();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        dialogEliminar.setNegativeButton(android.R.string.no, null);
        dialogEliminar.show();
    }

    private void loadCategories() throws SQLException {
        List<Category> categories = this.dbAdapter.getAllCategories();
        ArrayList<String> categoriesNames = new ArrayList<String>();
        for(Category c: categories) {
            categoriesNames.add(c.getName());
        }
        this.listAdapter = new ArrayAdapter<String>(this, R.layout.activity_list_view_phrases, categoriesNames);
        this.listView.setAdapter(this.listAdapter);
    }

    //ACTION BAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_categories, menu);
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
            case R.id.action_create_category:
                Intent intent = new Intent(getBaseContext(), CreateCategoryActivity.class);
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
