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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            TextView cvtv = findViewById(R.id.cvtv);

            InputStream stream2 = getContentResolver().openInputStream(data.getData());
            Bitmap bmp2 = BitmapFactory.decodeStream(stream2);
            stream2.close();

            ImageView iv = findViewById(R.id.photo);
            iv.setImageBitmap(bmp2);


            TextView adas = findViewById(R.id.adas);

            Mat imgdata = new Mat();
            Utils.bitmapToMat(bmp2, imgdata);

            Size sz = new Size(100, 100);
            Imgproc.resize(imgdata, imgdata, sz);

            double[] dbdata = imgdata.get(0,0);
            adas.setText(Double.toString(dbdata[0]) + " " + Double.toString(dbdata[1]) + " " + Double.toString(dbdata[2]));


            float[][][][] new_byte = new float[1][100][100][3];

            float[][] tester = new float[4][3];
            int k = 0;
            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 100; x++) {
                    double[] pixels = imgdata.get(x,y);
                    new_byte[0][x][y][0] = (float)pixels[0] / 255;
                    new_byte[0][x][y][1] = (float)pixels[1] / 255;
                    new_byte[0][x][y][2] = (float)pixels[2] / 255;
                    if ((x == 0 || x == 99) && (y == 0 || y == 99)) {
                        tester[k][0] = (float)pixels[0];
                        tester[k][1] = (float)pixels[1];
                        tester[k][2] = (float)pixels[2];
                        k++;
                    }
                }
            }

            // 파이썬에서 만든 모델 파일 로딩
            Interpreter tf_int = getTfliteInterpreter("dog_clsf_tf.tflite");

            float[][] outs = new float[1][120];

            tf_int.run(new_byte, outs);

            Log.d("predict", Arrays.toString(outs[0]));

            // [0] : 아까 사진 1개의 결과.
            StringBuffer strbuffff = new StringBuffer();
            int maxI12 = 0;
            float maxP2 = 0;
            for (int i = 0; i < 120; i++) {
                strbuffff.append(Integer.toString(i+1) + ":");
                strbuffff.append(String.format("%.5f", outs[0][i]));
                strbuffff.append("  ");
                if (outs[0][i] > maxP2) {
                    maxP2 = outs[0][i];
                    maxI12 = i+1;
                }
            }

            cvtv.setText(strbuffff);
            adas.setText("\n" + Integer.toString(maxI12) + " - " + Float.toString(maxP2) + "\n\n" +
                    Float.toString(new_byte[0][0][0][0]) + " " + Float.toString(new_byte[0][0][0][1]) + " " + Float.toString(new_byte[0][0][0][2]) + "\n" +
                    Float.toString(tester[0][0]) + " " + Float.toString(tester[0][1]) + " " + Float.toString(tester[0][2]) + "\n" +
                    Float.toString(tester[1][0]) + " " + Float.toString(tester[1][1]) + " " + Float.toString(tester[1][2]) + "\n" +
                    Float.toString(tester[2][0]) + " " + Float.toString(tester[2][1]) + " " + Float.toString(tester[2][2]) + "\n" +
                    Float.toString(tester[3][0]) + " " + Float.toString(tester[3][1]) + " " + Float.toString(tester[3][2]) + "\n"
            );

            // 키값 읽기 csv파일.
            InputStreamReader is = new InputStreamReader(getAssets().open("breed_data.csv"));

            BufferedReader readeres = new BufferedReader(is);
            readeres.readLine();
            String line;

            TextView rs = findViewById(R.id.resultShow);
            while ((line = readeres.readLine()) != null) {
                String[] lines = line.split(",");
                if (maxI12 == Integer.parseInt(lines[0])) {
                    rs.setText("본 개는 " + lines[2] + "입니다.");
                }
            }



        } catch (Exception e) {
            TextView rs = findViewById(R.id.resultShow);
            rs.setText("어플을 재시작 해주시길 바랍니다.");

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

    // LoaderCallback
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();

        // OpenCV load
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            isOpenCvLoaded = true;
        }
    }

    private final static String TAG = MainActivity.class.getClass().getSimpleName();
    private boolean isOpenCvLoaded = false;
}