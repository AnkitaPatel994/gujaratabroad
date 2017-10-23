package com.intelliworkz.gujaratabroad;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
 * Created by pc-6 on 6/28/2017.
 */

class RelatedNewsAdapter extends RecyclerView.Adapter<RelatedNewsAdapter.ViewHolder> {
    Context context;
    ArrayList<NewsModel> newsRelatedArrayList;
    View v;

    public RelatedNewsAdapter(Context context, ArrayList<NewsModel> newsRelatedArrayList) {
        this.context=context;
        this.newsRelatedArrayList=newsRelatedArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.relatednewslist,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String newsId=newsRelatedArrayList.get(position).getNewsId();
        final String mainCatId=newsRelatedArrayList.get(position).getMainCatId();
        final String mainCatName=newsRelatedArrayList.get(position).getMainCatName();
        final String newsDate=newsRelatedArrayList.get(position).getNewsDate();
        final String newsDesc=newsRelatedArrayList.get(position).getNewsDetails();
        final String newsTitle=newsRelatedArrayList.get(position).getNewsTitle();

        String Img = newsRelatedArrayList.get(position).getNewsImg();

        final String urlImg = HomeActivity.SERVICE_URL+"news_img/"+Img;

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

        holder.txtReNewsTitle.setText(Html.fromHtml(newsTitle));

        if(Img.equals("null"))
        {
            holder.imgReNews.setImageResource(R.drawable.background_news);
        }
        else
        {
            imageLoader.displayImage(urlImg,holder.imgReNews, options);
        }


        holder.layReNewsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,NewsDetailActivity.class);
                i.putExtra("newsId",newsId);
                i.putExtra("mainCatId",mainCatId);
                i.putExtra("mainCatName",mainCatName);
                i.putExtra("newsDate",newsDate);
                i.putExtra("newsTitle",newsTitle);
                i.putExtra("newsDesc",newsDesc);
                i.putExtra("urlImg",urlImg);
                context.startActivity(i);
                ((Activity)context).finish();
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
        return newsRelatedArrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtReNewsTitle;
        ImageView imgReNews;
        LinearLayout layReNewsList;
        public ViewHolder(View v) {
            super(v);

            txtReNewsTitle=(TextView)v.findViewById(R.id.txtReNewsTitle);
            imgReNews=(ImageView)v.findViewById(R.id.imgReNews);
            layReNewsList=(LinearLayout)v.findViewById(R.id.layReNewsList);

        }
    }
}

