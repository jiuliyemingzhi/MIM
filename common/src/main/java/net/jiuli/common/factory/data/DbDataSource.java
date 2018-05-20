package net.jiuli.common.factory.data;

import java.util.List;

/**
 * Created by jiuli on 17-10-7.
 */

public interface DbDataSource<Data> extends DataSource {
    /***
     * 有一个基本数据源加载方法
     * @param callback 传递一个callback回调 ,一般回调到 Presenter
     */
    void load(DataSource.SucceedCallback<List<Data>> callback);
}
