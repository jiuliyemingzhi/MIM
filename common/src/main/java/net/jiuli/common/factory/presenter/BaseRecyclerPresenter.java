package net.jiuli.common.factory.presenter;

import android.support.v7.util.DiffUtil;

import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

/**
 * ..Created by jiuli on 17-10-8.
 */

public class BaseRecyclerPresenter<ViewModel, View extends BaseContract.RecyclerView> extends BasePresenter<View> {
    public BaseRecyclerPresenter(View view) {
        super(view);
    }

    protected void refreshData(final List<ViewModel> viewModels) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                View view = getView();
                if (view == null) {
                    return;
                }
                RecyclerAdapter<ViewModel> adapter = view.getRecyclerViewAdapter();
                adapter.replace(viewModels);
                view.onAdapterDataChange();
            }
        });
    }

    protected void refreshData(final DiffUtil.DiffResult diffResult, final List<ViewModel> viewModels) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                View view = getView();
                if (view != null) {
                    RecyclerAdapter<ViewModel> adapter = view.getRecyclerViewAdapter();
                    adapter.getDataList().clear();
                    adapter.getDataList().addAll(viewModels);
                    view.onAdapterDataChange();
                    diffResult.dispatchUpdatesTo(adapter);
                }
            }
        });
    }

    private void refreshDataOnUiThread(DiffUtil.DiffResult diffResult, List<ViewModel> viewModels) {
        View view = getView();
        if (view != null) {
            RecyclerAdapter<ViewModel> adapter = view.getRecyclerViewAdapter();
            adapter.getDataList().clear();
            adapter.getDataList().addAll(viewModels);
            view.onAdapterDataChange();
            diffResult.dispatchUpdatesTo(adapter);
        }
    }
}
