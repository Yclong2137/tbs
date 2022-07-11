package com.ycl.tbs.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.ycl.tbs.FileDownloadListener;
import com.ycl.tbs.ProgressListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * 文件操作工具类
 * <p>
 * 将存储文件目录规范化，对应功能划分目录，后续操作缓存管理进行调用操作
 */
public class FileUtil {


    private static final OkHttpClient okHttpClient = new OkHttpClient();


    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    /**
     * 保存文件预览的目录
     *
     * @param context 上下文对象
     */
    public static File getTBSFileDir(Context context) {
        String dirName = "TBSFile";
        return context.getExternalFilesDir(dirName);
    }

    /**
     * 保存文件预览的目录
     *
     * @param context 上下文对象
     */
    public static File getTmpFileDir(Context context) {
        return context.getExternalFilesDir("tmpFile");
    }


    /**
     * 把asset的文件转化为本地文件
     *
     * @param context 上下文对象
     * @param oldPath 旧的文件路径
     * @param newPath 新的文件路径
     */
    public static void copyFileFromAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFileFromAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void execute(Runnable r) {
        HiExecutor.getInstance().execute(r);
    }

    /**
     * 获取url文件后缀
     *
     * @param url 文件链接
     */
    public static String getSuffix(String url) {
        if ((url != null) && (url.length() > 0)) {
            int dot = url.lastIndexOf('.');
            if ((dot > -1) && (dot < (url.length() - 1))) {
                return url.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * 下载文件
     *
     * @param url      下载路径
     * @param filePath 文件路径
     */
    public static void downloadFile(String url, String filePath, FileDownloadListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onFail(e.getMessage(), e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    File file = saveFile(response.body(), filePath, listener);
                    sMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onSuccess(file);
                            }

                        }
                    });

                } else {
                    sMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onFail(response.message(), null);
                            }
                        }
                    });

                }
            }
        });


    }


    /**
     * 保存文件
     *
     * @param body             响应体
     * @param filePath         文件路径
     * @param progressListener 进度监听
     * @throws IOException
     */
    private static File saveFile(ResponseBody body, String filePath, ProgressListener progressListener) throws IOException {
        File file;
        try (BufferedSink sink = Okio.buffer(Okio.sink(file = new File(filePath))); BufferedSource source = body.source()) {
            byte[] buffer = new byte[8192];
            int bytes;
            long downloadLength = 0;
            long total = body.contentLength();
            while ((bytes = source.read(buffer)) != -1) {
                sink.write(buffer, 0, bytes);
                downloadLength += bytes;
                long finalDownloadLength = downloadLength;
                sMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressListener != null) {
                            progressListener.onProgress((int) (finalDownloadLength * 1.0 / total * 100));
                        }
                    }
                });

            }
            sink.flush();
        }
        return file;
    }
}


