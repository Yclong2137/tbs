package com.ycl.tbs.fetcher;

import java.io.IOException;

public interface X5CoreFetcher {

    /**
     * 拉取内核
     *
     * @return 内核路径
     */
    String fetch() throws IOException;

}
