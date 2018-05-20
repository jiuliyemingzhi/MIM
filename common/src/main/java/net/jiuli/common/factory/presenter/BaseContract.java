package net.jiuli.common.factory.presenter;

import android.support.annotation.StringRes;

import net.jiuli.common.widget.recycler.RecyclerAdapter;

/**
 * Created by jiuli on 17-9-10.
 */

public interface BaseContract {

    interface View<T extends Presenter> {

        void showError(@StringRes int error);

        void setPresenter(T presenter);

        void showLoading();
    }


    interface Presenter {

        void start();

        void destroy();

    }


    interface RecyclerView<T extends Presenter, ViewMode> extends View<T> {
        RecyclerAdapter<ViewMode> getRecyclerViewAdapter();

        void onAdapterDataChange();
    }

}
