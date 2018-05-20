package net.jiuli.common.factory.data;

        import android.support.annotation.StringRes;

/**
 * Created by jiuli on 17-9-14.
 */

public interface DataSource {
    interface Callback<T> extends SucceedCallback<T>, FailedCallback {

    }

    interface SucceedCallback<T> {
        //数据加载成功,网络请求成功
        void onDataLoaded(T t);
    }


    interface FailedCallback {
        //数据加载失败网络请求失败
        void onDataNotAvailable(@StringRes int strRes);
    }


    void dispose();
}
