package net.jiuli.common.app;


import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.jiuli.common.R;

/**
 * Created by jiuli on 17-9-22.
 */

public abstract class ToolbarActivity extends Activity {
    protected Toolbar mToolbar;


    @Override
    protected void initWidget() {
        super.initWidget();
        initToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    public void initToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        initTitleNeedBack();
    }

    protected void initTitleNeedBack() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
