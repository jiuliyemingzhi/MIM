package net.jiuli.mim.fragments.panel;

import android.annotation.SuppressLint;

import net.jiuli.common.app.Application;
import net.jiuli.common.utils.FileTools;
import net.jiuli.mim.R;

import java.util.LinkedList;
import java.util.List;

import static net.jiuli.mim.fragments.panel.Source.TYPE_IMAGE;
import static net.jiuli.mim.fragments.panel.Source.TYPE_VIDEO;

final class SelectedEntry {

    private static final int MAX_IMAGE_COUNT = 15;


    private OnSelectChanged onSelectChanged;

    private int type = -1;

    public void setOnSelectChanged(OnSelectChanged onSelectChanged) {
        this.onSelectChanged = onSelectChanged;
    }

    final List<Source> sources = new LinkedList<>();

    private long filesSize = 0;

    long getFilesSize() {
        return filesSize;
    }

    private GalleryListener galleryListener;


    void setGalleryListeners(GalleryListener galleryListener) {
        this.galleryListener = galleryListener;
    }

    void clear() {
        if (!sources.isEmpty()) {
            for (Source source : sources) {
                source.isSelect = false;
                source.refresh();
            }
        }

        type = -1;
        sources.clear();
        filesSize = 0;

        if (galleryListener != null) {
            galleryListener.onSelectedCountChange(0, false);
        }
    }


    boolean isVideo() {
        return type == TYPE_VIDEO;
    }

    int size() {
        return sources.size();
    }

    boolean remove(Source source, boolean isRefresh) {
        boolean b;
        if (b = sources.remove(source)) {
            source.isSelect = false;
            for (int i = source.counter - 1; i < size() && i >= 0; i++) {
                Source s = sources.get(i);
                --s.counter;
                if (isRefresh) s.refresh();
            }
            source.counter = 0;
            filesSize -= FileTools.getSize(source.path);
            if (size() == 0) {
                filesSize = 0;
            }
        }
        return b;
    }

    public boolean isEmpty() {
        return sources.isEmpty();
    }

    private void add(Source source) {
        type = source.type;
        source.isSelect = true;
        sources.add(source);
        source.counter = sources.size();
        filesSize += FileTools.getSize(source.path);
    }


    @SuppressLint("StringFormatMatches")
    boolean addOrRemove(Source source) {
        if (remove(source, true)) {
            if (sources.isEmpty()) {
                type = -1;
            }
        } else if (type == -1) {
            add(source);
        } else if (type == TYPE_IMAGE) {
            if (source.type != type) {
                Application.showToast("视频和图片不能同时选择!");
                return false;
            }
            if (size() >= MAX_IMAGE_COUNT) {
                String str = Application.getInstance().getString(R.string.label_gallery_select_max_size);
                str = String.format(str, MAX_IMAGE_COUNT);
                Application.showToast(str);
                return false;
            } else {
                add(source);
            }
        } else {
            if (source.type == TYPE_VIDEO) {
                Application.showToast("视频只能选择一个!");
            } else {
                Application.showToast("视频和图片不能同时选择!");
            }
            return false;
        }
        if (onSelectChanged != null) {
            onSelectChanged.onSelectChanged(size());
        }

        return true;
    }

    public interface OnSelectChanged {
        void onSelectChanged(int sourceSize);
    }
}
