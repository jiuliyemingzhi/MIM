package net.jiuli.factoylib.presenter.group;

import net.jiuli.common.factory.presenter.BaseContract;

/**
 * Created by jiuli on 17-12-19.
 */

public interface GroupMemberAddContact extends BaseContract {
    interface Presenter extends BaseContract.Presenter {

        void submit();

        void changeSelect(GroupCreateContact.ViewModel viewModel, boolean isSelect);

    }

    interface View extends BaseContract.RecyclerView<Presenter, GroupCreateContact.ViewModel> {
        void onAddedSucceed();

        String getGroupId();
    }
}
