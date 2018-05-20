package net.jiuli.factoylib.data.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.jiuli.factoylib.data.BaseDbRepository;
import net.jiuli.factoylib.model.db.Session;
import net.jiuli.factoylib.model.db.Session_Table;

import java.util.Collections;
import java.util.List;

/**
 * Created by  on 17-10-18.
 */

public class SessionRepository extends BaseDbRepository<Session> implements SessionDataSource {
    public SessionRepository() {
        super(Session.class);
    }

    @Override
    protected boolean isRequired(Session session) {
        return true;
    }

    @Override
    protected void insert(Session session) {
        dataList.addFirst(session);
    }

    @Override
    protected void replace(int index, Session session) {
        dataList.remove(index);
        dataList.addFirst(session);
    }

    @Override
    public void load(SucceedCallback<List<Session>> callback) {
        super.load(callback);
        SQLite.select()
                .from(Session.class).orderBy(Session_Table.modifyAt, false)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }

}
