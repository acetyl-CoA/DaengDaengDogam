package com.example.dddg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PublicDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_detail_activity);

        TextView detailkind =(TextView)findViewById(R.id.public_detail_kindcd);

        TextView detailcolor =(TextView)findViewById(R.id.public_detail_color);
        TextView detailhappenplace =(TextView)findViewById(R.id.public_detail_happenplace);
        TextView detailhappendt =(TextView)findViewById(R.id.public_detail_happendt);
        TextView detailweight =(TextView)findViewById(R.id.public_detail_weight);
        TextView detailnoticeedt =(TextView)findViewById(R.id.public_detail_noticeedt);
        TextView detailage =(TextView)findViewById(R.id.public_detail_age);
        TextView detailcarenm =(TextView)findViewById(R.id.public_detail_careNm);
        TextView detailcaretel =(TextView)findViewById(R.id.public_detail_careTel);
        TextView detaildesertionNm =(TextView)findViewById(R.id.public_detail_desertionNm);
        TextView detailneuterYn =(TextView)findViewById(R.id.public_detail_neuterYn);
        TextView detailnoticeNo =(TextView)findViewById(R.id.public_detail_noticeNo);
        TextView detailnoticesdt =(TextView)findViewById(R.id.public_detail_noticeSdt);
        TextView detailsex =(TextView)findViewById(R.id.public_detail_sexCd);
        TextView detailfeature =(TextView)findViewById(R.id.public_detail_feature);



        Intent intent = getIntent();


        detailkind.setText(intent.getStringExtra("kindCd"));
        detailcolor.setText(intent.getStringExtra("colorCd"));
        detailweight.setText(intent.getStringExtra("weight"));
        detailhappenplace.setText(intent.getStringExtra("happenPlace"));
        detailhappendt.setText(intent.getStringExtra("happenDt"));
        detailnoticeedt.setText(intent.getStringExtra("noticeEdt"));
        detailage.setText(intent.getStringExtra("age"));
        detailcarenm.setText(intent.getStringExtra("careNm"));
        detailcaretel.setText(intent.getStringExtra("careTel"));
        detaildesertionNm.setText(intent.getStringExtra("desertionNm"));
        detailneuterYn.setText(intent.getStringExtra("neuterYn"));
        detailnoticeNo.setText(intent.getStringExtra("noticeNo"));
        detailnoticesdt.setText(intent.getStringExtra("noticeSdt"));
        detailsex.setText(intent.getStringExtra("sexCd"));
        detailfeature.setText(intent.getStringExtra("feature"));




    }
}