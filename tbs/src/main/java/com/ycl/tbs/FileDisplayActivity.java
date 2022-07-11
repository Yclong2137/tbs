package com.ycl.tbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.smtt.sdk.TbsReaderView;
import com.ycl.tbs.utils.FileUtil;
import com.ycl.tbs.utils.Logger;

import java.io.File;

/**
 * 文件预览
 */
public class FileDisplayActivity extends Activity {


    private TbsReaderView mTbsReaderView;

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
                    Logger.i("onCallBackAction() called with: integer = [" + integer + "], o = [" + o + "], o1 = [" + o1 + "]");
                }
            });
        }
        setContentView(mTbsReaderView);
        openFile();
    }


    /**
     * 打开文件
     */
    private void openFile() {
        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        String bsReaderTemp = FileUtil.getTBSFileDir(FileDisplayActivity.this) + "/TbsReaderTemp";
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            Logger.e("ready create file：/storage/emulated/0/TbsReaderTemp！！");
            boolean mkdir = bsReaderTempFile.mkdir();
            if (!mkdir) {
                Logger.e("create file：/storage/emulated/0/TbsReaderTemp fail！！！！！");
            }
        }

        final String path = getIntent().getStringExtra(KEY_PATH);
        Logger.e("preOpenFile path：" + path);
        if (path == null || !new File(path).exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
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