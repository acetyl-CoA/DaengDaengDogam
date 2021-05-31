package com.example.tftest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private static final int FROM_ALBUM = 1;    // onActivityResult 식별자
    private static final int FROM_CAMERA = 2;   // 카메라는 사용 안함

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");                      // 이미지만
                intent.setAction(Intent.ACTION_GET_CONTENT);    // 카메라(ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, FROM_ALBUM);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 카메라를 다루지 않기 때문에 앨범 상수에 대해서 성공한 경우에 대해서만 처리
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != FROM_ALBUM || resultCode != RESULT_OK)
            return;

        try {
            // 선택한 이미지에서 비트맵 생성
            InputStream stream = getContentResolver().openInputStream(data.getData());
            Bitmap bmp = BitmapFactory.decodeStream(stream);
            stream.close();
            /*
            ImageView iv = findViewById(R.id.photo);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);    // [100, 100]에 꽉 차게 표시
            iv.setImageBitmap(bmp);

            TextView tv = findViewById(R.id.textView);
            tv.setText(String.format("%.5f", 0.123456789));

            float[][] bytes_img = new float[1][10000];

            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 100; x++) {
                    int pixel = bmp.getPixel(x, y);
                    bytes_img[0][y * 100 + x] = (pixel & 0xff) / (float) 255;
                }
            }
             */

            ImageView iv = findViewById(R.id.photo);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);    // [100, 100]에 꽉 차게 표시
            iv.setImageBitmap(bmp);

            float[][][][] bytes_img = new float[1][100][100][3];

            float[] testf = new float[3];
            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 100; x++) {
                    int pixel = bmp.getPixel(x, y);
                    bytes_img[0][x][y][0] = Color.red(pixel) / (float)255;
                    bytes_img[0][x][y][1] = Color.green(pixel) / (float)255;
                    bytes_img[0][x][y][2] = Color.blue(pixel) / (float)255;
                    if (x == 0 && y == 0) {
                        testf[0] = Color.red(pixel);
                        testf[1] = Color.green(pixel);
                        testf[2] = Color.blue(pixel);
                    }
                }
            }

            // 파이썬에서 만든 모델 파일 로딩
            Interpreter tf_lite = getTfliteInterpreter("dog_clsf_tf.tflite");

            float[][] output = new float[1][120];
            tf_lite.run(bytes_img, output);

            Log.d("predict", Arrays.toString(output[0]));

            // [0] : 아까 사진 1개의 결과.
            TextView tv = findViewById(R.id.textView);
            StringBuffer strbuf = new StringBuffer();
            int maxI1 = 0;
            float maxP = 0;
            for (int i = 0; i < 120; i++) {
                strbuf.append(Integer.toString(i+1) + ":");
                strbuf.append(String.format("%.5f", output[0][i]));
                strbuf.append("  ");
                if (output[0][i] > maxP) {
                    maxP = output[0][i];
                    maxI1 = i+1;
                }
            }
            tv.setText(strbuf);

            TextView rslt = findViewById(R.id.resultShow);
            rslt.setText(Integer.toString(maxI1) + " - " + Float.toString(maxP) + "\n" +
                    Float.toString(bytes_img[0][0][0][0]) + " " + Float.toString(bytes_img[0][0][0][1]) + " " + Float.toString(bytes_img[0][0][0][2]) + "\n" +
                    Float.toString(testf[0]) + " " + Float.toString(testf[1]) + " " + Float.toString(testf[2]));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(MainActivity.this, modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /** Load TF Lite model from assets. */
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}