package net.jiuli.mim;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Property;
import android.view.View;

import net.jiuli.common.app.Activity;
import net.jiuli.common.app.Application;
import net.jiuli.factoylib.persistence.Account;
import net.jiuli.mim.activities.AccountActivity;
import net.jiuli.mim.activities.MainActivity;
import net.qiujuer.genius.ui.compat.UiCompat;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class LaunchActivity extends Activity implements EasyPermissions.PermissionCallbacks {


    private ColorDrawable mBgDrawable;

    private static final String TAG = "PERMISSIONS";

    private final int REQUEST_CODE = 0x100;

    private final static String[] PERMISSIONS = {
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO

    };


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                waitPushReceiverId();
            }
        });

    }

    private void waitPushReceiverId() {
        if (Account.isLogin()) {
            if (Account.isBind()) {
                skip();
                return;
            }
        } else {

            if (!TextUtils.isEmpty(Account.getPushId())) {
                skip();
                return;
            }
        }
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitPushReceiverId();
                    }
                }, 500);

    }


    private void skip() {
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                reallySkip();
            }
        });
    }

    private void reallySkip() {
        if (haveAll()) {
            if (Account.isLogin()) {
                MainActivity.show(this);
            } else {
                AccountActivity.show(this);
            }
            finish();
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        View root = findViewById(R.id.linear_launch_root);
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        ColorDrawable drawable = new ColorDrawable(color);
        root.setBackground(drawable);
        mBgDrawable = drawable;
    }

    /**
     * 给背景设置一个动画
     *
     * @param endProgress 动画的结束进度
     * @param endCallback 动画结束时触发
     */
    private void startAnim(float endProgress, final Runnable endCallback) {
        int finalColor = Color.WHITE;
        //运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        ObjectAnimator animator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        animator.setDuration(500);
        animator.setIntValues(mBgDrawable.getColor(), endColor);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                endCallback.run();
            }
        });
        animator.start();
    }


    private final Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public Object get(LaunchActivity launchActivity) {
            return launchActivity.mBgDrawable.getColor();
        }

        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }
    };


    public boolean haveAll() {
        boolean haveAll = haveAudioPerm()
                && haveNetworkPerm()
                && haveWritePerm()
                && haveReadPerm();
        if (!haveAll) {
            requestPermission();
        }
        return haveAll;
    }


    public void requestPermission() {
        if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            Application.showToast(R.string.label_permission_ok);
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.title_assist_permissions),
                    REQUEST_CODE, PERMISSIONS);
        }
    }

    private boolean haveNetworkPerm() {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        return EasyPermissions.hasPermissions(this, perms);
    }


    private boolean haveWritePerm() {
        return EasyPermissions.hasPermissions(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private boolean haveReadPerm() {
        return EasyPermissions.hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private boolean haveAudioPerm() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.RECORD_AUDIO);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
