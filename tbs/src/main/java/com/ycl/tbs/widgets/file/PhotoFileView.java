package com.ycl.tbs.widgets.file;

import android.net.Uri;
import android.view.ViewGroup;

import com.ycl.tbs.Hook;
import com.ycl.tbs.widgets.photoview.PhotoView;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class PhotoFileView extends FileView {

    private final PhotoView mPhotoView;

    private static final Set<String> suffixSet = new HashSet<>();


    static {
        suffixSet.add("Jpeg");
        suffixSet.add("jpg");
        suffixSet.add("png");
        suffixSet.add("svg");
        suffixSet.add("webp");
        suffixSet.add("bmp");
        suffixSet.add("gif");
    }


    public static boolean isSupport(String ext) {
        return suffixSet.contains(ext);
    }


    public PhotoFileView(ViewGroup container, Hook hook) {
        super(container, hook);
        container.addView(mPhotoView = new PhotoView(mContext));
    }

    @Override
    public void loadInto(String url) {
        downloadFile(url);
    }

    @Override
    public void openFile(File file) {
        mPhotoView.setImageURI(Uri.fromFile(file));
    }
}
