package net.jiuli.mim.fragments.panel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

import net.jiuli.common.app.Fragment;
import net.jiuli.common.face.Face;
import net.jiuli.common.utils.UiTool;
import net.jiuli.common.utils.FileTools;
import net.jiuli.common.widget.DragBubbleView;
import net.jiuli.common.widget.EmptyView;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.mim.R;
import net.jiuli.mim.fragments.message.ChatFragment;
import net.qiujuer.genius.ui.Ui;

import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by jiuli on 17-12-17
 */

public class PanelFragment extends Fragment implements GalleryListener, SelectedEntry.OnSelectChanged, DragBubbleView.OnExplosionListener {

    private PanelCallback callback;


    private boolean isFirst = true;

    @BindView(R.id.vp_face)
    ViewPager vpFace;


    @BindView(R.id.iv_backspace)
    ImageView ivBackspace;

    @BindView(R.id.tab_face)
    TabLayout tabFace;

    @BindView(R.id.panel_image_root)
    View panelImagePanel;

    @BindView(R.id.panel_face_root)
    View panelFaceRoot;

    @BindView(R.id.panel_voice_root)
    View panelVoiceRoot;

    @BindView(R.id.recycler_images)
    ImageVideoGalleryView mImageVideoGalleryView;

    @BindView(R.id.empty)
    EmptyView emptyView;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.btn_edit)
    Button btnEdit;

    @BindView(R.id.btn_open_image_folder)
    Button btnOpenFolder;

    @BindView(R.id.cb_full)
    CheckBox cbFull;

    SelectedEntry mEntry;

    DragBubbleView dragBubble;

    @Override
    protected void initWidget(final View root) {
        super.initWidget(root);
        initFace();
        initRecord();
        initGallery(root);
    }

    public void setDragBubble(DragBubbleView dragBubble) {
        this.dragBubble = dragBubble;
        dragBubble.setOnExplosionListener(this);
    }

    private void initFace() {


        int minFaceSize = (int) Ui.dipToPx(getResources(), 48);

        int totalScreen = UiTool.getScreenWidth(getActivity());

        final int spanCount = totalScreen / minFaceSize;

        vpFace.setAdapter(new PagerAdapter() {

            private List<Face.FaceTab> faceTabs = Face.all();


            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return faceTabs.get(position).getName();
            }

            @Override
            public int getCount() {
                return faceTabs.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.lay_face_content, container, false);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
                FaceAdapter adapter = new FaceAdapter();
                recyclerView.setAdapter(adapter);
                adapter.replace(faceTabs.get(position).getFaces());
                adapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Face.Bean>() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onItemClick(RecyclerAdapter.ViewHolder holder, Face.Bean bean, int position) {
                        if (callback == null) {
                            return;
                        }
                        EditText editText = callback.getInputEditText();
                        String text = editText.getText().toString();
                        String start = text.substring(0, editText.getSelectionStart());
                        String end = text.substring(editText.getSelectionEnd(), text.length());
                        text = start + String.format("[%s]", bean.getKey()) + end;
                        ChatFragment.setBefore_text(text);
                        Face.inputFace(getContext(), editText.getText(), bean, (int) (editText.getTextSize() * 1.5f), editText.getSelectionStart(), editText.getSelectionEnd());
                    }
                });
                container.addView(recyclerView);
                return recyclerView;
            }
        });
        tabFace.setupWithViewPager(vpFace);

    }


    private void initRecord() {

    }

    public void initGallery(View root) {
        emptyView.bind(mImageVideoGalleryView);
        emptyView.triggerLoading();
        root.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (Math.abs(top - oldTop) > 1 || Math.abs(bottom - oldBottom) > 1) {
                    mImageVideoGalleryView.setViewHolderInit();
                }
            }
        });
        mEntry = mImageVideoGalleryView.entry;
        mEntry.clear();
    }

    @OnClick(R.id.iv_backspace)
    void backspace() {
        if (callback == null) {
            return;
        }
        callback.getInputEditText().dispatchKeyEvent(UiTool.getDelKeyEvent());
    }


    @OnLongClick(R.id.iv_backspace)
    boolean clean() {
        if (callback == null) {
            return false;
        }
        callback.getInputEditText().setText("");
        return true;
    }


    public void setCallback(PanelCallback callback) {
        this.callback = callback;
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_panel;
    }

    public void showFace() {
        panelFaceRoot.setVisibility(View.VISIBLE);
        panelVoiceRoot.setVisibility(View.GONE);
        panelImagePanel.setVisibility(View.GONE);
    }

    public void showRecord() {
        panelFaceRoot.setVisibility(View.GONE);
        panelVoiceRoot.setVisibility(View.VISIBLE);
        panelImagePanel.setVisibility(View.GONE);
    }

    public void showGallery() {
        panelFaceRoot.setVisibility(View.GONE);
        panelVoiceRoot.setVisibility(View.GONE);
        panelImagePanel.setVisibility(View.VISIBLE);
        if (isFirst) {
            mImageVideoGalleryView.setup(getLoaderManager(), PanelFragment.this);
            isFirst = false;

            mEntry.setOnSelectChanged(this);
        }
        mImageVideoGalleryView.show();
    }

    @Override
    public void onSelectedCountChange(int count, boolean isVideo) {
        btnSubmit.setText(count == 0 ? "发送" : String.format("发送(%s)", count));
        btnSubmit.setEnabled(count > 0);
        boolean enable = !isVideo && count > 0;
        cbFull.setEnabled(enable);
        if (!enable && cbFull.isChecked()) {
            cbFull.setChecked(false);
        }

        if (isVideo) {
            btnEdit.setEnabled(false);
            btnOpenFolder.setEnabled(false);
        } else {
            btnOpenFolder.setEnabled(true);
            btnEdit.setEnabled(count == 1);
        }

        setCbFullText(cbFull);
    }

    void setCbFullText(CheckBox cbFull) {
        cbFull.setText(cbFull.isChecked() ? String.format("原图(%s)", FileTools.formatSize(mEntry.getFilesSize())) : "原图");
    }


    @SuppressWarnings("unused")
    @OnCheckedChanged(R.id.cb_full)
    void onCheckedChanged(boolean isChecked) {
        setCbFullText(cbFull);
    }


    @OnClick(R.id.btn_open_image_folder)
    void openFolderFragment() {
        new PhotoAlbumFragment().setup(this).show(getChildFragmentManager(), PhotoAlbumFragment.class.getSimpleName());
    }


    @Override
    public void onLoaded(int count) {
        emptyView.triggerOk();
        emptyView.triggerOkOrEmpty(count > 0);
    }

    @Override
    public void onSelectChanged(int sourceSize) {
        if (dragBubble != null) {
            dragBubble.setText(sourceSize);
            dragBubble.setVisibility(sourceSize > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onExplosion() {
        if (mEntry != null) {
            mEntry.clear();
        }
    }

    class FaceAdapter extends RecyclerAdapter<Face.Bean> {

        @Override
        public int getItemViewType(int position, Face.Bean bean) {
            return R.layout.cell_face;
        }

        @Override
        protected ViewHolder<Face.Bean> onCreateViewHolder(View root, int viewType) {
            return new FaceHolder(root);
        }
    }

    class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean> {

        @BindView(R.id.iv_face)
        ImageView ivFace;

        FaceHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Face.Bean bean) {

            if (bean == null) {
                return;
            }

            Glide.with(PanelFragment.this)
                    .load(bean.getPreview())
                    .asBitmap()
                    .format(DecodeFormat.DEFAULT)
                    .placeholder(R.drawable.default_face)
                    .into(ivFace);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Source.mAdapter = null;
    }

    public interface PanelCallback {
        EditText getInputEditText();
    }
}