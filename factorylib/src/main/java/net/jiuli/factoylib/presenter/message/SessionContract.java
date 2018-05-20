package net.jiuli.factoylib.presenter.message;

import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.factoylib.model.db.Session;

/**
 * Created by jiuli on 17-10-17.
 */

public class SessionContract {
    public interface Presenter extends BaseContract.Presenter {

    }

    public interface View extends BaseContract.RecyclerView<Presenter, Session> {

    }
}
