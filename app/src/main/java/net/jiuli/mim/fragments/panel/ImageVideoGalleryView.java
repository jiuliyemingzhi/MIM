package net.jiuli.mim.fragments.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.jiuli.common.R;
import net.jiuli.common.utils.UiTool;
import net.jiuli.common.utils.DiffUiDataCallback;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.qiujuer.genius.ui.Ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static net.jiuli.mim.fragments.panel.Source.TYPE_IMAGE;
import static net.jiuli.mim.fragments.panel.Source.TYPE_VIDEO;


/**
 * Created by jiuli on 18-2-7
 */

public class ImageVideoGalleryView extends RecyclerView implements RecyclerAdapter.AdapterListener<Source> {

    private static final int VIDEO_LOADER_ID = 0x0101;
    private static final int IMAGE_LOADER_ID = 0x0102;
    private static final int MIN_IMAGE_FILE_SIZE = 1024 * 32;
    private static final int MIN_VIDEO_FILE_SIZE = 1024 * 256;


//    private static final int MAX_VIDEO_FILE_SIZE = 2 << 30;


    private int HEIGHT = 0;

    private boolean isReady = false;

    private GalleryListener mListener;

    private final Adapter mAdapter = new Adapter();

    final List<Source> sources = new ArrayList<>();

    private final List<Source> imageSources = new ArrayList<>();

    private final ArrayMap<Integer, Source> videoSources = new ArrayMap<>();

    final SelectedEntry entry = new SelectedEntry();

    private RefreshListener mRefreshListener;

    public void setRefreshListener(RefreshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
    }

    public ImageVideoGalleryView(Context context) {
        super(context);
        init();
    }

    public ImageVideoGalleryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageVideoGalleryView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setAdapter(mAdapter);
        mAdapter.setListener(this);
        UiTool.setRecyclerViewItemAnimationDuration(this);
    }


    public void setViewHolderInit() {
        isViewHolderInit = false;
        mAdapter.notifyDataSetChanged();
    }

    public void show() {
        scrollToPosition(0);
    }

    private final String[] IMAGE_PROJECTION = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
    };

    private final String[] VIDEO_PROJECTION = new String[]{
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT

    };

    public void setup(final LoaderManager loaderManager, GalleryListener listener) {
        this.mListener = listener;
        entry.setGalleryListeners(listener);
        sources.clear();
        loaderManager.initLoader(
                IMAGE_LOADER_ID,
                null,
                new LoaderCallback(IMAGE_PROJECTION, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, TYPE_IMAGE));
        loaderManager.initLoader(VIDEO_LOADER_ID,
                null,
                new LoaderCallback(VIDEO_PROJECTION, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, TYPE_VIDEO));

    }


    @Override
    public void onItemClick(RecyclerAdapter.ViewHolder<Source> holder, Source source, int position) {
        if (entry.addOrRemove(source)) {
            holder.updateData(source);
            notifyDataSetChanged();
        }
    }


    @Override
    public void onItemLongClick(RecyclerAdapter.ViewHolder holder, Source source, int position) {

    }

    public void notifyDataSetChanged() {
        if (mListener != null) {
            mListener.onSelectedCountChange(entry.size(), entry.isVideo());
        }
    }

    public int indexOf(Source o) {
        if (o == null) {
            for (int i = 0; i < sources.size(); i++)
                if (sources.get(i) == null)
                    return i;
        } else {
            for (int i = 0; i < sources.size(); i++)
                if (o.isSame(sources.get(i)))
                    return i;
        }
        return -1;
    }

    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private String[] projection;

        private Uri uri;

        private int sourceType;

        LoaderCallback(String[] projection, Uri uri, int sourceType) {
            this.projection = projection;
            this.uri = uri;
            this.sourceType = sourceType;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new CursorLoader(getContext(),
                    uri,
                    projection,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (sourceType == TYPE_IMAGE) {
                imageSources.clear();
            } else {
                videoSources.clear();
            }
            if (cursor != null) {
                int count = cursor.getCount();
                if (count > 0) {
                    cursor.moveToFirst();
                    int indexId = cursor.getColumnIndexOrThrow(projection[0]);
                    int indexPath = cursor.getColumnIndexOrThrow(projection[1]);
                    int indexDate = cursor.getColumnIndexOrThrow(projection[2]);
                    int indexWidth = cursor.getColumnIndexOrThrow(projection[3]);
                    int indexHeight = cursor.getColumnIndexOrThrow(projection[4]);
                    do {
                        int id = cursor.getInt(indexId);
                        String path = cursor.getString(indexPath);
                        long dateTime = cursor.getLong(indexDate);
                        int width = cursor.getInt(indexWidth);
                        int height = cursor.getInt(indexHeight);
                        File file = new File(path);
                        if (file.exists()
                                && file.length() >
                                (sourceType == TYPE_IMAGE ? MIN_IMAGE_FILE_SIZE : MIN_VIDEO_FILE_SIZE)) {
                            Source source = new Source();
                            source.id = id;
                            source.path = path;
                            source.date = dateTime;
                            source.type = sourceType;
                            source.width = width;
                            source.height = height;
                            if (sourceType == TYPE_IMAGE) {
                                imageSources.add(source);
                            } else {
                                videoSources.put(source.id, source);
                            }
                        }
                    } while (cursor.moveToNext());
                }
            }
            updateSource();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    private Comparator<Source> comparator = new Comparator<Source>() {
        @Override
        public int compare(Source o1, Source o2) {
            return o1.date >= o2.date ? -1 : 1;
        }
    };

    private synchronized void updateSource() {
        if (isReady) {
            new AsyncTaskSort().execute();
        } else {
            isReady = true;
        }
    }


    @SuppressLint("StaticFieldLeak")
    final class AsyncTaskSort extends AsyncTask<Void, Void, DiffUtil.DiffResult> {

        private final String[] VIDEO_THUMBNAILS_PROJECTION = {
                MediaStore.Video.Thumbnails.VIDEO_ID,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.WIDTH,
                MediaStore.Video.Thumbnails.HEIGHT};

        @Override
        protected DiffUtil.DiffResult doInBackground(Void... voids) {
            sources.clear();
            sources.addAll(imageSources);
            if (!videoSources.isEmpty()) {
                Cursor cursor = loadInBackground();
                if (cursor != null) {
                    int count = cursor.getCount();
                    if (count > 0) {
                        cursor.moveToFirst();
                        int indexId = cursor.getColumnIndexOrThrow(VIDEO_THUMBNAILS_PROJECTION[0]);
                        int indexPath = cursor.getColumnIndexOrThrow(VIDEO_THUMBNAILS_PROJECTION[1]);
                        int indexWidth = cursor.getColumnIndexOrThrow(VIDEO_THUMBNAILS_PROJECTION[2]);
                        int indexHeight = cursor.getColumnIndexOrThrow(VIDEO_THUMBNAILS_PROJECTION[3]);
                        do {
                            int id = cursor.getInt(indexId);
                            int width = cursor.getInt(indexWidth);
                            int height = cursor.getInt(indexHeight);
                            String path = cursor.getString(indexPath);
                            File file = new File(path);
                            if (file.exists()) {
                                Source source = videoSources.get(id);
                                if (source != null) {
                                    source.thumbPath = path;
                                    source.width = width;
                                    source.height = height;
                                }
                            }
                        } while (cursor.moveToNext());
                    }
                }
                sources.addAll(videoSources.values());
            }
            for (int i = 0; i < entry.size(); i++) {
                Source source = entry.sources.get(i);
                int index = indexOf(source);
                if (index < 0) {
                    entry.remove(source, false);
                    i--;
                } else {
                    Source s = sources.get(index);
                    s.isSelect = true;
                    s.counter = source.counter;
                    entry.sources.set(i, s);
                }
            }
            List<Source> dataList = mAdapter.getDataList();
            Collections.sort(sources, comparator);
            DiffUiDataCallback<Source> callback = new DiffUiDataCallback<>(dataList, sources);
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            mAdapter.replaceAllData(sources, false);
            return result;
        }

        @Override
        protected void onPostExecute(DiffUtil.DiffResult result) {
            result.dispatchUpdatesTo(mAdapter);
            smoothScrollBy(-(int) Ui.dipToPx(getResources(), 50), 0);
            if (mRefreshListener != null) {
                mRefreshListener.onRefresh();
            }
            if (mListener != null) {
                mListener.onLoaded(sources.size());
            }
        }

        private synchronized Cursor loadInBackground() {
            Cursor cursor = ContentResolverCompat.query(
                    getContext().getContentResolver(),
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    VIDEO_THUMBNAILS_PROJECTION,
                    null,
                    null,
                    null,
                    null);
            if (cursor != null) {
                try {
                    // Ensure the cursor window is filled.
                    cursor.getCount();
                } catch (RuntimeException ex) {
                    cursor.close();
                    throw ex;
                }
            }
            return cursor;

        }
    }

    class Adapter extends RecyclerAdapter<Source> {

        @Override
        public int getItemViewType(int position, Source source) {
            return R.layout.cell_image;
        }

        @Override
        protected ViewHolder<Source> onCreateViewHolder(View root, int viewType) {
            return new ImageVideoGalleryView.ViewHolder(root);
        }
    }

    private boolean isViewHolderInit = false;

    class ViewHolder extends RecyclerAdapter.ViewHolder<Source> {
        private ImageView mPic;
        private TextView tvShade;
        private CheckBox mSelected;
        private ImageView ivVideoSub;

        public ViewHolder(View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.im_image);
            tvShade = itemView.findViewById(R.id.tv_shade);
            mSelected = itemView.findViewById(R.id.cb_select);
            ivVideoSub = itemView.findViewById(R.id.iv_video_sub);
        }


        @Override
        protected void onBind(final Source source) {
            if (!isViewHolderInit) {
                ViewGroup.LayoutParams params = mPic.getLayoutParams();
                params.width = UiTool.getScreenWidth(getContext());
                mPic.setLayoutParams(params);
                mPic.post(new Runnable() {
                    @Override
                    public void run() {
                        HEIGHT = mPic.getHeight();
                        isViewHolderInit = true;
                        onBind(source);
                    }
                });
                return;
            }

            if (source.height <= 0
                    || source.width <= 0
                    || HEIGHT <= 0) {
                Glide.with(
                        getContext())
                        .load(getPath(source)) // 加载路径
                        .asBitmap()
                        .format(DecodeFormat.DEFAULT)
                        .placeholder(R.color.grey_200)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                source.width = resource.getWidth();
                                source.height = resource.getHeight();
                                HEIGHT = mPic.getHeight();
                                int width = getWidth(source);
                                ViewGroup.LayoutParams params = mPic.getLayoutParams();
                                params.width = width;
                                mPic.setLayoutParams(params);
                                mPic.setImageBitmap(resource);
                                isViewHolderInit = true;
                            }
                        });

            } else {
                int width = getWidth(source);
                ViewGroup.LayoutParams params = mPic.getLayoutParams();
                params.width = width;
                mPic.setLayoutParams(params);
                Glide.with(
                        getContext())
                        .load(getPath(source)) // 加载路径
                        .asBitmap()
                        .format(DecodeFormat.DEFAULT)
                        .placeholder(R.color.grey_200)
                        .override(width, HEIGHT)
                        .into(mPic);
            }

            tvShade.setVisibility(source.isSelect ? VISIBLE : INVISIBLE);
            tvShade.setText(String.valueOf(source.counter));
            mSelected.setChecked(source.isSelect);
            mSelected.setVisibility(VISIBLE);
            ivVideoSub.setVisibility(source.type == TYPE_VIDEO ? VISIBLE : GONE);
            source.position = getAdapterPosition();
            Source.mAdapter = mAdapter;
        }


    }

    private int getWidth(Source source) {
        return HEIGHT * source.width / source.height;
    }

    String getPath(Source source) {
        return TextUtils.isEmpty(source.thumbPath) ? source.path : source.thumbPath;
    }

    interface RefreshListener {
        void onRefresh();
    }
}