package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.DbBitmapUtility;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       从中获取数据的 cursor.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * 创建一个新的空白 list item 视图.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * 将 product 数据与list item layout 连接起来
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView productName = (TextView) view.findViewById(R.id.name);
        TextView productQuantity = (TextView) view.findViewById(R.id.quantity);
        TextView productPrice = (TextView) view.findViewById(R.id.price);
        TextView productSales = (TextView) view.findViewById(R.id.sales);
        ImageView productImageView = (ImageView) view.findViewById(R.id.product_image);
        Log.i("tag2", "bindView: ");
        // Get ColumnIndex from cursor
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
        int salesColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SALES);
        int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE);
        // Get values from the items with the indexes
        String name = cursor.getString(nameColumnIndex);
        String quantityString = cursor.getString(quantityColumnIndex);
        String priceString = cursor.getString(priceColumnIndex);
        String salesString = cursor.getString(salesColumnIndex);
        Log.i("tag 1", "bindView: ");
        Bitmap image = DbBitmapUtility.getImage(cursor.getBlob(imageColumnIndex));
        if (TextUtils.isEmpty(quantityString) || Integer.parseInt(quantityString) == 0) {
            quantityString = context.getString(R.string.no_storage);//产品存量为0时提示信息
        }else {
            quantityString = context.getString(R.string.storage_text) + quantityString;
        }
        // 使用解析出的数据分别填充对应视图
        productName.setText(name);
        productQuantity.setText(quantityString);
        productPrice.setText(context.getString(R.string.RMB)+priceString);
        productSales.setText(context.getString(R.string.sales_text) + salesString);
        productImageView.setImageBitmap(image);
    }
}
