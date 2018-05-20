package net.jiuli.factoylib.data.helper;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.data.message.MessageCenter;
import net.jiuli.factoylib.data.message.MessageDispatcher;
import net.jiuli.factoylib.model.api.RespModel;
import net.jiuli.factoylib.model.card.MessageCard;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.db.Message_Table;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.model.message.MessageCreateModel;
import net.jiuli.factoylib.net.NetWork;
import net.jiuli.factoylib.net.RemoteService;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jiuli on 17-10-5.
 */

public class MessageHelper {

    /**
     * 从本地找数据
     *
     * @param id messageId
     * @return
     */
    public static Message findFormLocal(String id) {
        return SQLite.select().from(Message.class).where(Message_Table.id.eq(id)).querySingle();
    }

    public static Message findLastWidthGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause().and(Message_Table.group_id.eq(groupId)))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }

    public static Message findLastWidthUser(String userId) {
        return SQLite.select().from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }

    public static void push(final MessageCreateModel model) {
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                final Message message = findFormLocal(model.getId());
                if (message != null && message.getStatus() != Message.STATUS_FAILED) {
                    return;
                }

                final MessageCard card = model.buildCard();
                Factory.getMassageCenter().dispatch(card);
                RemoteService service = NetWork.remote();
                service.pushMessage(model).enqueue(new Callback<RespModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RespModel<MessageCard>> call, Response<RespModel<MessageCard>> response) {
                        RespModel<MessageCard> respModel = response.body();
                        if (respModel != null && respModel.success()) {
                            MessageCard messageCard = respModel.getResult();
                            if (messageCard != null) {
                                Factory.getMassageCenter().dispatch(messageCard);
                            }
                        } else {
                            Factory.decodeRespCode(respModel, null);
                            onFailure(call, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RespModel<MessageCard>> call, Throwable t) {
                        card.setStatus(Message.STATUS_FAILED);
                        Factory.getMassageCenter().dispatch(card);
                    }
                });
            }
        });
    }

}
