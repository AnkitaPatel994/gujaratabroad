package com.intelliworkz.gujaratabroad;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
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

public class NewsDetailActivity extends AppCompatActivity {

    ImageView img_adTopNews,img_adBottomLeftfullNews,img_adBottomRightfullNews,imgZoom,imgClose;
    TextView txtNDate,txtNTitle,txtNDesc;
    ArrayList<NewsModel> newsArrayList=new ArrayList<>();
    String message,status,NewsTitle,NewsDate,NewsDesc,newsId;
    String url=HomeActivity.SERVICE_URL;
    SliderLayout sliderAd;
    ArrayList<HashMap<String, String>> ImgArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        txtNDesc=(TextView)findViewById(R.id.txtNDesc);
        txtNDate=(TextView)findViewById(R.id.txtNDate);
        txtNTitle=(TextView)findViewById(R.id.txtNTitle);
        sliderAd=(SliderLayout)findViewById(R.id.sliderAd);
        img_adTopNews=(ImageView)findViewById(R.id.img_adTopNews);
        img_adBottomLeftfullNews=(ImageView)findViewById(R.id.img_adBottomLeftfullNews);
        img_adBottomRightfullNews=(ImageView)findViewById(R.id.img_adBottomRightfullNews);

        final Dialog dialog = new Dialog(NewsDetailActivity.this,android.R.style.Theme_Light_NoTitleBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);

        imgZoom=(ImageView) dialog.findViewById(R.id.imgZoom);
        GetPopupBanner popupBanner=new GetPopupBanner();
        popupBanner.execute();

        imgClose=(ImageView)dialog.findViewById(R.id.imgClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

        newsId=getIntent().getExtras().getString("newsId");

        NewsTitle=getIntent().getExtras().getString("newsTitle");
        txtNTitle.setText(Html.fromHtml(NewsTitle));

        NewsDate=getIntent().getExtras().getString("newsDate");
        txtNDate.setText(NewsDate);

        NewsDesc=getIntent().getExtras().getString("newsDesc");
        txtNDesc.setText(Html.fromHtml(NewsDesc));

        GetNewsImg newsImg = new GetNewsImg(newsId);
        newsImg.execute();

        GetTopBanner getTopBanner=new GetTopBanner();
        getTopBanner.execute();

        GetBottomLeftBanner getBottomLeftBanner=new GetBottomLeftBanner();
        getBottomLeftBanner.execute();

        GetBottomRightBanner getBottomRightBanner=new GetBottomRightBanner();
        getBottomRightBanner.execute();


        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private class GetTopBanner extends AsyncTask<String,Void,String> {
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
                        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
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
            imageLoader.displayImage(imgUrl,img_adTopNews, options);
            img_adTopNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(addLink));
                    if(!MyStartActivity(i))
                    {
                        i.setData(Uri.parse(addLink));
                        if(!MyStartActivity(i))
                        {
                            Toast.makeText(getApplicationContext(),"Could not open browser",Toast.LENGTH_SHORT).show();
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

    private class GetBottomLeftBanner extends AsyncTask<String,Void,String> {
        String addImg,addLink;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        @Override
        protected String doInBackground(String... params) {

            JSONObject adTopList=new JSONObject();
            try {
                adTopList.put("type","id_bottom");
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
                        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
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
            imageLoader.displayImage(imgUrl,img_adBottomLeftfullNews, options);
            img_adBottomLeftfullNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(addLink));
                    if(!MyStartActivity(i))
                    {
                        i.setData(Uri.parse(addLink));
                        if(!MyStartActivity(i))
                        {
                            Toast.makeText(getApplicationContext(),"Could not open browser",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private class GetBottomRightBanner extends AsyncTask<String,Void,String> {
        String addImg,addLink;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        @Override
        protected String doInBackground(String... params) {

            JSONObject adRightList=new JSONObject();
            try {
                adRightList.put("type","id_bottom");
                Postdata postdata=new Postdata();
                String adPd=postdata.post(url+"get_advertisement.php",adRightList.toString());
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
                        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
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
            imageLoader.displayImage(imgUrl,img_adBottomRightfullNews, options);
            img_adBottomRightfullNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(addLink));
                    if(!MyStartActivity(i))
                    {
                        i.setData(Uri.parse(addLink));
                        if(!MyStartActivity(i))
                        {
                            Toast.makeText(getApplicationContext(),"Could not open browser",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private class GetPopupBanner extends AsyncTask<String,Void,String> {
        String addImg,addLink;
        ImageLoader imageLoader;
        DisplayImageOptions options;
        @Override
        protected String doInBackground(String... params) {

            JSONObject adTopList=new JSONObject();
            try {
                adTopList.put("type","id_popup");
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

                        addImg=jo.getString("add_thumbnill");
                        addLink=jo.getString("add_link");

                        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                                .cacheOnDisc(true).cacheInMemory(true)
                                .imageScaleType(ImageScaleType.EXACTLY)
                                .displayer(new FadeInBitmapDisplayer(300)).build();
                        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
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
            imageLoader.displayImage(imgUrl,imgZoom, options);
            imgZoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(addLink));
                    if(!MyStartActivity(i))
                    {
                        i.setData(Uri.parse(addLink));
                        if(!MyStartActivity(i))
                        {
                            Toast.makeText(getApplicationContext(),"Could not open browser",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private class GetNewsImg extends AsyncTask<String,Void,String> implements BaseSliderView.OnSliderClickListener {
        String newsId;
        JSONArray joImgArray;

        public GetNewsImg(String newsId) {
            this.newsId=newsId;
        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject joNewsImg = new JSONObject();
            try {
                joNewsImg.put("news_id",newsId);
                Postdata postdata = new Postdata();
                String newsImgUrl = postdata.post(url+"fatch_news_img.php",joNewsImg.toString());
                JSONObject joImg=new JSONObject(newsImgUrl);
                status=joImg.getString("status");
                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = joImg.getString("message");
                    joImgArray = joImg.getJSONArray("tbl_news_img");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            for(int i=0;i<joImgArray.length();i++)
            {
                HashMap<String,String > con = new HashMap<>();
                try {
                    JSONObject jo = joImgArray.getJSONObject(i);
                    String news_img = jo.getString("news_img");
                    con.put(String.valueOf(i),url+"news_img/"+news_img);

                    ImgArrayList.add(con);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i1 = i; i1 <=i; i1++) {
                    for (String name : con.keySet()) {
                        DefaultSliderView textSliderView = new DefaultSliderView(NewsDetailActivity.this);
                        // initialize a SliderLayout
                        textSliderView
                                .image(con.get(String.valueOf(i1)))
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(this);

                        sliderAd.addSlider(textSliderView);
                    }
                }
            }
            sliderAd.setCustomAnimation(new DescriptionAnimation());
            sliderAd.setDuration(5000);
        }

        @Override
        public void onSliderClick(BaseSliderView slider) {
            sliderAd.startAutoCycle();
        }
    }
}
