package net.jiuli.mim.fragments.panel;

import net.jiuli.common.utils.DiffUiDataCallback;
import net.jiuli.common.widget.recycler.RecyclerAdapter;

import java.util.Objects;

final class Source extends DiffUiDataCallback<Source> implements DiffUiDataCallback.UiDataDiffer<Source> {

    static final int TYPE_IMAGE = 0;
    static final int TYPE_VIDEO = 1;

    int id;
    String path;
    String thumbPath;
    long date;
    boolean isSelect;
    int type;
    int width;
    int height;
    int counter;
    int position;

    static RecyclerAdapter mAdapter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return isUiContentSame(source);
    }


    void refresh() {
        mAdapter.notifyItemChanged(position);
    }


    boolean isImage() {
        return type == TYPE_IMAGE;
    }


    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    @Override
    public boolean isSame(Source old) {
        return Objects.equals(path, old.path);
    }

    @Override
    public boolean isUiContentSame(Source source) {
        return id == source.id &&
                isSelect == source.isSelect &&
                width == source.width &&
                height == source.height &&
                position == source.position &&
                counter == source.counter &&
                Objects.equals(path, source.path);
    }
}
