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
    private final Map<String, String> soundCategory = new HashMap<>(); 
    private final Map<String, Double> baseVolumes = new HashMap<>(); 

    private boolean backgroundMuted = false;
    private double masterVolume = 1.0;
    private double musicVolume = 1.0;
    private double sfxVolume = 1.0;

    private MusicManager() {
        loadSound("background", "/sounds/maingameaudio.mp3");
        loadSound("buttonClick", "/sounds/buttonClick.mp3");
        loadSound("win", "/sounds/win.mp3");
        loadSound("lose", "/sounds/lose.mp3");
        loadSound("punch", "/sounds/punch.mp3");
        loadSound("countdown", "/sounds/countdown.mp3");
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
                    soundCategory.put(soundKey, "music");
                } else {
                    soundCategory.put(soundKey, "sfx");
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
                baseVolumes.put(soundKey, volume);
                applyEffectiveVolume(soundKey);

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
        baseVolumes.put(soundKey, volume);
        applyEffectiveVolume(soundKey);
    }

    /** Computes and applies final volume for a sound, factoring in master/category volume and mute state. */
    private void applyEffectiveVolume(String soundKey) {
        MediaPlayer player = soundCache.get(soundKey);
        if (player == null) return;

        double base = baseVolumes.getOrDefault(soundKey, 1.0);
        String category = soundCategory.getOrDefault(soundKey, "sfx");
        double categoryVolume = category.equals("music") ? musicVolume : sfxVolume;

        double effective = backgroundMuted ? 0.0 : base * masterVolume * categoryVolume;
        try {
            player.setVolume(effective);
        } catch (Exception e) { /* Ignore */ }
    }

    private void reapplyAllVolumes() {
        for (String key : soundCache.keySet()) {
            applyEffectiveVolume(key);
        }
    }

    public boolean toggleBackgroundMuted() {
        backgroundMuted = !backgroundMuted;
        reapplyAllVolumes();
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

    // Master/music/SFX category volume controls, for Settings sliders

    public void setMasterVolume(double volume) {
        masterVolume = volume;
        reapplyAllVolumes();
    }

    public void setMusicVolume(double volume) {
        musicVolume = volume;
        reapplyAllVolumes();
    }

    public void setSfxVolume(double volume) {
        sfxVolume = volume;
        reapplyAllVolumes();
    }

    public double getMasterVolume() { return masterVolume; }
    public double getMusicVolume() { return musicVolume; }
    public double getSfxVolume() { return sfxVolume; }
}