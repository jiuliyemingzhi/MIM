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
import net.jiuli.factoylib.model.card.UserCard;
import net.jiuli.factoylib.presenter.contact.FollowContact;
import net.jiuli.factoylib.presenter.contact.FollowPresenter;
import net.jiuli.factoylib.presenter.search.SearchContract;
import net.jiuli.factoylib.presenter.search.SearchUserPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.PersonalActivity;
import net.jiuli.mim.activities.SearchActivity;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment, SearchContract.UserView {

    @BindView(R.id.user_empty_view)
    EmptyView mEmptyView;
    @BindView(R.id.user_search_recycler)
    RecyclerView mRecyclerSearch;

    private RecyclerAdapter<UserCard> mAdapter;

    public SearchUserFragment() {

    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerSearch.setAdapter(mAdapter = new Adapter());
        mEmptyView.bind(mRecyclerSearch);
        setPlaceHolderView(mEmptyView);
        search("");
    }

    private class Adapter extends RecyclerAdapter<UserCard> {

        @Override
        public int getItemViewType(int position, UserCard userCard) {
            return R.layout.cell_search;
        }

        @Override
        protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
            return new SearchUserFragment.ViewHolder(root);
        }
    }

    public class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard> implements FollowContact.View {
        @BindView(R.id.portrait)
        PortraitView mPortraitView;
        @BindView(R.id.im_follow)
        ImageView imFollow;
        @BindView(R.id.tv_name)
        TextView tvName;

        private FollowContact.Presenter mPresenter;

        ViewHolder(View itemView) {
            super(itemView);
            new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            Glide.with(SearchUserFragment.this)
                    .load(userCard.getPortrait())
                    .centerCrop()
                    .into(mPortraitView);
            imFollow.setEnabled(!userCard.isFollow());
            tvName.setText(userCard.getName());
        }

        @Override
        public void showError(int error) {
            if (imFollow.getDrawable() instanceof LoadingDrawable) {
                LoadingDrawable drawable = (LoadingDrawable) imFollow.getDrawable();
                drawable.setProgress(1);
                drawable.stop();
            }
        }


        @Override
        public void setPresenter(FollowContact.Presenter presenter) {
            this.mPresenter = presenter;
        }

        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(), 22);
            int maxSize = (int) Ui.dipToPx(getResources(), 30);
            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            drawable.setBackgroundColor(0);
            int color = UiCompat.getColor(getResources(), R.color.white_alpha_208);
            drawable.setForegroundColor(new int[]{color});
            imFollow.setImageDrawable(drawable);
            drawable.start();
        }

        @Override
        public void onFollowSucceed(UserCard userCard) {
            if (imFollow.getDrawable() instanceof LoadingDrawable) {
                ((LoadingDrawable) imFollow.getDrawable()).stop();
                imFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
            updateData(userCard);
        }

        @OnClick(R.id.portrait)
        void portraitClick() {
            PersonalActivity.show(getContext(), mData.getId());
        }

        @OnClick(R.id.im_follow)
        void followClick() {
            mPresenter.follow(mData.getId());
        }

    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchUserPresenter(this);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        mAdapter.replace(userCards);
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

}
