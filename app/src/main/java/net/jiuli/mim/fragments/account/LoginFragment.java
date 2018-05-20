package net.jiuli.mim.fragments.account;


import android.content.Context;
import android.widget.EditText;
import android.widget.FrameLayout;

import net.jiuli.common.app.Fragment;
import net.jiuli.common.app.PresenterFragment;
import net.jiuli.factoylib.presenter.account.LoginContract;
import net.jiuli.factoylib.presenter.account.LoginPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.MainActivity;
import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends PresenterFragment<LoginContract.Presenter>
        implements LoginContract.View {

    @BindView(R.id.et_input_phone)
    EditText etPhoneInput;

    @BindView(R.id.et_input_password)
    EditText etPasswordInput;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.frame_go_register)
    FrameLayout mFrameGoRegister;

    private AccountTrigger mAccountTrigger;


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAccountTrigger = (AccountTrigger) context;
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.frame_go_register)
    void onClick() {
        mAccountTrigger.triggerView();
    }

    @Override
    public void loginSuccess() {
        MainActivity.show(getContext());
        getActivity().finish();
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String phone = etPhoneInput.getText().toString();
        String password = etPasswordInput.getText().toString();
        mPresenter.login(phone, password);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mLoading.start();
        etPasswordInput.setEnabled(false);
        etPhoneInput.setEnabled(false);
        btnSubmit.setEnabled(false);
    }

    @Override
    public void showError(int error) {
        super.showError(error);
        mLoading.stop();
        etPasswordInput.setEnabled(true);
        etPhoneInput.setEnabled(true);
        btnSubmit.setEnabled(true);
    }
}
