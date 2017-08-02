package com.intelliworkz.gujaratabroad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdvertiseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ProgressDialog dialog;
    RecyclerView rvAdvertise;
    RecyclerView.LayoutManager rvAdvertiseManager;
    RecyclerView.Adapter rvAdvertiseAdapter;
    ArrayList<HashMap<String,String>> advertiseList=new ArrayList<>();
    String url=Common.SERVICE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rvAdvertise=(RecyclerView)findViewById(R.id.rvAdvertise);
        rvAdvertise.setHasFixedSize(true);

        rvAdvertiseManager = new GridLayoutManager(getApplicationContext(),1);
        rvAdvertise.setLayoutManager(rvAdvertiseManager);

        GetrvAdvertise rvAdvertise=new GetrvAdvertise();
        rvAdvertise.execute();

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

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
            Intent i=new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_news)
        {
            Intent i=new Intent(getApplicationContext(),NewsActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_videos)
        {
            Intent i=new Intent(getApplicationContext(),VideoActivity.class);
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
        else if (id == R.id.nav_share)
        {
            Intent i=new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            String body="Link here";
            i.putExtra(Intent.EXTRA_SUBJECT,body);
            i.putExtra(Intent.EXTRA_TEXT,body);
            startActivity(Intent.createChooser(i,"Share using"));
            finish();

        }
        else if (id == R.id.nav_rate) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetrvAdvertise extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(AdvertiseActivity.this);
            dialog.setMessage("Loading....");
            dialog.setCancelable(true);
            dialog.show();
        }

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
                        JSONObject j=Advertise.getJSONObject(i);

                        String addTitle=j.getString("add_title");
                        String addImg=j.getString("add_img");
                        String addLink=j.getString("add_link");

                        cat.put("addTitle",addTitle);
                        cat.put("addImg",url+"add_img/"+addImg);
                        cat.put("addLink",addLink);

                        advertiseList.add(cat);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Server Connection Not Found..",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            rvAdvertiseAdapter=new AdvertiseAdapter(AdvertiseActivity.this,advertiseList);
            rvAdvertise.setAdapter(rvAdvertiseAdapter);
        }
    }
}
