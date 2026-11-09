package com.github.viseking.yuga.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.viseking.yuga.Main;
import com.github.viseking.yuga.entity.PlayerCharacter;
import com.github.viseking.yuga.world.IsometricMap;

/**
 * GameScreen: The active gameplay screen for Yuga: The Chronicles of the Dharma Warrior.
 * Renders the procedurally generated 2:1 isometric temple courtyard,
 * controls the 8-directional animated player character, and handles HUD/combat.
 */
public class GameScreen implements Screen, InputProcessor {

    public static final float WORLD_WIDTH = 1248f;
    public static final float WORLD_HEIGHT = 832f;

    private final Main game;
    private final SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private OrthographicCamera worldCamera;
    private Viewport worldViewport;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private IsometricMap map;
    private PlayerCharacter player;
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    // Prana ambient particles floating in the courtyard
    private static class PranaParticle {
        float x, y;
        float vx, vy;
        float life, maxLife;
        float size;

        void reset(float spawnX, float spawnY) {
            x = spawnX + MathUtils.random(-500f, 500f);
            y = spawnY + MathUtils.random(-300f, 300f);
            vx = MathUtils.random(-15f, 15f);
            vy = MathUtils.random(15f, 40f);
            maxLife = MathUtils.random(2f, 4f);
            life = maxLife;
            size = MathUtils.random(2f, 4.5f);
        }
    }

    private final Array<PranaParticle> pranaParticles = new Array<PranaParticle>();
    private float totalTime = 0f;

    // Sacred Colors
    private final Color suryaGold = new Color(1.000f, 0.843f, 0.000f, 1.0f);     // #FFD700
    private final Color keshariSaffron = new Color(1.000f, 0.478f, 0.094f, 1.0f); // #FF7A18
    private final Color gangaAzure = new Color(0.220f, 0.714f, 1.000f, 1.0f);     // #38B6FF

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
    }

    @Override
    public void show() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, worldCamera);
        worldViewport.apply();

        uiCamera = new OrthographicCamera();
        uiViewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, uiCamera);
        uiViewport.apply();
        uiCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        uiCamera.update();

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.1f);
        glyphLayout = new GlyphLayout();

        map = new IsometricMap();
        player = new PlayerCharacter();

        // Snap camera directly to player on launch
        worldCamera.position.set(player.getScreenX(), player.getScreenY(), 0);
        worldCamera.update();

        // Initialize prana particles
        for (int i = 0; i < 35; i++) {
            PranaParticle p = new PranaParticle();
            p.reset(player.getScreenX(), player.getScreenY());
            p.life = MathUtils.random(0f, p.maxLife);
            pranaParticles.add(p);
        }

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        totalTime += delta;

        // 1. Update Game Logic & Physics
        player.update(delta, map);

        // Smooth camera follow
        float camLerp = delta * 6f;
        worldCamera.position.x = MathUtils.lerp(worldCamera.position.x, player.getScreenX(), camLerp);
        worldCamera.position.y = MathUtils.lerp(worldCamera.position.y, player.getScreenY() + 40f, camLerp);
        worldCamera.update();

        // Update prana particles
        for (PranaParticle p : pranaParticles) {
            p.life -= delta;
            p.x += p.vx * delta;
            p.y += p.vy * delta;
            if (p.life <= 0) {
                p.reset(player.getScreenX(), player.getScreenY());
            }
        }

        // 2. Clear Screen with deep cosmic midnight
        ScreenUtils.clear(0.06f, 0.05f, 0.09f, 1f);

        // 3. Render Isometric World (Map + Depth-Sorted Player)
        worldViewport.apply();
        batch.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.setProjectionMatrix(worldCamera.combined);

        batch.begin();
        map.render(batch, player);
        batch.end();

        // 4. Render Ambient Prana Sparks in World
        renderWorldParticles();

        // 5. Render HUD Overlay
        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        renderHUD();
    }

    private void renderWorldParticles() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE); // Additive

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (PranaParticle p : pranaParticles) {
            float progress = p.life / p.maxLife;
            float alpha = MathUtils.sin(progress * MathUtils.PI);
            shapeRenderer.setColor(suryaGold.r, suryaGold.g, suryaGold.b, alpha * 0.75f);
            shapeRenderer.circle(p.x, p.y, p.size);
        }
        shapeRenderer.end();

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Renders character status bars, active astra badge, and controls helper.
     */
    private void renderHUD() {
        float hudWidth = uiViewport.getWorldWidth();
        float hudHeight = uiViewport.getWorldHeight();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // --- TOP-LEFT: Character Health & Prana Frame ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Panel background
        shapeRenderer.setColor(0.08f, 0.06f, 0.05f, 0.85f);
        shapeRenderer.rect(20f, hudHeight - 110f, 320f, 90f);

        // Golden perimeter border
        shapeRenderer.setColor(suryaGold.r, suryaGold.g, suryaGold.b, 0.9f);
        shapeRenderer.rect(20f, hudHeight - 110f, 320f, 2f);
        shapeRenderer.rect(20f, hudHeight - 22f, 320f, 2f);
        shapeRenderer.rect(20f, hudHeight - 110f, 2f, 90f);
        shapeRenderer.rect(338f, hudHeight - 110f, 2f, 90f);

        // HP Bar Background
        shapeRenderer.setColor(0.2f, 0.08f, 0.08f, 1f);
        shapeRenderer.rect(90f, hudHeight - 65f, 230f, 16f);

        // HP Bar Fill (Vermilion/Keshari)
        float hpPercent = player.getHealth() / player.getMaxHealth();
        shapeRenderer.setColor(keshariSaffron.r, keshariSaffron.g, keshariSaffron.b, 1f);
        shapeRenderer.rect(90f, hudHeight - 65f, 230f * hpPercent, 16f);

        // Prana Bar Background
        shapeRenderer.setColor(0.05f, 0.15f, 0.25f, 1f);
        shapeRenderer.rect(90f, hudHeight - 95f, 230f, 16f);

        // Prana Bar Fill (Ganga Azure)
        float pranaPercent = player.getPrana() / player.getMaxPrana();
        shapeRenderer.setColor(gangaAzure.r, gangaAzure.g, gangaAzure.b, 1f);
        shapeRenderer.rect(90f, hudHeight - 95f, 230f * pranaPercent, 16f);

        // --- BOTTOM: Controls Helper Banner ---
        float bannerW = 680f;
        float bannerH = 34f;
        float bannerX = (hudWidth - bannerW) / 2f;
        shapeRenderer.setColor(0.08f, 0.06f, 0.05f, 0.75f);
        shapeRenderer.rect(bannerX, 15f, bannerW, bannerH);
        shapeRenderer.setColor(suryaGold.r, suryaGold.g, suryaGold.b, 0.6f);
        shapeRenderer.rect(bannerX, 15f, bannerW, 1.5f);
        shapeRenderer.rect(bannerX, 15f + bannerH, bannerW, 1.5f);

        shapeRenderer.end();

        // --- HUD Text ---
        batch.begin();

        // Portrait Icon Badge
        font.setColor(suryaGold);
        font.draw(batch, "YUGA", 35f, hudHeight - 45f);
        font.setColor(new Color(0.85f, 0.85f, 0.85f, 1f));
        font.draw(batch, "HP", 92f, hudHeight - 52f);
        font.draw(batch, "PRANA", 92f, hudHeight - 82f);

        // Top-Right Location
        String location = "Kailash Tapovan Sanctum  •  Act I";
        glyphLayout.setText(font, location);
        font.setColor(suryaGold);
        font.draw(batch, location, hudWidth - glyphLayout.width - 30f, hudHeight - 35f);

        String shloka = "धर्मो रक्षति रक्षितः";
        glyphLayout.setText(font, shloka);
        font.setColor(keshariSaffron);
        font.draw(batch, shloka, hudWidth - glyphLayout.width - 30f, hudHeight - 65f);

        // Controls hint text
        String controls = "[W A S D] Move   •   [SHIFT] Dash   •   [SPACE / J] Mallayuddha Strike   •   [ESC] Menu";
        glyphLayout.setText(font, controls);
        font.setColor(Color.WHITE);
        font.draw(batch, controls, (hudWidth - glyphLayout.width) / 2f, 38f);

        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            // Return to Main Menu
            game.setScreen(new MainMenuScreen(game));
            return true;
        }
        return false;
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, false);
        uiViewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (player != null) player.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
    }
}

