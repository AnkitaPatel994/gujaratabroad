package com.intelliworkz.gujaratabroad;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class VideoViewActivity extends AppCompatActivity {
    UniversalVideoView vvVideo;
    UniversalMediaController media_controller;
    TextView txtVVTile;
    String url=HomeActivity.SERVICE_URL;
    ImageView img_adTopVideo,img_adBottomLeftVideo,img_adBottomRightVideo;
    RelativeLayout top,bottom,rlVideo;
    String status,message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        setTitle(R.string.title_app);
        img_adBottomLeftVideo=(ImageView)findViewById(R.id.img_adBottomLeftVideo);
        img_adBottomRightVideo=(ImageView)findViewById(R.id.img_adBottomRightVideo);
        img_adTopVideo=(ImageView)findViewById(R.id.img_adTopVideo);
        top=(RelativeLayout)findViewById(R.id.top);
        bottom=(RelativeLayout)findViewById(R.id.bottom);
        rlVideo=(RelativeLayout)findViewById(R.id.rlVideo);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        vvVideo = (UniversalVideoView)findViewById(R.id.vvVideo);
        txtVVTile = (TextView)findViewById(R.id.txtVVTile);

        String videoTitleView = getIntent().getExtras().getString("videoTitleView");
        txtVVTile.setText(videoTitleView);

        String videoUrlLinkView = getIntent().getExtras().getString("videoUrlLinkView");

        media_controller = (UniversalMediaController) findViewById(R.id.media_controller);

        vvVideo.setMediaController(media_controller);
        vvVideo.setVideoURI(Uri.parse(videoUrlLinkView));
        vvVideo.requestFocus();
        vvVideo.start();

        vvVideo.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
            @Override
            public void onScaleChange(boolean isFullscreen) {
                if(isFullscreen)
                {
                    ViewGroup.LayoutParams layoutParams = vvVideo.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    vvVideo.setLayoutParams(layoutParams);

                    ViewGroup.LayoutParams layoutParams1 = media_controller.getLayoutParams();
                    layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams1.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    media_controller.setLayoutParams(layoutParams1);

                    txtVVTile.setVisibility(View.GONE);
                    top.setVisibility(View.GONE);
                    bottom.setVisibility(View.GONE);
                    getSupportActionBar().hide();
                }
                else
                {
                    ViewGroup.LayoutParams layoutParams = vvVideo.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = (int)getResources().getDimension(R.dimen.video_height);
                    vvVideo.setLayoutParams(layoutParams);

                    ViewGroup.LayoutParams layoutParams1 = media_controller.getLayoutParams();
                    layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams1.height = (int)getResources().getDimension(R.dimen.video_height);
                    media_controller.setLayoutParams(layoutParams1);

                    txtVVTile.setVisibility(View.VISIBLE);
                    top.setVisibility(View.VISIBLE);
                    bottom.setVisibility(View.VISIBLE);
                    getSupportActionBar().show();
                }
            }

            @Override
            public void onPause(MediaPlayer mediaPlayer) {

            }

            @Override
            public void onStart(MediaPlayer mediaPlayer) {

            }

            @Override
            public void onBufferingStart(MediaPlayer mediaPlayer) {

            }

            @Override
            public void onBufferingEnd(MediaPlayer mediaPlayer) {

            }
        });

        GetTopAdBanner getTopAdBanner=new GetTopAdBanner();
        getTopAdBanner.execute();

        GetBottomAdBanner getBottomAdBanner=new GetBottomAdBanner();
        getBottomAdBanner.execute();

        GetBottomAdRightBanner bottomAdRightBanner = new GetBottomAdRightBanner();
        bottomAdRightBanner.execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private class GetTopAdBanner extends AsyncTask<String,Void,String>{
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
            imageLoader.displayImage(imgUrl,img_adTopVideo, options);
            img_adTopVideo.setOnClickListener(new View.OnClickListener() {
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

    private class GetBottomAdBanner extends AsyncTask<String,Void,String> {
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
            imageLoader.displayImage(imgUrl,img_adBottomLeftVideo, options);
            img_adBottomLeftVideo.setOnClickListener(new View.OnClickListener() {
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

    private class GetBottomAdRightBanner extends AsyncTask<String,Void,String> {
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
                        JSONObject jo=adJsArry.getJSONObject(1);

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
            imageLoader.displayImage(imgUrl,img_adBottomRightVideo, options);
            img_adBottomRightVideo.setOnClickListener(new View.OnClickListener() {
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
}
