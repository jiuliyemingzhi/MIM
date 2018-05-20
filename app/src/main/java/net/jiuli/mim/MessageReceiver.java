package net.jiuli.mim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;

import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.data.helper.AccountHelper;
import net.jiuli.factoylib.persistence.Account;

/**
 * Created by jiuli on 17-9-16 .
 */

public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = MessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            switch (bundle.getInt(PushConsts.CMD_ACTION)) {
                case PushConsts.GET_CLIENTID:
                    Log.i(TAG, "GET_CLIENTID: " + bundle.toString());
                    onClientInit(bundle.getString("clientid"));
                    break;
                case PushConsts.GET_MSG_DATA:

                    byte[] payloads = bundle.getByteArray("payload");
                    if (payloads != null) {
                        String message = new String(payloads);
                        Log.i(TAG, "GET_MSG_DATA: " + message);
                        onMessageArrived(message);
                    }

                    break;
                default:
                    Log.i(TAG, "OTHER: " + bundle.toString());
                    break;
            }

        }
    }

    private void onClientInit(String cid) {
        Account.setPushId(cid);
        if (Account.isLogin()) {
            AccountHelper.bindPush(null);
        }

    }

    private void onMessageArrived(String message) {
        Factory.dispatchPush(message);
    }
}
