package net.jiuli.factoylib.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.jiuli.factoylib.model.db.Session;
import net.jiuli.factoylib.model.db.Session_Table;

/**
 * Created by jiuli on 17-10-5.
 */

public class SessionHelper {
    public static Session findFromLocal(String id) {
        return SQLite.select().from(Session.class).where(Session_Table.id.eq(id)).querySingle();
    }
}
