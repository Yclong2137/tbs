package com.ycl.tbs;

import java.io.Serializable;

public interface ProgressListener extends Serializable {

    void onProgress(int progress);

}
