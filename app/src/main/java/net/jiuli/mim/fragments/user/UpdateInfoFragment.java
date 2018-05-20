package net.jiuli.mim.fragments.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.jiuli.common.app.Application;
import net.jiuli.common.app.PresenterFragment;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.factoylib.presenter.user.UpdateInfoContract;
import net.jiuli.factoylib.presenter.user.UpdateInfoPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.MainActivity;
import net.jiuli.mim.fragments.media.GalleryFragment;
import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.EditText;
import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jiuli on 17-8-27 .
 */

public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter>
        implements GalleryFragment.OnSelectedListener
        , UpdateInfoContract.View {
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.im_sex)
    ImageView imSex;

    @BindView(R.id.et_desc)
    EditText etDesc;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button btnSubmit;


    private String mPortraitPath;
    private boolean isMan = true;


    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        new GalleryFragment()
                .setListener(this)
                .setCheckBoxIsGone(true)
                .show(getChildFragmentManager(), GalleryFragment.class.getName());
    }


    @OnClick(R.id.im_sex)
    void onSexClick() {
        isMan = !isMan;
        Drawable drawable = getResources().getDrawable(isMan ? R.drawable.ic_sex_man : R.drawable.ic_sex_woman);
        imSex.setImageDrawable(drawable);
        imSex.getBackground().setLevel(isMan ? 0 : 1);
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        mPresenter.update(mPortraitPath, etDesc.getText().toString(), isMan);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                Glide.with(this).load(resultUri).centerCrop().into(mPortrait);
                mPortraitPath = resultUri.getPath();
                Log.e("TAG", "localPath" + mPortraitPath);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Application.showToast(R.string.data_rsp_error_unknown);
        }
    }

    @Override
    public void imageSelectChanged(String path) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(90);
        File dPath = Application.getPortraitTmpFile();
        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                .withAspectRatio(1, 1)
                .withMaxResultSize(1024, 1024)
                .withOptions(options)
                .start(getActivity());

    }

    public PortraitView getPortrait() {
        return mPortrait;
    }

    @Override
    public void showError(int error) {
        super.showError(error);
        mLoading.stop();
        imSex.setEnabled(true);
        btnSubmit.setEnabled(true);
        etDesc.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mLoading.start();
        imSex.setEnabled(false);
        btnSubmit.setEnabled(false);
        etDesc.setEnabled(false);
    }

    @Override
    public void updateSucceed() {
        MainActivity.show(getContext());
        getActivity().finish();
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }




}
