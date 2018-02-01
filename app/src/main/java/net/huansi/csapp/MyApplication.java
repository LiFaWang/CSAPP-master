package net.huansi.csapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import net.huansi.csapp.utils.MultiLanguageUtil;


/**
 * Created by lx on 17-10-26.
 */

public class MyApplication extends Application {
    private static MyApplication sInstances;
    private static Context sContext;
    public static MyApplication getInstances() {
        return sInstances;
    }
    public static Context getContext() {
        return sContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sInstances = this;
        sContext = this;
        MultiLanguageUtil.init(this);
        MultiLanguageUtil.getInstance().setConfiguration();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                MultiLanguageUtil.getInstance().setConfiguration();
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MultiLanguageUtil.getInstance().setConfiguration();
    }
}
