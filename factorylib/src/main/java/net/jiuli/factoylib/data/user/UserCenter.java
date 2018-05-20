package net.jiuli.factoylib.data.user;

import net.jiuli.factoylib.model.card.UserCard;
import net.jiuli.factoylib.model.db.User;

/**
 * Created by jiuli on 17-10-7.
 */

public interface UserCenter {
    void dispatch(UserCard... cards);
}
