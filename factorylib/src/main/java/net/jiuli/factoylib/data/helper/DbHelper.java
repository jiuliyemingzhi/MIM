package net.jiuli.factoylib.data.helper;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import net.jiuli.factoylib.model.Db;
import net.jiuli.factoylib.model.db.AppDataBase;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.GroupMember;
import net.jiuli.factoylib.model.db.Group_Table;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.db.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jiuli on 17-10-3.
 */

public class DbHelper {

    final static int SAVE = 0;
    final static int DELETE = 1;
    private final Map<Class<?>, Set<ChangedListener>> changedListener = new HashMap<>();

    private DbHelper() {

    }

    private <Model extends BaseModel> Set<ChangedListener> getListeners(Class<Model> modelClass) {
        return changedListener.get(modelClass);
    }

    public final static DbHelper getInstance() {
        return SingleHolder.instance;
    }

    private final static class SingleHolder {
        private static final DbHelper instance = new DbHelper();
    }


    public static <Model extends BaseModel> void save(Class<Model> modelClass, Model... models) {
        if (models == null || models.length < 1) {
            return;
        }
        List<Model> modelList = new ArrayList<>();
        for (Model model : models) {
            if (model != null && !TextUtils.isEmpty(((Db)model).getId())) {
                modelList.add(model);
            }
        }
        definition(SAVE, modelClass, modelList);
    }

    public static <Model extends BaseModel> void save(Class<Model> modelClass, List<Model> models) {
        if (models == null || models.size() < 1) {
            return;
        }

        for (Model model : models) {
            if (model == null || TextUtils.isEmpty(((Db)model).getId())) {
                models.remove(model);
            }
        }
        definition(SAVE, modelClass, models);
    }

    public static <Model extends BaseModel> void delete(Class<Model> modelClass, List<Model> models) {
        definition(DELETE, modelClass, models);
    }

    private static <Model extends BaseModel> void definition(final int operation, final Class<Model> modelClass, final List<Model> models) {
        if (models == null || models.size() < 1) {
            return;
        }
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Model> modelAdapter = FlowManager.getModelAdapter(modelClass);
                switch (operation) {
                    case SAVE:
                        modelAdapter.saveAll(models);
                        getInstance().notifySave(modelClass, models);
                        break;
                    case DELETE:
                        modelAdapter.deleteAll(models);
                        getInstance().notifyDelete(modelClass, models);
                        break;
                }

            }
        }).build().execute();
    }

    public static <Model extends BaseModel> void removeChangedListener(final Class<Model> tClass, final ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = getInstance().getListeners(tClass);
        if (changedListeners != null) {
            changedListeners.remove(listener);
        }
    }

    public static <Model extends BaseModel> void addChangedListener(final Class<Model> tClass, final ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = getInstance().getListeners(tClass);
        if (changedListeners == null) {
            changedListeners = new HashSet<>();
            getInstance().changedListener.put(tClass, changedListeners);
        }
        changedListeners.add(listener);
    }

    private static void updateGroup(final List<GroupMember> members) {
        final Set<String> groupIds = new HashSet<>();
        for (GroupMember member : members) {
            groupIds.add(member.getGroupId());
        }
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                List<Group> groups = SQLite.select().from(Group.class).where(Group_Table.id.in(groupIds)).queryList();
                getInstance().notifySave(Group.class, groups);
            }
        }).build().execute();
    }

    private void updateSession(List<Message> messages) {
        final Set<Session.Identify> identifies = new HashSet<>();
        for (Message message : messages) {
            Session.Identify identify = Session.createSessionIdentify(message);
            identifies.add(identify);
        }
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
                List<Session> sessions = new ArrayList<>();
                int index = 0;
                for (Session.Identify identify : identifies) {
                    Session session = SessionHelper.findFromLocal(identify.id);
                    if (session == null) {
                        session = new Session(identify);
                    }
                    session.refreshToNow();
                    adapter.save(session);
                    sessions.add(session);
                }
                getInstance().notifySave(Session.class, sessions);
            }
        }).build().execute();
    }

    /**
     * 进行通知
     *
     * @param modelClass
     * @param models
     * @param <Model>
     */
    private final <Model extends BaseModel> void notifySave(Class<Model> modelClass, List<Model> models) {
        Set<ChangedListener> listeners = getListeners(modelClass);
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener listener : listeners) {
                listener.onDataSave(models);
            }
        }

        if (GroupMember.class.equals(modelClass)) {
            updateGroup((List<GroupMember>) models);
        } else if (Message.class.equals(modelClass)) {
            updateSession((List<Message>) models);
        }
    }


    private final <Model extends BaseModel> void notifyDelete(Class<Model> modelClass, List<Model> models) {

        final Set<ChangedListener> listeners = getListeners(modelClass);
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener listener : listeners) {
                listener.onDateDelete(models);
            }
        }
        if (GroupMember.class.equals(modelClass)) {
            updateGroup((List<GroupMember>) models);
        } else if (Message.class.equals(modelClass)) {
            updateSession((List<Message>) models);
        }

    }


    @SuppressWarnings({"unused", "unchecked"})
    public interface ChangedListener<Data extends BaseModel> {
        void onDataSave(List<Data> dataArray);

        void onDateDelete(List<Data> dataArray);
    }
}
