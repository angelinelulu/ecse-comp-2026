package com.kirbken.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.HashMap;
import java.util.Map;

public class MusicManager {

    private static MusicManager instance;
    
    // Core storage for active audio tracks
    private final Map<String, MediaPlayer> soundCache = new HashMap<>();
    private boolean backgroundMuted = false;

    private MusicManager() {
        try {
            String musicFile = getClass().getResource("/sounds/maingameaudio.mp3").toExternalForm();
            Media media = new Media(musicFile);
            MediaPlayer backgroundPlayer = new MediaPlayer(media);
            backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundPlayer.setVolume(0.5);
            
            // Register the main track into the cache so your functions can find it via its key
            soundCache.put("background", backgroundPlayer);
        } catch (Exception e) {
            System.err.println("Could not initialize background music asset: " + e.getMessage());
        }
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void play() {
        MediaPlayer backgroundPlayer = soundCache.get("background");
        if (backgroundPlayer != null && backgroundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            backgroundPlayer.play();
        }
    }

    public void pause() {
        MediaPlayer backgroundPlayer = soundCache.get("background");
        if (backgroundPlayer != null) {
            backgroundPlayer.pause();
        }
    }

    /** Stops all currently playing sounds. */
    public void stopAllSounds() {
        // Stop any ongoing audio transitions
        for (MediaPlayer player : soundCache.values()) {
            if (player != null) {
                try {
                    player.stop();
                } catch (Exception e) {
                    System.err.println("Error stopping sound: " + e.getMessage());
                }
            }
        }
    }

    /** Disposes of all MediaPlayer resources. */
    public void dispose() {
        // Dispose all MediaPlayers to free resources
        for (MediaPlayer player : soundCache.values()) {
            if (player != null) {
                try {
                    player.dispose();
                } catch (Exception e) {
                    System.err.println("Error disposing sound: " + e.getMessage());
                }
            }
        }
        soundCache.clear();
    }

    /**
     * Sets the volume for a specific sound.
     *
     * @param soundKey the key identifying the sound
     * @param volume the volume level (0.0 to 1.0)
     */
    public void setVolume(String soundKey, double volume) {
        MediaPlayer player = soundCache.get(soundKey);
        if (player != null) {
            try {
                player.setVolume(volume);
            } catch (Exception e) {
                System.err.println("Error setting volume for " + soundKey + ": " + e.getMessage());
            }
        }
    }

    /**
     * Sets the playback rate for a specific sound.
     *
     * @param soundKey the key identifying the sound
     * @param rate the playback rate (1.0 is normal speed)
     */
    public void setRate(String soundKey, double rate) {
        MediaPlayer player = soundCache.get(soundKey);
        if (player != null) {
            try {
                player.setRate(rate);
            } catch (Exception e) {
                System.err.println("Error setting rate for " + soundKey + ": " + e.getMessage());
            }
        }
    }

    public boolean toggleBackgroundMuted() {
        backgroundMuted = !backgroundMuted;

        // Set background music volume using the unified key tracking
        setVolume("background", backgroundMuted ? 0.0 : 0.25);

        return backgroundMuted;
    }

    public void stopSound(String soundKey) {
        MediaPlayer player = soundCache.get(soundKey);
        if (player != null) {
            try {
                player.stop();
            } catch (Exception e) {
                System.err.println("Error stopping sound " + soundKey + ": " + e.getMessage());
            }
        }
    }
}