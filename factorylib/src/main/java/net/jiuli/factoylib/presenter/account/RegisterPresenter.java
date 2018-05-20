package net.jiuli.factoylib.presenter.account;

import android.text.TextUtils;

import net.jiuli.common.Common;
import net.jiuli.common.factory.presenter.BasePresenter;
import net.jiuli.factoylib.R;
import net.jiuli.factoylib.data.helper.AccountHelper;
import net.jiuli.common.factory.data.DataSource;
import net.jiuli.factoylib.model.api.account.RegisterModel;
import net.jiuli.factoylib.model.db.User;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

/**
 * Created by jiuli on 17-9-10.
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter, DataSource.Callback<User> {


    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {
        start();
        RegisterContract.View view = getView();
        if (!checkMobile(phone)) {
            view.showError(R.string.data_account_register_invalid_parameter_mobile);

        } else if (name.length() < 2) {
            view.showError(R.string.data_account_register_invalid_parameter_name);
        } else if (password.length() < 6) {
            view.showError(R.string.data_account_register_invalid_parameter_password);
        } else {
            RegisterModel model = new RegisterModel(phone, password, name);
            AccountHelper.register(model, this);
        }
    }

    @Override
    public boolean checkMobile(String phone) {

        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constance.REGEXP_MOBILE, phone);
    }

    @Override
    public void onDataLoaded(User user) {
        final RegisterContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.registerSuccess();
                }
            });
        }

    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final RegisterContract.View view = getView();
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
