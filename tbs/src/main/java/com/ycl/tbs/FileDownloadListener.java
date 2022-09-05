package com.ycl.tbs;

import java.io.File;

/**
 * 文件下载
 */
public interface FileDownloadListener extends ProgressListener {


    void onSuccess(File file);

    void onFail(String msg, Exception e);

}
