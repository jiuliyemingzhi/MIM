package net.jiuli.factoylib.presenter.contact;

import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.factoylib.model.db.User;

/**
 * Created by jiuli on 17-9-25.
 */

public interface ContactContact extends BaseContract {


    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter, User> {

    }
}
