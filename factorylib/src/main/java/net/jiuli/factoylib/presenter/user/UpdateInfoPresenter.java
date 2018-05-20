package net.jiuli.factoylib.presenter.user;

import android.text.TextUtils;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.common.factory.presenter.BasePresenter;
import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.R;
import net.jiuli.factoylib.model.card.UserCard;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.jiuli.factoylib.model.api.user.UserUpdateModel;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.net.UploadHelper;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by jiuli on 17-9-16.
 */

public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View>
        implements UpdateInfoContract.Presenter, DataSource.Callback<UserCard> {

    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String photoFilePath, final String desc, final boolean isMan) {
        start();
        final UpdateInfoContract.View view = getView();
        if (TextUtils.isEmpty(photoFilePath) || TextUtils.isEmpty(desc)) {
            view.showError(R.string.data_account_update_invalid_parameter);
        } else {
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    String url = UploadHelper.uploadPortrait(photoFilePath);
                    if (TextUtils.isEmpty(url)) {
                        view.showError(R.string.data_upload_error);
                    } else {
                        UserUpdateModel model = new UserUpdateModel("", url, desc, isMan ? User.SEX_MAN : User.SEX_WOMAN);
                        UserHelper.update(model, UpdateInfoPresenter.this);
                    }
                }
            });
        }
    }

    @Override
    public void onDataLoaded(UserCard user) {
        final UpdateInfoContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.updateSucceed();
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final UpdateInfoContract.View view = getView();
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
