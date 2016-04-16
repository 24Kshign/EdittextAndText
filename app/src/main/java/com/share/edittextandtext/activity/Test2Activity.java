package com.share.edittextandtext.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.share.editandtext.widget.ImageUtils;
import com.share.editandtext.widget.MyOwnEditText;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author:Created by JackCheng
 * Email:17764576259@163.com
 * Time:2016/2/23 09:52
 * Copyright:1.0
 */
public class Test2Activity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "Test1Activity";

    @Bind(R.id.at_btn_insert)
    Button mBtnInsert;
    @Bind(R.id.at_btn_finish)
    Button mBtnFinish;
    @Bind(R.id.at_et_content)
    MyOwnEditText mEtContent;

    private static final int PHOTO_REQUEST_GALLERY2 = 2;   // 从相册中选择
    private static final int PHOTO_REQUEST_RESULT = 3;   // 结果
    private ArrayList<Integer> mPicIndexList = new ArrayList<Integer>();
    private ArrayList<String> mPicList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        ButterKnife.bind(this);

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

    @Override
    public void onClick(View v) {
        String strContent = mEtContent.getText().toString();
        switch (v.getId()) {
            case R.id.at_btn_insert:        //从图库中选图片
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY2);
                break;
            case R.id.at_btn_finish:
                intent = new Intent(Test2Activity.this, ReceiveActivity1.class);
                intent.putIntegerArrayListExtra("picIndexList", mPicIndexList);
                intent.putStringArrayListExtra("picIndexList", mPicList);
                intent.putExtra("content", strContent);
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
                    Bitmap bitmap = null;
                    try {
                        bitmap = ImageUtils.comp(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
                        Log.d(TAG, "Base64=" + ImageUtils.bitmaptoString(bitmap));
                        mPicList.add(ImageUtils.bitmaptoString(bitmap));
                        int pos = mEtContent.getText().toString().length();
                        mEtContent.insertDrawable(mEtContent, bitmap, getScreenWidth());
                        mPicIndexList.add(pos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //将content类型的uri转化成file类型的uri
    private Uri contentUriToFileUri(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            bitmap = ImageUtils.comp(bitmap);
            is.close();
            return saveHeadToSd(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //调用系统的裁剪
    private void startImageZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP"); //跳转到系统裁剪界面
        intent.setDataAndType(uri, "image/*");   //同时设置数据和类型
        intent.putExtra("crop", "true");   //true表示在显示的view中是可裁剪的
        intent.putExtra("aspectX", 1);   //要裁剪的宽高比
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);   //输入图片的宽高
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_RESULT);
    }

}
