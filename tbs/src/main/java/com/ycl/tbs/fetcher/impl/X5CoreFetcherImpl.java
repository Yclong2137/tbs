package com.ycl.tbs.fetcher.impl;

import android.content.Context;

import com.ycl.tbs.ProgressListener;
import com.ycl.tbs.fetcher.X5CoreFetcher;
import com.ycl.tbs.utils.FileUtil;
import com.ycl.tbs.utils.Logger;

import java.io.IOException;

public class X5CoreFetcherImpl implements X5CoreFetcher {



    private final Context context;

    public X5CoreFetcherImpl(Context context) {
        this.context = context;
    }

    @Override
    public String fetch() throws IOException {
        String filePath = FileUtil.getTBSFileDir(context) + "/045912_x5.tbs.apk";
        FileUtil.downloadFile("http://192.168.31.72:6081/sysFile/045912_x5.tbs.apk", filePath, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                Logger.i("内核下载进度：" + progress);
            }
        });
        return filePath;

    }


}
