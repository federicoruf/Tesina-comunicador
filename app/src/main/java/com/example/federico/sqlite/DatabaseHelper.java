package com.example.federico.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.api.client.util.Key;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

/**
 * Created by federico on 24/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper  {

    private static int version = 1;
    private static String name = "TackLouderDb" ;
    private static SQLiteDatabase.CursorFactory factory = null;

    //TABLAS
    private static final String TABLE_CATEGORY = "CATEGORY";
    private static final String TABLE_PLACE = "PLACE";

    //COLUMNAS
    //las columnas repetidas en ambas tablas las pongo una sola vez
    private static final String COLUMN_ID ="id";
    private static final String COLUMN_NAME ="name";

    private static final String COLUMN_ENGLISH_NAME ="englishName";
    private static final String COLUMN_REFERENCE ="reference";
    private static final String COLUMN_GEOMETRY ="geometry";
    private static final String COLUMN_VINICITY ="vinicity";
    private static final String COLUMN_TYPES ="types";

    //definición de las tablas
    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE "
            + TABLE_CATEGORY + "("  + COLUMN_ID + " INTEGER PRIMARY KEY, "
                                    + COLUMN_NAME + "TEXT, "
                                    + COLUMN_ENGLISH_NAME + "TEXT)";
    private static final String CREATE_TABLE_PLACE = "CREATE TABLE "
            + TABLE_PLACE + "("     + COLUMN_ID + " INTEGER PRIMARY KEY, "
                                    + COLUMN_NAME + "TEXT, "
                                    + COLUMN_REFERENCE + "TEXT, "
                                    + COLUMN_GEOMETRY + "TEXT, "
                                    + COLUMN_VINICITY + "TEXT, "
                                    + COLUMN_TYPES + "TEXT)";

    //lista de columnas de la tabla para utilizarla en las consultas a la base de datos
    private String[] columnas = new String[]{ COLUMN_ENGLISH_NAME, COLUMN_GEOMETRY, COLUMN_ID,
            COLUMN_NAME, COLUMN_REFERENCE, COLUMN_TYPES, COLUMN_VINICITY };

    public DatabaseHelper(Context context) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_PLACE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
