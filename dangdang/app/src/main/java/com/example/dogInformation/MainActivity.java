package com.example.dogInformation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.searchBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override // 익명 클래스로
            public void onClick(View v) {


                TextView searchText = (TextView) findViewById(R.id.searchText);
                //final TextView searchResult = (TextView) findViewById(R.id.searchResult);
                String keyword = searchText.getText().toString();

                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
                //searchResult.setText(str);


            }
        });
    }
}