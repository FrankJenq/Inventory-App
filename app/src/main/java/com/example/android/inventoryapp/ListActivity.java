package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.DbBitmapUtility;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    public static final String LOG_TAG = ListActivity.class.getSimpleName();
    ProductCursorAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ListView listView = findViewById(R.id.list);

        //设置空白提示
        View emptyView = findViewById(R.id.empty_text);
        listView.setEmptyView(emptyView);

        //设置新的CursorAdapter
        productAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(productAdapter);

        // 设置OnItemClickListener
        // 点击列表中的产品项会进入详情页面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, EditActivity.class);
                Uri currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        //初始化Loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    /**
     * 设置主界面选项菜单
     * 可以选择添加新产品
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_add_new_data:
                Intent intent = new Intent(ListActivity.this, NewProductActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_add_dummy_data:
                insertDummyProduct();
                Toast.makeText(getApplicationContext(), R.string.toast_add_dummy, Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyProduct() {
        ContentValues values = new ContentValues();
        Bitmap image = BitmapFactory.decodeResource(this.getResources(), R.drawable.empty_image);
        values.put(ProductEntry.COLUMN_NAME, getString(R.string.dummy_name));
        values.put(ProductEntry.COLUMN_IMAGE, DbBitmapUtility.getBytes(image));
        values.put(ProductEntry.COLUMN_QUANTITY, 10);
        values.put(ProductEntry.COLUMN_PRICE, 5);
        values.put(ProductEntry.COLUMN_SALES, 0);
        values.put(ProductEntry.COLUMN_UNIT, 1);
        values.put(ProductEntry.COLUMN_CONTACT,getString(R.string.dummy_contact));

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 创建Loader的时候会加载指定的数据列
     *
     * @param id
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = {
                ProductEntry.COLUMN_ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_IMAGE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_SALES,
                ProductEntry.COLUMN_UNIT
        };
        return new CursorLoader(this, ProductEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        productAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        productAdapter.swapCursor(null);
    }
}
