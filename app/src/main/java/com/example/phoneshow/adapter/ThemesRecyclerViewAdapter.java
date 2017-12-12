package com.example.phoneshow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.phoneshow.R;
import com.example.phoneshow.Utils.LogUtils;
import com.example.phoneshow.bean.ThemesBean;
import com.example.phoneshow.data.DataManager;
import com.example.phoneshow.phone.CallerOperationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class ThemesRecyclerViewAdapter extends RecyclerView.Adapter<ThemesRecyclerViewAdapter.ViewHolder> {
    private TranslateAnimation animation;

    private Context context;

    private List<ThemesBean> themesList = new ArrayList<>();

    public ThemesRecyclerViewAdapter(Context context, List<ThemesBean> themesList) {
        this.context = context;
        this.themesList = themesList;
        animation = CallerOperationManager.getInstance().animationAnswerImg();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (themesList.get(position).isUse()){
            if (animation != null){
                holder.mAnswer.startAnimation(animation);
            }

            holder.mVideoView.setVisibility(View.VISIBLE);
            holder.mVideoView.setBackground(new BitmapDrawable(themesList.get(position).getCacheBg()));
            //设置播放加载路径
            holder.mVideoView.setVideoURI(Uri.parse(themesList.get(position).getUriStr()));
            //监听加载是否完成
            holder.mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                            if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                                holder.mVideoView.setBackgroundColor(Color.TRANSPARENT);
                            return true;
                        }
                    });
                }
            });
            //播放
            holder.mVideoView.start();
            //循环播放
            holder.mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    holder.mVideoView.start();
                }
            });
        } else {
            if (animation != null){
                holder.mAnswer.clearAnimation();
            }

            holder.mVideoView.pause();
            holder.mVideoView.clearAnimation();
            holder.mRelativeView.setBackground(new BitmapDrawable(themesList.get(position).getCacheBg()));
            holder.mVideoView.setVisibility(View.GONE);
        }

        holder.mName.setText(themesList.get(position).getThemeName());
        if (themesList.get(position).isUse()) {
            holder.mUse.setBackgroundResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.mUse.setBackgroundResource(android.R.drawable.btn_star_big_off);
        }
        if (themesList.get(position).isCollection()) {
            holder.mCollection.setBackgroundResource(android.R.drawable.star_on);
        } else {
            holder.mCollection.setBackgroundResource(android.R.drawable.star_off);
        }
    }

    @Override
    public int getItemCount() {
        return themesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mUse;
        ImageView mCollection;
        ImageView mAnswer;
        TextView mName;
        VideoView mVideoView;
        RelativeLayout mRelativeView;
        RelativeLayout mRelative;

        public ViewHolder(View itemView) {
            super(itemView);
            mRelative = (RelativeLayout) itemView.findViewById(R.id.item_overlay_relative);
            mRelativeView = (RelativeLayout) itemView.findViewById(R.id.video_relative);
            mVideoView = (VideoView) itemView.findViewById(R.id.video_view);
            mName = (TextView) itemView.findViewById(R.id.item_name);
            mCollection = (ImageView) itemView.findViewById(R.id.item_collection);
            mUse = (ImageView) itemView.findViewById(R.id.item_use);
            mAnswer = (ImageView) itemView.findViewById(R.id.item_overlay_answer);

            mCollection.setOnClickListener(this);
            mUse.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_collection:
                    themesList.get(getAdapterPosition()).setCollection(!themesList.get(getAdapterPosition()).isCollection());
                    if (getAdapterPosition() == 0) {
                        DataManager.getInstance().saveCollection(context, "item1", themesList.get(getAdapterPosition()).isCollection());
                    } else if (getAdapterPosition() == 1) {
                        DataManager.getInstance().saveCollection(context, "item2", themesList.get(getAdapterPosition()).isCollection());
                    } else if (getAdapterPosition() == 2) {
                        DataManager.getInstance().saveCollection(context, "item3", themesList.get(getAdapterPosition()).isCollection());
                    }
                    notifyDataSetChanged();
                    break;
                case R.id.item_use:
                    if (!themesList.get(getAdapterPosition()).isUse()) {
                        themesList.get(getAdapterPosition()).setUse(!themesList.get(getAdapterPosition()).isUse());
                        if (getAdapterPosition() == 0) {
                            DataManager.getInstance().saveUse(context, "item1", themesList.get(getAdapterPosition()).isUse());
                            if (DataManager.getInstance().getUse(context, "item2")) {
                                DataManager.getInstance().saveUse(context, "item2", false);
                                themesList.get(1).setUse(false);
                            } else if (DataManager.getInstance().getUse(context, "item3")) {
                                DataManager.getInstance().saveUse(context, "item3", false);
                                themesList.get(2).setUse(false);
                            }
                        } else if (getAdapterPosition() == 1) {
                            DataManager.getInstance().saveUse(context, "item2", themesList.get(getAdapterPosition()).isUse());
                            if (DataManager.getInstance().getUse(context, "item1")) {
                                DataManager.getInstance().saveUse(context, "item1", false);
                                themesList.get(0).setUse(false);
                            } else if (DataManager.getInstance().getUse(context, "item3")) {
                                DataManager.getInstance().saveUse(context, "item3", false);
                                themesList.get(2).setUse(false);
                            }
                        } else if (getAdapterPosition() == 2) {
                            DataManager.getInstance().saveUse(context, "item3", themesList.get(getAdapterPosition()).isUse());
                            if (DataManager.getInstance().getUse(context, "item2")) {
                                DataManager.getInstance().saveUse(context, "item2", false);
                                themesList.get(1).setUse(false);
                            } else if (DataManager.getInstance().getUse(context, "item1")) {
                                DataManager.getInstance().saveUse(context, "item1", false);
                                themesList.get(0).setUse(false);
                            }
                        }
                        notifyDataSetChanged();
                    }
                    break;
            }
        }
    }
}
