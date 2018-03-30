package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.DbBitmapUtility;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentUri;
    private TextView mNameView;
    private TextView mQuantityView;
    private TextView mPriceView;
    private TextView mUnitView;
    private TextView mSalesView;
    private ImageView mImageView;
    private String mContactAddress;
    private static final int EDIT_LOADER = 0;
    private static final String LOG_TAG = EditActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mCurrentUri = getIntent().getData();
        getLoaderManager().initLoader(EDIT_LOADER, null, this);
        mNameView = findViewById(R.id.text_name);
        mQuantityView = findViewById(R.id.text_quantity);
        mPriceView = findViewById(R.id.price_text);
        mUnitView = findViewById(R.id.sell_unit);
        mSalesView = findViewById(R.id.text_sales);
        mImageView = findViewById(R.id.show_image);
        Button purchaseProductButton = findViewById(R.id.button_purchase);
        Button unitUpButton = findViewById(R.id.button_increase_unit);
        Button unitDownButton = findViewById(R.id.button_decrease_unit);
        Button emailButton = findViewById(R.id.button_send_email);
        /**
         * 设置增添和减少产品数量的按钮
         */
        purchaseProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                int quantity = Integer.parseInt(mQuantityView.getText().toString().trim());
                values.put(ProductEntry.COLUMN_QUANTITY, quantity + 1);
                getContentResolver().update(mCurrentUri, values, null, null);
            }
        });

        unitUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                int unit = Integer.parseInt(mUnitView.getText().toString().trim());
                values.put(ProductEntry.COLUMN_UNIT, unit + 1);
                getContentResolver().update(mCurrentUri, values, null, null);
            }
        });

        unitDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                int unit = Integer.parseInt(mUnitView.getText().toString().trim());
                if (unit > 1) {
                    values.put(ProductEntry.COLUMN_UNIT, unit - 1);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.unit_limit_msg, Toast.LENGTH_SHORT).show();
                }
                getContentResolver().update(mCurrentUri, values, null, null);
            }
        });

        /**
         * 设置发送email的按钮
         */
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderSummary = getString(R.string.order_request) + mNameView.getText().toString() + "。";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", mContactAddress, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT, orderSummary);
                startActivity(Intent.createChooser(emailIntent, getString(R.string.choose_email_app)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry.COLUMN_ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_IMAGE,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SALES,
                ProductEntry.COLUMN_UNIT,
                ProductEntry.COLUMN_CONTACT
        };
        return new CursorLoader(this, mCurrentUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE);
            int salesColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SALES);
            int unitColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_UNIT);
            int contactColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_CONTACT);
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int sales = cursor.getInt(salesColumnIndex);
            int unit = cursor.getInt(unitColumnIndex);
            mContactAddress = cursor.getString(contactColumnIndex);
            byte[] imageByte = cursor.getBlob(imageColumnIndex);
            if (imageByte != null) {
                Bitmap image = DbBitmapUtility.getImage(imageByte);
                mImageView.setImageBitmap(image);
            } else {
                mImageView.setImageResource(R.drawable.empty_image);
            }
            mNameView.setText(name);
            mQuantityView.setText(Integer.toString(quantity));
            mPriceView.setText(getString(R.string.RMB) + Integer.toString(price));
            mSalesView.setText(Integer.toString(sales));
            mUnitView.setText(Integer.toString(unit));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameView.setText("");
        mQuantityView.setText("");
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                getContentResolver().delete(mCurrentUri, null, null);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
