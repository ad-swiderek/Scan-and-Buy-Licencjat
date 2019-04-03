package com.example.adrian.myapplication;

import android.provider.BaseColumns;


public final class DBContract {
    private DBContract() {
    }

    public static class Product implements BaseColumns { //utworzenie naszej tabeli i jej kolumn
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_BARCODE_NUMBER = "barcode";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //nadanie typow danych i ograniczen
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BARCODE_NUMBER + " TEXT UNIQUE, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_PRODUCT_PRICE + " REAL, " +
                COLUMN_PRODUCT_QUANTITY + " INTEGER" + ")";
    }
}
