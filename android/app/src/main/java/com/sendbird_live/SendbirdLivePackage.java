// SendbirdLivePackage.java

package com.sendbird_live;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.sendbird_live.view.HostViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SendbirdLivePackage implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(new SendbirdLiveModule(reactContext));
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(new HostViewManager(reactContext));
    }
}
