package net.jiuli.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jiuli.common.widget.EmptyView;
import net.jiuli.common.widget.convention.PlaceHolderView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jiuli on 17-8-16.
 */

public abstract class Fragment extends android.support.v4.app.Fragment {

    protected View mRoot;
    protected Unbinder mRootUnBind;
    protected PlaceHolderView mPlaceHolderView;
    protected boolean mIsFirstInitData = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            View root = inflater.inflate(getContentLayoutId(), container, false);
            initWidget(root);
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                container.removeView(mRoot);
            }
        }
        return mRoot;
    }

    protected void onFirstInit() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsFirstInitData) {
            onFirstInit();
            mIsFirstInitData = false;
        }
        //当View创建完成后初始化数据
        initData();
    }

    protected abstract int getContentLayoutId();

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 初始化控件
     *
     * @param root
     */
    protected void initWidget(View root) {
        mRootUnBind = ButterKnife.bind(this, root);
    }

    /**
     * 初始化参数
     *
     * @param bundle
     */
    protected void initArgs(Bundle bundle) {

    }

    /**
     * 点击返回按键时触发
     * 返回True 表示已经处理，返回false表示未处理，交给Activity处理
     */

    public boolean onBackPressed() {
        return false;
    }

    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
    }
}
