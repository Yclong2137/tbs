package com.ycl.tbs.widgets.file;

import android.os.Bundle;
import android.view.ViewGroup;

import com.tencent.smtt.sdk.TbsReaderView;
import com.ycl.tbs.Hook;
import com.ycl.tbs.utils.FileUtil;
import com.ycl.tbs.utils.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class OfficeFileView extends FileView {


    private TbsReaderView mTbsReaderView;

    private static final Set<String> suffixSet = new HashSet<>();


    static {
        suffixSet.add("doc");
        suffixSet.add("docx");
        suffixSet.add("xls");
        suffixSet.add("xlsx");
        suffixSet.add("ppt");
        suffixSet.add("pdf");
    }


    public static boolean isSupport(String ext) {
        return suffixSet.contains(ext);
    }


    public OfficeFileView(ViewGroup container, Hook hook) {
        super(container, hook);
        if (mTbsReaderView == null) {
            mTbsReaderView = new TbsReaderView(container.getContext(), new TbsReaderView.ReaderCallback() {
                @Override
                public void onCallBackAction(Integer integer, Object o, Object o1) {
                    Logger.i("onCallBackAction() called with: integer = [" + integer + "], o = [" + o + "], o1 = [" + o1 + "]");
                }
            });
            container.addView(mTbsReaderView);
        }
    }


    @Override
    public void loadInto(String url) {
        downloadFile(url);
    }

    @Override
    public void openFile(File file) {
        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        String bsReaderTemp = FileUtil.getTBSFileDir(mContext) + "/TbsReaderTemp";
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            Logger.e("ready create file：/storage/emulated/0/TbsReaderTemp！！");
            boolean mkdir = bsReaderTempFile.mkdir();
            if (!mkdir) {
                Logger.e("create file：/storage/emulated/0/TbsReaderTemp fail！！！！！");
            }
        }
        String path = file.getPath();
        String suffix = FileUtil.getExtension(path);
        boolean bool = mTbsReaderView.preOpen(suffix, false);
        Logger.e("preOpen：" + bool);
        if (bool) {
            Bundle bundle = new Bundle();
            bundle.putString("filePath", path);
            bundle.putString("tempPath", FileUtil.getTBSFileDir(mContext) + "/" + "TbsReaderTemp");
            mTbsReaderView.openFile(bundle);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }


}
