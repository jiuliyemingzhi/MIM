package net.jiuli.common.widget.recycler;

/**
 * Created by jiuli on 17-8-17.
 */

public interface AdapterCallback<Data> {
    Data update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
