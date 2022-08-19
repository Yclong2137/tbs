package com.ycl.tbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.tencent.smtt.sdk.TbsReaderView;
import com.ycl.tbs.utils.FileUtil;
import com.ycl.tbs.utils.Logger;
import com.ycl.tbs.widgets.RoundProgressBarWidthNumber;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * 文件预览
 */
public class FileDisplayActivity extends Activity {


    private TbsReaderView mTbsReaderView;

    private static final String KEY_URL = "key_url";
    private RoundProgressBarWidthNumber mProgressBar;
    private TextView mTitleView;

    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());


    public static void start(Context context, String filePath) {
        Intent starter = new Intent(context, FileDisplayActivity.class);
        starter.putExtra(KEY_URL, filePath);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_file);
        initView();
        mProgressBar.setVisibility(View.VISIBLE);
        openFile(new FileCallback() {
            @Override
            public void onSuccess(File file) {
                Logger.i("onSuccess() called with: file = [" + file + "]");
                post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                        openFileInternal(file);
                    }
                });

            }

            @Override
            public void onFail(String msg, Exception e) {
                Logger.e("onFail() called with: msg = [" + msg + "], e = [" + e + "]");
                post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onProgress(int progress) {
                Logger.i("onProgress() called with: progress = [" + progress + "]");
                updateProgressBar(progress);
            }
        });
    }

    private void updateProgressBar(int progress) {
        post(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setProgress(progress);
            }
        });
    }


    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        mTitleView = findViewById(R.id.tv_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        ViewGroup mContainer = findViewById(R.id.fragment_container);
        mProgressBar = findViewById(R.id.progress_bar);
        if (mTbsReaderView == null) {
            mTbsReaderView = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
                @Override
                public void onCallBackAction(Integer integer, Object o, Object o1) {
                    Logger.i("onCallBackAction() called with: integer = [" + integer + "], o = [" + o + "], o1 = [" + o1 + "]");
                }
            });
        }
        mContainer.addView(mTbsReaderView);
    }


    /**
     * 现在文件
     *
     * @param url 下载路径
     */
    private void downloadFile(String url, File dest, FileDownloadListener l) {
        try {
            File parentFile = dest.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            dest.createNewFile();
            FileUtil.downloadFile(url, dest, l);
        } catch (IOException e) {
            e.printStackTrace();
            l.onFail(e.getMessage(), e);
        }

    }

    /**
     * 打开文件
     */
    private void openFile(FileCallback callback) {
        String path = getIntent().getStringExtra(KEY_URL);
        Logger.e("preOpenFile path：" + path);
        if (mTitleView != null) {
            mTitleView.setText(FilenameUtils.getName(path));
        }
        if (path == null) {
            callback.onFail("文件路径不存在", null);
            return;
        }
        File destFile = getDestFile(path);
        if (destFile != null && destFile.exists()) {
            FileUtils.deleteQuietly(destFile);
        }
        downloadFile(path, getDestFile(path), callback);

    }

    /**
     * 获取目标文件
     *
     * @param path
     * @return
     */
    private File getDestFile(String path) {
        String fileName = FilenameUtils.getName(path);
        File dir = this.getExternalFilesDir("downloads");
        return FileUtils.getFile(dir, fileName);
    }


    /**
     * 打开文件
     */
    private void openFileInternal(File file) {
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
        String path = file.getPath();
        String suffix = FilenameUtils.getExtension(path);
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
        sMainHandler.removeCallbacksAndMessages(null);
        FileUtil.cancelAll();
    }


    interface FileCallback extends FileDownloadListener {

    }


    void post(Runnable r) {
        if (isMainThread()) {
            r.run();
        } else {
            sMainHandler.post(r);
        }
    }


    boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

}

