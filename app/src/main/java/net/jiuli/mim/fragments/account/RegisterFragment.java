package net.jiuli.mim.fragments.account;


import android.content.Context;
import android.widget.EditText;

import net.jiuli.common.app.PresenterFragment;
import net.jiuli.factoylib.presenter.account.RegisterContract;
import net.jiuli.factoylib.presenter.account.RegisterPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.MainActivity;
import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterFragment
        extends PresenterFragment<RegisterContract.Presenter>
        implements RegisterContract.View {

    @BindView(R.id.et_input_phone)
    EditText etPhoneInput;

    @BindView(R.id.et_input_password)
    EditText etPasswordInput;

    @BindView(R.id.et_input_name)
    EditText etNameInput;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private AccountTrigger mAccountTrigger;


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAccountTrigger = (AccountTrigger) context;
    }

    @Override
    protected RegisterContract.Presenter initPresenter() {
        return new RegisterPresenter(this);
    }

    @Override
    public void registerSuccess() {
        MainActivity.show(getContext());
        getActivity().finish();
    }


    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String phone = etPhoneInput.getText().toString();
        String password = etPasswordInput.getText().toString();
        String name = etNameInput.getText().toString();
        mPresenter.register(phone, name, password);
    }

    @OnClick(R.id.frame_go_login)
    void onShowLoginClick() {
        mAccountTrigger.triggerView();
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mLoading.start();
        etPhoneInput.setEnabled(false);
        etPasswordInput.setEnabled(false);
        etNameInput.setEnabled(false);
        btnSubmit.setEnabled(false);
    }

    @Override
    public void showError(int error) {
        super.showError(error);
        mLoading.stop();
        etNameInput.setEnabled(true);
        etPasswordInput.setEnabled(true);
        etPhoneInput.setEnabled(true);
        btnSubmit.setEnabled(true);
    }
}
