package net.jiuli.factoylib.presenter.message;

import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.data.message.MessageRepository;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.db.view.MemberUserModel;
import net.jiuli.factoylib.persistence.Account;

import java.util.List;

/**
 * Created by jiuli on 17-10-17.
 */

public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView> {


    public ChatGroupPresenter(ChatContract.GroupView view, String receiverId) {
        super(new MessageRepository(receiverId, Message.RECEIVER_TYPE_GROUP), view, receiverId, Message.RECEIVER_TYPE_GROUP);
    }

    @Override
    public void start() {
        super.start();
        GroupHelper.refreshGroupMember(mReceiverId);
        Group group = GroupHelper.findFormLocal(mReceiverId);
        if (group != null) {
            ChatContract.GroupView view = getView();
            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            view.showAdminOption(isAdmin);

            //基础信息初始化
            view.onInit(group);
            ///成员初始化
            List<MemberUserModel> members = group.getGroupLatelyMembers();
            final long memberCount = group.getGroupMemberCount();

            //没有显示的成员数量
            view.onInitGroupMembers(members, memberCount);
        }
    }
}
