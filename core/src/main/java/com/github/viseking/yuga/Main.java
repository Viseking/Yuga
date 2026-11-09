package com.github.viseking.yuga;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.viseking.yuga.screens.MainMenuScreen;

/**
 * Main game application class for Yuga: The Chronicles of the Dharma Warrior.
 * Extends {@link Game} to manage screen lifecycles.
 */
public class Main extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void render() {
        super.render(); // Delegates rendering to the active Screen (MainMenuScreen)
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) {
            batch.dispose();
        }
    }
}
