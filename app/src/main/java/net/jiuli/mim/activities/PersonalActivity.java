package net.jiuli.mim.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.jiuli.common.app.PresenterToolbarActivity;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.presenter.contact.PersonalContact;
import net.jiuli.factoylib.presenter.contact.PersonalPresenter;
import net.jiuli.mim.R;
import net.qiujuer.genius.ui.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonalActivity extends
        PresenterToolbarActivity<PersonalContact.Presenter> implements PersonalContact.View {

    private static final String BOUND_KEY_ID = "BOUND_KEY_ID";
    private String userId;


    @BindView(R.id.personal_portrait)
    PortraitView mPortraitView;

    @BindView(R.id.im_header)
    ImageView imHeader;

    @BindView(R.id.tv_follows)
    TextView tvFollows;

    @BindView(R.id.tv_following)
    TextView tvFollowing;

    @BindView(R.id.tv_desc)
    TextView tvDesc;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.btn_say_hello)
    Button btnSayHello;

    private MenuItem mFollowItem;


    private boolean mIsFollowUser = false;

    @Override
    public String getUserId() {
        return userId;
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onLoadDone(User user) {
        hideLoading();
        if (user != null) {
            mPortraitView.setup(Glide.with(this), user.getPortrait());
            tvName.setText(user.getName());
            tvDesc.setText(user.getDesc());
            tvFollows.setText(String.format(getString(R.string.label_follows), user.getFollows()));
            tvFollowing.setText(String.format(getString(R.string.label_following), user.getFollowing()));
        }
    }

    @Override
    public void allowSayHello(boolean isAllow) {
        btnSayHello.setVisibility(isAllow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setFollowStatus(boolean isFollow) {
        mIsFollowUser = isFollow;
        changeFollowItemStatus();
    }

    @Override
    protected PersonalContact.Presenter initPresenter() {
        return new PersonalPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_personal;
    }

    public static void show(Context context, String userId) {
        Intent intent = new Intent(context, PersonalActivity.class);
        intent.putExtra(BOUND_KEY_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        this.userId = bundle.getString(BOUND_KEY_ID);
        return !TextUtils.isEmpty(userId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
        hideDialogLoading();
    }


    @OnClick(R.id.btn_say_hello)
    void sayHelloClick() {
        User user = mPresenter.getUserPersonal();
        if (user != null) {
            MessageActivity.show(this, user);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_follow) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.personal, menu);
        mFollowItem = menu.findItem(R.id.action_follow);
        changeFollowItemStatus();
        return true;
    }



    private void changeFollowItemStatus() {
        if (mFollowItem != null) {
            Drawable drawable = mIsFollowUser ? getResources().getDrawable(R.drawable.ic_favorite) :
                    getResources().getDrawable(R.drawable.ic_favorite_border);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, mIsFollowUser ? Color.RED : Color.WHITE);
            mFollowItem.setIcon(drawable);
        }
    }
}
