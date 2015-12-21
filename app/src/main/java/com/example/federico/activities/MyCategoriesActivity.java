package com.example.federico.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class MyCategoriesActivity extends ListFragment {

    private static final int C_DELETE = 101;

    private DatabaseAdapter dbAdapter;
    private FragmentActivity context;
    private ListView listView;
    private ArrayAdapter listAdapter;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        this.dbAdapter = new DatabaseAdapter(this.context);

    }

    private void setListView() throws SQLException {
        this.listView = getListView();
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
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this.context);
        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(listAdapter.getItem((int) id).toString());
        dialogEliminar.setMessage(getResources().getString(R.string.delete_message_category));
        dialogEliminar.setCancelable(false);
        dialogEliminar.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dbAdapter.deleteCategory(listAdapter.getItem((int) id).toString());
                    Toast.makeText(context, R.string.category_deleted, Toast.LENGTH_SHORT).show();
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
        List<Category> categories = this.dbAdapter.getAllRealCategories();
        ArrayList<String> categoriesNames = new ArrayList<String>();
        for(Category c: categories) {
                categoriesNames.add(c.getName());
        }
        this.listAdapter = new ArrayAdapter<String>(this.context, R.layout.activity_list_view_phrases, categoriesNames);
        this.listView.setAdapter(this.listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_categories_layout, container, false);
    }

    //al ser un fragment funciona de otra manera, entonces para setear la vista es lo último que se debe hacer.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            this.setListView();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}