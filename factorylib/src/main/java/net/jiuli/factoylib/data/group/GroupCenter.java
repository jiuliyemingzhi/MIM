package net.jiuli.factoylib.data.group;

import net.jiuli.factoylib.model.card.GroupCard;
import net.jiuli.factoylib.model.card.GroupMemberCard;
import net.jiuli.factoylib.model.db.GroupMember;

/**
 * Created by jiuli on 17-10-7.
 */

public interface GroupCenter {
    void dispatch(GroupCard... cards);

    void dispatch(GroupMemberCard... cards);
}
