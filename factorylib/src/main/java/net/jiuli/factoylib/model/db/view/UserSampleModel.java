package net.jiuli.factoylib.model.db.view;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

import net.jiuli.factoylib.model.Author;
import net.jiuli.factoylib.model.db.AppDataBase;

/**
 * Created by jiuli on 17-12-15.
 */

@QueryModel(database = AppDataBase.class)
public class UserSampleModel implements Author {
    @Column
    private String id;

    @Column
    private String name;

    @Column
    private String portrait;

    @Column
    private String alias;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        if (!TextUtils.isEmpty(alias)) {
            return alias;
        }
        return name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPortrait() {
        return portrait;
    }

    @Override
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
