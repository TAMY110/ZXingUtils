package com.example.zxingutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private EditText tv_qrCode_content;//用来生成二维码图片内包含的内容
    private TextView tv_click;//按钮
    private TextView tv;//按钮

    private ImageView iv_qr_code;//显示二维码的ImageView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();

        tv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = tv_qrCode_content.getText().toString().trim();
                if(TextUtils.isEmpty(content)){
                    Toast.makeText(MainActivity.this,"请先输入需要生成二维码的内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                Bitmap bitmap = createQRImage(content, iv_qr_code.getWidth(), iv_qr_code.getHeight(), BitmapFactory.decodeResource(getResources(), R.mipmap.logm));
                iv_qr_code.setImageBitmap(bitmap);
                tv.setText(content);
            }
        });
    }
    private void findView() {
        tv_qrCode_content= (EditText) findViewById(R.id.tv_qrCode_content);

        iv_qr_code= (ImageView) findViewById(R.id.iv_qr_code);

        tv_click= (TextView) findViewById(R.id.tv_click);

        tv = findViewById(R.id.tv);

    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }


    public static Bitmap createQRImage(String url, final int width, final int height,Bitmap logoBm) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;

    }
}