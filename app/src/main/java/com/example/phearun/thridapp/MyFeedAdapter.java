package com.example.phearun.thridapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Phearun on 12/14/2016.
 */

public class MyFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Feed> feeds;
    private Context context;
    private ItemClickListener itemClickListener;

    public MyFeedAdapter(List<Feed> feeds, Context context){
        this.feeds = feeds;
        this.context = context;
        this.itemClickListener = (ItemClickListener) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_row, parent, false);
        return new FeedHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Feed feed = feeds.get(position);
        FeedHolder feedHolder = (FeedHolder) holder;
        feedHolder.txtUsername.setText(feed.getUsername());
        feedHolder.txtStatus.setText(feed.getText());
        feedHolder.btnLike.setText(feed.getLike() + " Like");
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    class FeedHolder extends RecyclerView.ViewHolder{
        TextView txtUsername, txtStatus;
        Button btnRemove, btnLike;

        public FeedHolder(View itemView) {
            super(itemView);
            txtStatus = (TextView) itemView.findViewById(R.id.txtstatus);
            txtUsername = (TextView) itemView.findViewById(R.id.txtusername);
            btnRemove = (Button) itemView.findViewById(R.id.btnRemove);
            btnLike = (Button) itemView.findViewById(R.id.btnLike);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onLikeClick(getAdapterPosition());
                }
            });
        }
    }

}
