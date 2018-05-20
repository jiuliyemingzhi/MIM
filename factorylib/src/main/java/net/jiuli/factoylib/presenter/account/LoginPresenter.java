package net.jiuli.factoylib.presenter.account;

import android.text.TextUtils;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.common.factory.presenter.BasePresenter;
import net.jiuli.factoylib.R;
import net.jiuli.factoylib.data.helper.AccountHelper;
import net.jiuli.factoylib.model.api.account.LoginModel;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.persistence.Account;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;


/**
 * Created by jiuli on 17-9-15.
 */

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter, DataSource.Callback<User> {
    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void onDataLoaded(User user) {
        final LoginContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.loginSuccess();
                }
            });
        }
    }

    @Override
    public void login(String phone, String password) {
        start();
        final LoginContract.View view = getView();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            view.showError(R.string.data_account_login_invalid_parameter);

        } else {
//            LoginModel loginModel = new LoginModel(phone, password);
            LoginModel loginModel = new LoginModel(phone, password, Account.getPushId());
            AccountHelper.login(loginModel, this);
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final LoginContract.View view = getView();
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
