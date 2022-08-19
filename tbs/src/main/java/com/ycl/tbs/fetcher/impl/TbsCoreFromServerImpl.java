package com.ycl.tbs.fetcher.impl;

import android.content.Context;

import com.ycl.tbs.FileDownloadListener;
import com.ycl.tbs.fetcher.TbsCoreFetcher;
import com.ycl.tbs.utils.FileUtil;

import java.io.IOException;

public class TbsCoreFromServerImpl implements TbsCoreFetcher {


    private final Context context;

    public TbsCoreFromServerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void fetch(FileDownloadListener listener) throws IOException {
        String filePath = FileUtil.getTBSFileDir(context) + "/045912_x5.tbs.apk";
        //FileUtil.downloadFile("http://api.developer.ttxc.net:6081/sysFile/045912_x5.tbs.apk", filePath, listener);

    }


}
