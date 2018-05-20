package net.jiuli.factoylib.presenter.contact;

import net.jiuli.common.factory.presenter.BasePresenter;
import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.persistence.Account;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by jiuli on 17-9-24.
 */

public class PersonalPresenter extends BasePresenter<PersonalContact.View>
        implements PersonalContact.Presenter {
    private User user;

    public PersonalPresenter(PersonalContact.View view) {
        super(view);
    }


    @Override
    public void start() {
        super.start();
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                PersonalContact.View view = getView();
                if (view != null) {
                    String userId = view.getUserId();
                    User user = UserHelper.searchFirstOfNet(userId);
                    onLoaded(view, user);
                }
            }
        });
    }





    @Override
    public User getUserPersonal() {
        return user;
    }

    private void onLoaded(final PersonalContact.View view, final User user) {
        if (user == null) {
            return;
        }

        this.user = user;
        boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
        final boolean isFollow = isSelf || user.isFollow();
        final boolean allowSayHello = isFollow && !isSelf;

        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.onLoadDone(user);
                view.allowSayHello(allowSayHello);
                view.setFollowStatus(isFollow);
            }
        });
    }
}
