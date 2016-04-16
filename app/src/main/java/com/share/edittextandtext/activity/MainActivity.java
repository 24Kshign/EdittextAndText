package com.share.edittextandtext.activity;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static final String TAG = "ImgLabelActivity";
    /**
     * 网络图片
     */
    private TextView mTvThree;
    /**
     * 网络图片name
     */
    private String picName = "networkPic.jpg";
    /**
     * 网络图片Getter
     */
    private NetworkImageGetter mImageGetter;
    /**
     * 网络图片路径
     */
    private String htmlThree = "网络图片测试：" + "<img src='http://img.my.csdn.net/uploads/201307/14/1373780364_7576.jpg'>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initeView();
    }

    private void initeView() {
        mTvThree = (TextView) this.findViewById(R.id.tv_img_label_one);
        mImageGetter = new NetworkImageGetter();
        mTvThree.setText(Html.fromHtml(htmlThree, mImageGetter, null));
    }

    /**
     * 网络图片
     *
     * @author Susie
     */
    private final class NetworkImageGetter implements Html.ImageGetter {
        @Override
        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            // 封装路径
            File file = new File(Environment.getExternalStorageDirectory(), picName);
            // 判断是否以http开头
            if (source.startsWith("http")) {
                // 判断路径是否存在
                if (file.exists()) {
                    // 存在即获取drawable
                    drawable = Drawable.createFromPath(file.getAbsolutePath());
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                } else {
                    // 不存在即开启异步任务加载网络图片
                    AsyncLoadNetworkPic networkPic = new AsyncLoadNetworkPic();
                    networkPic.execute(source);
                }
            }
            return drawable;
        }
    }


    /**
     * 加载网络图片异步类
     *
     * @author Susie
     */
    private final class AsyncLoadNetworkPic extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            // 加载网络图片
            loadNetPic(params);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // 当执行完成后再次为其设置一次
            mTvThree.setText(Html.fromHtml(htmlThree, mImageGetter, null));
        }

        /**
         * 加载网络图片
         */
        private void loadNetPic(String... params) {
            String path = params[0];

            File file = new File(Environment.getExternalStorageDirectory(), picName);

            InputStream in = null;

            FileOutputStream out = null;

            try {
                URL url = new URL(path);

                HttpURLConnection connUrl = (HttpURLConnection) url.openConnection();

                connUrl.setConnectTimeout(5000);

                connUrl.setRequestMethod("GET");

                if (connUrl.getResponseCode() == 200) {

                    in = connUrl.getInputStream();

                    out = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];

                    int len;

                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                } else {
                    Log.i(TAG, connUrl.getResponseCode() + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
