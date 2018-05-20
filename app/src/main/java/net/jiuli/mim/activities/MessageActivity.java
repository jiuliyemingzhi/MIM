package net.jiuli.mim.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import net.jiuli.common.app.Activity;
import net.jiuli.common.app.Fragment;
import net.jiuli.factoylib.model.Author;
import net.jiuli.factoylib.model.db.Group;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.db.Session;
import net.jiuli.mim.R;
import net.jiuli.mim.fragments.message.ChatGroupFragment;
import net.jiuli.mim.fragments.message.ChatUserFragment;

public class MessageActivity extends Activity {
    //receiverId
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    public static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";

    private String mReceiverId;


    private boolean mIsGroup;

    private Fragment fragment;

    public static void show(Context context, Author author) {
        if (author == null || TextUtils.isEmpty(author.getId())) {
            return;
        }
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, author.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, false);
        context.startActivity(intent);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        if (initArgs(getIntent().getExtras())) {
//            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//            fragment = null;
//            initWidget();
//        } else {
//            finish();
//        }
//    }


    public static void show(Context context, Session session) {
        if (session == null || TextUtils.isEmpty(session.getId())) {
            return;
        }
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, session.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, session.getReceiverType() == Message.RECEIVER_TYPE_GROUP);
        context.startActivity(intent);
    }

    public static void show(Context context, Group group) {
        if (group == null || TextUtils.isEmpty(group.getId())) {
            return;
        }
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, true);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
        mIsGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return mReceiverId != null;
    }

    @Override
    protected void initWidget() {
        setTitle("");

        if (fragment == null) {
            if (mIsGroup) {
                fragment = new ChatGroupFragment();
            } else {
                fragment = new ChatUserFragment();
            }
            Bundle bundle = new Bundle();
            bundle.putString(KEY_RECEIVER_ID, mReceiverId);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_root, fragment)
                    .commit();
        }
    }


}
