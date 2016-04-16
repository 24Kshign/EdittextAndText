package com.share.edittextandtext.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author:Created by JackCheng
 * Email:17764576259@163.com
 * Time:2016/1/20 19:54
 * Copyright:1.0
 */
public class BaseActivity extends Activity {

    private static Toast mToast;
    private InputMethodManager inputMethodManager;
    private static Handler mHandler = new Handler();
    DisplayMetrics dm = new DisplayMetrics();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    //根据输入框中的内容是否为空来判断按钮是否显示
    public void buttonShowOrNot(EditText editText, Button button) {
        String strContent = editText.getText().toString();
        if (!strContent.isEmpty()) {
            showView(button);
        } else {
            hideView(button);
        }
    }

    //隐藏控件
    public void hideView(View v) {
        v.setVisibility(v.INVISIBLE);
    }

    //显示控件
    public void showView(View v) {
        v.setVisibility(v.VISIBLE);
    }

    //弹出提示框
    public void showToast(String str) {
        mHandler.removeCallbacks(r);
        if (mToast != null) {
            mToast.setText(str);
        } else {
            mToast = Toast.makeText(this, str, 1000);
        }
        mHandler.postDelayed(r, 1000);
        mToast.show();
    }

    //弹出提示框
    public void showToast(int resId) {
        String str = this.getResources().getString(resId);
        mHandler.removeCallbacks(r);
        if (mToast != null) {
            mToast.setText(str);
        } else {
            mToast = Toast.makeText(this, str, 1000);
        }
        mHandler.postDelayed(r, 1000);
        mToast.show();
    }

    //让按钮不可点击
    public void disableButton(Button button) {
        button.setEnabled(false);
    }

    //让按钮不可点击
    public void enableButton(Button button) {
        button.setEnabled(true);
    }

    /**
     * 根据EditText文本内容是否为空判断是否disable按钮
     * 如果有一个EditText文本内容为空，则disable按钮
     */
    public void enableButtonOrNot(final Button button, final EditText... editText) {
        int length = editText.length; // 表示传入的EditText的个数
        final boolean[] flag = new boolean[length]; // 表示各个edittext文本内容是否为空，false表示空，true表示非空
        for (int i = 0; i < length; i++) {
            final int finalI = i;
            editText[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (editText[finalI].getText().toString().length() == 0) {
                        flag[finalI] = false;
                    } else {
                        flag[finalI] = true;
                    }
                    if (allNotEmpty(flag)) {
                        enableButton(button);
                    } else {
                        disableButton(button);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    /**
     * 判断是不是所有的标记表示的都是非空
     * 如果有一个表示的为空，则返回false
     * 否则，返回true
     */
    private boolean allNotEmpty(boolean[] flag) {
        for (int i = 0; i < flag.length; i++) {
            if (false == flag[i])
                return false;
        }
        return true;
    }

    /**
     * 触屏事件
     * 为隐藏软键盘做判断
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (null != getCurrentFocus() && null != getCurrentFocus().getWindowToken()) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 隐藏软键盘
     */
    protected void hideKeyboard() {
        if (null != getCurrentFocus() && null != getCurrentFocus().getWindowToken()) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //判断键盘是否弹出
    public boolean isKeyboard() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
            return true;
        }
        return false;
    }

    //将bitmap保存到sd卡中，并返回一个路径
    public static Uri saveHeadToSd(Bitmap bt) {
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/com.");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        File img = new File(tmpDir.getAbsolutePath() + "avater.png");
        if (img.exists()) {
            img.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bt.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取屏幕宽度
    public int getScreenWidth() {
        return dm.widthPixels;
    }

    //获取屏幕高度
    public int getScreenHeight() {
        return dm.heightPixels;
    }
}
