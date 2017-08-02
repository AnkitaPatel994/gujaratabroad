package com.intelliworkz.gujaratabroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {
    Button btnNews,btnVideos,btnAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnNews=(Button)findViewById(R.id.btnNews);
        btnVideos=(Button)findViewById(R.id.btnVideos);
        btnAd=(Button)findViewById(R.id.btnAd);

        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),NewsActivity.class);
                startActivity(i);
            }
        });
        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),VideoActivity.class);
                startActivity(i);
            }
        });
        btnAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),AdvertiseActivity.class);
                startActivity(i);
            }
        });
    }
}
