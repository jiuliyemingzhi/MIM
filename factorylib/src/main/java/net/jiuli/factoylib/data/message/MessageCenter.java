package net.jiuli.factoylib.data.message;

import net.jiuli.factoylib.model.card.MessageCard;

/**
 * Created by jiuli on 17-10-7.
 */

public interface MessageCenter {
    void dispatch(MessageCard... cards);
}
