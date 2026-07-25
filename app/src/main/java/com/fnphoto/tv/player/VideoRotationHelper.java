package com.fnphoto.tv.player;

import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.TextureView;
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
                TextureView textureView = findTextureViewInHierarchy(playerViewRoot);
                if (textureView != null) {
                    applyRotationToTextureView(textureView, rotation, playerViewRoot);
                } else {
                    playerViewRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            playerViewRoot.removeOnLayoutChangeListener(this);
                            TextureView tv = findTextureViewInHierarchy(playerViewRoot);
                            if (tv != null) {
                                applyRotationToTextureView(tv, rotation, playerViewRoot);
                            }
                        }
                    });
                }
            }
        });
    }

    private static TextureView findTextureViewInHierarchy(View view) {
        if (view instanceof TextureView) return (TextureView) view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                TextureView found = findTextureViewInHierarchy(group.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }

    private static void applyRotationToTextureView(TextureView textureView, int rotation,
                                                    ViewGroup parentView) {
        int vw = textureView.getWidth();
        int vh = textureView.getHeight();
        if (vw <= 0 || vh <= 0) {
            vw = parentView.getWidth();
            vh = parentView.getHeight();
        }
        if (vw <= 0 || vh <= 0) return;

        textureView.setOpaque(true);

        Matrix matrix = new Matrix();

        float cx = vw / 2f;
        float cy = vh / 2f;
        matrix.postRotate(rotation, cx, cy);

        textureView.setTransform(matrix);
        Log.i(TAG, "Applied rotation " + rotation + " to video TextureView (" + vw + "x" + vh + ")");
    }
}