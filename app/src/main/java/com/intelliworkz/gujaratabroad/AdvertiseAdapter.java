package com.intelliworkz.gujaratabroad;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pc-6 on 6/19/2017.
 */

class AdvertiseAdapter extends RecyclerView.Adapter<AdvertiseAdapter.ViewHolder> {
    Context context;
    ArrayList<HashMap<String,String>> advertiseList;
    View v;

    public AdvertiseAdapter(Context context, ArrayList<HashMap<String, String>> advertiseList) {
        this.context=context;
        this.advertiseList=advertiseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.advertise_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String adWebUrl = advertiseList.get(position).get("addLink");

        final String add_thumbnill = advertiseList.get(position).get("add_thumbnill");

        final String urlImg = advertiseList.get(position).get("add_banner");

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

        imageLoader.displayImage(urlImg,holder.imgAd, options);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"hi",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(context,ZoomAdvertiseActivity.class);
                i.putExtra("add_thumbnill",add_thumbnill);
                i.putExtra("adWebUrl",adWebUrl);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return advertiseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAd;
        public ViewHolder(View itemView) {
            super(itemView);
            imgAd=(ImageView)v.findViewById(R.id.imgAd);
        }
    }
}
