package net.jiuli.common.factory.presenter;

/**
 * Created by jiuli on 17-9-10.
 */

public class BasePresenter<T extends BaseContract.View> implements BaseContract.Presenter {

    private T mView;


    @SuppressWarnings("unchecked")
    public BasePresenter(T view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    protected void setView(T view) {
        mView = view;
    }

    @Override
    public void start() {
        if (mView != null) {
            mView.showLoading();
        }
    }

    protected final T getView() {
        return mView;
    }

    @Override
    public void destroy() {
        if (mView != null) {
            mView.setPresenter(null);
            mView = null;
        }
    }
}
