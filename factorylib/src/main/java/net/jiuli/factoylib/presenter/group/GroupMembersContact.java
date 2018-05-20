package net.jiuli.factoylib.presenter.group;

import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.factoylib.model.db.view.MemberUserModel;

/**
 * Created by jiuli on 17-12-19.
 */

public interface GroupMembersContact extends BaseContract {
    interface Presenter extends BaseContract.Presenter {
        void refresh();
    }

    interface View extends BaseContract.RecyclerView<Presenter, MemberUserModel> {
        String getGroupId();
    }
}
