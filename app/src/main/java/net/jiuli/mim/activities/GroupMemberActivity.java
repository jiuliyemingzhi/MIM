package net.jiuli.mim.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.jiuli.common.app.PresenterToolbarActivity;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.model.db.view.MemberUserModel;
import net.jiuli.factoylib.presenter.group.GroupMembersContact;
import net.jiuli.factoylib.presenter.group.GroupMembersPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.fragments.group.GroupMemberAddFragment;

import butterknife.BindView;


public class GroupMemberActivity extends PresenterToolbarActivity<GroupMembersContact.Presenter>
        implements GroupMembersContact.View, GroupMemberAddFragment.Callback {

    @BindView(R.id.recycler_member)
    RecyclerView recyclerView;

    private String groupId;

    private boolean isAdmin;

    private Adapter adapter;

    private static final String GROUP_ID = "GROUP_ID";

    private static final String IS_ADMIN = "IS_ADMIN";

    public static void show(Context context, String groupId, boolean isAdmin) {
        Intent intent = new Intent(context, GroupMemberActivity.class);
        intent.putExtra(GROUP_ID, groupId);
        intent.putExtra(IS_ADMIN, isAdmin);
        context.startActivity(intent);
    }

    public static void show(Context context, String groupId) {
        show(context, groupId, false);
    }


    public static void showAdmin(Context context, String groupId) {
        show(context, groupId, true);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        groupId = bundle.getString(GROUP_ID);
        isAdmin = bundle.getBoolean(IS_ADMIN);
        return !TextUtils.isEmpty(groupId);
    }

    @Override
    protected void initData() {
        super.initData();
        if (mPresenter != null) {
            mPresenter.start();
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new Adapter());
        adapter.setListener(new RecyclerAdapter.AdapterListenerImpl<MemberUserModel>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, MemberUserModel memberUserModel, int position) {
                PersonalActivity.show(GroupMemberActivity.this, memberUserModel.userId);
            }
        });
        mPresenter.refresh();
        setTitle("成员");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isAdmin) {
            getMenuInflater().inflate(R.menu.group_member_add, menu);

            MenuItem item = menu.findItem(R.id.member_add);
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    GroupMemberAddFragment fragment = new GroupMemberAddFragment();
                    fragment.setCallback(GroupMemberActivity.this);
                    fragment.show(getSupportFragmentManager(), GroupMemberAddFragment.class.getSimpleName());
                    return true;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    public void refreshMembers() {
        if (mPresenter != null) {
            mPresenter.refresh();
        }
    }

    @Override
    public RecyclerAdapter<MemberUserModel> getRecyclerViewAdapter() {
        return adapter;
    }

    @Override
    public void onAdapterDataChange() {
        hideLoading();
    }


    class Adapter extends RecyclerAdapter<MemberUserModel> {

        @Override
        public int getItemViewType(int position, MemberUserModel model) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<MemberUserModel> onCreateViewHolder(View root, int viewType) {
            return new GroupMemberActivity.ViewHolder(root);
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<MemberUserModel> {


        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.cb_select)
        CheckBox checkBox;


        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(MemberUserModel model) {
            mPortraitView.setup(Glide.with(GroupMemberActivity.this), model.portrait);
            tvName.setText(model.alias);
            checkBox.setVisibility(View.GONE);
        }
    }



    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_member;
    }

    @Override
    protected GroupMembersContact.Presenter initPresenter() {
        return new GroupMembersPresenter(this);
    }
}
