package net.jiuli.factoylib.model.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by jiuli on 17-9-15.
 */
@Database(name = AppDataBase.NAME, version = AppDataBase.VERSION)
public class AppDataBase {
    public static final String NAME = "MIMDatabase";
    public static final int VERSION = 2;
}
