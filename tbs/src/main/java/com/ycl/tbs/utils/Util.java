package com.ycl.tbs.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderPredownload;
import com.ycl.tbs.fetcher.X5CoreFetcher;
import com.ycl.tbs.fetcher.impl.X5CoreFetcherImpl;

import java.io.File;
import java.util.HashMap;

public class Util {


    private static final Object lock = new Object();

    /**
     * 内核拉取器
     */
    private static X5CoreFetcher coreFetcher;


    private static volatile boolean isInit = false;

    private Util() {
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
    public static void initTbs(Context context) {
        synchronized (lock) {
            ///解决打开文件白屏
            new TbsReaderPredownload(new TbsReaderPredownload.ReaderPreDownloadCallback() {
                @Override
                public void onEvent(String s, int i, boolean b) {

                }
            }).init(context);
            try {
                isInit = false;
                boolean isInitTbs = QbSdk.canLoadX5(context);
                if (!isInitTbs || (QbSdk.getTbsVersion(context)) < 45912) {
                    FileUtil.copyFileFromAssets(context, "045912_x5.tbs.apk", FileUtil.getTBSFileDir(context).getPath() + "/045912_x5.tbs.apk");
                }
                HashMap<String, Object> map = new HashMap<>(2);
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);

                QbSdk.initTbsSettings(map);
                boolean canLoadX5 = QbSdk.canLoadX5(context);
                Logger.e("canLoadX5: " + canLoadX5 + " | TbsVersion:" + QbSdk.getTbsVersion(context));
                if (canLoadX5) {
                    isInit = true;
                    lock.notifyAll();
                    return;
                }
                if (QbSdk.getTbsVersion(context) == 0) {
                    ///下载内核
                    if (coreFetcher == null) {
                        coreFetcher = new X5CoreFetcherImpl(context);
                    }
                    Logger.e("fetch x5 core from server");
                    String corePath = coreFetcher.fetch();
                    Logger.e("x5 core path：" + corePath);
                    if (corePath != null && new File(corePath).exists()) {
                        QbSdk.reset(context);
                        ///安装内核
                        QbSdk.installLocalTbsCore(context, 45912, corePath);
                        int tTbsVersion;
//                        Logger.e("wait x5 core loaded");
//                        ///等待内核加载成功
//                        while ((tTbsVersion = QbSdk.getTbsVersion(context)) == 0) {
//                        }
                        Logger.e("x5 core loaded");
//                        Logger.e("canLoadX5: " + QbSdk.canLoadX5(context) + " | TbsVersion:" + tTbsVersion);
                        isInit = true;
                        lock.notifyAll();
                    }
                }

            } catch (Exception e) {
                Logger.e("initTbs: occur error", e);
                isInit = true;
                lock.notifyAll();
            }
        }
    }

    /**
     * 等待内核加载完成
     */
    public static void awaitX5CoreLoaded() {
        Logger.e("等待内核加载完成");
        if (isInit) {
            Logger.e("内核已加载完成");
            return;
        }
        synchronized (lock) {

        }
        Logger.e("内核加载完成");

    }

}


