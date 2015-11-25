package com.example.federico.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.federico.myapplication.R;

public class TabsCategoriesActivity extends FragmentActivity {

    private FragmentTabHost tabHost;
    private TabsCategoriesActivity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AQUÍ DEBE HACER LA PETICIÓN A ALGUN SERVIDOR PARA DESCARGAR LOS NOMBRES DE LOS PAQUETES DISPONIBLES
        // que pasa con los paquetes que ya tengo, los muestro en otro color? no los muestro? le
        // doy la posibilidad al usuario de actualizar el paquete que ya tiene instalado?

        //probar armar en esta activity la de descargar una nueva categoría + la lista de categorías que tengo intanadas

        //SEGUIR LA IDEA DEL DESCARGAR IDIOMAS DE GOOGLE!!!!!

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs_categories);

        this.context = TabsCategoriesActivity.this;

        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.text_my_categories)),
                MyCategoriesActivity.class, null);
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.download_title)),
                DownloadCategoryActivity.class, null);
    }

    //ACTION BAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
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