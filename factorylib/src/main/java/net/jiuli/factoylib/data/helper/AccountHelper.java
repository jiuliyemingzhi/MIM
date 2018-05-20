package net.jiuli.factoylib.data.helper;

import android.text.TextUtils;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.R;
import net.jiuli.factoylib.model.api.account.AccountRespModel;
import net.jiuli.factoylib.model.api.account.LoginModel;
import net.jiuli.factoylib.model.api.account.RegisterModel;
import net.jiuli.factoylib.model.api.RespModel;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.net.NetWork;
import net.jiuli.factoylib.net.RemoteService;
import net.jiuli.factoylib.persistence.Account;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jiuli on 17-9-13.
 */

public class AccountHelper {

    public static void register(RegisterModel model, final DataSource.Callback callback) {
        //调用Retrofit对我们的网络请求进行代理
        RemoteService service = NetWork.remote();
        //得到一个call
        Call<RespModel<AccountRespModel>> call = service.accountRegister(model);
        call.enqueue(new AccountRespCallback(callback));
    }

    /**
     * 对设备id进行bind
     *
     * @param callback
     */
    public static void bindPush(final DataSource.Callback<User> callback) {
        String pushId = Account.getPushId();
        if (!TextUtils.isEmpty(pushId)) {
            RemoteService service = NetWork.remote();
            Call<RespModel<AccountRespModel>> call = service.accountBind(pushId);
            call.enqueue(new AccountRespCallback(callback));
        }
    }

    public static void login(final LoginModel model, final DataSource.Callback callback) {
        RemoteService service = NetWork.remote();
        Call<RespModel<AccountRespModel>> call = service.accountLogin(model);
        call.enqueue(new AccountRespCallback(callback));
    }

    public static class AccountRespCallback implements Callback<RespModel<AccountRespModel>> {
        final DataSource.Callback<User> callback;

        AccountRespCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<RespModel<AccountRespModel>> call,
                               Response<RespModel<AccountRespModel>> response) {
            RespModel<AccountRespModel> respModel = response.body();
            if (respModel.success()) {
                AccountRespModel result = respModel.getResult();
                User user = result.getUser();
                DbHelper.save(User.class, user);
                Account.login(result);
                if (result.isBind()) {
                    Account.setBind(true);
                    if (callback != null) {
                        callback.onDataLoaded(user);
                    }
                } else {
                    bindPush(callback);
                }

            } else {
                Factory.decodeRespCode(respModel, callback);
            }
        }


        @Override
        public void onFailure(Call<RespModel<AccountRespModel>> call, Throwable t) {
            if (callback != null) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        }
    }
}
