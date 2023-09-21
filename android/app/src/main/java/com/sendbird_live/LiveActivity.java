package com.sendbird_live;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sendbird.live.Host;
import com.sendbird.live.LiveEvent;
import com.sendbird.live.LiveEventListener;
import com.sendbird.live.LiveEventRole;
import com.sendbird.live.LiveEventState;
import com.sendbird.live.MediaOptions;
import com.sendbird.live.ParticipantCountInfo;
import com.sendbird.live.SendbirdLive;
import com.sendbird.webrtc.AudioDevice;
import com.sendbird.webrtc.SendbirdException;
import com.sendbird.webrtc.SendbirdVideoView;
import com.sendbird.webrtc.VideoDevice;
import com.sendbird.webrtc.handler.CompletionHandler;

import org.webrtc.RendererCommon;

import java.util.List;
import java.util.Map;

public class LiveActivity extends ReactActivity {
    String TAG = this.getClass().getSimpleName();
    String cameraId = null;
    LiveEvent liveEventRef;
    int CAMERA_PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        //Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST);
        }
        String liveEventId = getIntent().getStringExtra("LiveEventId");
//        boolean isHost = getIntent().getBooleanExtra("isHost", false);
        assert liveEventId != null;
        SendbirdLive.getLiveEvent(liveEventId, (liveEvent, e) -> {
            boolean isHost = liveEvent.getMyRole() == LiveEventRole.HOST;
            if (!isHost || liveEvent.getState().getValue() == LiveEventState.ONGOING.getValue()) {
                findViewById(R.id.btnStartLive).setVisibility(View.INVISIBLE);
            }
            if (!isHost) {
                findViewById(R.id.btnFlipCamera).setVisibility(View.INVISIBLE);
            }

            liveEventRef = liveEvent;

            liveEvent.addListener("liveEventListener", new LiveEventListener() {

                @Override
                public void onCustomItemsDelete(@NonNull LiveEvent liveEvent, @NonNull Map<String, String> map, @NonNull List<String> list) {

                }

                @Override
                public void onCustomItemsUpdate(@NonNull LiveEvent liveEvent, @NonNull Map<String, String> map, @NonNull List<String> list) {

                }

                @Override
                public void onDisconnected(@NonNull LiveEvent liveEvent, @NonNull SendbirdException e) {
                    Toast.makeText(LiveActivity.this, "You are disconnected", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onHostConnected(@NonNull LiveEvent liveEvent, @NonNull Host host) {
                    Toast.makeText(LiveActivity.this, "Host is connected", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onHostDisconnected(@NonNull LiveEvent liveEvent, @NonNull Host host) {
                    Toast.makeText(LiveActivity.this, "Host is disconnected", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onHostEntered(@NonNull LiveEvent liveEvent, @NonNull Host host) {
                    Toast.makeText(LiveActivity.this, "Host entered", Toast.LENGTH_LONG).show();
                    SendbirdVideoView hostView = findViewById(R.id.sendbirdVideoLiveView);
                    hostView.setEnableHardwareScaler(true);
                    hostView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                    liveEvent.setVideoViewForLiveEvent(hostView, liveEvent.getHost().getHostId());
                }

                @Override
                public void onHostExited(@NonNull LiveEvent liveEvent, @NonNull Host host) {
                    Toast.makeText(LiveActivity.this, "Host Exited", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onHostMuteAudio(@NonNull LiveEvent liveEvent, @NonNull Host host) {

                }

                @Override
                public void onHostStartVideo(@NonNull LiveEvent liveEvent, @NonNull Host host) {

                    Toast.makeText(LiveActivity.this, "Host started video", Toast.LENGTH_LONG).show();
                    SendbirdVideoView hostView = findViewById(R.id.sendbirdVideoLiveView);
                    hostView.setEnableHardwareScaler(true);
                    hostView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                    liveEvent.setVideoViewForLiveEvent(hostView, liveEvent.getHost().getHostId());
                }

                @Override
                public void onHostStopVideo(@NonNull LiveEvent liveEvent, @NonNull Host host) {
                    Toast.makeText(LiveActivity.this, "Host stopped video", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onHostUnmuteAudio(@NonNull LiveEvent liveEvent, @NonNull Host host) {
                    Toast.makeText(LiveActivity.this, "Host unmute Audio", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onLiveEventEnded(@NonNull LiveEvent liveEvent) {
                    updateEventState(liveEventId, LiveEventState.ENDED.getValue());
                    new AlertDialog.Builder(LiveActivity.this).setTitle("Live has ended").setMessage("Live has ended").setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("Cancel", null).show();

                }

                @Override
                public void onLiveEventInfoUpdated(@NonNull LiveEvent liveEvent) {
                }

                @Override
                public void onLiveEventReady(@NonNull LiveEvent liveEvent) {
                    Toast.makeText(LiveActivity.this, "Live Event Ready", Toast.LENGTH_LONG).show();
                    updateEventState(liveEventId, LiveEventState.READY.getValue());
                }

                @Override
                public void onLiveEventStarted(@NonNull LiveEvent liveEvent) {
                    Toast.makeText(LiveActivity.this, "Live Event Started", Toast.LENGTH_LONG).show();
                    updateEventState(liveEventId, LiveEventState.ONGOING.getValue());
                }

                @Override
                public void onParticipantCountChanged(@NonNull LiveEvent liveEvent, @NonNull ParticipantCountInfo participantCountInfo) {
                    TextView txtParticipants = findViewById(R.id.idTxtParticipants);
                    txtParticipants.setText(participantCountInfo.getParticipantCount() + " Participants");
                    updateEventParticipantsCount(liveEventId, participantCountInfo.getParticipantCount());
                }

                @Override
                public void onReactionCountUpdated(@NonNull LiveEvent liveEvent, @NonNull String s, int i) {

                }
            });
            TextView txtParticipants = findViewById(R.id.idTxtParticipants);
            txtParticipants.setText(liveEvent.getParticipantCount() + " Participants");
            if (e != null) {
                Log.e(TAG, e.getMessage());
                return;
            }
            findViewById(R.id.btnEndLive).setOnClickListener(view -> {
//                liveEvent.endEvent(e1 -> {
//                    if (e1 != null) {
//                        Log.d(TAG, e1.getMessage());
//                        return;
//                    }
//                    Log.d(TAG, "Live ended");
//                    finish();
//                });
                if (isHost) {
                    liveEvent.exitAsHost(e1 -> {
                        if (e1 != null) {
                            Log.d(TAG, e1.getMessage());
                            return;
                        }
                        Log.d(TAG, "exited live");
                        finish();
                    });
                } else {
                    liveEvent.exit(e2 -> {
                        if (e2 != null) {
                            Log.d(TAG, e2.getMessage());
                            return;
                        }
                        Log.d(TAG, "exited live");
                        finish();
                    });
                }
            });
            findViewById(R.id.btnStartLive).setOnClickListener(v -> {
                try {
                    cameraId = cameraManager.getCameraIdList()[1];
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                    VideoDevice videoDevice = VideoDevice.Companion.createVideoDevice(Build.MODEL, VideoDevice.Position.FRONT, cameraCharacteristics);
                    MediaOptions mediaOptions = new MediaOptions(videoDevice, AudioDevice.SPEAKERPHONE, true, true, null);
                    liveEvent.startEvent(mediaOptions, e3 -> {

                        if (e3 != null) {
                            Log.d(TAG, e3.getMessage());
                            return;
                        }
                        Log.d(TAG, "Live Event has started");
                        Toast.makeText(this, "Live Event Started, Participants now can view your live stream", Toast.LENGTH_SHORT).show();
                    });
                } catch (CameraAccessException ex) {
                    throw new RuntimeException(ex);
                }

            });
            if (isHost) {
                findViewById(R.id.btnFlipCamera).setOnClickListener(v -> {

                    liveEvent.switchCamera(e4 -> {
                        if (e4 != null) {
                            Log.d(TAG, e4.getMessage());
                            return;
                        }
                    });
                });
                SendbirdVideoView hostView = findViewById(R.id.sendbirdVideoLiveView);
                hostView.setEnableHardwareScaler(true);
                hostView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                String hostId = liveEvent.getHost().getHostId();
                liveEvent.setVideoViewForLiveEvent(hostView, hostId);

                try {
                    cameraId = cameraManager.getCameraIdList()[1];
                    CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                    VideoDevice videoDevice = VideoDevice.Companion.createVideoDevice(Build.MODEL, VideoDevice.Position.FRONT, cameraCharacteristics);
                    MediaOptions mediaOptions = new MediaOptions(videoDevice, AudioDevice.SPEAKERPHONE, true, true, null);

                    liveEvent.startStreaming(mediaOptions, e12 -> {
                        Log.d("startStreaming", "startStreaming===============");
                        liveEvent.setEventReady(e13 -> {
                            Log.d("setEventReady", "Event is Ready===============");

                        });
                    });
                } catch (CameraAccessException ex) {
                    throw new RuntimeException(ex);
                }

            } else {
                if (liveEvent.getHost() != null) {
                    SendbirdVideoView hostView = findViewById(R.id.sendbirdVideoLiveView);
                    hostView.setEnableHardwareScaler(true);
                    hostView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                    String hostId = liveEvent.getHost().getHostId();
                    liveEvent.setVideoViewForLiveEvent(hostView, hostId);
                }
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liveEventRef.removeListener("liveEventListener");
    }

    void updateEventParticipantsCount(String liveEventId, int liveEventParticipantCount) {
        WritableMap map = Arguments.createMap();
        map.putString("id", liveEventId);
        map.putInt("participantCount", liveEventParticipantCount);
        getReactInstanceManager().getCurrentReactContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("updateLiveParticipantCount", map);

    }

    void updateEventState(String liveEventId, String liveEventState) {
        WritableMap map = Arguments.createMap();
        map.putString("id", liveEventId);
        map.putString("state", liveEventState);
        getReactInstanceManager().getCurrentReactContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("updateLiveState", map);

    }
}