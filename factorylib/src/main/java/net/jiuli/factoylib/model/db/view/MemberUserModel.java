package net.jiuli.factoylib.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

import net.jiuli.factoylib.model.db.AppDataBase;

/**
 * Created by jiuli on 17-12-15.
 */

@QueryModel(database = AppDataBase.class)
public class MemberUserModel {
    @Column
    public String userId;
    @Column
    public String alias;
    @Column
    public String portrait;
}
