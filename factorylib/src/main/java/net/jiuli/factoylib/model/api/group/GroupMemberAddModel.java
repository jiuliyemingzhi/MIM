package net.jiuli.factoylib.model.api.group;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

public class GroupMemberAddModel {
    @Expose
    private Set<String> users = new HashSet<>();

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public GroupMemberAddModel(Set<String> users) {
        this.users = users;
    }
}