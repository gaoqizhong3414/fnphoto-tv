package com.fnphoto.tv.player;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class VideoRotationHelper {
    private static final String TAG = "VideoRotationHelper";

    public static int getVideoRotation(Uri videoUri, String token, String authx) {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            Map<String, String> headers = new HashMap<>();
            headers.put("accesstoken", token != null ? token : "");
            if (authx != null) {
                headers.put("authx", authx);
            }
            mmr.setDataSource(videoUri.toString(), headers);
            String rotation = mmr.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            if (rotation != null) {
                int rot = Integer.parseInt(rotation);
                Log.i(TAG, "Detected video rotation: " + rot);
                return rot;
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to detect video rotation", e);
        } finally {
            if (mmr != null) {
                try { mmr.release(); } catch (Exception ignored) {}
            }
        }
        return 0;
    }

    public static void applyRotationToPlayerView(final ViewGroup playerViewRoot, final int rotation) {
        if (rotation == 0 || playerViewRoot == null) return;

        playerViewRoot.post(new Runnable() {
            @Override
            public void run() {
                try {
                    playerViewRoot.setPivotX(playerViewRoot.getWidth() / 2f);
                    playerViewRoot.setPivotY(playerViewRoot.getHeight() / 2f);
                    playerViewRoot.setRotation(rotation);
                    Log.i(TAG, "Applied rotation " + rotation + " to PlayerView root");
                } catch (Exception e) {
                    Log.w(TAG, "Failed to apply rotation to root view", e);
                }
            }
        });
    }
}
