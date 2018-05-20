package net.jiuli.factoylib.presenter.contact;

import android.support.v7.util.DiffUtil;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.jiuli.factoylib.data.user.ContactDataSource;
import net.jiuli.factoylib.data.user.ContactRepository;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.presenter.BaseSourcePresenter;
import net.jiuli.common.utils.DiffUiDataCallback;

import java.util.List;

/**
 * Created by jiuli on 17-9-25.
 */

public class ContactPresenter extends BaseSourcePresenter<User, User, ContactDataSource, ContactContact.View>
        implements ContactContact.Presenter, DataSource.SucceedCallback<List<User>> {


    public ContactPresenter(ContactContact.View view) {
        super(new ContactRepository(), view);
    }

    @Override
    public void start() {
        super.start();
        UserHelper.refreshContacts();
    }

    @Override
    public void onDataLoaded(List<User> users) {
        ContactContact.View view = getView();
        if (view != null) {
            RecyclerAdapter<User> adapter = view.getRecyclerViewAdapter();
            List<User> userList = adapter.getDataList();
            DiffUiDataCallback<User> callback = new DiffUiDataCallback<>(userList, users);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
            refreshData(diffResult, users);
        }
    }
}
