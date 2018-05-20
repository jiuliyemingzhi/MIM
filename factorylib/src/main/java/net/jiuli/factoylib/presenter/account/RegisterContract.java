package net.jiuli.factoylib.presenter.account;

import net.jiuli.common.factory.presenter.BaseContract;

/**
 * Created by jiuli on 17-9-10.
 */

public interface RegisterContract  {

    interface View extends BaseContract.View<Presenter> {
        void registerSuccess();
    }


    interface Presenter extends BaseContract.Presenter {

        void register(String phone, String name, String password);

        boolean checkMobile(String phone);

    }


}