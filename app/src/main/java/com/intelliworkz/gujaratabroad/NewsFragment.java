package com.intelliworkz.gujaratabroad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NewsFragment extends Fragment{


    public NewsFragment() {
        // Required empty public constructor
    }
    View view;
    //TextView tv;
    private String title;
    private int page;

    Activity activity;

    RecyclerView rvNewsList;
    ImageView img_adNews,imgAdBottomNews;
    String status,message;
    ArrayList<NewsModel> newsArrayList=new ArrayList<>();
    ArrayList<HashMap<String,String>> adcenterList=new ArrayList<>();
    String url=HomeActivity.SERVICE_URL;

    // newInstance constructor for creating fragment with arguments
    public static NewsFragment newInstance(int page, String title) {
        NewsFragment fragmentFirst = new NewsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_news, container, false);

        rvNewsList=(RecyclerView)view.findViewById(R.id.rvNewsList);
        rvNewsList.setHasFixedSize(true);

        RecyclerView.LayoutManager rvLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        rvNewsList.setLayoutManager(rvLayoutManager);

        GetNewsList getNewsList=new GetNewsList();
        getNewsList.execute();

        img_adNews=(ImageView)view.findViewById(R.id.img_adNews);
        GetAdTopNews getAdTopNews=new GetAdTopNews();
        getAdTopNews.execute();

        imgAdBottomNews=(ImageView)view.findViewById(R.id.img_adNewsBottom);
        GetAdBottomNews getAdBottomNews=new GetAdBottomNews();
        getAdBottomNews.execute();

        return view;
    }

    private class GetNewsList extends AsyncTask<String,Void,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(getActivity());
            dialog.setTitle("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            newsArrayList.clear();
            String langId=HomeActivity.str_language_Code;
            String news_cat=title;
            String last_news_id="0";
            JSONObject newsList=new JSONObject();
            try {
                newsList.put("lang",langId);
                newsList.put("news_cat",news_cat);
                newsList.put("last_news_id",last_news_id);
                Postdata postdata=new Postdata();
                String news=postdata.post(url+"fatch_newslist.php",newsList.toString());
                JSONObject j=new JSONObject(news);
                status=j.getString("status");
                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = j.getString("message");
                    JSONArray newsarr=j.getJSONArray("tbl_newslist");
                    for(int i=0;i<newsarr.length();i++)
                    {
                        JSONObject newsJson=newsarr.getJSONObject(i);
                        String newsCatId=newsJson.getString("news_cat");
                        String newsTitle=newsJson.getString("news_title");
                        String newsDetails=newsJson.getString("news_desc");
                        String newsImg=newsJson.getString("news_img");
                        String newsDate=newsJson.getString("news_date");

                        NewsModel n=new NewsModel(newsCatId,newsTitle,newsDetails,newsImg,newsDate);
                        newsArrayList.add(n);
                    }
                }
                else
                {
                    message = j.getString("message");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String response;
            HttpHandler h=new HttpHandler();
            response= h.serverConnection(url+"fatch_addlist.php");
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray Advertise=jsonObject.getJSONArray("tbl_addlist");
                for (int i=1;i<(Advertise.length())-1;i++)
                {
                    HashMap<String,String > cat = new HashMap<>();
                    JSONObject j=Advertise.getJSONObject(i);

                    String addImg=j.getString("add_img");
                    String addLink=j.getString("add_link");

                    cat.put("addImg",url+"add_img/"+addImg);
                    cat.put("addLink",addLink);

                    adcenterList.add(cat);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            RecyclerView.Adapter rvNewsAdapter=new NewsAdapter(getActivity(),newsArrayList,adcenterList);
            rvNewsList.setAdapter(rvNewsAdapter);
        }
    }


    private class GetAdTopNews extends AsyncTask<String,Void,String>{
        String addImg,addLink;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        @Override
        protected String doInBackground(String... params) {
            String response;
            HttpHandler h=new HttpHandler();
            response= h.serverConnection(url+"fatch_addlist.php");
            if(response!=null)
            {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray Advertise=jsonObject.getJSONArray("tbl_addlist");
                    for (int i=0;i<Advertise.length();i++)
                    {
                        HashMap<String,String > cat = new HashMap<>();
                        JSONObject j=Advertise.getJSONObject(0);

                        addImg=j.getString("add_img");
                        addLink=j.getString("add_link");

                        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                                .cacheOnDisc(true).cacheInMemory(true)
                                .imageScaleType(ImageScaleType.EXACTLY)
                                .displayer(new FadeInBitmapDisplayer(300)).build();
                        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                                .defaultDisplayImageOptions(defaultOptions)
                                .memoryCache(new WeakMemoryCache())
                                .discCacheSize(100 * 1024 * 1024).build();

                        ImageLoader.getInstance().init(config);

                        imageLoader = ImageLoader.getInstance();
                        int fallback = 0;
                        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisc(true).resetViewBeforeLoading(true)
                                .showImageForEmptyUri(fallback)
                                .showImageOnFail(fallback)
                                .showImageOnLoading(fallback).build();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"Server Connection Not Found..",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String imgUrl=url+"add_img/"+addImg;
            imageLoader.displayImage(imgUrl,img_adNews, options);
            img_adNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(addLink));
                    if(!MyStartActivity(i))
                    {
                        i.setData(Uri.parse(addLink));
                        if(!MyStartActivity(i))
                        {
                            Toast.makeText(getContext(),"Could not open browser",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }
    private boolean MyStartActivity(Intent i) {

        try
        {
            startActivity(i);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }

    private class GetAdBottomNews extends AsyncTask<String,Void,String>{
        String addImg,addLink;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        @Override
        protected String doInBackground(String... params) {
            String response;
            HttpHandler h=new HttpHandler();
            response= h.serverConnection(url+"fatch_addlist.php");
            if(response!=null)
            {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray Advertise=jsonObject.getJSONArray("tbl_addlist");
                    for (int i=0;i<Advertise.length();i++)
                    {
                        JSONObject j=Advertise.getJSONObject(i);

                        addImg=j.getString("add_img");
                        addLink=j.getString("add_link");

                        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                                .cacheOnDisc(true).cacheInMemory(true)
                                .imageScaleType(ImageScaleType.EXACTLY)
                                .displayer(new FadeInBitmapDisplayer(300)).build();
                        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                                .defaultDisplayImageOptions(defaultOptions)
                                .memoryCache(new WeakMemoryCache())
                                .discCacheSize(100 * 1024 * 1024).build();

                        ImageLoader.getInstance().init(config);

                        imageLoader = ImageLoader.getInstance();
                        int fallback = 0;
                        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisc(true).resetViewBeforeLoading(true)
                                .showImageForEmptyUri(fallback)
                                .showImageOnFail(fallback)
                                .showImageOnLoading(fallback).build();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"Server Connection Not Found..",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String imgUrl=url+"add_img/"+addImg;
            imageLoader.displayImage(imgUrl,imgAdBottomNews, options);

            imgAdBottomNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(addLink));
                    if(!MyStartActivity(i))
                    {
                        i.setData(Uri.parse(addLink));
                        if(!MyStartActivity(i))
                        {
                            Toast.makeText(getContext(),"Could not open browser",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}
