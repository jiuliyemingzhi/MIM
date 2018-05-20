package net.jiuli.mim.fragments.main;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.jiuli.common.app.Fragment;
import net.jiuli.common.app.PresenterFragment;
import net.jiuli.common.widget.EmptyView;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.presenter.contact.ContactContact;
import net.jiuli.factoylib.presenter.contact.ContactPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.MessageActivity;
import net.jiuli.mim.activities.PersonalActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends PresenterFragment<ContactContact.Presenter>
        implements ContactContact.View {

    @BindView(R.id.contact_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.contact_empty)
    EmptyView mEmpty;

    private RecyclerAdapter<User> mAdapter;

    public ContactFragment() {    // Required empty public constructor

    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new Adapter());
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user, int position) {
                MessageActivity.show(getContext(), user);
            }
        });
        mEmpty.bind(mRecyclerView);
        setPlaceHolderView(mEmpty);
    }


    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();
    }


    @SuppressWarnings("unchecked")
    @Override
    public RecyclerAdapter getRecyclerViewAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChange() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    @Override
    protected ContactContact.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    private class Adapter extends RecyclerAdapter<User> {

        @Override
        public int getItemViewType(int position, User user) {
            return R.layout.cell_contact_list;
        }

        @Override
        protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
            return new ContactFragment.ViewHolder(root);
        }
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<User> {

        @BindView(R.id.contact_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_desc)
        TextView tvDesc;


        @OnClick(R.id.contact_portrait)
        void portraitViewClick() {
            PersonalActivity.show(getContext(), mData.getId());
        }

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            mPortraitView.setup(Glide.with(ContactFragment.this), user.getPortrait());
            tvDesc.setText(user.getDesc());
            tvName.setText(user.getName());
        }
    }
}
