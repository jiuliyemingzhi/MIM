package net.jiuli.mim.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.jiuli.common.app.Activity;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.factoylib.persistence.Account;
import net.jiuli.mim.R;
import net.jiuli.mim.fragments.main.ActiveFragment;
import net.jiuli.mim.fragments.main.ContactFragment;
import net.jiuli.mim.fragments.main.GroupFragment;
import net.jiuli.mim.helper.NavHelper;
import net.qiujuer.genius.ui.Ui;

import java.nio.file.Path;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity implements
        BottomNavigationView.OnNavigationItemSelectedListener
        , NavHelper.OnTabChangeListener<Integer> {
    @BindView(R.id.appbar)
    View mAppBar;

    @BindView(R.id.im_portrait)
    PortraitView mPortraitView;

    @BindView(R.id.navigation_bar)
    BottomNavigationView mNavigationBar;

    @BindView(R.id.float_btn_action)
    FloatingActionButton mFloatActionBtn;

    @BindView(R.id.frame_container)
    FrameLayout mFrameContainer;

    @BindView(R.id.text_title)
    TextView mTextTitle;


    private NavHelper mNavHelper;


    @Override
    protected void initWidget() {

        super.initWidget();
        Glide.with(this).load(R.drawable.bg_src_morning).centerCrop().into(new ViewTarget<View, GlideDrawable>(mAppBar) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                this.view.setBackground(resource.getCurrent());
            }
        });
        mNavigationBar.setOnNavigationItemSelectedListener(this);
        mNavHelper.setListener(this);
        mNavigationBar.setSelectedItemId(R.id.action_home);
        mPortraitView.setup(Glide.with(this), Account.getUser().getPortrait());
    }

    @Override
    protected void initData() {
        mNavHelper = new NavHelper<Integer>(this, getSupportFragmentManager(), R.id.frame_container);
        mNavHelper
                .add(R.id.action_home, ActiveFragment.class, R.string.action_home)
                .add(R.id.action_group, GroupFragment.class, R.string.action_group)
                .add(R.id.action_contact, ContactFragment.class, R.string.action_contact);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if (Account.isComplete()) {
            return super.initArgs(bundle);
        } else {
            UserActivity.show(this);
            return false;
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }


    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        switch (mNavigationBar.getSelectedItemId()) {
            case R.id.action_contact:
                SearchActivity.show(this, SearchActivity.TYPE_USER);
                break;
            case R.id.action_group:
                SearchActivity.show(this, SearchActivity.TYPE_GROUP);
                break;
            default:

                break;
        }
    }

    @OnClick(R.id.float_btn_action)
    void onActionBtnClick() {
        switch (mNavigationBar.getSelectedItemId()) {
            case R.id.action_contact:
                SearchActivity.show(this, SearchActivity.TYPE_USER);
                break;
            case R.id.action_group:
                GroupCreateActivity.show(this);
                break;
            default:

                break;
        }
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        PersonalActivity.show(this, Account.getUserId());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return mNavHelper.performClickMenu(item.getItemId());
    }

    @Override
    public void onTabChange(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        mTextTitle.setText(newTab.getExtra());
        float transY = 0;
        float rotation = 0;

        switch (newTab.getExtra()) {
            case R.string.action_home:
                transY = Ui.dipToPx(getResources(), 68);
                break;
            case R.string.action_group:
                rotation = 360;
                mFloatActionBtn.setImageResource(R.drawable.ic_group_add);
                break;
            case R.string.action_contact:
                rotation = -360;
                mFloatActionBtn.setImageResource(R.drawable.ic_contact_add);
                break;

        }
        floatBtnShowAndHide(transY, rotation);
    }

    private void floatBtnShowAndHide(float transY, float rotation) {
        mFloatActionBtn.animate()
                .translationY(transY)
                .rotation(rotation)
                .setDuration(600)
                .setInterpolator(new AnticipateInterpolator(1))
                .start();
    }

    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
