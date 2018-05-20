package net.jiuli.mim.assist;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jiuli.common.app.Application;
import net.jiuli.mim.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 权限申请弹出框
 */
public class PermissionsFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "PERMISSIONS";
    private final int REQUEST_CODE = 0x100;
    private View mRoot;
//    @BindView(R.id.nest_permissions_scroll_view)
//    NestedScrollView mNestScrollView;

    private final static String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO

    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext());
    }


    public PermissionsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.fragment_permissions, container, false);
        } else {
            if (mRoot.getParent() != null) {
                ((ViewGroup) mRoot.getParent()).removeView(mRoot);
            }
        }
        ButterKnife.bind(this, mRoot);
        refreshState();
        return mRoot;
    }

    private void refreshState() {
        mRoot.findViewById(R.id.im_permission_network).setVisibility(haveNetworkPerm(getContext()) ? View.VISIBLE : View.GONE);
        mRoot.findViewById(R.id.im_permission_read).setVisibility(haveWritePerm(getContext()) ? View.VISIBLE : View.GONE);
        mRoot.findViewById(R.id.im_permission_write).setVisibility(haveReadPerm(getContext()) ? View.VISIBLE : View.GONE);
        mRoot.findViewById(R.id.im_permission_audio).setVisibility(haveAudioPerm(getContext()) ? View.VISIBLE : View.GONE);
    }

    private static boolean haveNetworkPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveWritePerm(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static boolean haveReadPerm(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private static boolean haveAudioPerm(Context context) {
        return EasyPermissions.hasPermissions(context, Manifest.permission.RECORD_AUDIO);
    }

    private static void show(FragmentManager manager) {
        new PermissionsFragment().show(manager, PermissionsFragment.class.getName());
    }

    /**
     * 检查是否有权限
     *
     * @param context
     * @param manager
     * @return
     */

    public static boolean haveAll(Context context, FragmentManager manager) {
        boolean haveAll = haveAudioPerm(context)
                && haveNetworkPerm(context)
                && haveWritePerm(context)
                && haveReadPerm(context);
        if (!haveAll) {
            show(manager);
        }
        return haveAll;
    }

    @OnClick(R.id.btn_submit)
    @AfterPermissionGranted(REQUEST_CODE)
    public void requestPermission() {
        if (EasyPermissions.hasPermissions(getContext(), PERMISSIONS)) {
            Application.showToast(R.string.label_permission_ok);
            refreshState();
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.title_assist_permissions),
                    REQUEST_CODE, PERMISSIONS);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Application.showToast(R.string.label_permission_ok);
            dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


}
