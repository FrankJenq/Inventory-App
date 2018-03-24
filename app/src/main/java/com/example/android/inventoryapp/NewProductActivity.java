package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.DbBitmapUtility;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

public class NewProductActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private ImageView mImageView;
    private TextView mUploadImageTextView;
    private Bitmap mSelectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newproduct);
        mNameEditText = findViewById(R.id.new_product_name);
        mQuantityEditText = findViewById(R.id.new_product_quantity);
        mPriceEditText = findViewById(R.id.new_product_price);
        mUploadImageTextView = findViewById(R.id.upload_image);
        mImageView = findViewById(R.id.image_select);
        mImageView.setVisibility(View.GONE);
        mUploadImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                mUploadImageTextView.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        });
        // 点击ImageView可以上传图片
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
        // 设置保存按钮
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });
    }

    /**
     * 检查输入内容，如果非空，就将新产品保存到数据库中
     */
    private void saveProduct() {
        ContentValues values = new ContentValues();
        String mNameString = mNameEditText.getText().toString();
        String mQuantityString = mQuantityEditText.getText().toString();
        String mPriceString = mPriceEditText.getText().toString();
        if (TextUtils.isEmpty(mNameString + mQuantityString + mPriceString) && mSelectedImage == null) {
            finish();
            return;
        }// 检查是否所有项目都没有输入内容
        // 如果是这样那么直接返回主页面
        if (TextUtils.isEmpty(mNameString)){
            Toast.makeText(getApplicationContext(), getString(R.string.toast_empty_name), Toast.LENGTH_SHORT).show();
            return;
        }//商品名称为空时显示提示信息
        int mQuantity;
        if (!TextUtils.isEmpty(mQuantityString) && Integer.parseInt(mQuantityString.trim()) >= 0) {
            mQuantity = Integer.parseInt(mQuantityString.trim());
        } else {
            mQuantity = 0;
        }  // 未输入初始数量或者输入数量为负数时将商品数量初始化为0
        int mPrice;
        if (!TextUtils.isEmpty(mPriceString) && Integer.parseInt(mPriceString.trim()) >= 0) {
            mPrice = Integer.parseInt(mPriceString.trim());
        } else {
            mPrice = 0;
        }  // 未输入单价或者输入价格为负数时将价格初始化为0
        values.put(ProductEntry.COLUMN_NAME, mNameString.trim());
        values.put(ProductEntry.COLUMN_QUANTITY,mQuantity);
        values.put(ProductEntry.COLUMN_PRICE, mPrice);
        values.put(ProductEntry.COLUMN_SALES,0);//设置初始销量为0
        values.put(ProductEntry.COLUMN_UNIT,1);//设置初始销售单位为1
        byte[] mImageByte;
        if (mSelectedImage != null){
            mImageByte = DbBitmapUtility.getBytes(mSelectedImage);
        }else{
            mImageView.setImageResource(R.drawable.empty_image);
            mImageByte = DbBitmapUtility.getBytes(((BitmapDrawable)mImageView.getDrawable()).getBitmap());
        }//检查是否添加了图片，如果没有则使用默认图片
        values.put(ProductEntry.COLUMN_IMAGE,mImageByte);
        Uri mNewUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);  // 保存新产品并反馈结果
        finish();
        if (mNewUri == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_saving), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap temporaryImage = BitmapFactory.decodeFile(filePath);
            mSelectedImage = DbBitmapUtility.getResizedBitmap(temporaryImage);
            mImageView.setImageBitmap(mSelectedImage);
        }
    }
}
