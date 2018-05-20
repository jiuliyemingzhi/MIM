package net.jiuli.factoylib.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import net.jiuli.factoylib.model.Author;

import java.util.Date;
import java.util.Objects;

/**
 * Created by jiuli on 17-9-14.
 */
@Table(database = AppDataBase.class)
public class User extends BaseDbModel<User>
        implements Author {

    public static final int SEX_MAN = 1;
    public static final int SEX_WOMAN = 2;

    @PrimaryKey
    private String id;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String portrait;

    @Column
    private String desc;

    @Column
    private String alias;

    @Column
    private int sex = 0;

    @Column
    private int follows;

    @Column
    private int following;

    @Column
    private boolean isFollow;

    @Column
    private Date modifyAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", portrait='" + portrait + '\'' +
                ", desc='" + desc + '\'' +
                ", alias='" + alias + '\'' +
                ", sex=" + sex +
                ", follows=" + follows +
                ", following=" + following +
                ", isFollow=" + isFollow +
                ", modifyAt=" + modifyAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return sex == user.sex &&
                follows == user.follows &&
                following == user.following &&
                isFollow == user.isFollow &&
                Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(portrait, user.portrait) &&
                Objects.equals(desc, user.desc) &&
                Objects.equals(alias, user.alias) &&
                Objects.equals(modifyAt, user.modifyAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean isSame(User old) {
        return this == old || Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(User old) {
        return this == old ||
                Objects.equals(name, old.name)
                        && Objects.equals(portrait, old.portrait)
                        && Objects.equals(sex, old.sex)
                        && Objects.equals(isFollow, old.isFollow);
    }
}
