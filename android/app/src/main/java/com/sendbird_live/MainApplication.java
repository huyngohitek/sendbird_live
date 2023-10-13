package com.sendbird_live;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.soloader.SoLoader;
import com.sendbird.android.LogLevel;
import com.sendbird.android.SendbirdChat;
import com.sendbird.android.caching.LocalCacheConfig;
import com.sendbird.android.exception.SendbirdException;
import com.sendbird.android.handler.InitResultHandler;
import com.sendbird.android.params.InitParams;
import com.sendbird.live.SendbirdLive;
import com.sendbird_live.newarchitecture.MainApplicationReactNativeHost;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {
    private static String APP_ID = "5E720D37-FB7D-41F3-924A-6246070BD1F8";

    private final ReactNativeHost mReactNativeHost =
            new ReactNativeHost(this) {
                @Override
                public boolean getUseDeveloperSupport() {
                    return BuildConfig.DEBUG;
                }

                @Override
                protected List<ReactPackage> getPackages() {
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    List<ReactPackage> packages = new PackageList(this).getPackages();
                    // Packages that cannot be autolinked yet can be added manually here, for example:
                    // packages.add(new MyReactNativePackage());
                    packages.add(new SendbirdLivePackage());
                    return packages;
                }

                @Override
                protected String getJSMainModuleName() {
                    return "index";
                }
            };

    private final ReactNativeHost mNewArchitectureNativeHost =
            new MainApplicationReactNativeHost(this);

    @Override
    public ReactNativeHost getReactNativeHost() {
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            return mNewArchitectureNativeHost;
        } else {
            return mReactNativeHost;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // If you opted-in for the New Architecture, we enable the TurboModule system
        ReactFeatureFlags.useTurboModules = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
        SoLoader.init(this, /* native exopackage */ false);
        SendbirdChat.init(
                new InitParams(APP_ID, this, true), new InitResultHandler() {
                    @Override
                    public void onMigrationStarted() {

                    }

                    @Override
                    public void onInitFailed(@NonNull SendbirdException e) {
                        Log.d("Application", " init sendbird chat failed" + e);
                    }

                    @Override
                    public void onInitSucceed() {
                        Log.d("Application", " init sendbird chat success");
                        SendbirdLive.init(new com.sendbird.live.InitParams(APP_ID, MainApplication.this), new com.sendbird.live.handler.InitResultHandler() {
                            @Override
                            public void onMigrationStarted() {

                            }

                            @Override
                            public void onInitFailed(@NonNull com.sendbird.webrtc.SendbirdException e) {
                                Log.d("Sendbird Live init", "sendbird live init failed");
                            }

                            @Override
                            public void onInitSucceed() {
                                Log.d("Sendbird Live init", "sendbird live init success");

                            }
                        });
                    }
                }
        );
        initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
    }

    /**
     * Loads Flipper in React Native templates. Call this in the onCreate method with something like
     * initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
     *
     * @param context
     * @param reactInstanceManager
     */
    private static void initializeFlipper(
            Context context, ReactInstanceManager reactInstanceManager) {
        if (BuildConfig.DEBUG) {
            try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
                Class<?> aClass = Class.forName("com.sendbird_live.ReactNativeFlipper");
                aClass
                        .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
                        .invoke(null, context, reactInstanceManager);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
