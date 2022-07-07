package com.ycl.tbs.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;

public class Util {

    private static final String TAG = "Util";

    public static final String TBS_VERSION = "045912";
    /**
     * 内核路径
     */
    public static final String TBS_CORE_PATH = TBS_VERSION + "_x5.tbs.apk";

    private Util() {
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void openManageWriteSettings(Context context) {
        if (!Settings.System.canWrite(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }


    public static  boolean isSupportOpenFile(String fileType){
        return  QbSdk.isSuportOpenFile(fileType,1);
    }

    /**
     * 初始化内核
     *
     * @param context 上下文对象（全文）
     */
    public static void initTbs(Context context) {
        //new TbsReaderPredownload(null).init(context);
        boolean isInitTbs = QbSdk.canLoadX5(context);
        if (!isInitTbs || QbSdk.getTbsVersion(context) < 46007) {
            FileUtil.copyFileFromAssets(context, "045912_x5.tbs.apk", FileUtil.getTBSFileDir(context).getPath() + "/045912_x5.tbs.apk");
        }
        HashMap<String, Object> map = new HashMap<>(2);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_PRIVATE_CLASSLOADER, true);

        QbSdk.initTbsSettings(map);
        boolean canLoadX5 = QbSdk.canLoadX5(context);
        Log.e(TAG, "canLoadX5: " + canLoadX5 + "|TbsVersion:" + QbSdk.getTbsVersion(context));
        if (canLoadX5) {
            return;
        }
        if (QbSdk.getTbsVersion(context) == 0) {
            QbSdk.reset(context);
            QbSdk.installLocalTbsCore(context, 45912, FileUtil.getTBSFileDir(context).getPath() + "/045912_x5.tbs.apk");
        }
    }

}
