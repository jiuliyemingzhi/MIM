package net.jiuli.mim.fragments.message;


import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.presenter.message.ChatContract;
import net.jiuli.factoylib.presenter.message.ChatUserPresenter;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.PersonalActivity;

import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatUserFragment
        extends ChatFragment<User>
        implements ChatContract.UserView {

    private User user;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        ivMenu.setImageResource(R.drawable.ic_person);
    }

    @OnClick(R.id.iv_menu)
    void showPersonalInfo() {
        if (user == null) {
            return;
        }
        PersonalActivity.show(getContext(), mReceiverId);
    }


    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatUserPresenter(this, mReceiverId);
    }


    @Override
    public void onInit(User user) {
        if (user != null) {
            this.user = user;
            tvTitle.setText(TextUtils.isEmpty(user.getAlias()) ? user.getName() : user.getAlias());
            tvSubTitle.setText("手机在线");
        }
    }
}
