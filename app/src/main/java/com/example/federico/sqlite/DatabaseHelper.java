package com.example.federico.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.federico.objects.Category;
import com.google.api.client.util.Key;

import java.nio.DoubleBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by federico on 24/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper  {

    //cada vez q hago un cambio en la BD, tengo q cambiar la versión
    private static int version = 4;
    private static String name = "TackLouderDb" ;
    private static SQLiteDatabase.CursorFactory factory = null;

    //definición de las tablas
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE "
            + DatabaseAdapter.TABLE_CATEGORIES + "("+ DatabaseAdapter.COLUMN_ID + " INTEGER PRIMARY KEY, "
                                    + DatabaseAdapter.COLUMN_NAME + " TEXT NOT NULL, "
                                    + DatabaseAdapter.COLUMN_ENGLISH_NAME + " TEXT NOT NULL)";
    private static final String CREATE_TABLE_PHRASES = "CREATE TABLE "
            + DatabaseAdapter.TABLE_PHRASES + "("   + DatabaseAdapter.COLUMN_ID + " INTEGER PRIMARY KEY, "
                                    + DatabaseAdapter.COLUMN_PHRASE + " TEXT NOT NULL, "
                                    + DatabaseAdapter.COLUMN_CATEGORY_ID + " INTEGER NOT NULL, "
                                    + "FOREIGN KEY("+ DatabaseAdapter.COLUMN_CATEGORY_ID +") REFERENCES "+ DatabaseAdapter.TABLE_CATEGORIES +"("+ DatabaseAdapter.COLUMN_ID +"))";

    public DatabaseHelper(Context context) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_PHRASES);

        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_CATEGORIES + "(" +
                DatabaseAdapter.COLUMN_ID + ", " + DatabaseAdapter.COLUMN_NAME + ", " + DatabaseAdapter.COLUMN_ENGLISH_NAME +
                ") VALUES(1, 'alojamiento', 'lodging')");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_CATEGORIES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_NAME+", "+DatabaseAdapter.COLUMN_ENGLISH_NAME+
                ") VALUES(2, 'hospital', 'hospital')");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_CATEGORIES + "(" +
                DatabaseAdapter.COLUMN_ID + ", " + DatabaseAdapter.COLUMN_NAME + ", " + DatabaseAdapter.COLUMN_ENGLISH_NAME +
                ") VALUES(3, 'biblioteca', 'library')");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_CATEGORIES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_NAME+", "+DatabaseAdapter.COLUMN_ENGLISH_NAME+
                ") VALUES(4, 'bar', 'bar')");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_CATEGORIES + "(" +
                DatabaseAdapter.COLUMN_ID + ", " + DatabaseAdapter.COLUMN_NAME + ", " + DatabaseAdapter.COLUMN_ENGLISH_NAME +
                ") VALUES(5, 'tinda de ropas', 'clothes store')");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_CATEGORIES + "(" +
                DatabaseAdapter.COLUMN_ID + ", " + DatabaseAdapter.COLUMN_NAME + ", " + DatabaseAdapter.COLUMN_ENGLISH_NAME +
                ") VALUES(6, 'cine', 'cinema')");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_CATEGORIES + "(" +
                DatabaseAdapter.COLUMN_ID + ", " + DatabaseAdapter.COLUMN_NAME + ", " + DatabaseAdapter.COLUMN_ENGLISH_NAME +
                ") VALUES(7, 'default', 'default')");

        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(1, 'la película es el 3D?', 6)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(2, 'cuánto dura la película?', 6)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(3, 'la película tiene subtitulos?', 6)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(4, 'la película es apta para menores de edad?', 6)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(5, 'cuánto cuesta?', 6)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(6, 'quisiera comprar X entradas', 6)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(7, 'a que hora es la próxima función?', 6)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(8, 'quiero una entrada', 6)");

        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(9, 'tendría un talle más chico?', 5)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(10, 'tendría un talle más grande?', 5)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(11, 'qué precio tiene X?', 5)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(12, 'tiene remeras manga largas?', 5)");

        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(13, 'podría traerme la cuenta?', 4)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(14, 'quisiera un tostado', 4)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(15, 'quisiera una medialuna', 4)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(16, 'quisiera tomar un café', 4)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(17, 'podría traerme edulcorante?', 4)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(18, 'quisiera tomar un té', 4)");

        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(19, 'quisiera obtener el carnet de socio', 3)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(20, 'necesito una copia de ', 3)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(21, 'quiero el libro ', 3)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(22, 'quiero devolver este libro', 3)");

        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(23, 'quisiera sacar un turno para el doctor', 2)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(24, 'qué días atiende el doctor ?', 2)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(25, 'el doctor  atiende por la obra social', 2)");

        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(26, 'quisiera reservar una habitacion', 1)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(27, 'incluye desayuno?', 1)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(28, 'cuanto cuesta la noche?', 1)");

        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(29, 'donde esta la comisaría?', 7)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(30, 'hola buen día', 7)");
        db.execSQL("INSERT INTO " + DatabaseAdapter.TABLE_PHRASES + "(" +
                DatabaseAdapter.COLUMN_ID+", "+DatabaseAdapter.COLUMN_PHRASE+", "+DatabaseAdapter.COLUMN_CATEGORY_ID+
                ") VALUES(31, 'adios', 7)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseAdapter.TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseAdapter.TABLE_PHRASES);
        onCreate(db);
    }
}
