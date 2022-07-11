package com.ycl.tbs.fetcher.impl;

import android.content.Context;

import com.ycl.tbs.FileDownloadListener;
import com.ycl.tbs.fetcher.TbsCoreFetcher;
import com.ycl.tbs.utils.FileUtil;

import java.io.File;
import java.io.IOException;

public class TbsCoreFromMemoryImpl implements TbsCoreFetcher {

    private final Context context;

    public TbsCoreFromMemoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public void fetch(FileDownloadListener listener) throws IOException {
        try {
            String filePath = FileUtil.getTBSFileDir(context) + "/045912_x5.tbs.apk";
            FileUtil.copyFileFromAssets(context, "045912_x5.tbs.apk", filePath);
            if (listener != null) listener.onSuccess(new File(filePath));
        } catch (Exception e) {
            if (listener != null) listener.onFail("fetch fail", e);
        }
    }
}
