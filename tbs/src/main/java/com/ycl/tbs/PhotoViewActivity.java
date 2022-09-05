package com.ycl.tbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.ycl.tbs.widgets.photoview.PhotoView;

public class PhotoViewActivity extends Activity {

    private static final String KEY_URL = "key_url";

    private PhotoView mPhotoView;


    public static void start(Context context, String url) {
        Intent starter = new Intent(context, PhotoViewActivity.class);
        starter.putExtra(KEY_URL, url);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        mPhotoView = findViewById(R.id.photo_view);

    }
}
