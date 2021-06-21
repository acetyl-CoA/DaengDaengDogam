package com.example.dddg.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dddg.R;
import com.example.dddg.SearchActivity;

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

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View root;
    private String dog_name = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        TextView hometxt = (TextView) root.findViewById(R.id.text_homes);
        Button homebtn = (Button) root.findViewById(R.id.button_home);

        root.findViewById(R.id.add_dog_pict).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imgintent = new Intent();
                imgintent.setType("image/*");
                imgintent.setAction(Intent.ACTION_GET_CONTENT);   // for using gallery
                startActivityForResult(imgintent, 1);
            }
        });

        homebtn.setOnClickListener(new View.OnClickListener() {  // 검색 기능
            @Override
            public void onClick(View v) {
                if (dog_name != null) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("keyword", dog_name);
                    startActivity(intent);
                }
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intents) {
        super.onActivityResult(requestCode, resultCode, intents);
        if (requestCode != 1 || resultCode != -1)
            return;

        try {  // 정상 처리 시,
            InputStream stream = getActivity().getContentResolver().openInputStream(intents.getData());
            Bitmap bitdata = BitmapFactory.decodeStream(stream);
            stream.close();

            ImageView picture_view = root.findViewById(R.id.view_dog_pict);
            picture_view.setScaleType(ImageView.ScaleType.FIT_XY);  // 적절한 크기로 이미지 삽입
            picture_view.setImageBitmap(bitdata);

            // opencv를 활용한 이미지 데이터 처리 (4.5.2버전)
            Mat imgdata = new Mat();
            Utils.bitmapToMat(bitdata, imgdata);

            Size sz = new Size(100, 100);
            Imgproc.resize(imgdata, imgdata, sz);

            float[][][][] new_byte = new float[1][100][100][3];
            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 100; x++) {
                    double[] pixels = imgdata.get(x,y);
                    for (int k = 0; k < 3; k++)
                        new_byte[0][x][y][k] = (float)pixels[k] / 255;  // preprocessing
                }
            }

            // 모델 파일 로딩. assets 폴더 내 존재.
            Interpreter tf_int = getTfliteInterpreter("dog_clsf_tf.tflite");

            float[][] outs = new float[1][120];
            tf_int.run(new_byte, outs);  // 모델에 데이터를 적용

            // [0] : 아까 사진 1개의 결과.
            int maxI = 0;
            float maxP = 0;
            for (int i = 0; i < 120; i++) {
                if (outs[0][i] > maxP) {
                    maxP = outs[0][i];
                    maxI = i+1;
                }
            }

            // 견종 키값 읽기 csv 파일. assets 폴더 내 존재.
            InputStreamReader is = new InputStreamReader(getResources().getAssets().open("breed_data.csv"));

            BufferedReader buffread = new BufferedReader(is);
            buffread.readLine();
            String line;

            TextView hometxt = (TextView) root.findViewById(R.id.text_homes);
            while ((line = buffread.readLine()) != null) {
                String[] lines = line.split(",");
                if (maxI == Integer.parseInt(lines[0])) {
                    dog_name = lines[2];
                    String result_str = "본 개는 " + dog_name + "입니다.";
                    hometxt.setText(result_str);
                    break;
                }
            }


        } catch (Exception e) {  // 비정상 동작 시,
            TextView hometxt = (TextView) root.findViewById(R.id.text_homes);
            hometxt.setText("== 앱을 재실행해주시기 바랍니다. ==");
            dog_name = null;
            e.printStackTrace();
        }
    }

    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            //AssetManager assetManager = null;
            return new Interpreter(loadModelFile(getActivity(), modelPath));
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

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully!");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private boolean isOpenCvLoaded = false;
    private final static String TAG = HomeFragment.class.getSimpleName();

    @Override
    public void onResume()
    {
        super.onResume();

        // OpenCV load
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, getContext(), mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            isOpenCvLoaded = true;
        }
    }
}