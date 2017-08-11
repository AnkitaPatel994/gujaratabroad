package com.intelliworkz.gujaratabroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    Button btnEnglish,btnGujarati;
    public  static String str_language_Code="2";
    public static String SERVICE_URL= "http://gujaratabroad.online/GujaratAboardWebService/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnEnglish=(Button)findViewById(R.id.btnEnglish);
        btnGujarati=(Button)findViewById(R.id.btnGujarati);

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_language_Code="1";
                Intent i=new Intent(getApplicationContext(),FirstActivity.class);
                startActivity(i);

            }
        });

        btnGujarati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_language_Code="2";
                Intent i=new Intent(getApplicationContext(),FirstActivity.class);
                startActivity(i);

            }
        });
    }
}
