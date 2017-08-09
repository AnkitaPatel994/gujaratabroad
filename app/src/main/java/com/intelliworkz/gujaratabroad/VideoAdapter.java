package com.intelliworkz.gujaratabroad;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shyam group on 6/19/2017.
 */

class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> videoList;
    ArrayList<HashMap<String, String>> advideocenterList;
    View v;
    int count=0;
    public VideoAdapter(Context context, ArrayList<HashMap<String, String>> videoList, ArrayList<HashMap<String, String>> advideocenterList) {
        this.context = context;
        this.videoList = videoList;
        this.advideocenterList=advideocenterList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String videoTitleView = videoList.get(position).get("video_title");
        holder.txtVideoTitleView.setText(videoTitleView);

        final String videoUrlView = videoList.get(position).get("video_thumb");
        holder.layAdVideo.setVisibility(View.GONE);

        //Image Thumb Code
        GetImgThumb imgThumb=new GetImgThumb(videoUrlView,holder);
        imgThumb.execute();
        //Image Thumb Code

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        ImageLoader imageLoader = ImageLoader.getInstance();
        int fallback = 0;
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback).build();

        int pos=position+1;
        if(pos%4==0)
        {
            holder.layAdVideo.setVisibility(View.VISIBLE);
            if(count < advideocenterList.size())
            {
                String adVideoCenterImg = advideocenterList.get(count).get("addImg");
                final String adVideoCenterLink = advideocenterList.get(count).get("addLink");
                imageLoader.displayImage(adVideoCenterImg,holder.imgCenterAdVideo, options);
                holder.layAdVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(adVideoCenterLink));
                        if(!MyStartActivity(i))
                        {
                            i.setData(Uri.parse(adVideoCenterLink));
                            if(!MyStartActivity(i))
                            {
                                Toast.makeText(context,"Could not open browser", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
            count++;

        }


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,VideoViewActivity.class);
                i.putExtra("videoTitleView",videoTitleView);
                i.putExtra("videoUrlLinkView",videoUrlView);
                context.startActivity(i);
            }
        });
    }
    private boolean MyStartActivity(Intent i) {

        try
        {
            context.startActivity(i);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }
    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVideoTitleView;
        ImageView ivVideoImgView,imgCenterAdVideo;
        LinearLayout layAdVideo;
        //ImageView img_adVideo;


        public ViewHolder(View v) {
            super(v);
            txtVideoTitleView= (TextView)v.findViewById(R.id.txtVideoTitleView);
            ivVideoImgView= (ImageView) v.findViewById(R.id.ivVideoImgView);
            imgCenterAdVideo= (ImageView) v.findViewById(R.id.imgCenterAdVideo);
            layAdVideo=(LinearLayout)v.findViewById(R.id.layAdVideo);

            //img_adVideo=(ImageView)vAd.findViewById(R.id.img_adNews);
        }
    }

    private class GetImgThumb extends AsyncTask<String,Void,String> {

        String videoUrlView;
        ViewHolder holder;
        Bitmap bitmap = null;

        public GetImgThumb(String videoUrlView, ViewHolder holder) {
            this.videoUrlView=videoUrlView;
            this.holder=holder;
        }

        @Override
        protected String doInBackground(String... params) {


            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14) {
                    // no headers included
                    mediaMetadataRetriever.setDataSource(videoUrlView, new HashMap<String, String>());
                }
                else {
                    mediaMetadataRetriever.setDataSource(videoUrlView);
                }
                bitmap = mediaMetadataRetriever.getFrameAtTime();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (mediaMetadataRetriever != null)
                    mediaMetadataRetriever.release();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            holder.ivVideoImgView.setImageBitmap(bitmap);
        }
    }
}
