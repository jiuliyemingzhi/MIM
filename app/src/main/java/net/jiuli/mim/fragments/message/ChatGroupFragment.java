package net.jiuli.mim.fragments.message;


import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.jiuli.common.widget.PortraitView;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.view.MemberUserModel;
import net.jiuli.factoylib.presenter.message.ChatContract;
import net.jiuli.factoylib.presenter.message.ChatGroupPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.GroupMemberActivity;
import net.jiuli.mim.activities.PersonalActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {



    private List<MemberUserModel> mUserModels;

    private Adapter mSampleAdapter;


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this, mReceiverId);
    }

    @Override
    public void onInit(Group group) {
        if (group == null) {
            return;
        }

        mRecyclerMember.setLayoutManager(new LinearLayoutManager(getContext(), GridLayoutManager.HORIZONTAL, false));
        tvTitle.setText(group.getName());

    }

    @Override
    public void showAdminOption(final boolean isAdmin) {
        ivMenu.setVisibility(View.VISIBLE);
        ivMenu.setImageResource(R.drawable.ic_group);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupMemberActivity.show(getContext(), mReceiverId, isAdmin);
            }
        });
    }


    @OnClick(R.id.frame_bar)
    void onFrameBarClick() {
        if (mSampleAdapter == null) {
            if (mUserModels == null || mUserModels.isEmpty()) {
                return;
            }
            mSampleAdapter = new Adapter();
            mSampleAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<MemberUserModel>() {
                @Override
                public void onItemClick(RecyclerAdapter.ViewHolder holder, MemberUserModel memberUserModel, int position) {
                    PersonalActivity.show(getContext(), memberUserModel.userId);
                }
            });
            mSampleAdapter.replace(mUserModels);
            mRecyclerMember.setAdapter(mSampleAdapter);


        }
        mRecyclerMember.setVisibility(getRecyclerIsShow() ? View.GONE : View.VISIBLE);

    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onInitGroupMembers(List<MemberUserModel> userModels, long count) {
        this.mUserModels = userModels;
        long mCount = count;
        if (userModels != null && !userModels.isEmpty()) {
            tvSubTitle.setText(mCount + " 人");
        }
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<MemberUserModel> {
        @BindView(R.id.portrait)
        PortraitView portrait;

        @BindView(R.id.tv_alias)
        TextView tvAlias;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(MemberUserModel model) {
            portrait.setup(Glide.with(ChatGroupFragment.this), model.portrait);
            tvAlias.setText(model.alias);
        }
    }


    class Adapter extends RecyclerAdapter<MemberUserModel> {

        @Override
        public int getItemViewType(int position, MemberUserModel memberUserModel) {
            return R.layout.cell_member_sample;
        }

        @Override
        protected ViewHolder<MemberUserModel> onCreateViewHolder(View root, int viewType) {
            return new ChatGroupFragment.ViewHolder(root);
        }
    }
}