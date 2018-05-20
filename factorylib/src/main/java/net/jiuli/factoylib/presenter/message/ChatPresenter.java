package net.jiuli.factoylib.presenter.message;

import android.support.v7.util.DiffUtil;

import net.jiuli.factoylib.data.helper.MessageHelper;
import net.jiuli.factoylib.data.message.MessageDataSource;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.message.MessageCreateModel;
import net.jiuli.factoylib.persistence.Account;
import net.jiuli.factoylib.presenter.BaseSourcePresenter;
import net.jiuli.common.utils.DiffUiDataCallback;

import java.util.List;

/**
 * Created by jiuli on 17-10-17.
 */

public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message,Message, MessageDataSource, View>
        implements ChatContract.Presenter {

    protected String mReceiverId;
    private int mReceiverType;

    public ChatPresenter(MessageDataSource source, View view, String receiverId, int receiverType) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }


    @Override
    public void pushText(String content) {
        MessageCreateModel model = new MessageCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content, Message.TYPE_STR)
                .build();

        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path) {
        //TODO

    }

    @Override
    public void pushImages(String[] paths) {
        //TODO

    }

    @Override
    public void pushFile(String path) {
        //TODO

    }

    @Override
    public void pushVideo(String path) {
        //TODO

    }

    @Override
    public boolean rePush(Message message) {
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId()) && message.getStatus() == Message.STATUS_FAILED) {
            message.setStatus(Message.STATUS_CREATED);
            MessageCreateModel model = MessageCreateModel.buildWidthMessage(message);
            MessageHelper.push(model);
            return true;
        }
        return false;
    }


    @Override
    public void onDataLoaded(List<Message> messages) {
        View view = getView();
        if (view == null) {
            return;
        }
        List<Message> dataList = view.getRecyclerViewAdapter().getDataList();
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(dataList, messages);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        refreshData(result, messages);
    }
}
