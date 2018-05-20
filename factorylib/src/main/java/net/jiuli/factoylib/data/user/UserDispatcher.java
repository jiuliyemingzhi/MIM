package net.jiuli.factoylib.data.user;

import android.text.TextUtils;

import net.jiuli.factoylib.data.helper.DbHelper;
import net.jiuli.factoylib.model.card.UserCard;
import net.jiuli.factoylib.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jiuli on 17-10-7.
 */

public class UserDispatcher implements UserCenter {
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static UserCenter instance() {
        return Instance.mInstance;
    }

    private static class Instance {
        private static final UserDispatcher mInstance = new UserDispatcher();
    }

    @Override
    public void dispatch(UserCard... cards) {
        if (cards != null && cards.length > 0) {
            executor.execute(new UserCardHandler(cards));
        }
    }

    private class UserCardHandler implements Runnable {
        private UserCard[] cards;

        public UserCardHandler(UserCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<User> users = new ArrayList<>();
            for (UserCard card : cards) {
                if (card != null && !TextUtils.isEmpty(card.getId())) {
                    users.add(card.build());
                }
            }
            DbHelper.save(User.class, users);
        }
    }
}
