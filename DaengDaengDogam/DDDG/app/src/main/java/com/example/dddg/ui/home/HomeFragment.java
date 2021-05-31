package com.example.dddg.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.dddg.R;

import java.io.InputStream;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);/*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        TextView hometxt = (TextView) root.findViewById(R.id.text_homes);
        Button homebtn = (Button) root.findViewById(R.id.button_home);
        EditText homeedit = (EditText) root.findViewById(R.id.edit_home);
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hometxt.setText(homeedit.getText().toString());
            }
        });

        root.findViewById(R.id.add_dog_pict).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);   // for using gallery
                startActivityForResult(intent, 1);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intents) {
        super.onActivityResult(requestCode, resultCode, intents);
        if (requestCode != 1 || resultCode != -1)
            return;

        try {
            InputStream stream = getActivity().getContentResolver().openInputStream(intents.getData());
            Bitmap data = BitmapFactory.decodeStream(stream);
            stream.close();

            ImageView picture_view = root.findViewById(R.id.view_dog_pict);
            picture_view.setScaleType(ImageView.ScaleType.FIT_XY);  // 100 by 100 size.
            picture_view.setImageBitmap(data);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}