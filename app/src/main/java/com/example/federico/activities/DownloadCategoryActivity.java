package com.example.federico.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * Created by federico on 24/11/2015.
 */
public class DownloadCategoryActivity extends ListFragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("11111111");
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

    private void setListView() throws SQLException {
        this.listView = getListView();
        this.loadCategories();
        this.registerForContextMenu(this.listView);
        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // aqui debe hacer la petición a algún servidor remoto donde esten guardadas el resto de las categorías
                downloadCategory(id);
                return false;
            }
        });
    }


    private void loadCategories() throws SQLException {
        // pedir las categorías de algún servidor remoto
        // List<Category> categories = this.dbAdapter.getAllCategories();
        /*
        ArrayList<String> categoriesNames = new ArrayList<String>();
        for(Category c: categories) {
            categoriesNames.add(c.getName());
        }
        */
        // de prueba
        ArrayList<String> categoriesNames = new ArrayList<String>();
        categoriesNames.add("cat 1");
        categoriesNames.add("cat 12");
        categoriesNames.add("cat 13");
        categoriesNames.add("cat 2");
        categoriesNames.add("cat 3");
        categoriesNames.add("bar");
        categoriesNames.add("alojamiento");
        categoriesNames.add("cine");
        categoriesNames.add("biblioteca ");
        categoriesNames.add("lodging");
        //-----
        for (int i=0; i<categoriesNames.size(); i++) {
            if (dbAdapter.existsCategory(categoriesNames.get(i))) {
                categoriesNames.remove(categoriesNames.remove(i));
            }
        }

        this.listAdapter = new ArrayAdapter<String>(this.context, R.layout.activity_list_view_phrases, categoriesNames);
        this.listView.setAdapter(this.listAdapter);
    }

    private void downloadCategory(final long id) {
        AlertDialog.Builder dialogDownload = new AlertDialog.Builder(this.context);
        dialogDownload.setIcon(android.R.drawable.ic_dialog_alert);
        dialogDownload.setTitle(listAdapter.getItem((int) id).toString());
        dialogDownload.setMessage(getResources().getString(R.string.download_message_category));
        dialogDownload.setCancelable(false);
        dialogDownload.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //agrega la categoría a la base de datos

                    //dbAdapter.deleteCategory(listAdapter.getItem((int) id).toString());

                    Toast.makeText(context, R.string.category_downloaded, Toast.LENGTH_SHORT).show();
                    loadCategories();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        dialogDownload.setNegativeButton(android.R.string.no, null);
        dialogDownload.show();
    }

}