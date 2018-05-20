package net.jiuli.factoylib.data.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.jiuli.factoylib.data.BaseDbRepository;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.db.Message_Table;

import java.util.Collections;
import java.util.List;

/**
 * Created by jiuli on 17-10-17.
 */

public class MessageRepository extends BaseDbRepository<Message> implements MessageDataSource {


    private int type;

    private String receiverId;

    public MessageRepository(String receiverId) {
        super(Message.class);
        this.receiverId = receiverId;
        this.type = Message.RECEIVER_TYPE_NONE;
    }

    public MessageRepository(String receiverId, int type) {
        super(Message.class);
        this.receiverId = receiverId;
        this.type = type;
    }


    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);
        switch (type) {
            case Message.RECEIVER_TYPE_NONE:
                SQLite.select().from(Message.class)
                        .where(OperatorGroup.clause()
                                .and(Message_Table.sender_id.eq(receiverId))
                                .and(Message_Table.group_id.isNull()))
                        .or(Message_Table.receiver_id.eq(receiverId))
                        .orderBy(Message_Table.createAt, false)
                        .limit(30)
                        .async()
                        .queryListResultCallback(this)
                        .execute();
                break;
            case Message.RECEIVER_TYPE_GROUP:
                SQLite.select()
                        .from(Message.class)
                        .where(Message_Table.group_id.eq(receiverId))
                        .orderBy(Message_Table.createAt, false)
                        .limit(30)
                        .async()
                        .queryListResultCallback(this)
                        .execute();
                break;
        }
    }

    @Override
    protected boolean isRequired(Message message) {
        switch (type) {
            case Message.RECEIVER_TYPE_NONE:
                return receiverId.equalsIgnoreCase(message.getSender().getId())
                        && message.getGroup() == null
                        || message.getReceiver() != null
                        && receiverId.equalsIgnoreCase(message.getReceiver().getId());
            case Message.RECEIVER_TYPE_GROUP:
                return message.getGroup() != null
                        && receiverId.equalsIgnoreCase(message.getGroup().getId());
            default:
                return true;
        }

    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
