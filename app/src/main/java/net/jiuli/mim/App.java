package net.jiuli.mim;

import com.igexin.sdk.PushManager;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.jiuli.common.app.Application;
import net.jiuli.factoylib.Factory;

/**
 * Created by jiuli on 17-9-3.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Factory.setup();
        PushManager.getInstance().initialize(this, null);
        FlowManager.init(this);
    }
}
