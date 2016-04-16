package com.share.edittextandtext.activity;

import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.share.editandtext.widget.ImageUtils;
import com.share.editandtext.widget.URLImageParser;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 程 on 2016/3/31.
 */
public class DemanActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "DemanActivity";

    @Bind(R.id.ad_btn_insert)
    Button mBtnInsert;
    @Bind(R.id.ad_btn_finish)
    Button mBtnFinish;
    @Bind(R.id.ad_et_content)
    EditText mEtContent;

    private final int INSERTIMG_CODE = 502;
    private ContentResolver contentresolver;
    private ArrayList<String> filepathes = new ArrayList<String>();

    private static String[] mPic = {"http://d.lanrentuku.com/down/png/1406/40xiaodongwu/octopus.png"
            , "http://d.lanrentuku.com/down/png/1406/40xiaodongwu/crab.png"
            , "http://img1.zxxk.com/2011-12/ZXXKCOM201112301654462564258.png"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deman);
        ButterKnife.bind(this);
        initeView();
    }

    private void initeView() {
        contentresolver = DemanActivity.this.getContentResolver();
        mBtnInsert.setOnClickListener(this);
        mBtnFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ad_btn_insert:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, INSERTIMG_CODE);
                break;
            case R.id.ad_btn_finish:
                Editable eb = mEtContent.getEditableText();
                Log.d(TAG, "content=" + Html.toHtml(eb));
                startActivity(new Intent(DemanActivity.this, ReceiveActivity1.class).putExtra("content", Html.toHtml(eb)));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        // 插图
        if (resultCode == RESULT_OK && requestCode == INSERTIMG_CODE) {
            Uri uri = data.getData();
            if (uri != null) {
                filepathes.add(getRealPathFromURI(uri));
            }
            Editable eb = mEtContent.getEditableText();
            // 获得光标所在位置
            int startPosition = mEtContent.getSelectionStart();
            eb.insert(startPosition, Html.fromHtml("<br/><img src='" + mPic[0]
                    + "'/><br/>", imageGetter, null));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            try {
                Uri uri = Uri.parse(source);
                Bitmap bitmap = getimage(contentresolver, uri);
                return getMyDrawable(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    private Drawable getMyDrawable(Bitmap bitmap) {
        Drawable d = new BitmapDrawable(bitmap);
        if (getScreenWidth() < d.getIntrinsicWidth()) {
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        } else {
            d.setBounds((getScreenWidth() - d.getIntrinsicWidth()) / 2, 0
                    , d.getIntrinsicWidth() + (getScreenWidth() - d.getIntrinsicWidth()) / 2, d.getIntrinsicHeight());
        }
        return d;
    }

    private Bitmap getimage(ContentResolver cr, Uri uri) {
        try {
            Bitmap bitmap = null;
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // options.inJustDecodeBounds=true,图片不加载到内存中
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
            return bitmap;
        } catch (Exception e) {
            System.out.println("文件不存在");
            return null;
        }
    }

    /**
     * 从uri获取文件路径,uri以content开始
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
