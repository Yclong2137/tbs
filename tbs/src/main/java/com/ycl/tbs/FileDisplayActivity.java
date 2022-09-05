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
import android.widget.Toast;
import android.widget.Toolbar;

import com.ycl.tbs.utils.FileUtil;
import com.ycl.tbs.utils.Logger;
import com.ycl.tbs.widgets.RoundProgressBarWidthNumber;
import com.ycl.tbs.widgets.file.FileView;
import com.ycl.tbs.widgets.file.OfficeFileView;
import com.ycl.tbs.widgets.file.PhotoFileView;

import java.io.File;

/**
 * 文件预览
 */
public class FileDisplayActivity extends Activity {

    private static final String KEY_URL = "key_url";
    public static final String KEY_HOOK = "key_hook";
    private static final String STR_LOADING = "加载中...";
    private static final String STR_FAIL = "加载失败";
    private static final String STR_NOT_SUPPORT = "暂不支持该类型%s文件";
    private static final String STR_SUCCESS = "加载成功";
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());


    private FileView mFileView;

    private RoundProgressBarWidthNumber mProgressBar;
    private TextView mMsgView;
    private TextView mTitleView;
    private Hook hook;

    public static void startPreview(Context context, String filePath, Class<? extends Hook> hookClazz) {
        Intent starter = new Intent(context, FileDisplayActivity.class);
        starter.putExtra(KEY_URL, filePath);
        starter.putExtra(KEY_HOOK, hookClazz);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_file);
        String url = getIntent().getStringExtra(KEY_URL);
        try {
            Class<? extends Hook> hookClazz = (Class<Hook>) getIntent().getSerializableExtra(KEY_HOOK);
            hook = hookClazz.newInstance();
        } catch (Exception e) {
            Logger.e("HookClass is error", e);
        }
        initView();

        mProgressBar.setVisibility(View.VISIBLE);
        mMsgView.setText(STR_LOADING);
        ViewGroup mContainer = findViewById(R.id.fragment_container);
        String fileName = FileUtil.getFileName(url);
        String ext = FileUtil.getExtension(fileName);
        if (OfficeFileView.isSupport(ext)) {
            mFileView = new OfficeFileView(mContainer, hook);
        } else if (PhotoFileView.isSupport(ext)) {
            mFileView = new PhotoFileView(mContainer, hook);
        } else {
            Toast.makeText(this, "暂不支持该类型:" + ext + "的文件", Toast.LENGTH_SHORT).show();
            mMsgView.setText(String.format(STR_NOT_SUPPORT, ext));
            return;
        }
        mFileView.setFileCallback(new FileView.FileCallback() {
            @Override
            public void onFileNameChanged(String fileName) {
                if (mTitleView != null) {
                    mTitleView.setText(fileName);
                }
            }

            @Override
            public void onProgress(int progress) {
                if (mProgressBar != null) {
                    mProgressBar.setProgress(progress);
                }
            }

            @Override
            public void onFail(String msg, Exception e) {
                mProgressBar.setVisibility(View.GONE);
                mMsgView.setText(STR_FAIL + "：" + e.getMessage());
            }

            @Override
            public void onSuccess(File dstFile) {
                mProgressBar.setVisibility(View.GONE);
                mMsgView.setText(STR_SUCCESS);
            }
        });
        mFileView.loadInto(url);


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
        mProgressBar = findViewById(R.id.progress_bar);
        mMsgView = findViewById(R.id.tv_msg);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFileView.onDestroy();
    }


}

