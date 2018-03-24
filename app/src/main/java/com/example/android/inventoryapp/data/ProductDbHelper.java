package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "products_shelter.db";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;//删除数据表的命令

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String mSqlCreateProductsTable =
                "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                        ProductEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ProductEntry.COLUMN_NAME + " TEXT NOT NULL," +
                        ProductEntry.COLUMN_IMAGE + " BLOB," +
                        ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                        ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0," +
                        ProductEntry.COLUMN_SALES + " INTEGER," +
                        ProductEntry.COLUMN_UNIT + " INTEGER" + ");";
        db.execSQL(mSqlCreateProductsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
