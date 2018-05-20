package net.jiuli.factoylib.presenter.user;

import net.jiuli.common.factory.presenter.BaseContract;

/**
 * Created by jiuli on 17-9-16.
 */

public interface UpdateInfoContract {
    interface Presenter extends BaseContract.Presenter {
        void update(String photoFilePath, String desc, boolean isMan);
    }

    interface View extends BaseContract.View<Presenter> {
        void updateSucceed();
    }
}
