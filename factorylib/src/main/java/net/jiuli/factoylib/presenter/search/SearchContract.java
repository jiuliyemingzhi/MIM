package net.jiuli.factoylib.presenter.search;

import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.factoylib.model.card.GroupCard;
import net.jiuli.factoylib.model.card.UserCard;

import java.util.List;

/**
 * Created by jiuli on 17-9-24.
 */

public interface SearchContract {
    interface Presenter extends BaseContract.Presenter {
        void search(String content);
    }

    interface UserView extends BaseContract.View<Presenter> {
        void onSearchDone(List<UserCard> userCards);
    }

    interface GroupView extends BaseContract.View<Presenter> {
        void onSearchDone(List<GroupCard> groupCards);
    }
}
