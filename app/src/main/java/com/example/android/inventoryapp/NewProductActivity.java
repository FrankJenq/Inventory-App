package com.example.android.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private static final int MY_PERMISSIONS_REQUEST_USE_STORAGE = 1;
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mContactEditText;
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
        mContactEditText = findViewById(R.id.new_product_contact);
        mUploadImageTextView = findViewById(R.id.upload_image);
        mImageView = findViewById(R.id.image_select);
        mImageView.setVisibility(View.GONE);
        mUploadImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testUseExternalStorage(v);
                mUploadImageTextView.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }
        });
        // 点击ImageView可以上传图片
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectImage();
            }
        });//点击选好的图片可以重新进行选择

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
        String mContactString = mContactEditText.getText().toString();
        if (TextUtils.isEmpty(mNameString + mQuantityString + mPriceString + mContactString)
                && mSelectedImage == null) {
            finish();
            return;
        }// 检查是否所有项目都没有输入内容
        // 如果是这样那么直接返回ListActivity
        if (TextUtils.isEmpty(mNameString) || TextUtils.isEmpty(mQuantityString) ||
                TextUtils.isEmpty(mPriceString) || TextUtils.isEmpty(mContactString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_empty_name), Toast.LENGTH_SHORT).show();
            return;
        }//有输入项为空时显示提示信息

        int quantity;
        try {
            quantity = Integer.parseInt(mQuantityString);
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_quantity), Toast.LENGTH_SHORT).show();
            return;
        }//检查库存数据是否有效

        int price;
        try {
            price = Integer.parseInt(mPriceString);
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_price), Toast.LENGTH_SHORT).show();
            return;
        }//检查价格数据是否有效

        if (!isValidEmail(mContactString)) {
            Toast.makeText(getApplicationContext(), R.string.invalid_email_address, Toast.LENGTH_SHORT).show();
            return;
        }//检查Email地址是否有效

        values.put(ProductEntry.COLUMN_NAME, mNameString.trim());
        values.put(ProductEntry.COLUMN_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRICE, price);
        values.put(ProductEntry.COLUMN_SALES, 0);//设置初始销量为0
        values.put(ProductEntry.COLUMN_UNIT, 1);//设置初始销售单位为1
        values.put(ProductEntry.COLUMN_CONTACT, mContactString.trim());
        byte[] mImageByte;
        if (mSelectedImage != null) {
            mImageByte = DbBitmapUtility.getBytes(mSelectedImage);
        } else {
            mImageView.setImageResource(R.drawable.empty_image);
            mImageByte = DbBitmapUtility.getBytes(((BitmapDrawable) mImageView.getDrawable()).getBitmap());
        }//检查是否添加了图片，如果没有则使用默认图片
        values.put(ProductEntry.COLUMN_IMAGE, mImageByte);
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

    /**
     * 检查Email地址是否有效
     *
     * @param target 输入的Email地址
     * @return
     */
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void testUseExternalStorage(View view) {
        //检查系统版本
        // 对Android 6.0以上的系统检查是否具有读写扩展存储的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_USE_STORAGE);
            } else {
                selectImage();
            }
        } else{//如果系统版本低于Android 6.0则不进行权限检查
            selectImage();
        }
    }

    //从设备上选择图片
    public void selectImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_USE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                // Permission Denied
                Toast.makeText(NewProductActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
