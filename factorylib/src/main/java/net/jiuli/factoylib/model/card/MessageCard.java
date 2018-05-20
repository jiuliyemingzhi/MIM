package net.jiuli.factoylib.model.card;

import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.db.User;

import java.util.Date;

/**
 * Created by jiuli on 17-10-1.
 */

public class MessageCard {
    private String id;
    private String content;
    private String attach;
    private int type;
    private Date createAt;
    private String groupId;
    private String senderId;
    private String receiverId;


    private transient int status = Message.STATUS_DONE;

    private transient boolean uploaded = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }


    public Message build(User sender, User receiver, Group group) {
        Message message = new Message();
        message.setId(id);
        message.setContent(content);
        message.setAttach(attach);
        message.setType(type);
        message.setCreateAt(createAt);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setGroup(group);
        message.setStatus(status);
        return message;
    }

    @Override
    public String toString() {
        return "MessageCard{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", attach='" + attach + '\'' +
                ", type=" + type +
                ", createAt=" + createAt +
                ", groupId='" + groupId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", status=" + status +
                ", uploaded=" + uploaded +
                '}';
    }
}
