package com.alexz.messenger.app.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.bumptech.glide.Glide;
import com.messenger.app.R;

public class FullscreenImageActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGE_URI = "IMAGE_URI";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        String uri = getIntent().getStringExtra(EXTRA_IMAGE_URI);
        imageView = findViewById(R.id.imageview_fullscreen);

        if (imageView != null && uri != null){
            Glide.with(this).load(uri).into(imageView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public static void startActivity(Context context, String imageUri,@Nullable  Bundle bundle) {
        Intent intent = new Intent(context, FullscreenImageActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI,imageUri);
        if (bundle == null) {
            context.startActivity(intent);
        } else{
            context.startActivity(intent,bundle);
        }
    }
}