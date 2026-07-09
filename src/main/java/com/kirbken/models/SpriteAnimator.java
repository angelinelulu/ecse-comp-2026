package com.kirbken.models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

/** Handles per-character sprite frame swapping based on animation state. */
public class SpriteAnimator {

    public enum State { IDLE, WALK, ATTACK, SPECIAL_WINDUP,  SPECIAL_WINDUP_2, SPECIAL_THROW }

    private final ImageView view;
    private final Map<State, Image[]> frames = new HashMap<>();
    private State currentState = State.IDLE;

    private int frameIndex = 0;
    private long lastFrameTime = 0;
    private static final long FRAME_DURATION_NS = 150_000_000L;  

    public SpriteAnimator(ImageView view) {
        this.view = view;
    }

    public void addFrames(State state, String... resourcePaths) {
        Image[] images = new Image[resourcePaths.length];
        for (int i = 0; i < resourcePaths.length; i++) {
            var url = getClass().getResource(resourcePaths[i]);
            if (url != null) {
                images[i] = new Image(url.toExternalForm());
            } else {
                System.out.println("Missing sprite frame: " + resourcePaths[i]);
            }
        }
        frames.put(state, images);
    }

    public void setState(State state) {
        if (currentState != state) {
            currentState = state;
            frameIndex = 0;
            lastFrameTime = 0;
        }
    }

    public void update(long now) {
        Image[] currentFrames = frames.get(currentState);
        if (currentFrames == null || currentFrames.length == 0) return;

        if (currentFrames.length == 1) {
            view.setImage(currentFrames[0]);
            return;
        }

        if (lastFrameTime == 0) {
            lastFrameTime = now;
        }

        if (now - lastFrameTime >= FRAME_DURATION_NS) {
            frameIndex = (frameIndex + 1) % currentFrames.length;
            lastFrameTime = now;
        }

        view.setImage(currentFrames[frameIndex]);
    }

    public boolean hasFrames(State state) {
        Image[] f = frames.get(state);
        return f != null && f.length > 0;
    }
}