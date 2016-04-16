package com.share.editandtext.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by 程 on 2016/3/31.
 */
public class URLImageParser implements Html.ImageGetter {

    Context context;
    EditText editText;

    /***
     * 构建URLImageParser将执行AsyncTask,刷新容器
     *
     * @param editText
     * @param context
     */
    public URLImageParser(EditText editText, Context context) {
        this.context = context;
        this.editText = editText;
    }


    @Override
    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();
        // 获得实际的源
        ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);
        asyncTask.execute(source);
        //返回引用URLDrawable我将改变从src与实际图像标记
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            // 设置正确的绑定根据HTTP调用的结果
            Log.d("height", "" + result.getIntrinsicHeight());
            Log.d("width", "" + result.getIntrinsicWidth());
            urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 + result.getIntrinsicHeight());
            // 改变当前可提取的参考结果从HTTP调用
            urlDrawable.drawable = result;
            // 绘制图像容器
            URLImageParser.this.editText.invalidate();
            // For ICS
            URLImageParser.this.editText.setHeight((URLImageParser.this.editText.getHeight() + result.getIntrinsicHeight()));
            // Pre ICS
            URLImageParser.this.editText.setEllipsize(null);
        }

        /***
         * 得到Drawable的URL
         *
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0
                        + drawable.getIntrinsicHeight());
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        private InputStream fetch(String urlString) throws IOException {
            ByteArrayInputStream is = new ByteArrayInputStream(urlString.getBytes("ISO-8859-1"));
            return is;
        }
    }

}
