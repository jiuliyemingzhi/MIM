package net.jiuli.factoylib.presenter;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.common.factory.data.DbDataSource;
import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.common.factory.presenter.BaseRecyclerPresenter;

import java.util.List;

/**
 * Created by jiuli on 17-10-8.
 */

public abstract class BaseSourcePresenter<Data, ViewModel,
        Source extends DbDataSource<Data>,
        View extends BaseContract.RecyclerView>
        extends BaseRecyclerPresenter<ViewModel, View>
        implements DataSource.SucceedCallback<List<Data>> {

    protected Source mSource;

    public BaseSourcePresenter(Source source, View view) {
        super(view);
        this.mSource = source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource != null) {
            mSource.load(this);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource = null;
    }
}
