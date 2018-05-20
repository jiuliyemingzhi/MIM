package net.jiuli.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import net.jiuli.common.widget.convention.PlaceHolderView;

import java.util.List;

import butterknife.ButterKnife;


/**
 * // Created by jiuli on 17-8-16.
 */

public abstract class Activity extends AppCompatActivity {
    protected PlaceHolderView mPlaceHolderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //界面未初始化的时候调用初始化窗口
        initWindow();
        if (initArgs(getIntent().getExtras())) {
            setContentView(getContentLayoutId());
            initData();
            ButterKnife.bind(this);
            initWidget();
        } else {
            finish();
        }
    }

    /**
     * 初始化窗口
     */
    protected void initWindow() {

    }

    /**
     * 初始化相关参数
     *
     * @param bundle 　参数Bundle
     * @return 参数正确返回True ,错误返回　false
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    protected abstract int getContentLayoutId();

    protected void initWidget() {

    }

    protected void initData() {

    }


    @Override
    public boolean onSupportNavigateUp() {
        //当点击见面导航返回时finish当前界面．
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {

        List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof net.jiuli.common.app.Fragment) {
                    if (((net.jiuli.common.app.Fragment) fragment).onBackPressed()) {
                        return;
                    } else {

                    }
                }
            }
        }
        super.onBackPressed();
        finish();
    }

    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
    }
}
