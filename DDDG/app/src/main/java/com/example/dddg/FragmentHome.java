package com.example.dddg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentHome extends Fragment {
    TextView hometxt;
    Button homebtn;
    EditText homeedit;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        hometxt = (TextView) view.findViewById(R.id.text_homes);
        homebtn = (Button) view.findViewById(R.id.button_home);
        homeedit = (EditText) view.findViewById(R.id.edit_home);
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hometxt.setText(homeedit.getText().toString());
            }
        });

        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
