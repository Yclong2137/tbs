package com.ycl.tbs.fetcher;

import com.ycl.tbs.FileDownloadListener;
import com.ycl.tbs.ProgressListener;

import java.io.IOException;

public interface TbsCoreFetcher {

    /**
     * 拉取内核
     *
     * @return 内核路径
     */
    void fetch(FileDownloadListener listener) throws IOException;

}
