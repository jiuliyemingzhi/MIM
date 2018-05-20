package net.jiuli.mim.fragments.media;


import android.support.v4.app.Fragment;
import android.view.View;

import net.jiuli.common.app.BottomDialogFragment;
import net.jiuli.common.widget.GalleryView;
import net.jiuli.mim.R;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */

public class GalleryFragment extends BottomDialogFragment implements GalleryView.SelectedChangeListener {
    private boolean isGone;
    private OnSelectedListener mListener;

    @BindView(R.id.gv_image_select)
    GalleryView mGalleryView;

    public GalleryFragment() {

    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_gallery;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mGalleryView.setSelectBoxGone(isGone);
    }

    @Override
    protected void onFirstInit() {
        mGalleryView.setup(getLoaderManager(), this);
    }

    @Override
    public void onSelectedCountChange(int count) {
        if (count > 0) {
            dismiss();
            if (mListener != null) {
                String[] paths = mGalleryView.getSelectedPath();
                mListener.imageSelectChanged(paths[0]);
                mListener = null;
            } else {
                throw new Error("OnSelectedListener is null");
            }
        }
    }

    public GalleryFragment setListener(OnSelectedListener listener) {
        this.mListener = listener;
        return this;
    }

    public interface OnSelectedListener {
        void imageSelectChanged(String path);
    }

    public GalleryFragment setCheckBoxIsGone(boolean  isGone) {
        this.isGone = isGone;
        return this;
    }
}