package com.ycl.tbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.tencent.smtt.sdk.TbsReaderPredownload;
import com.tencent.smtt.sdk.TbsReaderView;
import com.ycl.tbs.utils.FileUtil;
import com.ycl.tbs.utils.HiExecutor;
import com.ycl.tbs.utils.Logger;
import com.ycl.tbs.utils.Util;

import java.io.File;

/**
 * 文件预览
 */
public class FileDisplayActivity extends Activity {


    private TbsReaderView mTbsReaderView;

    private static final String KEY_PATH = "key_path";


    private static Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Logger.i("handleMessage() called with: msg = [" + msg + "]");
        }
    };

    public static void start(Context context, String filePath) {
        Intent starter = new Intent(context, FileDisplayActivity.class);
        starter.putExtra(KEY_PATH, filePath);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HiExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Util.awaitX5CoreLoaded();
            }
        });

        if (mTbsReaderView == null) {
            mTbsReaderView = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
                @Override
                public void onCallBackAction(Integer integer, Object o, Object o1) {
                    Logger.i("onCallBackAction() called with: integer = [" + integer + "], o = [" + o + "], o1 = [" + o1 + "]");
                }
            });
        }
        setContentView(mTbsReaderView);

        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        String bsReaderTemp = FileUtil.getTBSFileDir(FileDisplayActivity.this) + "/TbsReaderTemp";
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            Logger.e("准备创建/storage/emulated/0/TbsReaderTemp！！");
            boolean mkdir = bsReaderTempFile.mkdir();
            if (!mkdir) {
                Logger.e("创建/storage/emulated/0/TbsReaderTemp失败！！！！！");
            }
        }

        final String path = FileUtil.getTBSFileDir(getApplicationContext()).getPath() + "/HowToLoadX5Core.doc";
        String suffix = FileUtil.getSuffix(path);
        boolean bool = mTbsReaderView.preOpen(suffix, false);
        Logger.e("preOpen：" + bool);
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