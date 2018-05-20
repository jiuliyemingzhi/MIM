package net.jiuli.factoylib.presenter.search;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.common.factory.presenter.BasePresenter;
import net.jiuli.factoylib.model.card.UserCard;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;

/**
 * Created by jiuli on 17-9-24.
 */

public class SearchUserPresenter extends BasePresenter<SearchContract.UserView>
        implements SearchContract.Presenter, DataSource.Callback<List<UserCard>> {
    private Call call;

    public SearchUserPresenter(SearchContract.UserView view) {
        super(view);
    }

    @Override
    public void search(String content) {
        start();
        if (this.call != null && !call.isCanceled()) {
            call.cancel();
        }
        this.call = UserHelper.search(content, this);

    }

    @Override
    public void onDataLoaded(final List<UserCard> userCards) {
        final SearchContract.UserView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onSearchDone(userCards);
                }
            });
        }
    }


    @Override
    public void onDataNotAvailable(final int strRes) {
        final SearchContract.UserView view = getView();
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
