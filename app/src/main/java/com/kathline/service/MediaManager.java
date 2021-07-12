package com.kathline.service;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;

import androidx.annotation.RawRes;

import java.io.IOException;

public class MediaManager {
    private static MediaPlayer mMediaPlayer;

    private static boolean isPause;

    public static void playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        playSound(filePath, false, onCompletionListener);
    }

    public static void playSound(String filePath, boolean looping, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setLooping(looping);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void playRaw(Context context, @RawRes int id) {
        playRaw(context, id, false);
    }

    public static void playRaw(Context context, @RawRes int id, boolean looping) {
        float BEEP_VOLUME = 9.10f;
        AssetFileDescriptor file = context.getResources().openRawResourceFd(id);
        if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
//            mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(looping);
            mMediaPlayer.start();
        } catch (IOException e) {
            mMediaPlayer = null;
        }
    }

    public static void playAssets(Context context, String name) {
        playAssets(context, name, false);
    }

    public static void playAssets(Context context, String name, boolean looping) {
        if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd(name);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),
                    fileDescriptor.getStartOffset());
            mMediaPlayer.setLooping(looping);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            mMediaPlayer = null;
        }
    }

    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static void resume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
