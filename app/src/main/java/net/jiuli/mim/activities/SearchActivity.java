package net.jiuli.mim.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.jiuli.common.app.Activity;
import net.jiuli.common.app.Application;
import net.jiuli.common.app.Fragment;
import net.jiuli.common.app.ToolbarActivity;
import net.jiuli.mim.R;
import net.jiuli.mim.fragments.search.SearchGroupFragment;
import net.jiuli.mim.fragments.search.SearchUserFragment;

import butterknife.BindView;

public class SearchActivity extends ToolbarActivity {

    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int TYPE_USER = 1;
    public static final int TYPE_GROUP = 2;
    //具体显示的类型
    private int type;

    private SearchFragment mSearchFragment;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search;
    }

    /**
     * 显示搜索界面
     *
     * @param context 上下文
     * @param type    显示类型 ,用户还是群
     */
    public static void show(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        String tag = "TYPE参数错误";
        switch (type) {
            case TYPE_USER:
                mSearchFragment  = new SearchUserFragment();
                tag = SearchUserFragment.class.getSimpleName();
                break;
            case TYPE_GROUP:
                mSearchFragment = new SearchGroupFragment();
                tag = SearchGroupFragment.class.getSimpleName();
                break;
            default:
                Application.showToast(tag);
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_container, (Fragment) mSearchFragment,tag)
        .commit();
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        type = bundle.getInt("EXTRA_TYPE");
        return type == TYPE_GROUP || type == TYPE_USER;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setScaleX(1);
        searchView.setScaleY(1);
        if (searchView != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (!TextUtils.isEmpty(s)) {
                        search(s);
                    }else {

                    }
                    return true;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void search(String query) {
        if (mSearchFragment != null) {
            mSearchFragment.search(query);
        }
    }

    public interface SearchFragment {
        void search(String content);
    }
}
