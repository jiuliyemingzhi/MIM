package net.jiuli.factoylib.data.group;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.jiuli.factoylib.data.BaseDbRepository;
import net.jiuli.factoylib.data.helper.GroupHelper;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.view.MemberUserModel;

import java.util.List;

/**
 * Created by jiuli on 17-12-16.
 */

public class GroupsRepository extends BaseDbRepository<Group> implements GroupsDataSource {
    public GroupsRepository() {
        super(Group.class);
    }

    @Override
    public void load(SucceedCallback<List<Group>> callback) {
        super.load(callback);
        SQLite.select()
                .from(Group.class)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Group group) {
        if (group.getGroupMemberCount() > 0) {
            group.setHolder(buildGroupHolder(group));
        } else {
            group.setHolder(null);
            GroupHelper.refreshGroupMember(group.getId());
        }
        return true;
    }


    private String buildGroupHolder(Group group) {
        List<MemberUserModel> userModels = group.getGroupLatelyMembers();
        if (userModels == null || userModels.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();

        for (MemberUserModel model : userModels) {
            builder.append(model.alias).append(", ");
        }

        return builder.delete(builder.lastIndexOf(", "), builder.length()).toString();
    }
}
