package net.jiuli.mim.fragments.search;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.jiuli.common.app.Fragment;
import net.jiuli.common.app.PresenterFragment;
import net.jiuli.common.widget.EmptyView;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.model.card.GroupCard;
import net.jiuli.factoylib.presenter.search.SearchContract;
import net.jiuli.factoylib.presenter.search.SearchGroupPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.PersonalActivity;
import net.jiuli.mim.activities.SearchActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment, SearchContract.GroupView {

    @BindView(R.id.group_empty_view)
    EmptyView mEmptyView;

    @BindView(R.id.group_search_recycler)
    RecyclerView mRecyclerSearch;

    private Adapter adapter;

    @Override
    protected void initWidget(View root) {

        super.initWidget(root);
        mEmptyView.bind(mRecyclerSearch);
        setPlaceHolderView(mEmptyView);
        mRecyclerSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerSearch.setAdapter(adapter = new Adapter());
        search("");
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchGroupPresenter(this);
    }


    @Override
    public void onSearchDone(List<GroupCard> userCards) {
        adapter.replace(userCards);
        mPlaceHolderView.triggerOkOrEmpty(adapter.getItemCount() > 0);
    }

    class Adapter extends RecyclerAdapter<GroupCard> {

        @Override
        public int getItemViewType(int position, GroupCard groupCard) {
            return R.layout.cell_search;
        }

        @Override
        protected ViewHolder<GroupCard> onCreateViewHolder(View root, int viewType) {
            return new SearchGroupFragment.ViewHolder(root);
        }
    }



    public class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCard> {

        @BindView(R.id.portrait)
        PortraitView mPortraitView;

        @BindView(R.id.im_follow)
        ImageView imFollow;

        @BindView(R.id.tv_name)
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(GroupCard groupCard) {
            mPortraitView.setup(Glide.with(SearchGroupFragment.this), groupCard.getPicture());
            imFollow.setEnabled(groupCard.getJoinAt() == null);
            tvName.setText(groupCard.getName());
        }

        @OnClick(R.id.im_follow)
        void onJoinClick() {
            PersonalActivity.show(getContext(), mData.getOwnerId());
        }
    }
}
