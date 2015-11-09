package com.example.federico.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.federico.objects.Category;
import com.example.federico.objects.Phrase;
import com.example.federico.objects.Place;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by federico on 26/10/2015.
 */
public class DatabaseAdapter {

    private Context contexto;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    //APERTURA Y CIERRE DE LA SESIÓN DE LA BD

    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_NAME ="name";
    public static final String COLUMN_ENGLISH_NAME ="englishName";
    public static final String COLUMN_PHRASE ="phrase";
    public static final String COLUMN_CATEGORY_ID ="categoryId";

    public static final String TABLE_CATEGORIES = "CATEGORIES";
    public static final String TABLE_PHRASES = "PHRASES";

    private String[] columnsCategories = new String[]{ COLUMN_ENGLISH_NAME, COLUMN_ID, COLUMN_NAME };
    private String[] columnsPhrases = new String[]{ COLUMN_ID, COLUMN_PHRASE, COLUMN_CATEGORY_ID };

    public DatabaseAdapter (Context context){
        this.contexto = context;
    }

    public DatabaseAdapter open() throws SQLException {
        this.dbHelper = new DatabaseHelper(contexto);
        this.database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        this.close();
    }

    public List getAllCategories() throws SQLException{
        this.isOpen();
        //Cursor c = database.query(true, TABLE_CATEGORIES, columnsCategories, C_COLUMNA_ID + "=" + id, null, null, null, null, null);
        Cursor cursor = database.query(true, TABLE_CATEGORIES, columnsCategories, null, null, null, null, null, null);
        List<Category> categories = new ArrayList<Category>();
        if (cursor.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ID)));
                cat.setName(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_NAME)));
                cat.setEnglishName(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ENGLISH_NAME)));
                categories.add(cat);
            } while (cursor.moveToNext());
        }
        return categories;
    }
    //uno de los usos es para conseguir la categoría en español
    public Category getCategoryLikeFromPlace(Place place) throws SQLException {
        this.isOpen();
        ArrayList<String> typesPlace = place.types;

        for (String type: typesPlace) {
            String typeFilter = COLUMN_ENGLISH_NAME + "='" + type + "'";
            Cursor cursor = database.query(true, TABLE_CATEGORIES, columnsCategories, typeFilter, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                Category cat = new Category();
                cat = Category.cursorToCategory(cursor);
                return cat;
            }
        }
        return null;
    }


    public Category getCategoryFromSpanishName(String categorySpanish) throws SQLException {
        this.isOpen();
        Category cat = null;
        String filter = COLUMN_NAME + " = '" + categorySpanish + "'";
        Cursor cursor = database.query(true, TABLE_CATEGORIES, columnsCategories, filter, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            cat = Category.cursorToCategory(cursor);
        }
        return cat;
    }

    //a partir del string ingresado devuelve aquellas categorias que COINCIDAN PARCIALMENTE O NO CON EL NOMRE EN ESPAÑOL
    public ArrayList<Category> getAllCategoriesThatMatchWith(String placeToSearch) throws SQLException {
        this.isOpen();
        ArrayList<Category> categoriesFounded = new ArrayList<Category>();
        String startWithFilter = COLUMN_NAME + " LIKE '" + placeToSearch + "%'";
        Cursor cursor = database.query(true, TABLE_CATEGORIES, columnsCategories, startWithFilter, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                categoriesFounded.add(Category.cursorToCategory(cursor));
                cursor.moveToNext();
            }
        }
        return categoriesFounded;
    }

    private void isOpen () throws SQLException {
        if (database == null) {
            this.open();
        }
    }

    public long addPhraseToCategory(ContentValues phrase) throws SQLException {
        this.isOpen();
        System.out.println("frase: " + phrase.get(DatabaseAdapter.COLUMN_PHRASE));
        System.out.println("id: " + phrase.get(DatabaseAdapter.COLUMN_CATEGORY_ID));
        boolean exists = this.existPhrase(phrase.get(COLUMN_PHRASE).toString(),phrase.get(COLUMN_CATEGORY_ID).toString());
        if(!exists) {
            return database.insert(TABLE_PHRASES, null, phrase);
        }
        return -1;
    }

    public long addCategoryDatabase(ContentValues category) throws SQLException {
        this.isOpen();
        return database.insert(TABLE_CATEGORIES, null, category);
    }

    public boolean existsCategory(String category) throws SQLException {
        this.isOpen();
        String filter = COLUMN_NAME + " = '" + category + "'";
        Cursor c = database.query(true, TABLE_CATEGORIES, columnsCategories, filter, null, null, null, null, null);
        return (c.getCount()>0);
    }

    private boolean existPhrase(String phrase, String categoryId) {
        String filter = COLUMN_CATEGORY_ID + " = '" + categoryId + "' AND " + COLUMN_PHRASE + " = '" + phrase + "'";
        Cursor c = database.query(true, TABLE_PHRASES, columnsPhrases, filter, null, null, null, null, null);
        return (c.getCount()>0);
    }

    public ArrayList<String> getPhrasesFromCategory(long id) throws SQLException {
        this.isOpen();
        ArrayList<String> phrasesFounded = new ArrayList<String>();
        String categoryIdFilter = COLUMN_CATEGORY_ID + " = " + id;
        Cursor cursor = database.query(true, TABLE_PHRASES, columnsPhrases, categoryIdFilter, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                phrasesFounded.add(cursor.getString(cursor.getColumnIndex(COLUMN_PHRASE)));
                System.out.println("id: " + cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                cursor.moveToNext();
            }
        }
        for (int i=0; i<phrasesFounded.size(); i++){

        }
        return phrasesFounded;
    }

    public long deletePhrase(String phrase) throws SQLException {
        this.isOpen();
        return database.delete(TABLE_PHRASES, COLUMN_PHRASE + " = '" + phrase + "'", null);
    }

    public long updatePhrase(String oldPhrase, String updatedPhrase, Category category) throws SQLException {
        this.isOpen();
        long id = category.getId();
        if (!this.existPhrase(updatedPhrase, (String.valueOf(id)))) {
            Phrase updatedPh = new Phrase(updatedPhrase, category.getId());
            ContentValues contentValues = Phrase.toContentValues(updatedPh);
            return database.update(TABLE_PHRASES, contentValues, COLUMN_PHRASE + " = '" + oldPhrase + "' AND " +
                    COLUMN_CATEGORY_ID + " = '" + category.getId() + "'" , null);
        }
        return -1;
    }
}
