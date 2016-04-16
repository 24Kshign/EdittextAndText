package com.share.edittextandtext.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import com.share.editandtext.widget.ImageUtils;
import com.share.editandtext.widget.MyProgressDialog;
import com.share.editandtext.widget.MyTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author:Created by JackCheng
 * Email:17764576259@163.com
 * Time:2016/2/22 16:33
 * Copyright:1.0
 */
public class ReceiveActivity extends BaseActivity {

    private static final String TAG = "ReceiveActivity";

    @Bind(R.id.ar_tv_content)
    MyTextView mTvContent;
    private String str = null;
    private MyProgressDialog mDialog;

    private List<Bitmap> mBitmaps = new ArrayList<>();
    private boolean isFinish = false;

    private ProgressDialog pd = null;

    public static String[] ivHead = {"http://d.hiphotos.baidu.com/image/w%3D310/sign=96d6652a4b540923aa69657fa259d1dc/b812c8fcc3cec3fde7643c7cd488d43f87942764.jpg",
            "http://b.hiphotos.baidu.com/image/w%3D310/sign=2895b073d31373f0f53f699e940e4b8b/86d6277f9e2f0708d1ee0208eb24b899a801f2cb.jpg",
            "http://e.hiphotos.baidu.com/image/w%3D310/sign=fa77e856d2160924dc25a41ae406359b/f703738da9773912ff910a61fa198618367ae27f.jpg",
            "http://a.hiphotos.baidu.com/image/w%3D310/sign=e334df7ff01fbe091c5ec5155b610c30/a044ad345982b2b7485dd9cd33adcbef77099bfd.jpg",
            "http://g.hiphotos.baidu.com/image/w%3D310/sign=354f5f660db30f24359aea02f894d192/dbb44aed2e738bd4fdda4636a38b87d6277ff93a.jpg"
            , "http://b.hiphotos.baidu.com/image/w%3D310/sign=93de9b0aca95d143da76e22243f18296/b21c8701a18b87d6f606dc16050828381f30fd77.jpg"};


    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                hideProgressDialog();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        ButterKnife.bind(this);
        str = getIntent().getStringExtra("contents");


        showProgressDialog("加载中....");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        }).start();

//        initeView();
    }

//    private void inite() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                isFinish = true;
//            }
//        }).start();
//
//        hideProgressDialog();
//    }


    class MyThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < ivHead.length; i++) {
                mBitmaps.add(returnBitMap(ivHead[i]));
            }
            isFinish = true;
        }
    }

    private void initeView() {
        if (str != null) {
//                    new MyAsyncTaskTest().execute("http://scimg.jb51.net/allimg/160317/14-16031G13220936.jpg");
            SpannableString spanString = new SpannableString(str);
            int cnt = 0;
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '★') {
                    Drawable d = new BitmapDrawable(mBitmaps.get(cnt++));
                    if (getScreenWidth() < d.getIntrinsicWidth()) {
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    } else {
                        d.setBounds((getScreenWidth() - d.getIntrinsicWidth()) / 2, 0
                                , d.getIntrinsicWidth() + (getScreenWidth() - d.getIntrinsicWidth()) / 2, d.getIntrinsicHeight());
                    }
                    ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                    if (i == str.length() - 1) {
                        spanString.setSpan(span, i - 1, i, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    spanString.setSpan(span, i, i + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            mTvContent.append(spanString);
            hideProgressDialog();
        }
    }


    private void showProgressDialog(String messege) {
        if (null == mDialog) {
            mDialog = MyProgressDialog.createProgrssDialog(this);
        }
        if (null != mDialog) {
            mDialog.setMessege(messege);
            mDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }
    }


    /**
     * @param urlpath
     * @return Bitmap
     * 根据url获取图片的Drawable
     */
    public static Drawable getDrawable(String urlpath) {
        Drawable d = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            d = Drawable.createFromStream(in, "background.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return d;
    }


    /**
     * @param url
     * @return 根据url获取图片的bitmap
     */
    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


//    private Bitmap getBitmap(String url) {
//        Bitmap bm = null;
//        URLConnection conn;
//        InputStream is;
//        try {
//            conn = new URL(url).openConnection();
//            is = conn.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
//            bm = BitmapFactory.decodeStream(bis);
//            is.close();
//            bis.close();
//            return bm;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
