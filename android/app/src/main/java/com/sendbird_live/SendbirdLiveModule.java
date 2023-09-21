
// SendbirdLiveModule.java

package com.sendbird_live;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sendbird.live.AuthenticateParams;
import com.sendbird.live.Host;
import com.sendbird.live.HostType;
import com.sendbird.live.InitParams;
import com.sendbird.live.LiveEvent;
import com.sendbird.live.LiveEventCreateParams;
import com.sendbird.live.MediaOptions;
import com.sendbird.live.SendbirdLive;
import com.sendbird.webrtc.AudioDevice;
import com.sendbird.webrtc.SendbirdException;
import com.sendbird.webrtc.VideoDevice;
import com.sendbird.webrtc.handler.CompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class SendbirdLiveModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;
    private static String TAG = "SendbirdLiveModule";
    public String cameraId;
    private LiveEvent liveEventRef;

    private int listenerCount = 0;

    SendbirdLiveModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "SendbirdLiveModule";
    }

    @ReactMethod
    public void initializeSDK(String APP_ID, String USER_ID, String ACCESS_TOKEN, Callback successCallback,
                              Callback errorCallback) {
        SendbirdLive.init(new InitParams(APP_ID, reactContext), new com.sendbird.live.handler.InitResultHandler() {
            @Override
            public void onMigrationStarted() {

            }

            @Override
            public void onInitFailed(@NonNull SendbirdException e) {
                Log.d("Sendbird Live init", "sendbird live init failed");
                errorCallback.invoke(e.getMessage());
            }

            @Override
            public void onInitSucceed() {
                Log.d("Sendbird Live init", "sendbird live init success");
                successCallback.invoke("User authenticated");

            }
        });
//        SendbirdLiveUIKit.init(new SendbirdUIKitAdapter() {
//            @Override
//            public String getAppId() {
//                return APP_ID;
//            }
//
//            @Override
//            public String getAccessToken() {
//                return ACCESS_TOKEN;
//            }
//
//            @Override
//            public UserInfo getUserInfo() {
//                return new UserInfo() {
//                    @Override
//                    public String getUserId() {
//                        return USER_ID;
//                    }
//
//                    @Override
//                    public String getNickname() {
//                        return null;
//                    }
//
//                    @Override
//                    public String getProfileUrl() {
//                        return null;
//                    }
//                };
//            }
//
//            @Override
//            public InitResultHandler getInitResultHandler() {
//                return new InitResultHandler() {
//                    @Override
//                    public void onInitFailed(@NonNull com.sendbird.android.exception.SendbirdException e) {
//                        errorCallback.invoke(e.getMessage());
//                    }
//
//                    @Override
//                    public void onInitSucceed() {
//                        // SendbirdLiveUIKit.connect((user, e) -> {
//                        // if (e != null) {
//                        // errorCallback.invoke(e.getMessage());
//                        // return;
//                        // }
//                        successCallback.invoke("User authenticated");
//                        // });
//                    }
//
//                    @Override
//                    public void onMigrationStarted() {
//                        // handle migration started if necessary
//                    }
//                };
//            }
//        }, reactContext);
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
    public void createLiveEvent(ReadableArray userIdsForHostArray, String title, String imageUrl,
                                Callback successCallback,
                                Callback errorCallback) {

        List<String> userIdsList = new ArrayList<>();
        for (int i = 0; i < userIdsForHostArray.size(); i++) {
            userIdsList.add(userIdsForHostArray.getString(i));
        }
        LiveEventCreateParams params = new LiveEventCreateParams(userIdsList);
        params.setTitle(title);
        params.setCoverUrl(imageUrl);
//        params.setHostType(HostType.SINGLE_HOST);


        SendbirdLive.createLiveEvent(params, (liveEvent, e) -> {
            if (e != null) {
                errorCallback.invoke(e.getMessage());
                return;
            }
            // Assuming liveEvent has a method to get its identifier.
            // If there isn't such a method, you'd need to adjust this line accordingly.
            /* liveEvent.getId() or appropriate method */
            assert liveEvent != null;
            MediaOptions mediaOptions = new MediaOptions(null, null, true, true, null);
            liveEvent.enterAsHost(mediaOptions, e1 -> {
                if (e1 != null) {
                    Log.d(TAG, e1.getMessage());
                    return;
                }
                successCallback.invoke(liveEvent.getLiveEventId());
                goToLiveScreen(liveEvent.getLiveEventId(), true);

            });
        });
    }

    @ReactMethod
    public void goToLiveScreen(String liveEventId, boolean isHost) {
        Intent intent = new Intent(reactContext, LiveActivity.class);
        intent.putExtra("LiveEventId", liveEventId);
        intent.putExtra("isHost", isHost);
        getCurrentActivity().startActivity(intent);
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

    @ReactMethod
    public void enterLiveEvent(String liveEventId, boolean isHost) {
        SendbirdLive.getLiveEvent(liveEventId, (liveEvent, e) -> {
            Log.d(TAG, "enter live event ");
            if (isHost) {
                CameraManager cameraManager = (CameraManager) reactContext.getSystemService(Context.CAMERA_SERVICE);
                try {
                    cameraId = cameraManager.getCameraIdList()[1];
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

                    VideoDevice videoDevice = VideoDevice.Companion.createVideoDevice(Build.MODEL, VideoDevice.Position.FRONT, cameraCharacteristics);
                    MediaOptions mediaOptions = new MediaOptions(videoDevice, AudioDevice.SPEAKERPHONE, true, true, null);
                    liveEvent.enterAsHost(mediaOptions, e1 -> {
                        if (e1 != null) {
                            Log.d(TAG, e1.getMessage());
                            return;
                        }
                        goToLiveScreen(liveEventId, true);

                    });
                } catch (CameraAccessException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                liveEvent.enter(e2 -> {
                    if (e2 != null) {
                        Log.d(TAG, e2.getMessage());
                        return;
                    }
                    goToLiveScreen(liveEventId, false);
                });
            }
        });
        // Here you can add more methods to cover other functionalities like
        // `enterAsHost` and others.
    }

    // Required for rn built in EventEmitter Calls.
    @ReactMethod
    public void addListener(String eventName) {
        if (listenerCount == 0) {
            // Set up any upstream listeners or background tasks as necessary
        }

        listenerCount += 1;
    }

    @ReactMethod
    public void removeListeners(Integer count) {
        listenerCount -= count;
        if (listenerCount == 0) {
            // Remove upstream listeners, stop unnecessary background tasks
        }
    }

}