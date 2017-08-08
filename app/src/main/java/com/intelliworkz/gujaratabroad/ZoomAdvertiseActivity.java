package com.intelliworkz.gujaratabroad;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ZoomAdvertiseActivity extends AppCompatActivity {
    ImageView imgZoom;
    TextView txtAdZoomtitle;
    String zoomImage;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_advertise);
        setTitle(R.string.title_app);

        if(getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imgZoom=(ImageView)findViewById(R.id.imgZoom);
        zoomImage=getIntent().getExtras().getString("add_thumbnill");
        txtAdZoomtitle=(TextView)findViewById(R.id.txtAdZoomtitle);

        String addTitle=getIntent().getExtras().getString("adTitle");
        txtAdZoomtitle.setText(addTitle);

        GetZoomImgId getZoomImgId=new GetZoomImgId();
        getZoomImgId.execute();

        imgZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String adWebUrl=getIntent().getExtras().getString("adWebUrl");

                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(adWebUrl));
                if(!MyStartActivity(i))
                {
                    i.setData(Uri.parse(adWebUrl));
                    if(!MyStartActivity(i))
                    {
                        //Toast.makeText(getApplicationContext(),"Could not open browser",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private class GetZoomImgId extends AsyncTask<String,Void,String> {

        ImageLoader imageLoader;
        DisplayImageOptions options;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(ZoomAdvertiseActivity.this);
            dialog.setTitle("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }@Override
        protected String doInBackground(String... params) {
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).cacheInMemory(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .displayer(new FadeInBitmapDisplayer(300)).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
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

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            imageLoader.displayImage(zoomImage,imgZoom, options);
        }

    }
}
