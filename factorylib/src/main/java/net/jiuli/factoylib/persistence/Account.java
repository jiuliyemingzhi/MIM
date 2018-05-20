package net.jiuli.factoylib.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.model.api.account.AccountRespModel;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.model.db.User_Table;

/**
 * Created by jiuli on 17-9-15.
 */

public class Account {
    private static String KEY_PUSH_ID = "KEY_PUSH_ID";
    private static String KEY_IS_BIND = "KEY_IS_BIND";
    private static String KEY_TOKEN = "KEY_TOKEN";
    private static String KEY_USER_ID = "KEY_USER_ID";
    private static String KEY_ACCOUNT = "KEY_ACCOUNT";


    private static String pushId;
    private static boolean isBind;
    private static String token;
    private static String userId;
    private static String account;





    private static void save(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        sp.edit()
                .putString(KEY_PUSH_ID, pushId)
                .putBoolean(KEY_IS_BIND, isBind)
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_ACCOUNT, account)
                .apply();

    }

    public static void load(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        pushId = sp.getString(KEY_PUSH_ID, "");
        isBind = sp.getBoolean(KEY_IS_BIND, false);
        token = sp.getString(KEY_TOKEN, "");
        userId = sp.getString(KEY_USER_ID, "");
        account = sp.getString(KEY_ACCOUNT, "");
    }

    public static boolean isLogin() {
        return !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(token);
    }


    public static String getPushId() {
        return pushId;
    }

    public static boolean isBind() {
        return isBind;
    }

    public static void setBind(boolean isBind) {
        Account.isBind = isBind;
        Account.save(Factory.app());
    }
    public static void setPushId(String pushId) {
        Account.pushId = pushId;
        Account.save(Factory.app());
    }

    public static boolean isComplete() {
        if (isLogin()) {
            User user = getUser();
            return !TextUtils.isEmpty(user.getDesc())
                    && !TextUtils.isEmpty(user.getPortrait())
                    && user.getSex() != 0;
        }
        return false;
    }

    public static void login(AccountRespModel model) {
        Account.token = model.getToken();
        Account.account = model.getAccount();
        Account.userId = model.getUser().getId();
        save(Factory.app());
    }

    public static User getUser() {
        return TextUtils.isEmpty(userId) ? new User() :
                SQLite.select()
                        .from(User.class)
                        .where(User_Table.id.eq(userId))
                        .querySingle();
    }

    public static String getToken() {
        return token;
    }

    public static String getUserId() {
        return getUser().getId();
    }
}
