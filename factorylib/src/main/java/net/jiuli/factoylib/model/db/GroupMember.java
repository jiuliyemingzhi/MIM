package net.jiuli.factoylib.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.jiuli.factoylib.model.Db;

import java.util.Date;
import java.util.Objects;

/**
 * Created by jiuli on 17-9-30.
 */
@Table(database = AppDataBase.class)
public class GroupMember extends BaseModel implements Db {
    public static final int NOTIFY_LEVEL_INVALID = -1;
    public static final int NOTIFY_LEVEL_NONE = 0;

    @NotNull
    @PrimaryKey
    private String id;

    @Column
    private String alias;

    @Column
    private boolean isAdmin;
    @Column
    private boolean isOwner;

    @Column
    private Date modifyAt;

    @Column
    private String groupId;

    @Column
    private String userId;

    @Column
    private  String portrait;



//    @Column
//    @ForeignKey(tableClass = Group.class, stubbedRelationship = true)
//    private Group group;
//
//    @Column
//    @ForeignKey(tableClass = User.class, stubbedRelationship = true)
//    private User user;


    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }


    public Group getGroup() {
        return GroupHelper.find(groupId);
    }

//    public void setGroup(Group group) {
//        this.group = group;
//    }

    public User getUser() {
        return UserHelper.searchFirstOfNet(userId);
    }

//    public void setUser(User user) {
//        this.user = user;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMember that = (GroupMember) o;
        return isAdmin == that.isAdmin &&
                isOwner == that.isOwner &&
                Objects.equals(id, that.id) &&
                Objects.equals(alias, that.alias) &&
                Objects.equals(groupId, that.getGroupId()) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
