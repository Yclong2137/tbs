package com.ycl.tbs_plugin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ycl.tbs.FileDisplayActivity;
import com.ycl.tbs.utils.FileUtil;
import com.ycl.vpn_helper.VpnHelper;
import com.ycl.vpn_helper.bean.ConfigFile;
import com.ycl.vpn_helper.bean.P12File;
import com.ycl.vpn_helper.bean.X509File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VpnHelper vpnHelper = new VpnHelper();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path;
                vpnHelper.init(MainActivity.this, null, new VpnHelper.Callback() {
                    @Override
                    public void onVpnConnected(ConfigFile configFile, X509File x509File, P12File file) {
                        Log.d(TAG, "onVpnConnected() called with: configFile = [" + configFile + "], x509File = [" + x509File + "], file = [" + file + "]");
                    }

                    @Override
                    public void onFail(String msg, Exception e) {
                        Log.d(TAG, "onFail() called with: msg = [" + msg + "], e = [" + e + "]");
                    }

                    @Override
                    public void onExpired(X509File file, String msg) {
                        Log.d(TAG, "onExpired() called with: file = [" + file + "], msg = [" + msg + "]");
                    }

                    @Override
                    public void onConfigLoaded(ConfigFile file) {
                        Log.d(TAG, "onConfigLoaded() called with: file = [" + file + "]");
                    }

                    @Override
                    public void onP12FileLoaded(P12File file) {
                        Log.d(TAG, "onP12FileLoaded() called with: file = [" + file + "]");
                    }

                    @Override
                    public void onX509FileLoaded(X509File file) {
                        Log.d(TAG, "onX509FileLoaded() called with: file = [" + file + "]");
                    }
                });
                //FileUtil.copyFileFromAssets(getApplicationContext(), "HowToLoadX5Core.doc", path = FileUtil.getTBSFileDir(getApplicationContext()).getPath() + "/HowToLoadX5Core.doc");
                //FileDisplayActivity.start(MainActivity.this, "https://download.ttxc.net/word.doc");
                //startActivity(new Intent(MainActivity.this, FileDisplayActivity.class));
            }
        });
    }
}