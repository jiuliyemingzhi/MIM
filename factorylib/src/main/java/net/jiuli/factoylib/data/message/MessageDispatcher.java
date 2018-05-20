package net.jiuli.factoylib.data.message;

import android.text.TextUtils;

import net.jiuli.factoylib.data.helper.DbHelper;
import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.data.helper.MessageHelper;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.jiuli.factoylib.model.card.MessageCard;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jiuli on 17-10-7.
 */

public class MessageDispatcher implements MessageCenter {

    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MessageCenter instance() {
        return Instance.mInstance;
    }

    private static class Instance {
        private static final MessageCenter mInstance = new MessageDispatcher();
    }

    @Override
    public void dispatch(MessageCard... cards) {
        if (cards != null && cards.length > 0) {
            executor.execute(new MessageCardHandler(cards));
        }
    }

    private class MessageCardHandler implements Runnable {
        private MessageCard[] cards;

        public MessageCardHandler(MessageCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            for (MessageCard card : cards) {
                if (card != null
                        && !TextUtils.isEmpty(card.getId())
                        && !TextUtils.isEmpty(card.getSenderId())
                        && (!TextUtils.isEmpty(card.getReceiverId())
                        || !TextUtils.isEmpty(card.getGroupId()))) {
                    // 消息卡片可能是推送来的 , 也可能是自己造的
                    // 推送来的代表服务器一定有, 我们可以查询到
                    // 如果是自己造的, 那么先存储本地 ,然后发送网络
                    // 发送消息流程: 写消息 -> 存储本地 -> 发送网络 -> 网络返回 -> 刷新本地状态
                    Message message = MessageHelper.findFormLocal(card.getId());
                    if (message != null) {
                        //消息本身字段从发送之后就不能变化
                        if (message.getStatus() == Message.STATUS_DONE) {
                            continue;
                        }
                        if (card.getStatus() == Message.STATUS_DONE) {
                            //代表网络发送成功 此时需要修改时间为服务器的时间
                            message.setCreateAt(card.getCreateAt());
                        }
                        message.setContent(card.getContent());
                        message.setAttach(card.getAttach());
                        message.setStatus(card.getStatus());
                    } else {
                        User sender = UserHelper.getUser(card.getSenderId());
                        User receiver = null;
                        Group group = null;

                        if (!TextUtils.isEmpty(card.getReceiverId())) {
                            receiver = UserHelper.getUser(card.getReceiverId());
                        } else if (!TextUtils.isEmpty(card.getGroupId())) {
                            group = GroupHelper.findFormLocal(card.getGroupId());
                        }

                        if (sender != null && (receiver != null || group != null)) {
                            message = card.build(sender, receiver, group);
                        }
                    }
                    if (message != null) {
                        messages.add(message);
                    }
                }
            }

            if (messages.size() > 0) {
                DbHelper.save(Message.class, messages);
            }
        }
    }
}
