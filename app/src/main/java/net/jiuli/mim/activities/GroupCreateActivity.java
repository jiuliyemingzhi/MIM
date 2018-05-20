package net.jiuli.mim.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.jiuli.common.app.Application;
import net.jiuli.common.app.PresenterToolbarActivity;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.presenter.group.GroupCreateContact;
import net.jiuli.factoylib.presenter.group.GroupCreatePresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.fragments.media.GalleryFragment;
import net.qiujuer.genius.ui.compat.UiCompat;

import java.io.File;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class GroupCreateActivity extends PresenterToolbarActivity<GroupCreateContact.Presenter> implements GroupCreateContact.View {

    @BindView(R.id.recycler_user)
    RecyclerView mRecyclerView;

    @BindView(R.id.et_name)
    EditText etName;

    @BindView(R.id.et_desc)
    EditText etDesc;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    private String mPortraitPath;

    private Adapter mAdapter;


    public static void show(Context context) {
        context.startActivity(new Intent(context, GroupCreateActivity.class));
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        hideSoftKeyBoard();
        new GalleryFragment()
                .setCheckBoxIsGone(true)
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void imageSelectChanged(String path) {
                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        options.setCompressionQuality(75);
                        File dirFile = Application.getPortraitTmpFile();
                        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dirFile))
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(1024, 1024)
                                .withOptions(options)
                                .start(GroupCreateActivity.this);
                    }
                }).show(getSupportFragmentManager(), GalleryFragment.class.getSimpleName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            // 通过UCrop得到对应的Uri
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Application.showToast(R.string.data_rsp_error_unknown);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_create, menu);
        MenuItem item = menu.findItem(R.id.action_create);
        Drawable icon = item.getIcon();
        icon = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(icon, UiCompat.getColor(getResources(), R.color.white));
        item.setIcon(icon);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create) {
            onCreateClick();
        }
        return super.onOptionsItemSelected(item);

    }

    private void loadPortrait(Uri resultUri) {
        mPortrait.setup(Glide.with(this), mPortraitPath = resultUri.getPath());
    }

    private void hideSoftKeyBoard() {
        View view = getCurrentFocus();
        if (view == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    protected GroupCreateContact.Presenter initPresenter() {
        return new GroupCreatePresenter(this);
    }

    @Override
    public void onCreateSucceed() {
        hideLoading();
        Application.showToast(R.string.label_group_create_succeed);
        finish();
    }

    @Override
    public RecyclerAdapter<GroupCreateContact.ViewModel> getRecyclerViewAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChange() {
        hideLoading();
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
        setTitle("");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new Adapter());
    }

    private void onCreateClick() {
        hideSoftKeyBoard();
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        mPresenter.create(name, desc, mPortraitPath);
    }


    class Adapter extends RecyclerAdapter<GroupCreateContact.ViewModel> {

        @Override
        public int getItemViewType(int position, GroupCreateContact.ViewModel model) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<GroupCreateContact.ViewModel> onCreateViewHolder(View root, int viewType) {
            return new GroupCreateActivity.ViewHolder(root);
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
            if (model.author == null) {
                return;
            }
            mPortraitView.setup(Glide.with(GroupCreateActivity.this), model.author.getPortrait());
            tvName.setText(model.author.getName());
            checkBox.setChecked(model.isSelected);
        }

        @OnCheckedChanged(R.id.cb_select)
        void onSelectChanged(boolean checked) {
            mPresenter.changeSelect(mData, checked);
        }

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }
}
