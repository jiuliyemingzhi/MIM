package net.jiuli.common.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;


import com.afollestad.materialdialogs.MaterialDialog;

import net.jiuli.common.R;
import net.jiuli.common.factory.presenter.BaseContract;

/**
 * //Created by jiuli on 17-9-24.
 */

public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity implements BaseContract.View<Presenter> {
    protected Presenter mPresenter;

    protected MaterialDialog mDialog;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        initPresenter();
    }

    @Override
    public void showError(int error) {
        hideDialogLoading();
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(error);
        } else {
            Application.showToast(error);

        }

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.mPresenter = presenter;
    }

    protected abstract Presenter initPresenter();

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        } else {

            ProgressDialog progressDialog = new ProgressDialog(this);

            if (mDialog == null) {
                mDialog = new MaterialDialog.Builder(this)
                        .title("保存分组").canceledOnTouchOutside(false)
                        .cancelable(true)
                        .cancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        }).content(R.string.prompt_loading)
                        .backgroundColorRes(R.color.white_alpha_208)
                        .titleColorRes(R.color.colorAccent)
                        .contentColorRes(R.color.textPrimary)
                        .progress(true, 0)
                        .progressIndeterminateStyle(true).show();
            } else {
                mDialog.show();
            }
        }
    }

    protected void hideDialogLoading() {
        if (mDialog != null) {
            mDialog.dismiss();

            mDialog = null;
        }
    }

    protected void hideLoading() {
        hideDialogLoading();
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

}
