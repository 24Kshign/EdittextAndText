package com.share.edittextandtext.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import com.share.editandtext.widget.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 程 on 2016/3/21.
 */
public class ReceiveActivity1 extends BaseActivity {

    private static final String TAG = "ReceiveActivity1";

    @Bind(R.id.ar_tv_title)
    TextView mTvTitle;

    private static String[] mPic = {"http://d.lanrentuku.com/down/png/1406/40xiaodongwu/octopus.png"
            , "http://d.lanrentuku.com/down/png/1406/40xiaodongwu/crab.png"
            , "http://img1.zxxk.com/2011-12/ZXXKCOM201112301654462564258.png"};

    private String mHtml = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive1);
        ButterKnife.bind(this);
        mHtml = getIntent().getStringExtra("content");
        mTvTitle.setText(Html.fromHtml(mHtml));
    }
}
