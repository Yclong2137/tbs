package com.ycl.tbs.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.ycl.tbs.FileDownloadListener;
import com.ycl.tbs.fetcher.TbsCoreFetcher;
import com.ycl.tbs.fetcher.impl.TbsCoreFromMemoryImpl;
import com.ycl.tbs.fetcher.impl.TbsCoreFromServerImpl;

import java.io.File;
import java.util.HashMap;


public class TbsSdk {


    private static final Object lock = new Object();

    /**
     * 内核拉取器
     */
    private static TbsCoreFetcher sTbsCoreFetcher;


    public static void setTbsCoreFetcher(TbsCoreFetcher tbsCoreFetcher) {
        sTbsCoreFetcher = tbsCoreFetcher;
    }


    public static TbsCoreFetcher getTbsCoreFetcher(Context context) {
        if (sTbsCoreFetcher == null) {
            return sTbsCoreFetcher = new TbsCoreFromMemoryImpl(context);
        }
        return sTbsCoreFetcher;
    }

    private TbsSdk() {
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void openManageWriteSettings(Context context) {
        if (!Settings.System.canWrite(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }


    public static boolean isSupportOpenFile(String fileType) {
        return QbSdk.isSuportOpenFile(fileType, 1);
    }


    /**
     * 初始化内核
     *
     * @param context 上下文对象（全文）
     */
    public static void initTbs(Context context, TbsCallback callback) {
        synchronized (lock) {
            try {
                if ((QbSdk.getTbsVersion(context)) < 45912) {
                    FileUtil.copyFileFromAssets(context, "045912_x5.tbs.apk", FileUtil.getTBSFileDir(context).getPath() + "/045912_x5.tbs.apk");
                }
                HashMap<String, Object> map = new HashMap<>(2);
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
                QbSdk.initTbsSettings(map);
                if (QbSdk.getTbsVersion(context) == 0) {
                    ///下载内核
                    Logger.e("fetch x5 core from server");
                    getTbsCoreFetcher(context).fetch(new FileDownloadListener() {
                        @Override
                        public void onSuccess(File file) {
                            Logger.e("tbs core download success：" + file.getPath());
                            installTbsCore(context, file.getPath(), callback);
                        }

                        @Override
                        public void onFail(String msg, Exception e) {
                            Logger.e("tbs core download fail：", e);
                        }

                        @Override
                        public void onProgress(int progress) {
                            Logger.e("tbs core download progress：" + progress + "%");
                        }
                    });
                } else {
                    if (callback != null) callback.onSuccess();
                }

            } catch (Exception e) {
                Logger.e("initTbs: occur error", e);
                if (callback != null) callback.onFail(e);
            }
        }
    }

    /**
     * 安装内核
     *
     * @param context
     * @param path    内核路径
     */
    public static void installTbsCore(Context context, String path, TbsCallback callback) {
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Logger.i("onDownloadFinish() called with: i = [" + i + "]");
            }

            @Override
            public void onInstallFinish(int i) {
                Logger.e("x5 core install finished");
                if (callback != null) callback.onSuccess();
            }

            @Override
            public void onDownloadProgress(int i) {
                Logger.i("onDownloadProgress() called with: i = [" + i + "]");
            }
        });
        if (path != null && new File(path).exists()) {
            QbSdk.reset(context);
            ///安装内核
            Logger.e("install x5 core from " + path);
            QbSdk.installLocalTbsCore(context, 45912, path);
        } else {
            Logger.e("本地内核不存在");
        }
    }

    public interface TbsCallback {

        void onSuccess();

        void onFail(Exception e);


    }


}



