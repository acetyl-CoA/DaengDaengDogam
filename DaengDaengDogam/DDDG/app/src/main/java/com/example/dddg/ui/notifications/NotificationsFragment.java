package com.example.dddg.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dddg.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        InputStreamReader is = null;
        try {
            // csv 읽기
            is = new InputStreamReader(getResources().getAssets().open("breed_data.csv"));
            BufferedReader buffread = new BufferedReader(is);
            buffread.readLine();
            String line;

            // 자료 저장
            List<String> datalst = new ArrayList<>();
            while ((line = buffread.readLine()) != null) {
                String[] lines = line.split(",");
                datalst.add(lines[0] + " ▶ " + lines[2]);
            }

            // 목록 출력
            ListView breed_list = root.findViewById(R.id.breed_list);

            ArrayAdapter<String> adpt = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, datalst);
            breed_list.setAdapter(adpt);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return root;
    }
}