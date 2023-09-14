
// SendbirdLiveModule.java

package com.sendbird_live;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.sendbird.android.handler.InitResultHandler;
import com.sendbird.live.AuthenticateParams;
import com.sendbird.live.LiveEventCreateParams;
import com.sendbird.live.MediaOptions;
import com.sendbird.live.SendbirdLive;
import com.sendbird.live.uikit.SendbirdLiveUIKit;
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter;
import com.sendbird.uikit.interfaces.UserInfo;
import com.sendbird.webrtc.AudioDevice;
import com.sendbird.webrtc.SendbirdException;
import com.sendbird.webrtc.VideoDevice;
import com.sendbird.webrtc.handler.CompletionHandler;

import java.util.ArrayList;
import java.util.List;


public class SendbirdLiveModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;
    public String cameraId;

    SendbirdLiveModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Override
    public String getName() {
        return "SendbirdLiveModule";
    }

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
                    public void onInitFailed(@NonNull com.sendbird.android.exception.SendbirdException e) {
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
            successCallback.invoke("User " + user + " authenticated");
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
        params.setCoverUrl(imageUrl);


        SendbirdLive.createLiveEvent(params, (liveEvent, e) -> {
            if (e != null) {
                errorCallback.invoke(e.getMessage());
                return;
            }
            // Assuming liveEvent has a method to get its identifier.
            // If there isn't such a method, you'd need to adjust this line accordingly.
            /* liveEvent.getId() or appropriate method */
            assert liveEvent != null;
            CameraManager cameraManager = (CameraManager) reactContext.getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraId = cameraManager.getCameraIdList()[1];
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                VideoDevice videoDevice = VideoDevice.Companion.createVideoDevice("abbc", VideoDevice.Position.FRONT, cameraCharacteristics);
                MediaOptions mediaOptions = new MediaOptions(videoDevice, AudioDevice.SPEAKERPHONE, true, true);
                liveEvent.enterAsHost(mediaOptions, e1 -> {
                    if (e1 != null) {
                        Log.d("enterAsHost", e1.getMessage());
                        return;
                    }
                    successCallback.invoke(liveEvent.getLiveEventId());

                });
            } catch (CameraAccessException ex) {
                throw new RuntimeException(ex);
            }
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

            liveEvent.endEvent(e1 -> {

                if (e1 != null) {
                    errorCallback.invoke(e1.getMessage());
                    return;
                }
                successCallback.invoke("successfully ended live event ");
            });
        });
    }

    // Here you can add more methods to cover other functionalities like
    // `enterAsHost` and others.
//    @ReactMethod enterLiveEvent(){
//        SendbirdLive.
//    }
}
