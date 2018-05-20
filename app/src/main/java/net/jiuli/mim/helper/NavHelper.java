package net.jiuli.mim.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;


/**
 * Created by jiuli on 17-8-31.
 */

public class NavHelper<T> {
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();
    private Tab<T> currentTab;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final int containerId;
    private OnTabChangeListener listener;


    public NavHelper(Context context, FragmentManager fragmentManager, int containerId) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }


    public NavHelper<T> add(int menuId, Class<? extends Fragment> fragmentClass) {
        tabs.put(menuId, new Tab(fragmentClass));
        return this;
    }

    public NavHelper<T> add(int menuId, Class<? extends Fragment> fragmentClass, T extra) {
        tabs.put(menuId, new Tab(fragmentClass, extra));
        return this;
    }


    public boolean performClickMenu(int menuId) {

        Tab<T> tab = tabs.get(menuId);


        if (tab != null) {
            doSelect(tab);
            return true;
        }

        return false;
    }


    private void doRefresh(Tab<T> tab) {

    }


    private void doSelect(Tab<T> newTab) {
        Tab<T> oldTab = null;

        if (currentTab != null) {
            oldTab = currentTab;
        }
        if (currentTab == newTab) {
            doRefresh(newTab);
            return;
        } else {
            currentTab = newTab;
            doSelectChange(newTab, oldTab);
        }

    }

    private void doSelectChange(Tab<T> newTab, Tab<T> oldTab) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (oldTab != null && oldTab.fragment != null) {
            fragmentTransaction.detach(oldTab.fragment);
        }
        if (newTab.fragment != null) {
            fragmentTransaction.attach(newTab.fragment);
        } else {
            Fragment fragment = Fragment.instantiate(context, newTab.fragmentClass.getName());
            newTab.fragment = fragment;
            fragmentTransaction.add(containerId, fragment, newTab.getClass().getName());
        }
        fragmentTransaction.commit();
        notifyTabChanged(newTab, oldTab);
    }

    private void notifyTabChanged(Tab<T> newTab, Tab<T> oldTab) {
        if (listener != null) {
            listener.onTabChange(newTab, oldTab);
        }
    }

    public void setListener(OnTabChangeListener listener) {
        this.listener = listener;
    }

    public static class Tab<T> {
        Class<? extends Fragment> fragmentClass;
        Fragment fragment;
        T extra;

        public Tab(Class<? extends Fragment> fragmentClass, T extra) {
            this.fragmentClass = fragmentClass;
            this.extra = extra;
        }

        public T getExtra() {
            return extra;
        }

        public Tab(Class<? extends Fragment> fragmentClass) {
            this.fragmentClass = fragmentClass;
        }
    }


    public interface OnTabChangeListener<T> {
        void onTabChange(Tab<T> newTab, Tab<T> oldTab);
    }


//    private final Context context;
//
//    private final FragmentManager fragmentManager;
//
//    private final int containerId;
//
//    private final SparseArray<Tab<T>> tabs = new SparseArray();
//
//    private final OnTabChangeListener listener;
//
//    private Tab<T> currentTab;
//
//
//    public NavHelper(Context context, FragmentManager fragmentManager, int containerId, OnTabChangeListener listener) {
//        this.context = context;
//        this.fragmentManager = fragmentManager;
//        this.containerId = containerId;
//        this.listener = listener;
//    }
//
//
//    public NavHelper<T> add(int menuId, Tab<T> tab) {
//        tabs.put(menuId, tab);
//        return this;
//    }
//
//
//    public Tab<T> getCurrentTab() {
//        return currentTab;
//    }
//
//
//    public boolean performClickMenu(int menuId) {
//        Tab<T> tTab = tabs.get(menuId);
//        if (tTab != null) {
//            doSelect(tTab);
//            return true;
//        }
//        return false;
//    }
//
//    private void doSelect(Tab<T> tab) {
//        Tab<T> oldTab = null;
//        if (currentTab != null) {
//            oldTab = currentTab;
//        }
//        if (oldTab == tab) {
//            notifyReselect(tab);
//            return;
//        } else {
//            currentTab = tab;
//            doTabChange(currentTab, oldTab);
//        }
//    }
//
//    private void doTabChange(Tab<T> newTab, Tab<T> oldTab) {
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        if (oldTab != null && oldTab.fragment != null) {
//            ft.detach(oldTab.fragment);
//        }
//        if (newTab != null) {
//            if (newTab.fragment == null) {
//                Fragment fragment = Fragment.instantiate(context, newTab.aClass.getName(), null);
//                newTab.fragment = fragment;
//                ft.add(containerId, fragment, newTab.aClass.getName());
//            } else {
//                ft.attach(newTab.fragment);
//            }
//        }
//        ft.commit();
//    }
//
//    private void notifyReselect(Tab<T> tab) {
//        //TODO
//
//    }
//
//    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab) {
//        if (listener != null) {
//            listener.onTabChanged(newTab, oldTab);
//        }
//    }
//
//
//    public static class Tab<T> {
//        public Class<? extends Fragment> aClass;
//        public T extra;
//
//        public Tab(Class<? extends Fragment> aClass, T extra) {
//            this.aClass = aClass;
//            this.extra = extra;
//        }
//
//        Fragment fragment;
//
//    }
//
//
//    public interface OnTabChangeListener<T> {
//        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
//    }

}
