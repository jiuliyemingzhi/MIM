package net.jiuli.mim.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.jiuli.common.app.Activity;
import net.jiuli.common.app.Fragment;
import net.jiuli.mim.R;
import net.jiuli.mim.fragments.account.AccountTrigger;
import net.jiuli.mim.fragments.account.LoginFragment;
import net.jiuli.mim.fragments.account.RegisterFragment;
import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;

/**
 * Created by jiuli on 17-8-27.
 */

public class AccountActivity extends Activity implements AccountTrigger {
    private Fragment mCurFragment;
    private Fragment mLoginFragment;
    private Fragment mRegisterFragment;

    @BindView(R.id.im_background)
    ImageView mImBackground;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mCurFragment = mLoginFragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_container, mLoginFragment, LoginFragment.class.getSimpleName())
                .commit();

        Glide.with(this)
                .load(R.drawable.bg_src_tianjin)
                .centerCrop()
                .into(new ViewTarget<ImageView,GlideDrawable>(mImBackground) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        Drawable drawable = resource.getCurrent();
                        drawable = DrawableCompat.wrap(drawable);
                        drawable.setColorFilter(UiCompat.getColor(getResources(),R.color.colorAccent),
                                PorterDuff.Mode.SCREEN);
                        this.view.setImageDrawable(drawable);
                    }
                });
    }

    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    public void triggerView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        if (mCurFragment == mLoginFragment) {
            if (mRegisterFragment == null) {
                mRegisterFragment = new RegisterFragment();
                transaction.add(R.id.frame_container, mRegisterFragment, RegisterFragment.class.getSimpleName());
            }
            fragment = mRegisterFragment;
        } else {
            fragment = mLoginFragment;
        }
        transaction.detach(mCurFragment).attach(fragment).commit();
        mCurFragment = fragment;
    }

}
