package net.jiuli.factoylib.presenter.contact;

import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.factoylib.model.card.UserCard;

/**
 * Created by jiuli on 17-9-24.
 */

public interface FollowContact {
    interface Presenter extends BaseContract.Presenter {
        void follow(String id);
    }

    interface View extends BaseContract.View<Presenter> {
        void onFollowSucceed(UserCard userCard);
    }
}
