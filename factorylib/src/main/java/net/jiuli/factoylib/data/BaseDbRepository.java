package net.jiuli.factoylib.data;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.jiuli.common.factory.data.DbDataSource;
import net.jiuli.common.utils.CollectionUtil;
import net.jiuli.factoylib.data.helper.DbHelper;
import net.jiuli.factoylib.model.db.BaseDbModel;
import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jiuli on 17-10-8.
 */

@SuppressWarnings("ALL")
public abstract class BaseDbRepository<Data extends BaseDbModel<Data>>
        implements DbDataSource<Data>, DbHelper.ChangedListener<Data>,
        QueryTransaction.QueryResultListCallback<Data> {

    protected SucceedCallback<List<Data>> callback;

    protected final LinkedList<Data> dataList = new LinkedList<>();
    protected Class<Data> dataClass;

    public BaseDbRepository(Class<Data> dataClass) {
        this.dataClass = dataClass;

    }

    @Override
    public void load(SucceedCallback<List<Data>> callback) {
        this.callback = callback;
        registerDbChangedListener();
    }


    /**
     * 检查一个User 时候是我需要关注的数据
     *
     * @param data
     * @return true 是我要关注的数据
     */
    protected abstract boolean isRequired(Data data);

    @Override
    public void onDataSave(List<Data> dataArray) {
        boolean isChanged = false;
        for (Data data : dataArray) {
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged = true;
            }
        }
        if (isChanged) {
            notifyDataChange();
        }
    }

    protected void insert(Data data) {
        dataList.add(data);
    }

    protected void replace(int index, Data data) {
        dataList.remove(index);
        dataList.add(index, data);
    }

    @Override
    public void onDateDelete(List<Data> dataArray) {
        boolean isChanged = false;
        for (Data data : dataArray) {
            if (dataList.remove(data)) {
                isChanged = true;
            }
        }
        if (isChanged) {
            notifyDataChange();
        }
    }

    protected int indexOf(Data newData) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).isSame(newData)) {
                return i;
            }
        }
        return -1;
    }

    private void registerDbChangedListener() {
        DbHelper.addChangedListener(dataClass, this);
    }

    @Override
    public void dispose() {
        this.callback = null;
        DbHelper.removeChangedListener(dataClass, this);
        dataList.clear();
    }


    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        if (tResult.size() == 0) {
            dataList.clear();
            notifyDataChange();
            return;
        }

        onDataSave(tResult);
    }

    private void insertOrUpdate(Data data) {
        int index = indexOf(data);
        if (index >= 0) {
            replace(index, data);
        } else {
            insert(data);
        }
    }


    private void notifyDataChange() {
        if (this.callback != null) {
            callback.onDataLoaded(dataList);
        }
    }
}
