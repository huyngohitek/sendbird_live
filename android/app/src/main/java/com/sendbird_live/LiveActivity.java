package com.sendbird_live;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.sendbird.live.Host;
import com.sendbird.live.LiveEvent;
import com.sendbird.live.LiveEventListener;
import com.sendbird.live.MediaOptions;
import com.sendbird.live.ParticipantCountInfo;
import com.sendbird.live.SendbirdLive;
import com.sendbird.webrtc.SendbirdException;
import com.sendbird.webrtc.SendbirdVideoView;
import com.sendbird.webrtc.handler.CompletionHandler;

import org.webrtc.RendererCommon;

import java.util.List;
import java.util.Map;

public class LiveActivity extends FragmentActivity implements LiveEventListener {
    String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        String liveEventId = getIntent().getStringExtra("LiveEventId");
        boolean isHost = getIntent().getBooleanExtra("isHost", false);
        assert liveEventId != null;
        if (!isHost){
            findViewById(R.id.btnStartLive).setVisibility(View.INVISIBLE);
        }
        SendbirdLive.getLiveEvent(liveEventId, (liveEvent, e) -> {
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
                MediaOptions mediaOptions = new MediaOptions(null,null,true,true,null);
                liveEvent.startEvent(mediaOptions,e3 -> {

                    if (e3 != null) {
                        Log.d(TAG, e3.getMessage());
                        return;
                    }
                    Log.d(TAG, "Live Event has started");
                });
            });
            if (isHost) {
                SendbirdVideoView hostView = findViewById(R.id.sendbirdVideoLiveView);
                hostView.setMirror(false);
                hostView.setEnableHardwareScaler(true);
                hostView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                String hostId = liveEvent.getHost().getHostId();
                liveEvent.setVideoViewForLiveEvent(hostView, hostId);
            }

        });
    }

    @Override
    public void onCustomItemsDelete(@NonNull LiveEvent liveEvent, @NonNull Map<String, String> map, @NonNull List<String> list) {

    }

    @Override
    public void onCustomItemsUpdate(@NonNull LiveEvent liveEvent, @NonNull Map<String, String> map, @NonNull List<String> list) {

    }

    @Override
    public void onDisconnected(@NonNull LiveEvent liveEvent, @NonNull SendbirdException e) {

    }

    @Override
    public void onHostConnected(@NonNull LiveEvent liveEvent, @NonNull Host host) {

    }

    @Override
    public void onHostDisconnected(@NonNull LiveEvent liveEvent, @NonNull Host host) {

    }

    @Override
    public void onHostEntered(@NonNull LiveEvent liveEvent, @NonNull Host host) {
        SendbirdVideoView hostView = findViewById(R.id.sendbirdVideoLiveView);
        hostView.setMirror(false);
        hostView.setEnableHardwareScaler(true);
        hostView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        liveEvent.setVideoViewForLiveEvent(hostView, liveEvent.getHost().getHostId());
    }

    @Override
    public void onHostExited(@NonNull LiveEvent liveEvent, @NonNull Host host) {

    }

    @Override
    public void onHostMuteAudio(@NonNull LiveEvent liveEvent, @NonNull Host host) {

    }

    @Override
    public void onHostStartVideo(@NonNull LiveEvent liveEvent, @NonNull Host host) {

    }

    @Override
    public void onHostStopVideo(@NonNull LiveEvent liveEvent, @NonNull Host host) {

    }

    @Override
    public void onHostUnmuteAudio(@NonNull LiveEvent liveEvent, @NonNull Host host) {

    }

    @Override
    public void onLiveEventEnded(@NonNull LiveEvent liveEvent) {
        new AlertDialog.Builder(this).setTitle("Live has ended").setMessage("Live has ended").setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    }

    @Override
    public void onLiveEventStarted(@NonNull LiveEvent liveEvent) {

    }

    @Override
    public void onParticipantCountChanged(@NonNull LiveEvent liveEvent, @NonNull ParticipantCountInfo participantCountInfo) {

    }

    @Override
    public void onReactionCountUpdated(@NonNull LiveEvent liveEvent, @NonNull String s, int i) {

    }
}