package net.jiuli.factoylib.data.user;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.jiuli.factoylib.data.BaseDbRepository;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.model.db.User_Table;
import net.jiuli.factoylib.persistence.Account;

import java.util.List;

/**
 * Created by jiuli on 17-10-7.
 */

public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource {
    public ContactRepository() {
        super(User.class);
    }

    @Override
    public void load(SucceedCallback<List<User>> callback) {
        super.load(callback);
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();

    }

    @Override
    protected boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }
}
