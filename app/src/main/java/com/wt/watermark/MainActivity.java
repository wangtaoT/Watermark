/**
 * 图片添加水印
 */
package com.wt.watermark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PHOTO_REQUEST = 1;

    private EditText etAdd;
    private Button btnAdd;
    private ImageView imgSelect;
    private ImageView imgChange;
    private Button btnStart;

    private String string = null;
    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        etAdd = (EditText) findViewById(R.id.et_add);
        imgSelect = (ImageView) findViewById(R.id.img_select);
        imgSelect.setOnClickListener(this);
        imgChange = (ImageView) findViewById(R.id.img_change);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_select:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST);
                break;
            case R.id.btn_start:
                string = etAdd.getText().toString();
                if (TextUtils.isEmpty(string)) {
                } else {
                    if (uri == null) {
                    } else {
                        Bitmap bitmap = changeImage(uri, string);
                        imgChange.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }

    /**
     *
     */
    private Bitmap changeImage(Uri uri, String mark) {
        Bitmap photoBmp = null;
        try {
            photoBmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 当水印文字与水印图片都没有的时候，返回原图
        if (TextUtils.isEmpty(mark)) {
            return photoBmp;
        }

        // 获取图片的宽高
        int bitmapWidth = photoBmp.getWidth();
        int bitmapHeight = photoBmp.getHeight();

        // 创建一个和图片一样大的背景图
        Bitmap bmp = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        // 画背景图
        canvas.drawBitmap(photoBmp, 0, 0, null);

        // 文字开始的坐标,默认为左上角
        float textX = 0;
        float textY = 0;

        if (!TextUtils.isEmpty(mark)) {
            // 创建画笔
            Paint mPaint = new Paint();
            // 文字矩阵区域
            Rect textBounds = new Rect();
            // 获取屏幕的密度，用于设置文本大小
            //float scale = context.getResources().getDisplayMetrics().density;
            // 水印的字体大小
            //mPaint.setTextSize((int) (11 * scale));
            mPaint.setTextSize(100);
            // 文字阴影
            mPaint.setShadowLayer(0.5f, 0f, 1f, Color.BLACK);
            // 抗锯齿
            mPaint.setAntiAlias(true);
            // 水印的区域
            mPaint.getTextBounds(mark, 0, mark.length(), textBounds);
            // 水印的颜色
            mPaint.setColor(Color.WHITE);

            //当图片大小小于文字水印大小的3倍的时候，不绘制水印
            if (textBounds.width() > bitmapWidth / 3 || textBounds.height() > bitmapHeight / 3) {
                return photoBmp;
            }

            // 文字开始的坐标
            textX = bitmapWidth - textBounds.width() - 10;//这里的-10和下面的+6都是微调的结果
            textY = bitmapHeight - textBounds.height() + 6;
            // 画文字
            canvas.drawText(mark, textX, textY, mPaint);
        }

        //保存所有元素
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return bmp;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST:
                if (data != null) {
                    imgSelect.setImageURI(data.getData());
                    uri = data.getData();
                }
                break;
        }
    }
}
