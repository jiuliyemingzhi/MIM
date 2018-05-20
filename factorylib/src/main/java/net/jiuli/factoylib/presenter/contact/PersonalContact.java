package net.jiuli.factoylib.presenter.contact;

import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.factoylib.model.db.User;

/**
 * Created by jiuli on 17-9-24.
 */

public interface PersonalContact {
    interface Presenter extends BaseContract.Presenter {
        User  getUserPersonal();
    }

    interface View extends BaseContract.View<Presenter> {
        String getUserId();

        void onLoadDone(User user);

        void allowSayHello(boolean isAllow);

        void setFollowStatus(boolean isFollow);
    }
}
