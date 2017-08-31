package com.intelliworkz.gujaratabroad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
    private String title;
    private int page;

    Activity activity;
    RecyclerView rvNewsList;
    ImageView img_adNews,imgAdBottomLeftNews,imgAdBottomRightNews;
    String status,message;
    ArrayList<NewsModel> newsArrayList=new ArrayList<>();
    ArrayList<HashMap<String,String>> adcenterList=new ArrayList<>();
    String url=HomeActivity.SERVICE_URL;
    private ViewPager SubCatpager;
    private TabLayout subCattabLayout;
    SubCatPager subAdapter;
    public static ArrayList<String> tabTitlesSubCatId = new ArrayList<>();
    ArrayList<String> tabTitlesSubCat = new ArrayList<>();
    String arrPosition="0";

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        GetTabSubCategory getSubCategory = new GetTabSubCategory();
        getSubCategory.execute();
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

        SubCatpager = (ViewPager) view.findViewById(R.id.SubCatpager_hod);
        subCattabLayout = (TabLayout) view.findViewById(R.id.subCattabLayout);
        subCattabLayout.setupWithViewPager(SubCatpager);

        subCattabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabTitlesSubCat.clear();
                int p = tab.getPosition();
                arrPosition = tabTitlesSubCatId.get(p);
                GetNewsList getNewsList=new GetNewsList();
                getNewsList.execute();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        rvNewsList=(RecyclerView)view.findViewById(R.id.rvNewsList);
        rvNewsList.setHasFixedSize(true);

        RecyclerView.LayoutManager rvLayoutManager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        rvNewsList.setLayoutManager(rvLayoutManager);

        GetNewsList getNewsList=new GetNewsList();
        getNewsList.execute();

        img_adNews=(ImageView)view.findViewById(R.id.img_adNews);
        GetAdTopNews getAdTopNews=new GetAdTopNews();
        getAdTopNews.execute();

        imgAdBottomLeftNews=(ImageView)view.findViewById(R.id.img_adBottomLeftNews);
        GetAdBottomLeftNews adBottomLeftNews=new GetAdBottomLeftNews();
        adBottomLeftNews.execute();

        imgAdBottomRightNews=(ImageView)view.findViewById(R.id.img_adBottomRightNews);
        GetAdBottomRightNews adBottomRightNews=new GetAdBottomRightNews();
        adBottomRightNews.execute();

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
            JSONObject newsList=new JSONObject();
            try {
                newsList.put("lang",langId);
                newsList.put("news_cat",title);
                newsList.put("news_subcat",arrPosition);
                Postdata postdata=new Postdata();
                String news=postdata.post(url+"fatch_newslist.php",newsList.toString());
                JSONObject j=new JSONObject(news);
                status=j.getString("status");
                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = j.getString("message");
                    JSONArray newsarr=j.getJSONArray("tbl_news");
                    for(int i=0;i<newsarr.length();i++)
                    {
                        JSONObject newsJson=newsarr.getJSONObject(i);

                        String news_id=newsJson.getString("news_id");
                        String newsTitle=newsJson.getString("news_title");
                        String newsDetails=newsJson.getString("news_desc");
                        String newsDate=newsJson.getString("date");
                        String newsImg=newsJson.getString("news_img");

                        NewsModel n=new NewsModel(news_id,newsTitle,newsDetails,newsImg,newsDate);
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

            JSONObject adCenterList=new JSONObject();
            try {
                adCenterList.put("type","id_top");
                Postdata postdata=new Postdata();
                String adPd=postdata.post(url+"get_advertisement.php",adCenterList.toString());
                JSONObject j=new JSONObject(adPd);
                status=j.getString("status");
                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = j.getString("message");
                    JSONArray adJsArry=j.getJSONArray("data");
                    for (int i=0;i<adJsArry.length();i++)
                    {
                        HashMap<String,String > cat = new HashMap<>();
                        JSONObject jo=adJsArry.getJSONObject(i);

                        String addImg=jo.getString("add_banner");
                        String addLink=jo.getString("add_link");

                        cat.put("addImg",url+"add_img/"+addImg);
                        cat.put("addLink",addLink);

                        adcenterList.add(cat);
                    }
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
            if(status.equals("1"))
            {
                RecyclerView.Adapter rvNewsAdapter=new NewsAdapter(getActivity(),newsArrayList,adcenterList);
                rvNewsList.setAdapter(rvNewsAdapter);
            }else {
                rvNewsList.setAdapter(null);
            }

        }
    }

    private class GetAdTopNews extends AsyncTask<String,Void,String>{
        String addImg,addLink;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        @Override
        protected String doInBackground(String... params) {

            JSONObject adTopList=new JSONObject();
            try {
                adTopList.put("type","id_top");
                Postdata postdata=new Postdata();
                String adPd=postdata.post(url+"get_advertisement.php",adTopList.toString());
                JSONObject j=new JSONObject(adPd);
                status=j.getString("status");
                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = j.getString("message");
                    JSONArray adJsArry=j.getJSONArray("data");

                    for (int i=0;i<adJsArry.length();i++)
                    {
                        JSONObject jo=adJsArry.getJSONObject(0);

                        addImg=jo.getString("add_banner");
                        addLink=jo.getString("add_link");

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
                }
            } catch (JSONException e) {
                e.printStackTrace();
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

    private class GetAdBottomLeftNews extends AsyncTask<String,Void,String>{
        String addImg,addLink;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        @Override
        protected String doInBackground(String... params) {

            JSONObject adBottomLeftList=new JSONObject();
            try {
                adBottomLeftList.put("type","id_bottom");
                Postdata postdata=new Postdata();
                String adPd=postdata.post(url+"get_advertisement.php",adBottomLeftList.toString());
                JSONObject j=new JSONObject(adPd);
                status=j.getString("status");
                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = j.getString("message");
                    JSONArray adJsArry=j.getJSONArray("data");

                    for (int i=0;i<adJsArry.length();i++)
                    {
                        JSONObject jo=adJsArry.getJSONObject(0);

                        addImg=jo.getString("add_banner");
                        addLink=jo.getString("add_link");

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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String imgUrl=url+"add_img/"+addImg;
            imageLoader.displayImage(imgUrl,imgAdBottomLeftNews, options);
            imgAdBottomLeftNews.setOnClickListener(new View.OnClickListener() {
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

    private class GetAdBottomRightNews extends AsyncTask<String,Void,String>{
        String addImg,addLink;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        @Override
        protected String doInBackground(String... params) {

            JSONObject adBottomRightList=new JSONObject();
            try {
                adBottomRightList.put("type","id_bottom");
                Postdata postdata=new Postdata();
                String adPd=postdata.post(url+"get_advertisement.php",adBottomRightList.toString());
                JSONObject j=new JSONObject(adPd);
                status=j.getString("status");
                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = j.getString("message");
                    JSONArray adJsArry=j.getJSONArray("data");

                    for (int i=0;i<adJsArry.length();i++)
                    {
                        JSONObject jo=adJsArry.getJSONObject(0);

                        addImg=jo.getString("add_banner");
                        addLink=jo.getString("add_link");

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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String imgUrl=url+"add_img/"+addImg;
            imageLoader.displayImage(imgUrl,imgAdBottomRightNews, options);
            imgAdBottomRightNews.setOnClickListener(new View.OnClickListener() {
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

    private class GetTabSubCategory extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            tabTitlesSubCat.clear();

            JSONObject subCatList = new JSONObject();

            try {
                subCatList.put("subcat_cat", title);
                Postdata postdata = new Postdata();
                String subCatpd = postdata.post(url + "fatch_news_sub_category.php", subCatList.toString());
                JSONObject j1 = new JSONObject(subCatpd);
                status = j1.getString("status");
                if (status.equals("1")) {
                    Log.d("Like", "Successfully");
                    message = j1.getString("message");
                    tabTitlesSubCatId.clear();
                    JSONArray subcategory = j1.getJSONArray("tbl_subcategory");
                    for (int i1 = 0; i1 < subcategory.length(); i1++) {
                        JSONObject jo = subcategory.getJSONObject(i1);

                        String subcat_id = jo.getString("subcat_id");
                        String subcat_name = jo.getString("subcat_name");

                        tabTitlesSubCatId.add(subcat_id);
                        tabTitlesSubCat.add(subcat_name);
                    }
                } else {
                    message = j1.getString("message");
                    //subCattabLayout.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (status.equals("1")) {
                subAdapter = new SubCatPager(getFragmentManager());
                for (int i = 0; i < tabTitlesSubCat.size(); i++) {
                    subAdapter.addFrag(new SubNewsFragment(), tabTitlesSubCat.get(i).trim());
                }
                if (tabTitlesSubCat.size() <= 3) {
                    subCattabLayout.setTabMode(TabLayout.MODE_FIXED);
                } else {
                    subCattabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
                SubCatpager.setAdapter(subAdapter);
                subCattabLayout.setVisibility(View.VISIBLE);
            } else {
                subCattabLayout.setVisibility(View.GONE);
            }

        }
    }
}
