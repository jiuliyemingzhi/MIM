package net.jiuli.factoylib.presenter.group;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.common.factory.presenter.BaseRecyclerPresenter;
import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.R;
import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.jiuli.factoylib.model.api.group.GroupMemberAddModel;
import net.jiuli.factoylib.model.card.GroupMemberCard;
import net.jiuli.factoylib.model.db.view.MemberUserModel;
import net.jiuli.factoylib.model.db.view.UserSampleModel;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupMemberAddPresenter
        extends BaseRecyclerPresenter<GroupCreateContact.ViewModel, GroupMemberAddContact.View>
        implements GroupMemberAddContact.Presenter, DataSource.Callback<List<GroupMemberCard>> {
    private Set<String> users = new HashSet<>();

    public GroupMemberAddPresenter(GroupMemberAddContact.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                GroupMemberAddContact.View view = getView();
                if (view == null) {
                    return;
                }

                List<UserSampleModel> sampleModels = UserHelper.getSampleContact();

                List<MemberUserModel> userModels = GroupHelper.getMemberUsers(view.getGroupId(), -1);

                for (MemberUserModel userModel : userModels) {
                    int index = indexOfUserContact(sampleModels, userModel.userId);
                    if (index >= 0) {
                        sampleModels.remove(index);
                    }
                }
                List<GroupCreateContact.ViewModel> viewModels = new ArrayList<>();
                for (UserSampleModel model : sampleModels) {
                    viewModels.add(new GroupCreateContact.ViewModel(model, false));
                }
                refreshData(viewModels);
            }
        });
    }


    private int indexOfUserContact(List<UserSampleModel> models, String userId) {
        int index = 0;
        for (UserSampleModel model : models) {
            if (model.getId().equalsIgnoreCase(userId)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public void submit() {
        GroupMemberAddContact.View view = getView();
        view.showLoading();
        if (users.size() == 0) {
            view.showError(R.string.label_group_member_add_invalid);
            return;
        }
        GroupMemberAddModel addModel = new GroupMemberAddModel(users);
        GroupHelper.addMembers(view.getGroupId(), addModel, this);
    }

    @Override
    public void changeSelect(GroupCreateContact.ViewModel viewModel, boolean isSelect) {
        if (isSelect) {
            users.add(viewModel.author.getId());
        } else {
            users.remove(viewModel.author.getId());
        }
    }

    @Override
    public void onDataLoaded(List<GroupMemberCard> groupMemberCards) {
        final GroupMemberAddContact.View view = getView();
        if (view == null) {
            return;
        }
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.onAddedSucceed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final GroupMemberAddContact.View view = getView();
        if (view == null) {
            return;
        }
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}
