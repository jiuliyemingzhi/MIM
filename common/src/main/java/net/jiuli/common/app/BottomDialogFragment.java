package net.jiuli.common.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jiuli on 18-2-12.
 */

public abstract class BottomDialogFragment extends BottomSheetDialogFragment {
    protected View mRoot;
    protected Unbinder mRootUnbind;
    protected boolean isFirstInitData = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            View root = inflater.inflate(getContentLayoutId(), container, false);
            mRootUnbind = ButterKnife.bind(this, root);
            initWidget(root);
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                container.removeView(mRoot);
            }
        }
        return mRoot;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initArgs(getArguments());
    }

    private void initArgs(Bundle arguments) {

    }

    protected void onFirstInit() {

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isFirstInitData) {
            onFirstInit();
            isFirstInitData = false;
        }
        initData();
    }

    private void initData() {

    }

    protected void initWidget(View root) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRootUnbind.unbind();
        mRootUnbind = null;
    }

    protected abstract int getContentLayoutId();

}
