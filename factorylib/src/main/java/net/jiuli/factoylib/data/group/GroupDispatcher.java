package net.jiuli.factoylib.data.group;

import net.jiuli.factoylib.data.helper.DbHelper;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.jiuli.factoylib.model.card.GroupCard;
import net.jiuli.factoylib.model.card.GroupMemberCard;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.GroupMember;
import net.jiuli.factoylib.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jiuli on 17-10-7.
 */


public class GroupDispatcher implements GroupCenter {
    private Executor executor = Executors.newSingleThreadExecutor();

    public static GroupCenter instance() {
        return Instance.mInstance;
    }

    private static class Instance {
        private final static GroupDispatcher mInstance = new GroupDispatcher();
    }

    private class GroupMemberRespHandler implements Runnable {

        private final GroupMemberCard[] cards;

        GroupMemberRespHandler(GroupMemberCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<GroupMember> members = new ArrayList<>();
            for (GroupMemberCard card : cards) {
                GroupMember member = card.build(card.getGroupId(), card.getUserId());
                members.add(member);
            }
            if (members.size() > 0) {
                DbHelper.save(GroupMember.class, members);
            }
        }
    }

    @Override
    public void dispatch(GroupCard... cards) {
        if (cards != null && cards.length > 0) {
            executor.execute(new GroupHandler(cards));
        }
    }

    @Override
    public void dispatch(GroupMemberCard... cards) {
        if (cards != null && cards.length > 0) {
            executor.execute(new GroupMemberRespHandler(cards));
        }
    }

    private class GroupHandler implements Runnable {

        private final GroupCard[] cards;

        GroupHandler(GroupCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Group> groups = new ArrayList<>();
            for (GroupCard card : cards) {
                User owner = UserHelper.getUser(card.getOwnerId());
                if (owner != null) {
                    Group group = card.build(owner);
                    groups.add(group);
                }
            }
            if (groups.size() > 0) {
                DbHelper.save(Group.class, groups);
            }
        }
    }
}

