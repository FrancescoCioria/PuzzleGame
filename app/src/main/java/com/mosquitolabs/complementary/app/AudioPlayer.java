package com.mosquitolabs.complementary.app;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by francesco on 5/31/14.
 */


public class AudioPlayer {

    private static AudioPlayer audioPlayer = new AudioPlayer();

    private int WIN;
    private int GAME_OVER;
    private int WRONG_MOVE;
    private int SELECT;
    private int DESELECT;

    private float volume;

    private Context context;
    private SoundPool sounds = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);

    public static AudioPlayer getIstance() {
        return audioPlayer;
    }

    public void initSounds(Context context) {
        this.context = context;
        WIN = sounds.load(context, R.raw.tada, 1);
        SELECT = sounds.load(context, R.raw.select, 1);
        DESELECT = sounds.load(context, R.raw.deselect, 1);
        GAME_OVER = sounds.load(context, R.raw.game_over, 1);
        WRONG_MOVE = sounds.load(context, R.raw.wrong_move, 1);
    }

    private void updateVolume() {
        AudioManager mgr = (AudioManager) context.getSystemService(
                Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        final float streamVolumeMax = mgr
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = streamVolumeCurrent / streamVolumeMax;
    }

    public void playWin() {
        updateVolume();
        sounds.play(WIN, volume, volume, 1, 0, 1.5f);
    }


    public void playGameOver() {
        updateVolume();
        sounds.play(GAME_OVER, volume, volume, 1, 0, 1.5f);
    }

    public void playSelect() {
        updateVolume();
        sounds.play(SELECT, volume, volume, 1, 0, 1.5f);
    }

    public void playDeselect() {
        updateVolume();
        sounds.play(DESELECT, volume, volume, 1, 0, 1.5f);
    }

    public void playWrongMove() {
        updateVolume();
        sounds.play(WRONG_MOVE, volume, volume, 1, 0, 1.5f);
    }

}
