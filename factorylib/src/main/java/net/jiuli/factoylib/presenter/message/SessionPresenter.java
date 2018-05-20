package net.jiuli.factoylib.presenter.message;

import android.support.v7.util.DiffUtil;

import net.jiuli.factoylib.data.message.SessionRepository;
import net.jiuli.factoylib.model.db.Session;
import net.jiuli.factoylib.presenter.BaseSourcePresenter;
import net.jiuli.common.utils.DiffUiDataCallback;

import java.util.List;

/**
 * Created by jiuli on 17-10-17.
 */

public class SessionPresenter
        extends BaseSourcePresenter<Session, Session, SessionRepository, SessionContract.View>
        implements SessionContract.Presenter {
    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view = getView();
        if (view != null) {
            List<Session> dataList = view.getRecyclerViewAdapter().getDataList();
            DiffUiDataCallback callback = new DiffUiDataCallback(dataList, sessions);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            refreshData(result, sessions);
        }
    }
}
