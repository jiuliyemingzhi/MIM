package net.jiuli.mim.fragments.panel;

import android.annotation.SuppressLint;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

import net.jiuli.common.app.BottomDialogFragment;
import net.jiuli.common.utils.UiTool;
import net.jiuli.common.utils.DiffUiDataCallback;
import net.jiuli.common.utils.FileTools;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.mim.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static net.jiuli.mim.fragments.panel.Source.TYPE_VIDEO;


public final class PhotoAlbumFragment extends BottomDialogFragment implements ImageVideoGalleryView.RefreshListener {

    private static final String LOCAL_VIDEOS_KEY = "本地视频";

    private static final String LATE_PICTURE_KEY = "最近图片";

    @BindView(R.id.gv_album)
    RecyclerView gvAlbum;

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.btn_close_image_folder)
    Button btnClose;

    @BindView(R.id.cb_full)
    CheckBox cbFull;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.btn_edit)
    Button btnEdit;

    final LinkedHashMap<String, List<Source>> sourceMaps = new LinkedHashMap<>();

    final GvAdapter mAdapter = new GvAdapter();

    PanelFragment mPanelFragment;

    SelectedEntry mEntry;


    ImageVideoGalleryView mImageVideoGalleryView;


    private String selectAlbum;

    @OnClick(R.id.btn_close_image_folder)
    void closeAlbum() {
        if (!ivBack.isEnabled()) {
            ivBack.setEnabled(true);
            setGvAlbumRefresh();
        } else {
            dismiss();
        }
    }


    @OnClick(R.id.btn_submit)
    void sendImage() {
        mEntry.clear();
        dismiss();
    }

    @Override
    public void onDestroy() {
        if (!mEntry.isEmpty()) {
            boolean checked = cbFull.isChecked();
            if (checked == mPanelFragment.cbFull.isChecked()) {
                mPanelFragment.onCheckedChanged(checked);
            } else {
                mPanelFragment.cbFull.setChecked(checked);
            }
        }
        mPanelFragment.onSelectedCountChange(mEntry.size(), mEntry.isVideo());
        super.onDestroy();
        mImageVideoGalleryView.getAdapter().notifyDataSetChanged();
        mImageVideoGalleryView.setRefreshListener(null);
    }


    @SuppressWarnings("unused")
    @OnCheckedChanged(R.id.cb_full)
    void onCheckedChanged(boolean isSelected) {
        mPanelFragment.setCbFullText(cbFull);
    }

    PhotoAlbumFragment setup(PanelFragment panelFragment) {
        this.mPanelFragment = panelFragment;
        this.mEntry = panelFragment.mEntry;
        this.mImageVideoGalleryView = panelFragment.mImageVideoGalleryView;
        return this;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        if (!mEntry.isEmpty()) {
            boolean checked = mPanelFragment.cbFull.isChecked();
            if (checked == cbFull.isChecked()) {
                onCheckedChanged(checked);
            } else {
                cbFull.setChecked(checked);
            }
        }
        UiTool.setRecyclerViewItemAnimationDuration(gvAlbum);
        onSelectedCountChange();
        initRecyclerView();
    }

    @SuppressLint("DefaultLocale")
    private void refreshData() {
        sourceMaps.clear();
        List<Source> sources = mImageVideoGalleryView.sources;
        if (sources.isEmpty()) {
            return;
        }
        ArrayMap<String, List<Source>> sourceMaps = new ArrayMap<>();

        List<Source> localVideos = new ArrayList<>();

        List<Source> latePictures = new ArrayList<>();

        for (Source source : sources) {

            String parent = FileTools.getParent(source.path);

            List<Source> sourceList = sourceMaps.get(parent);

            if (sourceList == null) {
                sourceMaps.put(parent, sourceList = new ArrayList<>());
            }

            sourceList.add(source);

            if (latePictures.size() < 99 && source.isImage()) {
                latePictures.add(source);
            } else if (!source.isImage()) {
                localVideos.add(source);
            }

        }
        if (!latePictures.isEmpty()) {
            this.sourceMaps.put(String.format(LATE_PICTURE_KEY, latePictures.size()), latePictures);
        }

        if (!localVideos.isEmpty()) {
            this.sourceMaps.put(String.format(LOCAL_VIDEOS_KEY, localVideos.size()), localVideos);
        }


        this.sourceMaps.putAll(sourceMaps);
    }

    private void initRecyclerView() {
        mImageVideoGalleryView.setRefreshListener(this);
        gvAlbum.setAdapter(mAdapter);
        refreshData();
        setGvAlbumRefresh();

        //noinspection unchecked
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Object o, int position) {
                if (o instanceof Source) {
                    Source source = (Source) o;
                    if (mEntry.addOrRemove(source)) {
                        holder.refresh();
                        onSelectedCountChange();
                    }
                } else if (o instanceof String) {
                    String key = (String) o;
                    gvAlbum.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    //noinspection unchecked
                    mAdapter.replace(PhotoAlbumFragment.this.sourceMaps.get(key));
                    ivBack.setEnabled(false);
                    selectAlbum = key;
                }
            }
        });
    }


    public void setGvAlbumRefresh() {
        gvAlbum.setLayoutManager(new LinearLayoutManager(getContext()));
        //noinspection unchecked
        mAdapter.replace(sourceMaps.keySet());
    }

    public void onSelectedCountChange() {
        int count = mEntry.size();
        boolean isVideo = mEntry.isVideo();
        btnSubmit.setText(count == 0 ? "发送" : String.format("发送(%s)", count));
        btnSubmit.setEnabled(count > 0);
        boolean enable = !isVideo && count > 0;
        cbFull.setEnabled(enable);
        if (!enable && cbFull.isChecked()) {
            cbFull.setChecked(false);
        }

        if (isVideo) {
            btnEdit.setEnabled(false);
        } else {
            btnEdit.setEnabled(count == 1);
        }
        mPanelFragment.setCbFullText(cbFull);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_photo_album;
    }

    @Override
    public void onRefresh() {
        refreshData();
        if (ivBack.isEnabled()) {
            setGvAlbumRefresh();
        } else {
            List<Source> sources = sourceMaps.get(selectAlbum);

            if (sources != null) {
                for (int i = 0; i < sources.size(); i++) {
                    sources.get(i).position = i;
                }
                List<Source> dataList = mAdapter.getDataList();
                DiffUiDataCallback<Source> callback = new DiffUiDataCallback<>(dataList, sources);
                final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
                mAdapter.replaceAllData(sources, false);
                result.dispatchUpdatesTo(mAdapter);
                if (gvAlbum.canScrollVertically(1)) {
                    gvAlbum.scrollBy(0, -gvAlbum.getRootView().getWidth() / 3);
                }


            } else {
                //noinspection unchecked
                mAdapter.replace(null);
            }
        }
    }


    class GvAdapter extends RecyclerAdapter {

        @Override
        public int getItemViewType(int position, Object obj) {
            if (obj instanceof Source) {
                return R.layout.cell_gallery;
            } else if (obj instanceof String) {
                return R.layout.cell_album;
            } else {
                try {
                    throw new Exception("数据错误");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        }

        @Override
        protected ViewHolder onCreateViewHolder(View root, int viewType) {
            if (viewType == R.layout.cell_gallery) {
                return new GvHolder(root);
            } else {
                return new AlbumViewHolder(root);
            }
        }
    }

    class GvHolder extends RecyclerAdapter.ViewHolder<Source> {

        @BindView(R.id.im_image)
        ImageView imImage;

        @BindView(R.id.tv_shade)
        TextView tvShade;

        @BindView(R.id.cb_select)
        CheckBox cbSelect;

        @BindView(R.id.iv_video_sub)
        ImageView ivVideoSub;


        GvHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Source source) {
            Glide.with(PhotoAlbumFragment.this)
                    .load(mImageVideoGalleryView.getPath(source))
                    .asBitmap()
                    .format(DecodeFormat.DEFAULT)
                    .placeholder(R.color.red_200)
                    .into(imImage);

            tvShade.setVisibility(source.isSelect ? View.VISIBLE : View.INVISIBLE);
            tvShade.setText(String.valueOf(source.counter));
            cbSelect.setChecked(source.isSelect);
            cbSelect.setVisibility(View.VISIBLE);
            ivVideoSub.setVisibility(source.type == TYPE_VIDEO ? View.VISIBLE : View.GONE);
            source.position = getAdapterPosition();
            Source.mAdapter = mAdapter;
        }
    }

    class AlbumViewHolder extends RecyclerAdapter.ViewHolder<String> {
        @BindView(R.id.im_image)
        ImageView intoImage;

        @BindView(R.id.tv_album)
        TextView tvAlbum;

        AlbumViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        @SuppressLint("DefaultLocale")
        protected void onBind(String key) {
            Glide.with(PhotoAlbumFragment.this)
                    .load(mImageVideoGalleryView.getPath(sourceMaps.get(key).get(0)))
                    .asBitmap()
                    .format(DecodeFormat.DEFAULT)
                    .into(intoImage);
            String name = FileTools.getName(key) + String.format(" (%d)", sourceMaps.get(key).size());
            tvAlbum.setText(name);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //noinspection ConstantConditions
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    closeAlbum();
                    return true;
                }
                return false;
            }
        });
    }

}