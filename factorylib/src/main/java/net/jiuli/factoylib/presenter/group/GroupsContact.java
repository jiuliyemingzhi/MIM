package net.jiuli.factoylib.presenter.group;

import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.factoylib.model.db.Group;

/**
 * Created by jiuli on 17-12-16.
 */

public interface  GroupsContact  {
    interface Presenter extends BaseContract.Presenter{

    }

    interface  View extends  BaseContract.RecyclerView<Presenter,Group>{

    }

}
