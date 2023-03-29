package com.example.musicmap.screens;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicmap.R;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        ImageView imageView = findViewById(R.id.no_internet_placeholder);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.no_internet;
        Glide.with(this).load(path).into(imageView);
    }

}