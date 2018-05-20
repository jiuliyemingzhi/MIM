package net.jiuli.factoylib.model.db;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.data.helper.MessageHelper;
import net.jiuli.factoylib.data.helper.UserHelper;

import java.util.Date;
import java.util.Objects;

/**
 * Created by jiuli on 17-10-1.
 */

@Table(database = AppDataBase.class)
public class Session extends BaseDbModel<Session> {

    @PrimaryKey
    private String id;
    @Column
    private String picture;
    @Column
    private String title;
    @Column
    private String content;
    @Column
    private int receiverType = Message.RECEIVER_TYPE_NONE;
    @Column
    private int unReadCount;
    @Column
    private Date modifyAt;

    @ForeignKey(tableClass = Message.class, stubbedRelationship = true)
    private Message message;


    public Session() {

    }

    public Session(Message message) {
        if (message.getGroup() == null) {
            receiverType = Message.RECEIVER_TYPE_NONE;
            User other = message.getOther();
            id = other.getId();
            picture = other.getPortrait();
            title = other.getName();
        } else {
            receiverType = Message.RECEIVER_TYPE_GROUP;
            id = message.getGroup().getId();
            picture = message.getGroup().getId();
            title = message.getGroup().getName();
        }
        this.message = message;
        this.content = message.getSampleContent();
        this.modifyAt = message.getCreateAt();
    }

    public Session(Identify identify) {
        this.id = identify.id;
        this.receiverType = identify.type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return receiverType == session.receiverType &&
                unReadCount == session.unReadCount &&
                Objects.equals(id, session.id) &&
                Objects.equals(picture, session.picture) &&
                Objects.equals(title, session.title) &&
                Objects.equals(content, session.content) &&
                Objects.equals(modifyAt, session.modifyAt) &&
                Objects.equals(message, session.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiverType);
    }


    public void refreshToNow() {
        Message message;
        if (receiverType == Message.RECEIVER_TYPE_GROUP) {
            message = MessageHelper.findLastWidthGroup(id);
            if (message == null) {
                if (TextUtils.isEmpty(picture) || TextUtils.isEmpty(title)) {
                    Group group = GroupHelper.findFormLocal(id);
                    if (group != null) {
                        picture = group.getPicture();
                        title = group.getName();
                    }
                }

                this.message = null;
                this.content = "";
                this.modifyAt = new Date(System.currentTimeMillis());
            } else {
                if (TextUtils.isEmpty(picture) || TextUtils.isEmpty(title)) {
                    Group group = message.getGroup();
                    group.load();
                    picture = group.getPicture();
                    title = group.getName();
                }
                this.message = message;
                this.content = message.getSampleContent();
                this.modifyAt = message.getCreateAt();
            }
        } else {
            message = MessageHelper.findLastWidthUser(id);
            if (message == null) {
                if (TextUtils.isEmpty(picture) || TextUtils.isEmpty(title)) {
                    User user = UserHelper.findForLocal(id);
                    if (user != null) {
                        picture = user.getPortrait();
                        title = user.getName();
                    }
                }
                this.message = null;
                this.content = "";
                this.modifyAt = new Date(System.currentTimeMillis());
            } else {
                if (TextUtils.isEmpty(picture) || TextUtils.isEmpty(title)) {
                    User user = message.getOther();
                    user.load();
                    if (user != null) {
                        picture = user.getPortrait();
                        title = user.getName();
                    }
                }
                this.message = message;
                this.content = message.getSampleContent();
                this.modifyAt = message.getCreateAt();
            }
        }
    }


    public static Identify createSessionIdentify(Message message) {
        Identify identify = new Identify();
        if (message.getGroup() == null) {
            identify.type = Message.RECEIVER_TYPE_NONE;
            identify.id = message.getOther().getId();
        } else {
            identify.type = Message.RECEIVER_TYPE_GROUP;
            identify.id = message.getGroup().getId();
        }
        return identify;
    }


    @Override
    public boolean isSame(Session old) {
        return Objects.equals(id, old.id) && Objects.equals(receiverType, old.receiverType);
    }

    @Override
    public boolean isUiContentSame(Session old) {
        return Objects.equals(content, old.content) && Objects.equals(modifyAt, old.modifyAt);
    }


    /**
     * 对于会话信息, 最重要的部分进行提取
     * 其中我们主要关注两点:
     * 一个会话最重要的的是标识是和人聊天还是群聊.
     * id存储的是人或者群的id
     * 紧跟着Type: 存储的是具体的类型(人, 群)
     * equals 和 hashCode 也是对两个字段进行判断
     */
    public static class Identify {
        public String id;
        public int type;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Identify identify = (Identify) o;
            return type == identify.type &&
                    Objects.equals(id, identify.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, type);
        }
    }
}
