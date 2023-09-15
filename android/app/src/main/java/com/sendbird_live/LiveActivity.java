package com.sendbird_live;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.sendbird.live.LiveEvent;
import com.sendbird.live.SendbirdLive;
import com.sendbird.live.handler.LiveEventHandler;
import com.sendbird.webrtc.SendbirdException;
import com.sendbird.webrtc.SendbirdVideoView;
import com.sendbird.webrtc.VideoDevice;

import org.webrtc.RendererCommon;

public class LiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        String liveEventId = getIntent().getStringExtra("LiveEventId");
        SendbirdLive.getLiveEvent(liveEventId, (liveEvent, e) -> {
            if (e != null) {
                Log.e("LiveActivity", e.getMessage());
                return;
            }
            findViewById(R.id.btnEndLive).setOnClickListener(view -> {
                liveEvent.endEvent(e1 -> {
                    if (e1 != null) {
                        Log.d("LiveActivity", e1.getMessage());
                        return;
                    }
                    Log.d("LiveActivity", "Live ended");

                });
            });
//            liveEvent.selectVideoDevice();
//            SendbirdVideoView hostView = new SendbirdVideoView(this);
            SendbirdVideoView hostView = findViewById(R.id.sendbirdVideoLiveView);
            hostView.setMirror(false);
            hostView.setEnableHardwareScaler(true);
            hostView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

            String hostId = liveEvent.getHost().getHostId();
            liveEvent.setVideoViewForLiveEvent(hostView, hostId);
//            liveEvent.setVideoViewForLiveEvent(hostView, hostId);
//            liveEvent.startVideo(e1 -> {
//                if (e1 != null) {
//                    Log.d("LiveActivitystartVideo", e1.getMessage());
//                    return;
//                }
//                Log.d("LiveActivity", "start video");
//            });
        });
    }
}