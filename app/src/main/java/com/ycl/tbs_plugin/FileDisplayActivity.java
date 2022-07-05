package com.ycl.tbs_plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.tencent.smtt.sdk.TbsReaderPredownload;
import com.tencent.smtt.sdk.TbsReaderView;
import com.ycl.tbs_plugin.utils.FileUtil;

import java.io.File;

public class FileDisplayActivity extends AppCompatActivity {

    private static final String TAG = "FileDisplayActivity";

    TbsReaderView mTbsReaderView;

    private static final String KEY_PATH = "key_path";

    public static void start(Context context, String filePath) {
        Intent starter = new Intent(context, FileDisplayActivity.class);
        starter.putExtra(KEY_PATH, filePath);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mTbsReaderView == null) {
            mTbsReaderView = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
                @Override
                public void onCallBackAction(Integer integer, Object o, Object o1) {
                    Log.d(TAG, "onCallBackAction() called with: integer = [" + integer + "], o = [" + o + "], o1 = [" + o1 + "]");
                }
            });
        }

        setContentView(mTbsReaderView);
        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        String bsReaderTemp = FileUtil.getTBSFileDir(FileDisplayActivity.this) + "/TbsReaderTemp";
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            Log.e("111111", "准备创建/storage/emulated/0/TbsReaderTemp！！");
            boolean mkdir = bsReaderTempFile.mkdir();
            if (!mkdir) {
                Log.e("111111", "创建/storage/emulated/0/TbsReaderTemp失败！！！！！");
            }
        }


        final String path = FileUtil.getTBSFileDir(getApplicationContext()).getPath() + "/HowToLoadX5Core.doc";
        String suffix = FileUtil.getSuffix(path);
        boolean bool = mTbsReaderView.preOpen(suffix, false);
        Log.e("11111", "bool:" + bool);
        if (bool) {
            Bundle bundle = new Bundle();
            bundle.putString("filePath", path);
            bundle.putString("tempPath", FileUtil.getTBSFileDir(FileDisplayActivity.this) + "/" + "TbsReaderTemp");

            mTbsReaderView.openFile(bundle);
        }
    }

    /**
     * 打开文件
     */
    private void openFile() {
        String path = getIntent().getStringExtra(KEY_PATH);
        if (path == null || !new File(path).exists()) {
            Log.e(TAG, "文件：" + path + "不存在");
            return;
        }
        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        String bsReaderTemp = FileUtil.getTBSFileDir(FileDisplayActivity.this) + "/TbsReaderTemp";
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            Log.e(TAG, "准备创建/storage/emulated/0/TbsReaderTemp！！");
            boolean mkdir = bsReaderTempFile.mkdir();
            if (!mkdir) {
                Log.e(TAG, "创建/storage/emulated/0/TbsReaderTemp失败！！！！！");
            }
        }

        boolean bool = mTbsReaderView.preOpen(FileUtil.getSuffix(path), false);
        Log.e(TAG, "preOpen：" + bool);
        if (bool) {
            Bundle bundle = new Bundle();
            bundle.putString("filePath", path);
            bundle.putString("tempPath", FileUtil.getTBSFileDir(FileDisplayActivity.this) + "/" + "TbsReaderTemp");
            mTbsReaderView.openFile(bundle);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }


}