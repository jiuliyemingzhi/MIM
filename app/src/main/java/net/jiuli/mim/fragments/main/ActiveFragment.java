package net.jiuli.mim.fragments.main;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.jiuli.common.app.PresenterFragment;
import net.jiuli.common.face.Face;
import net.jiuli.common.utils.DateTimeUitl;
import net.jiuli.common.widget.EmptyView;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.model.db.Session;
import net.jiuli.factoylib.presenter.message.SessionContract;
import net.jiuli.factoylib.presenter.message.SessionPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.MessageActivity;
import net.qiujuer.genius.ui.Ui;

import butterknife.BindView;


public class ActiveFragment extends PresenterFragment<SessionContract.Presenter> implements SessionContract.View {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler_session)
    RecyclerView mRecyclerView;

    private RecyclerAdapter<Session> mAdapter;


    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new Adapter());
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session, int position) {
                MessageActivity.show(getContext(), session);
            }
        });
        mEmptyView.bind(mRecyclerView);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();

    }

    @Override
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }


    @Override
    public RecyclerAdapter<Session> getRecyclerViewAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChange() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    class Adapter extends RecyclerAdapter<Session> {

        @Override
        public int getItemViewType(int position, Session session) {
            return R.layout.cell_chat_list;
        }

        @Override
        protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
            return new ChatListViewHolder(root);
        }


    }

    class ChatListViewHolder extends RecyclerAdapter.ViewHolder<Session> {
        @BindView(R.id.portrait)
        PortraitView mPortraitView;

        @BindView(R.id.tv_content)
        TextView tvContent;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_time)
        TextView tvTime;


        ChatListViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Session session) {
            mPortraitView.setup(Glide.with(ActiveFragment.this), session.getPicture());
            tvName.setText(session.getTitle());
            Face.decode(tvContent, TextUtils.isEmpty(session.getContent()) ? "" : session.getContent(), tvContent.getTextSize(), Face.TYPE_ACTIVE);
            tvTime.setText(DateTimeUitl.getSampleDate(session.getModifyAt()));
        }
    }
}
