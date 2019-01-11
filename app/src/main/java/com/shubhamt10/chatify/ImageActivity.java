package com.shubhamt10.chatify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ImageActivity extends AppCompatActivity {

    Intent backIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        setTitle("Image");

        ImageView fullImageView = findViewById(R.id.fullImageView);

        Glide.with(fullImageView.getContext())
                .load(url)
                .into(fullImageView);

    }

}
