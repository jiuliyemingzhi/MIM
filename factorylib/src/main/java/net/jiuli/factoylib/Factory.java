package net.jiuli.factoylib;

import android.support.annotation.StringRes;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.jiuli.common.app.Application;
import net.jiuli.common.factory.data.DataSource;
import net.jiuli.factoylib.data.group.GroupCenter;
import net.jiuli.factoylib.data.group.GroupDispatcher;
import net.jiuli.factoylib.data.message.MessageCenter;
import net.jiuli.factoylib.data.message.MessageDispatcher;
import net.jiuli.factoylib.data.user.UserCenter;
import net.jiuli.factoylib.data.user.UserDispatcher;
import net.jiuli.factoylib.model.api.PushModel;
import net.jiuli.factoylib.model.api.RespModel;
import net.jiuli.factoylib.model.card.GroupCard;
import net.jiuli.factoylib.model.card.GroupMemberCard;
import net.jiuli.factoylib.model.card.MessageCard;
import net.jiuli.factoylib.model.card.UserCard;
import net.jiuli.factoylib.persistence.Account;
import net.jiuli.factoylib.utils.DBFlowExclusionStrategy;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *  Created by jiuli on 17-9-4.
 */

public class Factory {

    private static final String TAG = Factory.class.getSimpleName();

    private static final Factory instance;
    private final Executor executor;
    private final Gson gson;

    static {
        instance = new Factory();
    }

    private Factory() {
        executor = Executors.newFixedThreadPool(4);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                //TODO设置一个过滤器,数据库级别的Model不进行数据转换
                .setExclusionStrategies(new DBFlowExclusionStrategy())
                .create();
    }

    public static Application app() {
        return Application.getInstance();
    }


    public static void setup() {
        FlowManager.init(new FlowConfig.Builder(app())
                .openDatabasesOnInit(true)
                .build());
        Account.load(app());
    }

    public static void runOnAsync(Runnable runnable) {
        instance.executor.execute(runnable);
    }


    public static void decodeRespCode(RespModel model, DataSource.FailedCallback callback) {
        if (model != null) {
            switch (model.getCode()) {
                case RespModel.SUCCEED:
                    return;
                case RespModel.ERROR_SERVICE:
                    decodeRespCode(R.string.data_rsp_error_service, callback);
                    break;
                case RespModel.ERROR_NOT_FOUND_USER:
                    decodeRespCode(R.string.data_rsp_error_not_found_user, callback);
                    break;
                case RespModel.ERROR_NOT_FOUND_GROUP:
                    decodeRespCode(R.string.data_rsp_error_not_found_group, callback);
                    break;
                case RespModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                    decodeRespCode(R.string.data_rsp_error_not_found_group_member, callback);
                    break;
                case RespModel.ERROR_CREATE_USER:
                    decodeRespCode(R.string.data_rsp_error_create_user, callback);
                    break;
                case RespModel.ERROR_CREATE_GROUP:
                    decodeRespCode(R.string.data_rsp_error_create_group, callback);
                    break;
                case RespModel.ERROR_CREATE_MESSAGE:
                    decodeRespCode(R.string.data_rsp_error_create_message, callback);
                    break;
                case RespModel.ERROR_PARAMETERS:
                    decodeRespCode(R.string.data_rsp_error_parameters, callback);
                    break;
                case RespModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                    decodeRespCode(R.string.data_rsp_error_parameters_exist_account, callback);
                    break;
                case RespModel.ERROR_PARAMETERS_EXIST_NAME:
                    decodeRespCode(R.string.data_rsp_error_parameters_exist_name, callback);
                    break;
                case RespModel.ERROR_ACCOUNT_TOKEN:
                    Application.showToast(R.string.data_rsp_error_account_token);
                    instance.logout();
                    break;
                case RespModel.ERROR_ACCOUNT_LOGIN:
                    decodeRespCode(R.string.data_rsp_error_account_login, callback);
                    break;
                case RespModel.ERROR_ACCOUNT_REGISTER:
                    decodeRespCode(R.string.data_rsp_error_account_register, callback);
                    break;
                case RespModel.ERROR_ACCOUNT_NO_PERMISSION:
                    decodeRespCode(R.string.data_rsp_error_account_no_permission, callback);
                    break;
                case RespModel.ERROR_UNKNOWN:
                default:
                    decodeRespCode(R.string.data_rsp_error_unknown, callback);
                    break;
            }
        }
    }

    /**
     * 收到账户退出的消息需要进行账户退出重新登录
     */
    private void logout() {

    }

    private static void decodeRespCode(final @StringRes int resId,
                                       final DataSource.FailedCallback callback) {
        if (callback != null) {
            callback.onDataNotAvailable(resId);
        }

    }


    /**
     * 处理推送来的消息
     *
     * @param message 消息
     */
    public static void dispatchPush(String message) {

        if (!Account.isLogin()) {
            return;
        }

        PushModel model = PushModel.decode(message);
        if (model == null) {
            return;
        }

        for (PushModel.Entity entity : model.getEntities()) {
            Log.d(TAG, entity.toString());
            switch (entity.type) {
                case PushModel.ENTITY_TYPE_LOGOUT:
                    instance.logout();
                    break;
                case PushModel.ENTITY_TYPE_MESSAGE:
                    MessageCard card = getGson().fromJson(entity.content, MessageCard.class);
                    getMassageCenter().dispatch(card);
                    break;
                case PushModel.ENTITY_TYPE_ADD_FRIEND:
                    UserCard userCard = getGson().fromJson(entity.content, UserCard.class);
                    getUserCenter().dispatch(userCard);
                    break;
                case PushModel.ENTITY_TYPE_ADD_GROUP:
                    GroupCard groupCard = getGson().fromJson(entity.content, GroupCard.class);
                    getGroupCenter().dispatch(groupCard);
                    break;
                case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS:
                case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS:
                    GroupMemberCard[] groupMemberCards = getGson().fromJson(entity.content, new GroupMemberCard[0].getClass());
                    getGroupCenter().dispatch(groupMemberCards);
                    break;
                case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS:
                    // TODO 成员退出的推送
                    break;
            }
        }
    }

    public static Gson getGson() {
        return instance.gson;
    }

    public static UserCenter getUserCenter() {
        return UserDispatcher.instance();
    }

    public static MessageCenter getMassageCenter() {
        return MessageDispatcher.instance();
    }

    public static GroupCenter getGroupCenter() {
        return GroupDispatcher.instance();
    }
}
