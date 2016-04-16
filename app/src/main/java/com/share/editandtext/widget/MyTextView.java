package com.share.editandtext.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by 程 on 2016/3/31.
 */
public class MyTextView extends TextView {

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void insertDrawable(TextView textView, Bitmap bitmap, int index, int screenWidth) {
        final SpannableString ss = new SpannableString(" ");
        Drawable d = new BitmapDrawable(bitmap);
        Log.d("Edittext---->", "picWidth=" + d.getIntrinsicWidth());
        if (screenWidth < d.getIntrinsicWidth()) {
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        } else {
            d.setBounds((screenWidth - d.getIntrinsicWidth()) / 2, 0
                    , d.getIntrinsicWidth() + (screenWidth - d.getIntrinsicWidth()) / 2, d.getIntrinsicHeight());
        }
        //用这个drawable对象代替字符串easy
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        //包括0但是不包括path.length()即：4。[0,4)。实值得注意的是当我们复制这个图片的时候，际是复制了"easy"这个字符串。
        Log.d("MyTextView----->", "index=" + index);
        ss.setSpan(span, index - 1, index, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        if (!textView.getText().toString().equals("")) {
            append("\n");
        }
        append(ss);
        append("\n");
    }
}
