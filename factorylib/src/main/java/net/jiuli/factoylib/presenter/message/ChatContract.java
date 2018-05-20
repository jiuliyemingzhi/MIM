package net.jiuli.factoylib.presenter.message;

import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.model.db.view.MemberUserModel;

import java.util.List;

/**
 * Created by jiuli on 17-10-17.
 */

public class ChatContract {
    public interface Presenter extends BaseContract.Presenter {
        void pushText(String content);

        void pushAudio(String path);

        void pushImages(String[] paths);

        void pushFile(String path);

        void pushVideo(String path);

        boolean rePush(Message message);
    }

    public interface View<InitModel> extends BaseContract.RecyclerView<Presenter, Message> {
        void onInit(InitModel model);
    }

    public interface UserView extends View<User> {

    }

    public interface GroupView extends View<Group> {
        void showAdminOption(boolean isAdmin);

        void onInitGroupMembers(List<MemberUserModel> userModels, long moreCount);
    }
}
