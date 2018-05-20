package net.jiuli.factoylib.presenter.group;

import net.jiuli.common.factory.presenter.BaseRecyclerPresenter;
import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.model.db.view.MemberUserModel;

import java.util.List;

/**
 * Created by jiuli on 17-12-19.
 */

public class GroupMembersPresenter extends BaseRecyclerPresenter<MemberUserModel, GroupMembersContact.View> implements GroupMembersContact.Presenter {

    public GroupMembersPresenter(GroupMembersContact.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        start();
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                GroupMembersContact.View view = getView();
                if (view != null) {
                    String groupId = view.getGroupId();
                    List<MemberUserModel> models = GroupHelper.getMemberUsers(groupId, -1);
                    refreshData(models);
                }
            }
        });
    }
}
