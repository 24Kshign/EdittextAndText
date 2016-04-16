package com.share.edittextandtext.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.share.editandtext.widget.DisplayUtil;
import com.share.editandtext.widget.ImageUtils;
import com.share.editandtext.widget.MyEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author:Created by JackCheng
 * Email:17764576259@163.com
 * Time:2016/2/23 09:52
 * Copyright:1.0
 */
public class Test1Activity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "Test1Activity";

    Button mBtnInsert;
    Button mBtnFinish;
    MyEditText mEtContent;

    private static final int PHOTO_REQUEST_GALLERY2 = 2;   // 从相册中选择
    private ArrayList<Integer> mPicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        initeView();
        mBtnInsert.setOnClickListener(this);
        mBtnFinish.setOnClickListener(this);
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged_start=" + start);
                Log.d(TAG, "beforeTextChanged_count=" + count);
                Log.d(TAG, "beforeTextChanged_after=" + after);
                if (count > 2) {
                    if (mPicList.size() > 0) {
                        Log.d(TAG, "before_size=" + mPicList.size());
                        mPicList.remove(mPicList.size() - 1);
                        Log.d(TAG, "before_after=" + mPicList.size());
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged_start=" + start);
                Log.d(TAG, "onTextChanged_before=" + before);
                Log.d(TAG, "onTextChanged_count=" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged_s=" + s.toString());
            }
        });
    }

    private void initeView() {
        mBtnInsert = (Button) findViewById(R.id.at_btn_insert);
        mBtnFinish = (Button) findViewById(R.id.at_btn_finish);
        mEtContent = (MyEditText) findViewById(R.id.at_et_content);
    }

    @Override
    public void onClick(View v) {
        String strContent = mEtContent.getText().toString();
        switch (v.getId()) {
            case R.id.at_btn_insert:        //从图库中选图片
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY2);
                break;
            case R.id.at_btn_finish:
                intent = new Intent(Test1Activity.this, ReceiveActivity.class);
                intent.putExtra("contents", strContent);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_REQUEST_GALLERY2) {
                if (data == null) {
                    return;
                } else {
                    //data参数将会包含一个uri——统一资源标识符
                    //该uri就是我们选择的图片所对应的uri
                    Uri uri = data.getData();
                    Bitmap bitmap = getimage(getContentResolver(), uri);
                    Log.d(TAG, "Base64=" + ImageUtils.bitmaptoString(bitmap));
                    mEtContent.insertDrawable(mEtContent, bitmap, getScreenWidth(), "★");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap getimage(ContentResolver cr, Uri uri) {
        try {
            Bitmap bitmap = null;
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //图片不加载到内存中
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(cr.openInputStream(uri), null, newOpts);

            newOpts.inJustDecodeBounds = false;
            int imgWidth = newOpts.outWidth;
            int imgHeight = newOpts.outHeight;
            // 缩放比,1表示不缩放
            int scale = 1;
            if (imgWidth > imgHeight && imgWidth > getScreenWidth()) {
                scale = (int) (imgWidth / getScreenWidth());
            } else if (imgHeight > imgWidth && imgHeight > getScreenHeight()) {
                scale = (int) (imgHeight / getScreenHeight());
            }
            newOpts.inSampleSize = scale;// 设置缩放比例
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, newOpts);
            return ImageUtils.zoomImg(bitmap, imgWidth, imgHeight);
        } catch (Exception e) {
            System.out.println("文件不存在");
            return null;
        }
    }
}
