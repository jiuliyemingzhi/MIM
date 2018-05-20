package net.jiuli.mim.fragments.main;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.jiuli.common.app.Fragment;
import net.jiuli.common.app.PresenterFragment;
import net.jiuli.common.widget.EmptyView;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.presenter.group.GroupsContact;
import net.jiuli.factoylib.presenter.group.GroupsPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.MessageActivity;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends PresenterFragment<GroupsContact.Presenter> implements GroupsContact.View {

    @BindView(R.id.empty)
    EmptyView mEmpty;

    @BindView(R.id.recycler_group)
    RecyclerView mRecyclerView;

    private Adapter adapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected GroupsContact.Presenter initPresenter() {
        return new GroupsPresenter(this);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(adapter = new Adapter());
        adapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Group>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Group group, int position) {
                MessageActivity.show(getContext(), group);
            }
        });
        mEmpty.bind(mRecyclerView);
        setPlaceHolderView(mEmpty);
    }

    @Override
    public RecyclerAdapter<Group> getRecyclerViewAdapter() {
        return adapter;
    }

    @Override
    public void onAdapterDataChange() {
        mPlaceHolderView.triggerOkOrEmpty(adapter.getItemCount() > 0);
    }

    class Adapter extends RecyclerAdapter<Group> {

        @Override
        public int getItemViewType(int position, Group group) {
            return R.layout.cell_group_list;
        }

        @Override
        protected ViewHolder onCreateViewHolder(View root, int viewType) {
            return new GroupFragment.ViewHolder(root);
        }
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Group> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_desc)
        TextView tvDesc;

        @BindView(R.id.tv_member)
        TextView tvMember;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Group group) {
            mPortraitView.setup(Glide.with(GroupFragment.this), group.getPicture());
            tvName.setText(group.getName());
            tvDesc.setText(group.getDesc());

            if (group.getHolder() != null && group.getHolder() instanceof String) {
                tvMember.setText((String) group.getHolder());
            } else {
                tvMember.setText("");
            }
        }

    }
}
