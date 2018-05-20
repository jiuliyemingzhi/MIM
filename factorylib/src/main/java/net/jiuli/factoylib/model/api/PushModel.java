package net.jiuli.factoylib.model.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.jiuli.factoylib.Factory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * .Created by jiuli on 17-10-1.
 */

@SuppressWarnings("WeakerAccess")
public class PushModel {
    // 退出登录
    public static final int ENTITY_TYPE_LOGOUT = -1;
    // 普通消息送达
    public static final int ENTITY_TYPE_MESSAGE = 200;
    // 新朋友添加
    public static final int ENTITY_TYPE_ADD_FRIEND = 1001;
    // 新群添加
    public static final int ENTITY_TYPE_ADD_GROUP = 1002;
    // 新的群成员添加
    public static final int ENTITY_TYPE_ADD_GROUP_MEMBERS = 1003;
    // 群成员信息更改
    public static final int ENTITY_TYPE_MODIFY_GROUP_MEMBERS = 2001;
    // 群成员退出
    public static final int ENTITY_TYPE_EXIT_GROUP_MEMBERS = 3001;

    private List<Entity> entities = new ArrayList<>();

    private PushModel(List<Entity> entities) {
        this.entities = entities;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public static PushModel decode(String json) {
        Gson gson = Factory.getGson();
        Type type = new TypeToken<List<Entity>>() {

        }.getType();
        try {
            List<Entity> entities = gson.fromJson(json, type);
            if (entities.size() > 0) {
                return new PushModel(entities);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Entity {
        public Entity() {

        }

        public int type;
        public String content;
        public Date createAt;

        @Override
        public String toString() {
            return "Entity{" +
                    "type=" + type +
                    ", content='" + content + '\'' +
                    ", createAt=" + createAt +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PushModel{" +
                "entities=" + entities +
                '}';
    }
}