package net.jiuli.factoylib.presenter.search;

import android.text.TextUtils;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.common.factory.presenter.BasePresenter;
import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.model.card.GroupCard;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;

/**
 * Created by jiuli on 17-9-24.
 */

public class SearchGroupPresenter extends BasePresenter<SearchContract.GroupView>
        implements SearchContract.Presenter, DataSource.Callback<List<GroupCard>> {
    private Call call;

    public SearchGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {

        start();


        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        call = GroupHelper.search(content, this);

    }

    @Override
    public void onDataLoaded(final List<GroupCard> groupCards) {
        final SearchContract.GroupView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onSearchDone(groupCards);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final SearchContract.GroupView view = getView();
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
