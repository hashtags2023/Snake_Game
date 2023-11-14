package com.gamecodeschool.c17snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.io.IOException;

public class SoundManager {

    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    public SoundManager(Context context)  {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        mSP = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);
        }catch (Exception ignored){

        }

    }

    public void playEatSound() {
        mSP.play(mEat_ID, 1, 1, 0, 0, 1);
    }

    public void playCrashSound() {
        mSP.play(mCrashID, 1, 1, 0, 0, 1);
    }
}
