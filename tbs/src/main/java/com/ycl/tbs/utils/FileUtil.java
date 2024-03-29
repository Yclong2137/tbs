package com.ycl.tbs.utils;

import android.content.Context;

import com.ycl.tbs.FileDownloadListener;
import com.ycl.tbs.Hook;
import com.ycl.tbs.ProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.HttpUrl;
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
     * 取消所有请求
     */
    public static void cancelAll() {
        okHttpClient.dispatcher().cancelAll();
    }

    /**
     * 下载文件
     *
     * @param url  下载路径
     * @param dest 文件
     */
    public static void downloadFile(String url, final File dest, FileDownloadListener listener) {
        HiExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        File file = saveFile(response.body(), dest, listener);
                        if (listener != null) {
                            listener.onSuccess(file);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFail(response.message(), null);
                        }
                    }

                } catch (Exception e) {
                    listener.onFail("文件下载出现错误", e);
                }
            }
        });


    }

    /**
     * 获取文件名
     *
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) return null;
        List<String> pathSegments = httpUrl.pathSegments();
        if (pathSegments == null || pathSegments.isEmpty()) {
            return null;
        }
        return pathSegments.get(pathSegments.size() - 1);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static final String getExtension(String fileName) {
        if (fileName == null) {
            return null;
        } else {
            int index = indexOfExtension(fileName);
            return index == -1 ? "" : fileName.substring(index + 1);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int extensionPos = filename.lastIndexOf(46);
            int lastSeparator = indexOfLastSeparator(filename);
            return lastSeparator > extensionPos ? -1 : extensionPos;
        }
    }


    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int lastUnixPos = filename.lastIndexOf(47);
            int lastWindowsPos = filename.lastIndexOf(92);
            return Math.max(lastUnixPos, lastWindowsPos);
        }
    }


    /**
     * 保存文件
     *
     * @param body             响应体
     * @param dest             目的文件
     * @param progressListener 进度监听
     * @throws IOException
     */
    private static File saveFile(ResponseBody body, File dest, ProgressListener progressListener) throws IOException {
        try (BufferedSink sink = Okio.buffer(Okio.sink(dest)); BufferedSource source = body.source()) {
            byte[] buffer = new byte[8192];
            int bytes;
            long downloadLength = 0;
            long total = body.contentLength();
            while ((bytes = source.read(buffer)) != -1) {
                sink.write(buffer, 0, bytes);
                downloadLength += bytes;
                long finalDownloadLength = downloadLength;
                if (progressListener != null) {
                    progressListener.onProgress((int) (finalDownloadLength * 1.0 / total * 100));
                }

            }
            sink.flush();
        }
        return dest;
    }


}


