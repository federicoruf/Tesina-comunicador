package com.example.federico.objects;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.federico.sqlite.DatabaseAdapter;

public class Category {

    private long id;
    private String name;
    private String englishName;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }


    public Category() {

    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Category cursorToCategory(Cursor cursor) {
        Category category = null;
        if (cursor != null) {
           category = new Category();
            category.setId(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_NAME)));
            category.setEnglishName(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ENGLISH_NAME)));
        }
        return category;
    }

    public static ContentValues toContentValues(Category category){
        ContentValues reg = new ContentValues();
        if (category.getId() == 0) {
            reg.put(DatabaseAdapter.COLUMN_NAME, category.getName());
            reg.put(DatabaseAdapter.COLUMN_ENGLISH_NAME, category.getEnglishName());
        }
        return reg;
    }

}
