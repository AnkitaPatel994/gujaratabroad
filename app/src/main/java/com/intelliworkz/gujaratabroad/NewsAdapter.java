package com.intelliworkz.gujaratabroad;

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

class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    Context context;
    ArrayList<NewsModel> newsArrayList;
    ArrayList<HashMap<String, String>> adcenterList;
    String url=HomeActivity.SERVICE_URL;
    View v;
    int count=0;

    public NewsAdapter(Context context, ArrayList<NewsModel> newsArrayList, ArrayList<HashMap<String, String>> adcenterList) {
        this.context=context;
        this.newsArrayList=newsArrayList;
        this.adcenterList=adcenterList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.newslist,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String newsId=newsArrayList.get(position).getNewsId();
        final String newsDate=newsArrayList.get(position).getNewsDate();
        final String newsDesc=newsArrayList.get(position).getNewsDetails();
        final String newsTitle=newsArrayList.get(position).getNewsTitle();

        String Img = newsArrayList.get(position).getNewsImg();

        final String urlImg = url+"news_img/"+Img;
        holder.layAd.setVisibility(View.GONE);

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

            int pos = position+1;
            if(pos%4==0)
            {
                holder.layAd.setVisibility(View.VISIBLE);
                if(count < adcenterList.size())
                {
                    String adCenterImg = adcenterList.get(count).get("addImg");
                    final String adCenterLink = adcenterList.get(count).get("addLink");
                    imageLoader.displayImage(adCenterImg,holder.imgCenterAd, options);

                    holder.layAd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i=new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(adCenterLink));
                            if(!MyStartActivity(i))
                            {
                                i.setData(Uri.parse(adCenterLink));
                                if(!MyStartActivity(i))
                                {
                                    Toast.makeText(context,"Could not open browser",Toast.LENGTH_SHORT).show();
                                }
                            }
                            //Toast.makeText(context,"Hi",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                count++;
            }

        holder.txtNewsTitle.setText(Html.fromHtml(newsTitle));
        imageLoader.displayImage(urlImg,holder.imgNews, options);

        holder.layNewsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,NewsDetailActivity.class);
                i.putExtra("newsId",newsId);
                i.putExtra("newsDate",newsDate);
                i.putExtra("newsTitle",newsTitle);
                i.putExtra("newsDesc",newsDesc);
                i.putExtra("urlImg",urlImg);
                context.startActivity(i);
                //Toast.makeText(context,"Img",Toast.LENGTH_SHORT).show();
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
        return newsArrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNewsTitle;
        ImageView imgNews,imgCenterAd;
        LinearLayout layNewsList,layAd;
        public ViewHolder(View v) {
            super(v);
            txtNewsTitle=(TextView)v.findViewById(R.id.txtNewsTitle);
            imgNews=(ImageView)v.findViewById(R.id.imgNews);
            imgCenterAd=(ImageView)v.findViewById(R.id.imgCenterAd);

            layNewsList=(LinearLayout)v.findViewById(R.id.layNewsList);
            layAd=(LinearLayout)v.findViewById(R.id.layAd);
        }
    }
}
