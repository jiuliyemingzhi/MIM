package net.jiuli.mim.fragments.group;


import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.jiuli.common.widget.PortraitView;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.model.Author;
import net.jiuli.factoylib.presenter.group.GroupCreateContact;
import net.jiuli.factoylib.presenter.group.GroupMemberAddContact;
import net.jiuli.factoylib.presenter.group.GroupMemberAddPresenter;
import net.jiuli.mim.R;
import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class GroupMemberAddFragment extends BottomSheetDialogFragment implements GroupMemberAddContact.View {

    @BindView(R.id.recycler_member)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private Callback callback;

    private GroupMemberAddContact.Presenter presenter;

    private Adapter adapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext());
    }

    @Override
    public void showError(int error) {
        if (callback != null) {
            callback.showError(error);
        }
    }


    @Override
    public void setPresenter(GroupMemberAddContact.Presenter presenter) {
        this.presenter = presenter;
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        new GroupMemberAddPresenter(this);
        View inflate = inflater.inflate(R.layout.fragment_group_member_add, container, false);
        ButterKnife.bind(this, inflate);
        initRecyclerView();
        initToolbar();
        return inflate;
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.group_create);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_create) {
                    presenter.submit();
                    return true;
                }
                return false;
            }
        });
        MenuItem item = toolbar.getMenu().findItem(R.id.action_create);
        Drawable icon = item.getIcon();
        icon = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(icon, UiCompat.getColor(getResources(), R.color.textPrimary));
        item.setIcon(icon);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter = new Adapter());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.destroy();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.start();
    }

    @Override
    public void showLoading() {
        if (callback != null) {
            callback.showLoading();
        }
    }

    @Override
    public void onAddedSucceed() {
        if (callback != null) {
            callback.hideLoading();
            callback.refreshMembers();
        }
        dismiss();
    }

    class Adapter extends RecyclerAdapter<GroupCreateContact.ViewModel> {

        @Override
        public int getItemViewType(int position, GroupCreateContact.ViewModel viewModel) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<GroupCreateContact.ViewModel> onCreateViewHolder(View root, int viewType) {
            return new GroupMemberAddFragment.ViewHolder(root);
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCreateContact.ViewModel> {


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
        protected void onBind(GroupCreateContact.ViewModel model) {
            Author author;
            if ((author = model.author) == null) {
                return;
            }

            mPortraitView.setup(Glide.with(GroupMemberAddFragment.this), author.getPortrait());
            tvName.setText(author.getName());
            checkBox.setChecked(model.isSelected);
        }

        @OnCheckedChanged(R.id.cb_select)
        void onSelectChanged(boolean checked) {
            presenter.changeSelect(mData, checked);
        }

    }


    @Override
    public String getGroupId() {
        return callback.getGroupId();
    }

    @Override
    public RecyclerAdapter<GroupCreateContact.ViewModel> getRecyclerViewAdapter() {
        return adapter;
    }

    @Override
    public void onAdapterDataChange() {
        if (callback != null) {
            callback.hideLoading();
        }
    }

    public interface Callback {
        String getGroupId();

        void hideLoading();

        void showError(@StringRes int resStr);

        void showLoading();

        void refreshMembers();
    }
}
