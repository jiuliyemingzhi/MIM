package net.jiuli.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;


/**
 * Created by jiuli on 17-8-25.
 */

public class UiTool {
    private static int STATUS_BAR_HEIGHT = -1;

    public static int getStatusBarHeight(Activity activity) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && STATUS_BAR_HEIGHT == -1) {
            Resources resources = activity.getResources();
            int resId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resId <= 0) {
                try {
                    Class<?> dimenClass = Class.forName("com.android.internal.R$dimen");
                    Object instance = dimenClass.newInstance();
                    resId = Integer.parseInt(dimenClass.getField("status_bar_height").get(instance).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (resId > 0) {
                STATUS_BAR_HEIGHT = resources.getDimensionPixelSize(resId);
            }
            if (STATUS_BAR_HEIGHT <= 0) {
                Rect rect = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                STATUS_BAR_HEIGHT = rect.top;
            }
        }
        return STATUS_BAR_HEIGHT;
    }

    public static int getScreenWidth(Context context) {
//        return  activity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
//        return activity.getWindow().getDecorView().getDisplay().getWidth()
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static KeyEvent getDelKeyEvent() {
        return new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0
                , 0, 0, KeyEvent.KEYCODE_UNKNOWN, KeyEvent.KEYCODE_ENDCALL);
    }

    public static void setRecyclerViewItemAnimationDuration(RecyclerView recyclerView) {
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        animator.setRemoveDuration(50);
        animator.setChangeDuration(0);
        animator.setAddDuration(50);
    }
}
