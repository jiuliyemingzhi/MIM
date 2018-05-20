package net.jiuli.factoylib.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.model.db.view.MemberUserModel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by jiuli on 17-9-30.
 */

@Table(database = AppDataBase.class)
public class Group extends BaseDbModel<Group> implements Serializable {
    @PrimaryKey
    private String id;
    @Column
    private String name;
    @Column
    private String desc;
    @Column
    private String picture;
    @Column
    private int notifyLevel;
    @Column
    private Date joinAt;
    @Column
    private Date modifyAt;

    @ForeignKey(tableClass = User.class, stubbedRelationship = true)
    private User owner;

    private Object holder;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getJoinAt() {
        return joinAt;
    }

    public void setJoinAt(Date joinAt) {
        this.joinAt = joinAt;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    public int getNotifyLevel() {
        return notifyLevel;
    }

    public void setNotifyLevel(int notifyLevel) {
        this.notifyLevel = notifyLevel;
    }


    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Object getHolder() {
        return holder;
    }

    public void setHolder(Object holder) {
        this.holder = holder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return notifyLevel == group.notifyLevel &&
                Objects.equals(id, group.id) &&
                Objects.equals(name, group.name) &&
                Objects.equals(desc, group.desc) &&
                Objects.equals(picture, group.picture) &&
                Objects.equals(joinAt, group.joinAt) &&
                Objects.equals(modifyAt, group.modifyAt) &&
                Objects.equals(owner, group.owner) &&
                Objects.equals(holder, group.holder);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean isSame(Group old) {
        return Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(Group old) {
        return Objects.equals(this.name, old.name)
                && Objects.equals(this.desc, old.desc)
                && Objects.equals(this.picture, old.picture)
                && Objects.equals(this.holder, old.holder);
    }

    private long groupMemberCount = -1;

    public long getGroupMemberCount() {
        if (groupMemberCount == -1) {
            groupMemberCount = GroupHelper.getMemberCount(id);
        }
        return groupMemberCount;
    }

    private List<MemberUserModel> groupLatelyMembers;

    public List<MemberUserModel> getGroupLatelyMembers() {
        if (groupLatelyMembers == null || groupLatelyMembers.isEmpty()) {
            groupLatelyMembers = GroupHelper.getMemberUsers(id, 20);
        }
        return groupLatelyMembers;
    }
}
