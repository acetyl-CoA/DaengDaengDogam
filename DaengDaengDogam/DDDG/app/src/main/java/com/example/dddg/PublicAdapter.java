package com.example.dddg;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.ItemViewHolder>{
    private ArrayList<PublicItem> listP = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listP.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listP.size();

    }

    void addItem(PublicItem data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listP.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView textViewKC;
        private TextView textViewCO;
        private TextView textViewWT;
        private TextView textViewHP;
        private TextView textViewHD;
        private TextView textViewNE;
        private PublicItem publicdata;



        private TextView textView;

        ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.public_img);
            textViewKC = itemView.findViewById(R.id.public_kindcd);
            textViewCO = itemView.findViewById(R.id.public_color);
            textViewWT = itemView.findViewById(R.id.public_weight);
            textViewHP = itemView.findViewById(R.id.public_happenplace);
            textViewHD = itemView.findViewById(R.id.public_happendt);
            textViewNE = itemView.findViewById(R.id.public_noticeedt);
        }

        void onBind(PublicItem data) {
            this.publicdata = data;
            imageView.setImageBitmap(data.getImage());
            textViewKC.setText(data.getKindCd());
            textViewCO.setText(data.getColorCd());
            textViewWT.setText(data.getWeight());
            textViewHP.setText(data.getHappenPlace());
            textViewHD.setText(data.getHappenDt());
            textViewNE.setText(data.getNoticeEdt());

            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent sendintent = new Intent(v.getContext(), PublicDetail.class);
            sendintent.putExtra("age",publicdata.getAge());//
            sendintent.putExtra("careNm",publicdata.getCareNm());//
            sendintent.putExtra("careTel",publicdata.getCareTel());//
            sendintent.putExtra("colorCd",publicdata.getColorCd());//
            sendintent.putExtra("desertionNm",publicdata.getDesertionNm());//
            sendintent.putExtra("happenDt",publicdata.getHappenDt());//
            sendintent.putExtra("happenPlace",publicdata.getHappenPlace());//
            sendintent.putExtra("kindCd",publicdata.getKindCd());//
            sendintent.putExtra("neuterYn",publicdata.getNeuterYn());
            sendintent.putExtra("noticeEdt",publicdata.getNoticeEdt());//
            sendintent.putExtra("noticeNo",publicdata.getNoticeNo());
            sendintent.putExtra("noticeSdt",publicdata.getNoticeSdt());
            sendintent.putExtra("sexCd",publicdata.getSexCd());
            sendintent.putExtra("feature",publicdata.getFeature());
            sendintent.putExtra("weight",publicdata.getWeight());//

            v.getContext().startActivity(sendintent);



        }
    }
}
