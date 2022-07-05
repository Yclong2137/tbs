package com.ycl.tbs_plugin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.tencent.smtt.sdk.TbsReaderPredownload;
import com.ycl.tbs_plugin.utils.FileUtil;
import com.ycl.tbs_plugin.utils.Util;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Util.openManageWriteSettings(this);
        new TbsReaderPredownload(new TbsReaderPredownload.ReaderPreDownloadCallback() {
            @Override
            public void onEvent(String s, int i, boolean b) {

            }
        }).init(this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String path = FileUtil.getTBSFileDir(getApplicationContext()).getPath() + "/HowToLoadX5Core.doc";
                FileUtil.copyFileFromAssets(getApplicationContext(), "HowToLoadX5Core.doc", path);
                FileDisplayActivity.start(MainActivity.this, path);
            }
        });
        Util.initTbs(MainActivity.this.getApplicationContext());


    }
}