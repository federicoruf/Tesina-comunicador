package com.example.federico.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.federico.sqlite.DatabaseAdapter;

/**
 * Created by federico on 27/10/2015.
 */
public class Phrase {

    long id;

    public Phrase() {

    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTextPhrase(String textPhrase) {
        this.textPhrase = textPhrase;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    String textPhrase;
    long categoryId;

    public Phrase(String textPhrase, long categoryId) {
        this.textPhrase = textPhrase;
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public String getTextPhrase() {
        return textPhrase;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public static ContentValues toContentValues(Phrase phrase){
        ContentValues reg = new ContentValues();
        System.out.println("id: " + phrase.getId());
        if (phrase.getId() == 0) {
            reg.put(DatabaseAdapter.COLUMN_PHRASE, phrase.getTextPhrase());
            reg.put(DatabaseAdapter.COLUMN_CATEGORY_ID, phrase.getCategoryId());
        }
        return reg;
    }

    public static Phrase cursorToPhrase(Cursor cursor) {
        Phrase phrase = null;
        if (cursor != null) {
            phrase = new Phrase();
            phrase.setId(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ID)));
            phrase.setCategoryId(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.COLUMN_CATEGORY_ID)));
            phrase.setTextPhrase(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_PHRASE)));
        }
        return phrase;
    }
}