package com.example.dddg;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.ItemViewHolder>{
    private ArrayList<YoutubeItem> listY = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listY.get(position));

    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listY.size();
    }

    void addItem(YoutubeItem data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listY.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView textViewT;
        private YoutubeItem youtubedata;



        ItemViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.youtube_img);
            textViewT = itemView.findViewById(R.id.youtube_title);

        }

        void onBind(YoutubeItem data) {
            this.youtubedata = data;


            imageView.setImageBitmap(data.getThumbnail());
            textViewT.setText(data.getTitle());

            imageView.setOnClickListener(this);
            textViewT.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubedata.getLink()));
            v.getContext().startActivity(webIntent);

        }
    }
}
