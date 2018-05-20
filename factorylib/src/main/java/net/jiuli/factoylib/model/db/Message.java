package net.jiuli.factoylib.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import net.jiuli.factoylib.persistence.Account;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by jiuli on 17-10-1.11
 */

@Table(database = AppDataBase.class)
public class Message extends BaseDbModel<Message> implements Serializable {
    public static final int RECEIVER_TYPE_NONE = 1;
    public static final int RECEIVER_TYPE_GROUP = 2;

    public static final int TYPE_STR = 1;
    public static final int TYPE_PIC = 2;
    public static final int TYPE_FILE = 3;
    public static final int TYPE_AUDIO = 4;
    public static final int TYPE_VIDEO = 5;

    public static final int STATUS_DONE = 0;
    public static final int STATUS_CREATED = 1;
    public static final int STATUS_FAILED = 2;

    @PrimaryKey
    private String id;

    @Column
    private String content;

    @Column
    private String attach;

    @Column
    private int type;

    @Column
    private Date createAt;

    @Column
    private int status;

    @ForeignKey(tableClass = Group.class, stubbedRelationship = true)
    private Group group;

    @ForeignKey(tableClass = User.class, stubbedRelationship = true)
    private User sender;

    @ForeignKey(tableClass = User.class, stubbedRelationship = true)
    private User receiver;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getSampleContent() {
        switch (type) {
            case TYPE_AUDIO:
                return "🎵";
            case TYPE_PIC:
                return "[图片]";
            case TYPE_FILE:
                return "📃";
            case TYPE_VIDEO:
                return "[视频]";
            default:
                return content;
        }
    }

    @Override
    public boolean isSame(Message old) {
        return Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(Message old) {
        return this == old || status == old.status;
    }

    public User getOther() {
        if (Account.getUserId().equals(sender.getId())) {
            return receiver;
        } else {
            return sender;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return type == message.type &&
                status == message.status &&
                Objects.equals(id, message.id) &&
                Objects.equals(content, message.content) &&
                Objects.equals(attach, message.attach) &&
                Objects.equals(createAt, message.createAt) &&
                Objects.equals(group, message.group) &&
                Objects.equals(sender, message.sender) &&
                Objects.equals(receiver, message.receiver);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", attach='" + attach + '\'' +
                ", type=" + type +
                ", createAt=" + createAt +
                ", status=" + status +
                ", group=" + group +
                ", sender=" + sender +
                ", receiver=" + receiver +
                '}';
    }
}
