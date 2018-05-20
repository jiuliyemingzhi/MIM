package net.jiuli.factoylib.presenter.contact;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.common.factory.presenter.BasePresenter;
import net.jiuli.factoylib.model.card.UserCard;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by jiuli on 17-9-24.
 */

public class FollowPresenter extends BasePresenter<FollowContact.View>
        implements FollowContact.Presenter, DataSource.Callback<UserCard> {

    public FollowPresenter(FollowContact.View view) {
        super(view);
    }

    @Override
    public void follow(String id) {
        start();
        UserHelper.follow(id, this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        final FollowContact.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onFollowSucceed(userCard);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final FollowContact.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strRes);
                }
            });
        }
    }
}