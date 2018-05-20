package net.jiuli.factoylib.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.R;
import net.jiuli.factoylib.model.api.RespModel;
import net.jiuli.factoylib.model.api.group.GroupCreateModel;
import net.jiuli.factoylib.model.api.group.GroupMemberAddModel;
import net.jiuli.factoylib.model.card.GroupCard;
import net.jiuli.factoylib.model.card.GroupMemberCard;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.GroupMember;
import net.jiuli.factoylib.model.db.GroupMember_Table;
import net.jiuli.factoylib.model.db.Group_Table;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.model.db.view.MemberUserModel;
import net.jiuli.factoylib.net.NetWork;
import net.jiuli.factoylib.net.RemoteService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GroupHelper {
    public static Group find(String groupId) {
        Group group = findFormLocal(groupId);
        if (group == null) {
            group = findFormNet(groupId);
        }
        return group;
    }

    private static Group findFormNet(String groupId) {
        RemoteService remote = NetWork.remote();
        try {
            Response<RespModel<GroupCard>> response = remote.groupFind(groupId).execute();
            GroupCard card = response.body().getResult();
            if (card != null) {
                Factory.getGroupCenter().dispatch(card);
                User user = UserHelper.getUser(card.getOwnerId());
                return card.build(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Group findFormLocal(String groupId) {
        return SQLite.select().from(Group.class).where(Group_Table.id.eq(groupId)).querySingle();
    }

    public static void create(GroupCreateModel model, final DataSource.Callback<GroupCard> callback) {
        NetWork.remote().groupCreate(model).enqueue(new Callback<RespModel<GroupCard>>() {
            @Override
            public void onResponse(Call<RespModel<GroupCard>> call, Response<RespModel<GroupCard>> response) {
                RespModel<GroupCard> respModel = response.body();
                if (respModel.success()) {
                    GroupCard card = respModel.getResult();
                    if (card != null) {
                        Factory.getGroupCenter().dispatch(card);
                        callback.onDataLoaded(card);
                    }
                } else {
                    Factory.decodeRespCode(respModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RespModel<GroupCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    public static Call search(String name, final DataSource.Callback<List<GroupCard>> callback) {
        final RemoteService remote = NetWork.remote();
        Call<RespModel<List<GroupCard>>> call = remote.groupSearch(name);
        call.enqueue(new Callback<RespModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RespModel<List<GroupCard>>> call, Response<RespModel<List<GroupCard>>> response) {
                RespModel<List<GroupCard>> respModel = response.body();
                if (respModel.success()) {
                    callback.onDataLoaded(respModel.getResult());
                } else {
                    Factory.decodeRespCode(respModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RespModel<List<GroupCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return call;
    }

    public static void refreshGroups() {
        RemoteService service = NetWork.remote();
        Call<RespModel<List<GroupCard>>> call = service.groups("");
        call.enqueue(new Callback<RespModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RespModel<List<GroupCard>>> call, Response<RespModel<List<GroupCard>>> response) {
                RespModel<List<GroupCard>> respModel = response.body();
                if (respModel.success()) {
                    List<GroupCard> cards = respModel.getResult();
                    if (cards != null && cards.size() > 0) {
                        Factory.getGroupCenter().dispatch(cards.toArray(new GroupCard[0]));
                    }
                } else {
                    Factory.decodeRespCode(respModel, null);
                }
            }

            @Override
            public void onFailure(Call<RespModel<List<GroupCard>>> call, Throwable t) {

            }
        });
    }

    public static long getMemberCount(String id) {

        return SQLite.selectCountOf().from(GroupMember.class).where(GroupMember_Table.groupId.eq(id)).count();
    }

    public static void refreshGroupMember(final String groupId) {
        RemoteService service = NetWork.remote();
        service.members(groupId).enqueue(new Callback<RespModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RespModel<List<GroupMemberCard>>> call, Response<RespModel<List<GroupMemberCard>>> response) {
                RespModel<List<GroupMemberCard>> respModel = response.body();
                if (respModel.success()) {
                    List<GroupMemberCard> cards = respModel.getResult();
                    if (cards != null && cards.size() > 0) {
                        Factory.getGroupCenter().dispatch(cards.toArray(new GroupMemberCard[0]));
                    }
                } else {
                    Factory.decodeRespCode(respModel, null);
                }
            }

            @Override
            public void onFailure(Call<RespModel<List<GroupMemberCard>>> call, Throwable t) {
            }
        });
    }

    public static void refreshGroupMember(final String groupId, final DataSource.SucceedCallback<Integer> callback) {
        RemoteService service = NetWork.remote();
        service.members(groupId).enqueue(new Callback<RespModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RespModel<List<GroupMemberCard>>> call, Response<RespModel<List<GroupMemberCard>>> response) {
                RespModel<List<GroupMemberCard>> respModel = response.body();
                if (respModel.success()) {
                    List<GroupMemberCard> cards = respModel.getResult();
                    if (cards != null && cards.size() > 0) {
                        Factory.getGroupCenter().dispatch(cards.toArray(new GroupMemberCard[0]));
                        callback.onDataLoaded(cards.size());
                    }
                } else {
                    Factory.decodeRespCode(respModel, null);
                }
            }

            @Override
            public void onFailure(Call<RespModel<List<GroupMemberCard>>> call, Throwable t) {
            }
        });
    }

    public static List<MemberUserModel> getMemberUsers(String groupId, int size) {

        return SQLite.select(GroupMember_Table.alias.withTable().as("alias"),
                GroupMember_Table.userId.withTable().as("userId"),
                GroupMember_Table.portrait.withTable().as("portrait"))
                .from(GroupMember.class)
//                .join(User.class, Join.JoinType.INNER)
                .where(GroupMember_Table.groupId.eq(groupId))
//                .where(User_Table.id.withTable().eq(GroupMember_Table.user_id.withTable()))
                .orderBy(GroupMember_Table.userId, true)
                .limit(size)
                .queryCustomList(MemberUserModel.class);
    }

    public static void addMembers(String groupId, GroupMemberAddModel model, final DataSource.Callback<List<GroupMemberCard>> callback) {
        Call<RespModel<List<GroupMemberCard>>> call = NetWork.remote().addMembers(groupId, model);
        call.enqueue(new Callback<RespModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RespModel<List<GroupMemberCard>>> call, Response<RespModel<List<GroupMemberCard>>> response) {
                RespModel<List<GroupMemberCard>> respModel = response.body();
                if (respModel.success()) {
                    List<GroupMemberCard> cards = respModel.getResult();
                    if (cards != null && cards.size() > 0) {
                        Factory.getGroupCenter().dispatch(cards.toArray(new GroupMemberCard[0]));
                        callback.onDataLoaded(cards);
                    } else {
                        Factory.decodeRespCode(respModel, callback);
                    }
                }
            }

            @Override
            public void onFailure(Call<RespModel<List<GroupMemberCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }
}
