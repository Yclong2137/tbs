package com.ycl.tbs;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.smtt.sdk.TbsReaderPredownload;
import com.ycl.tbs.utils.HiExecutor;
import com.ycl.tbs.utils.Logger;
import com.ycl.tbs.utils.TbsSdk;

public class TbsPluginProvider extends ContentProvider {

    private static final String TAG = "TbsPluginProvider";


    @Override
    public boolean onCreate() {
        Logger.i("onCreate() called" + "currentThread：" + Thread.currentThread());
        HiExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                TbsSdk.initTbs(getContext(), new TbsSdk.TbsCallback() {
                    @Override
                    public void onSuccess() {
                        Logger.e("tbs core install success");
                        new TbsReaderPredownload(new TbsReaderPredownload.ReaderPreDownloadCallback() {
                            @Override
                            public void onEvent(String s, int i, boolean b) {

                            }
                        }).init(getContext());
                    }

                    @Override
                    public void onFail(Exception e) {
                        Logger.e("tbs core install fail", e);
                    }
                });
            }
        });
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
