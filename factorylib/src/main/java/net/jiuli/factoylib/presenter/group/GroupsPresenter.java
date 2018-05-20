package net.jiuli.factoylib.presenter.group;

import android.support.v7.util.DiffUtil;

import net.jiuli.factoylib.data.group.GroupsDataSource;
import net.jiuli.factoylib.data.group.GroupsRepository;
import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.presenter.BaseSourcePresenter;
import net.jiuli.common.utils.DiffUiDataCallback;

import java.util.List;

/**
 * Created by jiuli on 17-12-16.
 */

public class GroupsPresenter
        extends BaseSourcePresenter<Group, Group, GroupsDataSource, GroupsContact.View>
        implements GroupsContact.Presenter {


    public GroupsPresenter(GroupsContact.View view) {
        super(new GroupsRepository(), view);
        GroupHelper.refreshGroups();
    }

    @Override
    public void onDataLoaded(List<Group> groups) {
        GroupsContact.View view = getView();
        if (view != null) {
            List<Group> dataList = view.getRecyclerViewAdapter().getDataList();
            DiffUiDataCallback<Group> callback = new DiffUiDataCallback<>(dataList, groups);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            refreshData(result, groups);
        }
    }
}
