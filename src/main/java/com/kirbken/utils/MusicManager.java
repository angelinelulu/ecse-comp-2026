package com.kirbken.utils;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {

    private static MusicManager instance;
    private final Map<String, MediaPlayer> soundCache = new HashMap<>();
    private boolean backgroundMuted = false;

    private MusicManager() {
        // Load the background stream track
        loadSound("background", "/sounds/maingameaudio.mp3");
        // FIX: Pre-load your missing sound effect asset so playSound() can find it!
        loadSound("buttonClick", "/sounds/buttonClick.mp3"); 
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    /** Helper method to cleanly register audio assets into the global cache */
    public void loadSound(String soundKey, String resourcePath) {
        try {
            var resource = getClass().getResource(resourcePath);
            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                MediaPlayer player = new MediaPlayer(media);
                if (soundKey.equals("background") || soundKey.equals("arena")) {
                    player.setCycleCount(MediaPlayer.INDEFINITE);
                }
                soundCache.put(soundKey, player);
            } else {
                System.err.println("Audio resource path missing: " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("Failed loading asset [" + soundKey + "]: " + e.getMessage());
        }
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

    public void playSound(String soundKey, double volume) {
        if (Platform.isFxApplicationThread()) {
            playSoundInternal(soundKey, volume);
        } else {
            Platform.runLater(() -> playSoundInternal(soundKey, volume));
        }
    }

    private void playSoundInternal(String soundKey, double volume) {
        MediaPlayer player = soundCache.get(soundKey);
        if (player != null) {
            try {
                player.seek(Duration.ZERO);
                // If the master mute toggle is active, force volume to silence
                player.setVolume(backgroundMuted ? 0.0 : volume);

                if (player.getStatus() == MediaPlayer.Status.READY
                    || player.getStatus() == MediaPlayer.Status.PAUSED
                    || player.getStatus() == MediaPlayer.Status.STOPPED) {
                    player.play();
                }
            } catch (Exception e) {
                System.err.println("Error playing sound: " + soundKey + " - " + e.getMessage());
            }
        } else {
            System.err.println("Sound not found in cache: " + soundKey);
        }
    }

    public void stopAllSounds() {
        for (MediaPlayer player : soundCache.values()) {
            if (player != null) {
                try { player.stop(); } catch (Exception e) { /* Ignore */ }
            }
        }
    }

    public void dispose() {
        for (MediaPlayer player : soundCache.values()) {
            if (player != null) {
                try { player.dispose(); } catch (Exception e) { /* Ignore */ }
            }
        }
        soundCache.clear();
    }

    public void setVolume(String soundKey, double volume) {
        MediaPlayer player = soundCache.get(soundKey);
        if (player != null) {
            try { player.setVolume(volume); } catch (Exception e) { /* Ignore */ }
        }
    }

    public boolean toggleBackgroundMuted() {
        backgroundMuted = !backgroundMuted;
        
        // Mute or restore both potential master track environments
        double bgVol = backgroundMuted ? 0.0 : 0.25;
        double arenaVol = backgroundMuted ? 0.0 : 0.3;
        
        setVolume("background", bgVol);
        setVolume("arena", arenaVol);

        return backgroundMuted;
    }

    public boolean isMuted() {
        return backgroundMuted;
    }

    public void stopSound(String soundKey) {
        MediaPlayer player = soundCache.get(soundKey);
        if (player != null) {
            try { player.stop(); } catch (Exception e) { /* Ignore */ }
        }
    }
}