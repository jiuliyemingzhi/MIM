package net.jiuli.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.jiuli.common.R;
import net.jiuli.common.app.Application;
import net.jiuli.common.utils.UiTool;
import net.jiuli.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * .Created by si ye on 17-8-26 .
 */

public class GalleryView extends RecyclerView implements RecyclerAdapter.AdapterListener<GalleryView.Image> {
    private static final int LOADER_ID = 0x0100;
    private static final int MAX_IMAGE_COUNT = 4;
    private static final int MIN_IMAGE_FILE_SIZE = 2 << 14;
    private Adapter mAdapter = new Adapter();
    private final List<Image> mSelectedImages = new LinkedList<>();
    private SelectedChangeListener mListener;
    private LoaderCallback mLoaderCallback = new LoaderCallback();
    private boolean selectBoxGone = false;

    public void setSelectBoxGone(boolean selectBoxGone) {
        this.selectBoxGone = selectBoxGone;
    }

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 3));
        setAdapter(mAdapter);
        mAdapter.setListener(this);
        UiTool.setRecyclerViewItemAnimationDuration(this);
    }

    @Override
    public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image, int position) {
        if (onItemSelectClick(image)) {
            holder.updateData(image);
        }
    }

    @Override
    public void onItemLongClick(RecyclerAdapter.ViewHolder holder, Image image, int position) {

    }

    public int setup(LoaderManager loaderManager, SelectedChangeListener listener) {
        this.mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallback);
        return LOADER_ID;
    }


    @SuppressLint("StringFormatMatches")
    private boolean onItemSelectClick(Image image) {
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
            image.isSelect = false;
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAGE_COUNT) {
                String str = getContext().getString(R.string.label_gallery_select_max_size);
                str = String.format(str, MAX_IMAGE_COUNT);
                Application.showToast(str);
                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }
        if (notifyRefresh) {
            notifyDataSetChanged();
        }
        return true;
    }

    public void notifyDataSetChanged() {
        if (mListener != null) {
            mListener.onSelectedCountChange(mSelectedImages.size());
        }
    }


    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        for (int i = 0; i < mSelectedImages.size(); i++) {
            paths[i] = mSelectedImages.get(i).path;
        }
        return paths;
    }


    public void clear() {
        for (Image image : mSelectedImages) {
            image.isSelect = false;
        }
        mSelectedImages.clear();
        mAdapter.notifyDataSetChanged();
    }


    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            if (id == LOADER_ID) {
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            List<Image> images = new ArrayList<>();
            if (cursor != null) {
                int count = cursor.getCount();
                if (count > 0) {
                    cursor.moveToFirst();
                    int indexId = cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        int id = cursor.getInt(indexId);
                        String path = cursor.getString(indexPath);
                        long dateTime = cursor.getLong(indexDate);
                        File file = new File(path);
                        if (file.exists() && file.length() > MIN_IMAGE_FILE_SIZE) {
                            Image image = new Image();
                            image.id = id;
                            image.path = path;
                            image.date = dateTime;
                            images.add(image);
                        }
                    } while (cursor.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            updateSource(null);
        }
    }


    private void updateSource(List<Image> images) {
        mAdapter.replace(images);
    }


    public static class Image {
        int id;
        String path;
        long date;
        boolean isSelect;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;
        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected ViewHolder onCreateViewHolder(View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }

        @Override
        public int getItemViewType(int position, Image image) {
            return R.layout.cell_layout;
        }
    }


    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {
        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;


        public ViewHolder(View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {

            Glide.with(getContext())
                    .load(image.path) // 加载路径
                    .placeholder(R.color.grey_200)
                    .error(R.color.amber_200)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(mPic);

            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(selectBoxGone ? GONE : VISIBLE);
        }
    }

    public interface SelectedChangeListener {
        void onSelectedCountChange(int count);
    }
}
