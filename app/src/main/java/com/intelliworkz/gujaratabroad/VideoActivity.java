package com.intelliworkz.gujaratabroad;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class VideoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView rvVideo;
    RecyclerView.LayoutManager rvVideoManager;
    RecyclerView.Adapter rvVideoAdapter;
    ArrayList<HashMap<String,String>> videoListArray=new ArrayList<>();
    ArrayList<HashMap<String,String>> advideocenterList=new ArrayList<>();
    String url=HomeActivity.SERVICE_URL;
    ProgressDialog dialog;
    String message,status;

    ImageView imgAdTopVideo,imgAdBottomLeftVideo,imgAdBottomRightVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        rvVideo=(RecyclerView)findViewById(R.id.rvVideo);
        rvVideo.setHasFixedSize(true);

        rvVideoManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvVideo.setLayoutManager(rvVideoManager);

        GetrvVideo rvVideo=new GetrvVideo();
        rvVideo.execute();

        imgAdTopVideo=(ImageView)findViewById(R.id.imgAdTopVideo);
        GetAdTopBanner adTopBanner=new GetAdTopBanner();
        adTopBanner.execute();

        imgAdBottomLeftVideo=(ImageView)findViewById(R.id.imgAdBottomLeftVideo);
        GetAdBottomLeftBanner adBottomLeftBanner=new GetAdBottomLeftBanner();
        adBottomLeftBanner.execute();

        imgAdBottomRightVideo=(ImageView)findViewById(R.id.imgAdBottomRightVideo);
        GetAdBottomRightBanner adBottomRightBanner = new GetAdBottomRightBanner();
        adBottomRightBanner.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
            Intent i=new Intent(getApplicationContext(),FirstActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_news)
        {
            Intent i=new Intent(getApplicationContext(),NewsActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_dir)
        {
            Intent i=new Intent(getApplicationContext(),AdvertiseActivity.class);
            startActivity(i);
            finish();

        }
        else if (id == R.id.nav_epaper)
        {
            String url = "http://www.gujaratabroad.ca/index.aspx";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_contact)
        {
            Intent i=new Intent(getApplicationContext(),ContactUsActivity.class);
            startActivity(i);
            finish();

        }
        else if (id == R.id.nav_share)
        {
            Intent i=new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            String body="https://play.google.com/store/apps/details?id=com.intelliworkz.gujaratabroad";
            i.putExtra(Intent.EXTRA_SUBJECT,body);
            i.putExtra(Intent.EXTRA_TEXT,body);
            startActivity(Intent.createChooser(i,"Share using"));
            finish();

        }
        else if (id == R.id.nav_rate)
        {
            Intent i=new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.intelliworkz.gujaratabroad"));
            if(!MyStartActivity(i))
            {
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.intelliworkz.gujaratabroad"));
                if(!MyStartActivity(i))
                {
                    Log.d("Like","Could not open browser");
                }
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetrvVideo extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(VideoActivity.this);
            dialog.setTitle("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String langId = HomeActivity.str_language_Code;
            JSONObject videoList=new JSONObject();
            try {
                videoList.put("lang",langId);
                Postdata postdata=new Postdata();
                String videopd=postdata.post(url+"fatch_videolist.php",videoList.toString());
                JSONObject j=new JSONObject(videopd);
                status=j.getString("status");
                if(status.equals("1"))
                {
                    Log.d("Like","Successfully");
                    message = j.getString("message");
                    JSONArray video=j.getJSONArray("tbl_newslist");
                    for (int i=0;i<video.length();i++)
                    {
                        HashMap<String,String > videofe = new HashMap<>();
                        JSONObject jo=video.getJSONObject(i);

                        String video_id=jo.getString("video_id");
                        String video_title=jo.getString("video_title");
                        String video_thumb=jo.getString("video_thumb");

                        videofe.put("video_id",video_id);
                        videofe.put("video_title",video_title);
                        videofe.put("video_thumb",url+"video/"+video_thumb);

                        videoListArray.add(videofe);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Get Advertise Image
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

                    advideocenterList.add(cat);
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
            rvVideoAdapter=new VideoAdapter(VideoActivity.this,videoListArray,advideocenterList);
            rvVideo.setAdapter(rvVideoAdapter);

        }
    }

    private class GetAdTopBanner extends AsyncTask<String,Void,String>{
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
            imageLoader.displayImage(imgUrl,imgAdTopVideo, options);
            imgAdTopVideo.setOnClickListener(new View.OnClickListener() {
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

    private class GetAdBottomLeftBanner extends AsyncTask<String,Void,String>{
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
            imageLoader.displayImage(imgUrl,imgAdBottomLeftVideo, options);
            imgAdBottomLeftVideo.setOnClickListener(new View.OnClickListener() {
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
    private class GetAdBottomRightBanner extends AsyncTask<String,Void,String>{
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
            imageLoader.displayImage(imgUrl,imgAdBottomRightVideo, options);
            imgAdBottomRightVideo.setOnClickListener(new View.OnClickListener() {
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
