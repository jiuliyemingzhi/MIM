package net.jiuli.common.app;

import android.content.Context;

import net.jiuli.common.factory.presenter.BaseContract;

/**
 * Created by jiuli on 17-9-10
 */

public abstract class PresenterFragment
        <Presenter extends BaseContract.Presenter>
        extends Fragment implements BaseContract.View<Presenter> {
    protected Presenter mPresenter;

    @Override
    public void showError(int error) {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(error);
        } else {
            Application.showToast(error);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initPresenter();
    }

    protected abstract Presenter initPresenter();

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        }
    }
}
