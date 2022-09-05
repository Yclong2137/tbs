package com.ycl.tbs;

import com.ycl.tbs.utils.Logger;

import java.io.File;

public class Hook {


    public Hook() {
    }


    public boolean onEnvReady(String url, File srcFile) {
        Logger.i("current Thread:" + Thread.currentThread() + " onReady() called");
        return true;
    }

}
