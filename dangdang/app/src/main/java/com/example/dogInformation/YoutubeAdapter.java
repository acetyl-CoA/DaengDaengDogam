package com.example.dogInformation;

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
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewT;
        private TextView textViewD;


        ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.youtube_img);
            textViewT = itemView.findViewById(R.id.youtube_title);

        }

        void onBind(YoutubeItem data) {
            imageView.setImageBitmap(data.getThumbnail());
            textViewT.setText(data.getTitle());
        }
    }
}
