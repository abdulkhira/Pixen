package com.pixen.videos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixen.videos.R;
import com.pixen.videos.model.Home_Get_Set;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;



public class Watch_Videos_Adapter extends RecyclerView.Adapter<Watch_Videos_Adapter.CustomViewHolder > {

    private Context context;
    private Watch_Videos_Adapter.OnItemClickListener listener;
    private ArrayList<Home_Get_Set> dataList;



    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int positon, Home_Get_Set item, View view);
    }

    public Watch_Videos_Adapter(Context context, ArrayList<Home_Get_Set> dataList, Watch_Videos_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public Watch_Videos_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_watch_layout,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        return new CustomViewHolder(view);
    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Watch_Videos_Adapter.CustomViewHolder holder, final int i) {
        final Home_Get_Set item= dataList.get(i);

        holder.bind(i,item,listener);

        if(item.liked.equals("1")){
            holder.like_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
        } else {
            holder.like_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
        }


        holder.like_txt.setText(item.like_count);
        holder.viewText.setText(item.views);
    }


    static class CustomViewHolder extends RecyclerView.ViewHolder {

        PlayerView playerview;

        LinearLayout like_layout,shared_layout, download_layout;
        ImageView like_image;
        TextView like_txt, viewText;


        CustomViewHolder(View view) {
            super(view);

            playerview=view.findViewById(R.id.playerview);

            like_layout=view.findViewById(R.id.like_layout);
            like_image=view.findViewById(R.id.like_image);
            like_txt=view.findViewById(R.id.like_txt);
            viewText=view.findViewById(R.id.view_txt);

            shared_layout=view.findViewById(R.id.shared_layout);
            download_layout=view.findViewById(R.id.download_layout);
        }

        void bind(final int postion, final Home_Get_Set item, final Watch_Videos_Adapter.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(postion,item,v);
                }
            });

            like_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onItemClick(postion,item,v);
                }
            });

            shared_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onItemClick(postion,item,v);
                }
            });

            download_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onItemClick(postion,item,v);
                }
            });

        }


    }


}