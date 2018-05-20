package net.jiuli.factoylib.model.card;

import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.GroupMember;
import net.jiuli.factoylib.model.db.User;

import java.util.Date;

/**
 * Created by jiuli on 17-9-30.
 */

public class GroupMemberCard {
    private String id;
    private String alias;
    private boolean isAdmin;
    private boolean isOwner;
    private String userId;
    private String groupId;
    private Date modifyAt;
    private String portrait;


    public String getId() {
        return id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public GroupMember build(String groupId, String userId) {
        GroupMember gm = new GroupMember();
        gm.setId(id);
        gm.setAdmin(isAdmin);
        gm.setOwner(isOwner);
        gm.setAlias(alias);
        gm.setModifyAt(modifyAt);
        gm.setGroupId(groupId);
        gm.setUserId(userId);
        gm.setPortrait(portrait);
        return gm;
    }
}
