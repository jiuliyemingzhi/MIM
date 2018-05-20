package net.jiuli.factoylib.presenter.group;

import android.text.TextUtils;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.common.factory.presenter.BaseRecyclerPresenter;
import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.R;
import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.data.helper.UserHelper;
import net.jiuli.factoylib.model.api.group.GroupCreateModel;
import net.jiuli.factoylib.model.card.GroupCard;
import net.jiuli.factoylib.model.db.view.UserSampleModel;
import net.jiuli.factoylib.net.UploadHelper;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupCreatePresenter
        extends BaseRecyclerPresenter<GroupCreateContact.ViewModel, GroupCreateContact.View>
        implements GroupCreateContact.Presenter, DataSource.Callback<GroupCard> {

    private Set<String> users = new HashSet<>();

    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            List<UserSampleModel> sampleContact = UserHelper.getSampleContact();
            List<GroupCreateContact.ViewModel> models = new ArrayList<>();
            for (UserSampleModel sampleModel : sampleContact) {
                models.add(new GroupCreateContact.ViewModel(sampleModel, false));
            }
            refreshData(models);
        }
    };

    public GroupCreatePresenter(GroupCreateContact.View view) {
        super(view);
    }


    @Override
    public void create(final String name, final String desc, final String picture) {
        final GroupCreateContact.View view = getView();
        view.showLoading();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(picture) || users.size() < 1) {
            view.showError(R.string.label_group_create_invalid);
            return;
        }
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url = uploadPicture(picture);
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                GroupCreateModel model = new GroupCreateModel(name, desc, url, users);
                GroupHelper.create(model, GroupCreatePresenter.this);
            }
        });
    }

    private String uploadPicture(String picture) {
        String portrait = UploadHelper.uploadPortrait(picture);
        if (TextUtils.isEmpty(portrait)) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    GroupCreateContact.View view = getView();
                    if (view != null) {
                        view.showError(R.string.data_upload_error);
                    }
                }
            });
        }
        return portrait;
    }

    @Override
    public void changeSelect(GroupCreateContact.ViewModel model, boolean isSelect) {
        if (isSelect) {
            users.add(model.author.getId());
        } else {
            users.remove(model.author.getId());
        }
    }

    @Override
    public void start() {
        super.start();
        Factory.runOnAsync(loader);
    }


    @Override
    public void onDataLoaded(GroupCard groupCard) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContact.View view = getView();
                if (view != null) {
                    view.onCreateSucceed();
                }
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContact.View view = getView();
                if (view != null) {
                    view.showError(strRes);
                }
            }
        });
    }


}
