package net.jiuli.factoylib.presenter.message;


import net.jiuli.factoylib.data.helper.UserHelper;
import net.jiuli.factoylib.data.message.MessageRepository;
import net.jiuli.factoylib.model.db.Message;

/**
 * Created by jiuli on 17-10-17.
 */

public class ChatUserPresenter
        extends ChatPresenter<ChatContract.UserView>
        implements ChatContract.Presenter {
    public ChatUserPresenter(ChatContract.UserView view, String receiverId) {
        super(new  MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void start() {
        super.start();
        getView().onInit(UserHelper.findForLocal(mReceiverId));
    }
}
