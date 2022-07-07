package com.ycl.tbs;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.tencent.smtt.sdk.TbsReaderPredownload;
import com.ycl.tbs.utils.Util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TbsContentProvider extends ContentProvider {

    private static final String TAG = "TbsContentProvider";
    /**
     * 工作线程池
     */
    ThreadPoolExecutor workExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue(2));

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate() called" + "currentThread：" + Thread.currentThread());
        new TbsReaderPredownload(new TbsReaderPredownload.ReaderPreDownloadCallback() {
            @Override
            public void onEvent(String s, int i, boolean b) {
                Log.d(TAG, "onEvent() called with: s = [" + s + "], i = [" + i + "], b = [" + b + "]");
            }
        }).init(getContext());
        workExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Util.initTbs(getContext());
            }
        });
        workExecutor.shutdown();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
