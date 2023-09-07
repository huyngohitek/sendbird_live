
// SendbirdLiveModule.java

package com.sendbird_live;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.sendbird.live.uikit.SendbirdLiveUIKit;
import com.sendbird.live.SendbirdLive;
import com.sendbird.live.AuthenticateParams;
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter;
import com.sendbird.android.handler.InitResultHandler;
import com.sendbird.uikit.interfaces.UserInfo;
import com.sendbird.live.LiveEventCreateParams;
import com.sendbird.android.exception.SendbirdException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import com.facebook.react.bridge.ReadableArray;

import com.sendbird_live.SendbirdLiveCompletionHandler;

public class SendbirdLiveModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;

    SendbirdLiveModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Override
    public String getName() {
        return "SendbirdLiveModule";
    }

    // @ReactMethod
    // public void initializeSDK(String APP_ID) {
    // SendbirdLiveUIKit.init(reactContext, APP_ID);
    // }
    @ReactMethod
    public void initializeSDK(String APP_ID, String USER_ID, String ACCESS_TOKEN, Callback successCallback,
            Callback errorCallback) {
        SendbirdLiveUIKit.init(new SendbirdUIKitAdapter() {
            @Override
            public String getAppId() {
                return APP_ID;
            }

            @Override
            public String getAccessToken() {
                return ACCESS_TOKEN;
            }

            @Override
            public UserInfo getUserInfo() {
                return new UserInfo() {
                    @Override
                    public String getUserId() {
                        return USER_ID;
                    }

                    @Override
                    public String getNickname() {
                        return null;
                    }

                    @Override
                    public String getProfileUrl() {
                        return null;
                    }
                };
            }

            @Override
            public InitResultHandler getInitResultHandler() {
                return new InitResultHandler() {
                    @Override
                    public void onInitFailed(SendbirdException e) {
                        errorCallback.invoke(e.getMessage());
                    }

                    @Override
                    public void onInitSucceed() {
                        // SendbirdLiveUIKit.connect((user, e) -> {
                        // if (e != null) {
                        // errorCallback.invoke(e.getMessage());
                        // return;
                        // }
                        successCallback.invoke("User authenticated");
                        // });
                    }

                    @Override
                    public void onMigrationStarted() {
                        // handle migration started if necessary
                    }
                };
            }
        }, reactContext);
    }

    @ReactMethod
    public void authenticate(String USER_ID, String ACCESS_TOKEN, Callback successCallback, Callback errorCallback) {
        AuthenticateParams params = new AuthenticateParams(USER_ID, ACCESS_TOKEN);
        SendbirdLive.authenticate(params, (user, e) -> {
            if (e != null) {
                errorCallback.invoke(e.getMessage());
                return;
            }
            successCallback.invoke("User authenticated");
        });
    }

    @ReactMethod
    public void startLiveEvent(ReadableArray userIdsForHostArray, String title, String imageUrl,
            Callback successCallback,
            Callback errorCallback) {

        List<String> userIdsList = new ArrayList<>();
        for (int i = 0; i < userIdsForHostArray.size(); i++) {
            userIdsList.add(userIdsForHostArray.getString(i));
        }
        LiveEventCreateParams params = new LiveEventCreateParams(userIdsList);
        params.setTitle(title);
        // params.setCoverUrl(imageUrl);

        SendbirdLive.createLiveEvent(params, (liveEvent, e) -> {
            if (e != null) {
                errorCallback.invoke(e.getMessage());
                return;
            }
            // Assuming liveEvent has a method to get its identifier.
            // If there isn't such a method, you'd need to adjust this line accordingly.
            /* liveEvent.getId() or appropriate method */
            successCallback.invoke("successfully created live event");
        });
    }

    @ReactMethod
    public void endLiveEvent(String liveEventId, Callback successCallback, Callback errorCallback) {
        // Assuming you have a method to fetch the live event using its ID.
        // If not, you might need to adjust this part accordingly.
        SendbirdLive.getLiveEvent(liveEventId, (liveEvent, e) -> {
            if (e != null) {
                errorCallback.invoke(e.getMessage());
                return;
            }

            liveEvent.endEvent(new SendbirdLiveCompletionHandler() {
                @Override
                public void onCompleted(SendbirdException e) {
                    if (e != null) {
                        errorCallback.invoke(e.getMessage());
                        return;
                    }
                    successCallback.invoke("successfully ended live event");
                }
            });
        });
    }

    // Here you can add more methods to cover other functionalities like
    // `enterAsHost` and others.
}
