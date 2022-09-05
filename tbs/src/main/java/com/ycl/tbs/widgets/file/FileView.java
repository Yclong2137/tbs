package com.ycl.tbs.widgets.file;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;

import com.ycl.tbs.FileDownloadListener;
import com.ycl.tbs.Hook;
import com.ycl.tbs.utils.FileUtil;
import com.ycl.tbs.utils.HiExecutor;
import com.ycl.tbs.utils.Logger;

import java.io.File;

public abstract class FileView {


    public static final class Task implements Runnable {
        private final String url;
        private final File dir;
        private final String fileName;
        private final Hook hook;
        private final FileDownloadListener l;

        /**
         * task
         *
         * @param url      下载地址
         * @param dir      文件夹
         * @param fileName 文件名
         * @param hook     hook
         * @param l
         */
        public Task(String url, File dir, String fileName, Hook hook, FileDownloadListener l) {
            this.url = url;
            this.dir = dir;
            this.fileName = fileName;
            this.hook = hook;
            this.l = l;
        }


        @Override
        public void run() {
            File srcFile = new File(dir, fileName);
            boolean ready = true;
            if (hook != null && (ready = hook.onEnvReady(url, srcFile))) {
                Logger.i("Hook 生效");
            }
            if (ready) {
                try {
                    if (srcFile.getParentFile() == null) {
                        srcFile.mkdirs();
                    }
                    srcFile.createNewFile();
                    FileUtil.downloadFile(url, srcFile, l);
                } catch (Exception e) {
                    l.onFail("出现错误", e);
                }
            }
        }
    }

    protected Context mContext;
    protected File dir;
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private Hook mHook;

    private FileCallback mFileCallback;

    public void setFileCallback(FileCallback callback) {
        this.mFileCallback = callback;
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public void postToMain(Runnable r) {
        if (isMainThread()) {
            r.run();
        } else {
            uiHandler.post(r);
        }
    }

    public FileView(ViewGroup container, Hook hook) {
        this.mContext = container.getContext();
        dir = mContext.getExternalFilesDir("downloads");
        this.mHook = hook;
    }

    /**
     * 下载文件
     *
     * @param url
     */
    protected void downloadFile(String url) {
        String fileName = FileUtil.getFileName(url);
        if (mFileCallback != null) {
            mFileCallback.onFileNameChanged(fileName);
        }
        HiExecutor.getInstance().execute(new Task(url, dir, fileName, mHook, new FileDownloadListener() {
            @Override
            public void onSuccess(File file) {
                postToMain(new Runnable() {
                    @Override
                    public void run() {
                        if (mFileCallback != null) {
                            mFileCallback.onSuccess(file);
                        }
                        FileView.this.onSuccess(file);
                    }
                });
            }

            @Override
            public void onFail(String msg, Exception e) {
                postToMain(new Runnable() {
                    @Override
                    public void run() {
                        if (mFileCallback != null) {
                            mFileCallback.onFail(msg, e);
                        }
                        FileView.this.onFail(msg, e);
                    }
                });
            }

            @Override
            public void onProgress(int progress) {
                postToMain(new Runnable() {
                    @Override
                    public void run() {
                        if (mFileCallback != null) {
                            mFileCallback.onProgress(progress);
                        }
                        FileView.this.onProgress(progress);
                    }
                });
            }
        }));
    }

    /**
     * 文件下载成功
     *
     * @param file
     */
    protected void onSuccess(File file) {
        openFile(file);
    }

    /**
     * 文件下载失败
     *
     * @param msg
     * @param e
     */
    protected void onFail(String msg, Exception e) {

    }

    /**
     * 文件下载进度
     *
     * @param progress
     */
    protected void onProgress(int progress) {

    }




    public abstract void loadInto(String url);


    /**
     * 打开文件
     *
     * @param file 目标文件
     */
    public abstract void openFile(File file);


    public void onDestroy() {
        uiHandler.removeCallbacksAndMessages(null);
    }


    public interface FileCallback {

        default void onFileNameChanged(String fileName) {
        }

        default void onSuccess(File dstFile) {
        }

        default void onFail(String msg, Exception e) {
        }

        default void onProgress(int progress) {
        }

    }


}
