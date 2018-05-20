package net.jiuli.factoylib.presenter.account;

import net.jiuli.common.factory.presenter.BaseContract;

/**
 * Created by jiuli on 17-9-10.
 */

public interface LoginContract {
    interface View extends BaseContract.View<Presenter> {
        void loginSuccess();
    }

    interface Presenter extends BaseContract.Presenter {
        void login(String phone, String password);
    }
}

