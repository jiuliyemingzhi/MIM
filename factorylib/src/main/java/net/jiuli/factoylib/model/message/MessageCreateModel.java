package net.jiuli.factoylib.model.message;

import net.jiuli.factoylib.model.card.MessageCard;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.persistence.Account;

import java.util.Date;
import java.util.UUID;

/**
 * Created by jiuli on 17-10-17.
 */

public class MessageCreateModel {
    private String id;
    private String content;
    private String attach;
    private String receiverId;
    private int type = Message.TYPE_STR;

    private int receiverType = Message.RECEIVER_TYPE_NONE;

    private MessageCard card;

    private MessageCreateModel() {
        this.id = UUID.randomUUID().toString();
    }

    public MessageCard buildCard() {
        if (card == null) {
            card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());
            if (receiverType == Message.RECEIVER_TYPE_GROUP) {
                card.setGroupId(receiverId);
            } else {
                card.setReceiverId(receiverId);
            }
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
        }
        return card;
    }

    public final static class Builder {
        private MessageCreateModel model;

        public Builder() {
            this.model = new MessageCreateModel();
        }

        public Builder receiver(String receiverId, int receiverType) {
            this.model.receiverType = receiverType;
            this.model.receiverId = receiverId;
            return this;
        }

        public Builder content(String content, int type) {
            this.model.content = content;
            this.model.type = type;
            return this;
        }

        public Builder attach(String attach) {
            this.model.attach = attach;
            return this;
        }

        public MessageCreateModel build() {
            return this.model;
        }
    }

    public static MessageCreateModel buildWidthMessage(Message message) {
        MessageCreateModel model = new MessageCreateModel();
        model.id = message.getId();
        model.type = message.getType();
        model.content = message.getContent();
        model.attach = message.getAttach();
        if (message.getReceiver() == null) {
            model.receiverType = Message.RECEIVER_TYPE_GROUP;
            model.receiverId  = message.getGroup().getId();
        }else {
            model.receiverType = Message.RECEIVER_TYPE_NONE;
            model.receiverId  = message.getReceiver().getId();
        }
        return model;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAttach() {
        return attach;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getType() {
        return type;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public MessageCard getCard() {
        return card;
    }

    @Override
    public String toString() {
        return "MessageCreateModel{" +
                "id='" + id + '\'' +
                ", receiverType=" + receiverType +
                ", card=" + card +
                '}';
    }
}
