package com.github.viseking.yuga.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.viseking.yuga.Main;

/**
 * MainMenuScreen: The primary entry screen for "Yuga: The Chronicles of the Dharma Warrior".
 * Based on the visual artwork in assets/demo-ui.jpg.
 *
 * Features:
 * - Full 1248x832 native virtual resolution matching demo-ui.jpg with letterbox/pillarbox FitViewport.
 * - Interactive 7-option main menu: CONTINUE, NEW GAME, LOAD GAME, CHAPTERS, COLLECTION, OPTIONS, EXIT.
 * - Smooth lerp-interpolated golden diamond cursor (❖) and glowing underline.
 * - Mouse hover and click detection with unprojected coordinate mapping.
 * - Keyboard navigation (W/S, Arrow Keys, Enter, Space, Escape).
 * - Procedural dynamic effects: Floating temple embers from ghat bonfires and Surya Mani radiant pulse.
 * - Interactive dialog / modal overlays for each menu selection.
 */
public class MainMenuScreen implements Screen, InputProcessor {

    public static final float VIRTUAL_WIDTH = 1248f;
    public static final float VIRTUAL_HEIGHT = 832f;

    // Interactive menu item bounds in 1248x832 virtual coordinates (Y-up)
    private static final float MENU_X_MIN = 985f;
    private static final float MENU_X_MAX = 1225f;
    private static final float MENU_ITEM_HEIGHT = 36f;

    public enum MenuItem {
        CONTINUE("CONTINUE", 482f, "Resume the sacred quest from last meditation..."),
        NEW_GAME("NEW GAME", 436f, "Begin a new pilgrimage across Bharatvarsha..."),
        LOAD_GAME("LOAD GAME", 397f, "Access the Vedic Memory archives..."),
        CHAPTERS("CHAPTERS", 358f, "Chronicles of Dharma: Acts I through V..."),
        COLLECTION("COLLECTION", 319f, "Divya Astras & Celestial Relics..."),
        OPTIONS("OPTIONS", 282f, "Harmonize Audio, Display, and Controls..."),
        EXIT("EXIT", 244f, "Depart from Bharatvarsha...");

        public final String label;
        public final float centerY;
        public final String description;

        MenuItem(String label, float centerY, String description) {
            this.label = label;
            this.centerY = centerY;
            this.description = description;
        }
    }

    // Modal dialog state
    public enum ActiveModal {
        NONE,
        CONTINUE_NOTICE,
        NEW_GAME_DIALOG,
        LOAD_GAME_DIALOG,
        CHAPTERS_DIALOG,
        COLLECTION_DIALOG,
        OPTIONS_DIALOG,
        EXIT_CONFIRM
    }

    private final Main game;
    private final SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture backgroundTexture;
    private Texture glowTexture;
    private Texture whitePixel;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    private int selectedIndex = 0;
    private float cursorY = MenuItem.CONTINUE.centerY;
    private float animTime = 0f;
    private ActiveModal activeModal = ActiveModal.NONE;

    // Dynamic ember particle system (temple fires at the ghats)
    private static class Ember {
        float x, y;
        float vx, vy;
        float life, maxLife;
        float size;
        float phase;

        void reset() {
            x = MathUtils.random(680f, 960f); // Ghat fire locations in center-right
            y = MathUtils.random(140f, 240f);
            vx = MathUtils.random(-15f, 15f);
            vy = MathUtils.random(25f, 70f);
            maxLife = MathUtils.random(2.5f, 4.5f);
            life = maxLife;
            size = MathUtils.random(2.5f, 5.5f);
            phase = MathUtils.random(0f, MathUtils.PI2);
        }
    }

    private final Array<Ember> embers = new Array<Ember>();
    private final Vector3 touchPoint = new Vector3();

    // Sacred colors matching STYLE.md
    private final Color suryaGold = new Color(1.0f, 0.843f, 0.0f, 1.0f);     // #FFD700
    private final Color keshariSaffron = new Color(1.0f, 0.478f, 0.094f, 1.0f); // #FF7A18
    private final Color parchmentBase = new Color(0.957f, 0.910f, 0.820f, 1.0f); // #F4E8D1

    public MainMenuScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.2f);
        glyphLayout = new GlyphLayout();

        // Load background artwork
        backgroundTexture = new Texture(Gdx.files.internal("demo-ui.jpg"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Generate procedural 1x1 white pixel for shapes
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        // Generate procedural radial glow texture
        int glowSize = 64;
        Pixmap glowMap = new Pixmap(glowSize, glowSize, Pixmap.Format.RGBA8888);
        for (int x = 0; x < glowSize; x++) {
            for (int y = 0; y < glowSize; y++) {
                float dx = (x - glowSize / 2f) / (glowSize / 2f);
                float dy = (y - glowSize / 2f) / (glowSize / 2f);
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float alpha = Math.max(0f, 1f - dist);
                alpha = alpha * alpha; // Quadratic falloff
                glowMap.setColor(1f, 1f, 1f, alpha);
                glowMap.drawPixel(x, y);
            }
        }
        glowTexture = new Texture(glowMap);
        glowTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        glowMap.dispose();

        // Initialize ember particles
        for (int i = 0; i < 40; i++) {
            Ember e = new Ember();
            e.reset();
            e.life = MathUtils.random(0f, e.maxLife); // Stagger initial life
            embers.add(e);
        }

        cursorY = MenuItem.values()[selectedIndex].centerY;
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        animTime += delta;

        // Smoothly lerp cursor position
        float targetY = MenuItem.values()[selectedIndex].centerY;
        cursorY = MathUtils.lerp(cursorY, targetY, delta * 14f);

        // Update ember particles
        updateEmbers(delta);

        // Clear screen with deep Vedic umber / black
        ScreenUtils.clear(0.04f, 0.03f, 0.05f, 1f);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // 1. Draw Background Artwork
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(backgroundTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.end();

        // 2. Draw Dynamic Environmental FX (Embers & Surya Mani Pulse)
        drawAtmosphericFX();

        // 3. Draw Interactive Menu Highlights (Golden diamond & glowing underline)
        if (activeModal == ActiveModal.NONE) {
            drawMenuHighlights();
        }

        // 4. Draw Active Modal Dialogs if any
        if (activeModal != ActiveModal.NONE) {
            drawActiveModal();
        }
    }

    /**
     * Draws procedural ambient effects:
     * - Pulsing aura on Yuga's Divya-Yuga Mani (X=237, Y=342)
     * - Rising fire sparks/embers from the riverside temple ghats
     */
    private void drawAtmosphericFX() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE); // Additive blending for glows

        batch.begin();

        // Pulsing Divya-Yuga Mani Solar Emblem
        float pulseAlpha = 0.35f + 0.20f * MathUtils.sin(animTime * 3.0f);
        float pulseScale = 90f + 12f * MathUtils.sin(animTime * 2.0f);
        batch.setColor(suryaGold.r, suryaGold.g, suryaGold.b, pulseAlpha);
        batch.draw(glowTexture, 237f - pulseScale / 2f, 342f - pulseScale / 2f, pulseScale, pulseScale);

        // Secondary warm core
        batch.setColor(1.0f, 0.95f, 0.8f, pulseAlpha * 0.8f);
        batch.draw(glowTexture, 237f - 30f, 342f - 30f, 60f, 60f);

        // Floating temple embers
        for (Ember e : embers) {
            float progress = e.life / e.maxLife;
            float alpha = MathUtils.sin(progress * MathUtils.PI);
            float colorLerp = progress;
            Color emberColor = keshariSaffron.cpy().lerp(suryaGold, colorLerp);
            batch.setColor(emberColor.r, emberColor.g, emberColor.b, alpha * 0.85f);
            batch.draw(glowTexture, e.x - e.size / 2f, e.y - e.size / 2f, e.size, e.size);
        }

        batch.end();
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Draws the interactive selection cursor, glowing underline, and active pill plate.
     */
    private void drawMenuHighlights() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Active Item Highlight Bar (Translucent warm dark pill)
        shapeRenderer.setColor(0.12f, 0.08f, 0.04f, 0.45f);
        shapeRenderer.rect(MENU_X_MIN, cursorY - 17f, MENU_X_MAX - MENU_X_MIN, 34f);

        // Golden Underline with smooth glowing alpha
        float pulse = 0.75f + 0.25f * MathUtils.sin(animTime * 6f);
        shapeRenderer.setColor(suryaGold.r, suryaGold.g, suryaGold.b, 0.85f * pulse);
        shapeRenderer.rect(MENU_X_MIN + 5f, cursorY - 17f, (MENU_X_MAX - MENU_X_MIN) - 10f, 1.8f);

        shapeRenderer.end();

        // Glowing 4-Pointed Golden Selection Diamond (❖) on the left
        batch.begin();
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE); // Additive glow
        float diamondGlow = 40f + 6f * MathUtils.sin(animTime * 5f);
        batch.setColor(suryaGold.r, suryaGold.g, suryaGold.b, 0.9f * pulse);
        batch.draw(glowTexture, 1008f - diamondGlow / 2f, cursorY - diamondGlow / 2f, diamondGlow, diamondGlow);
        batch.end();

        // Sharp golden diamond core
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float dSize = 7.5f;
        float dX = 1008f;
        float dY = cursorY;

        // Outer diamond
        shapeRenderer.setColor(suryaGold.r, suryaGold.g, suryaGold.b, 1.0f);
        shapeRenderer.triangle(dX, dY + dSize, dX + dSize, dY, dX, dY - dSize);
        shapeRenderer.triangle(dX, dY + dSize, dX - dSize, dY, dX, dY - dSize);

        // Bright inner diamond
        shapeRenderer.setColor(1f, 1f, 0.85f, 1.0f);
        float innerSize = 4f;
        shapeRenderer.triangle(dX, dY + innerSize, dX + innerSize, dY, dX, dY - innerSize);
        shapeRenderer.triangle(dX, dY + innerSize, dX - innerSize, dY, dX, dY - innerSize);

        shapeRenderer.end();
    }

    /**
     * Draws modal dialogue overlays when an option is selected.
     */
    private void drawActiveModal() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Dark dim backdrop
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.72f);
        shapeRenderer.rect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        // Modal Box
        float boxW = 680f;
        float boxH = 420f;
        float boxX = (VIRTUAL_WIDTH - boxW) / 2f;
        float boxY = (VIRTUAL_HEIGHT - boxH) / 2f;

        // Dark parchment background
        shapeRenderer.setColor(0.08f, 0.06f, 0.05f, 0.95f);
        shapeRenderer.rect(boxX, boxY, boxW, boxH);

        // Ornate golden border
        shapeRenderer.setColor(suryaGold.r, suryaGold.g, suryaGold.b, 0.85f);
        shapeRenderer.rect(boxX, boxY, boxW, 2.5f);
        shapeRenderer.rect(boxX, boxY + boxH - 2.5f, boxW, 2.5f);
        shapeRenderer.rect(boxX, boxY, 2.5f, boxH);
        shapeRenderer.rect(boxX + boxW - 2.5f, boxY, 2.5f, boxH);

        // Inner decorative margin
        shapeRenderer.setColor(keshariSaffron.r, keshariSaffron.g, keshariSaffron.b, 0.4f);
        shapeRenderer.rect(boxX + 6f, boxY + 6f, boxW - 12f, 1.2f);
        shapeRenderer.rect(boxX + 6f, boxY + boxH - 7.2f, boxW - 12f, 1.2f);
        shapeRenderer.rect(boxX + 6f, boxY + 6f, 1.2f, boxH - 12f);
        shapeRenderer.rect(boxX + boxW - 7.2f, boxY + 6f, 1.2f, boxH - 12f);

        shapeRenderer.end();

        // Render Modal Text Content
        batch.begin();
        font.setColor(suryaGold);

        String title = "";
        String line1 = "";
        String line2 = "";
        String line3 = "";
        String footer = "[ESC] Return to Menu   |   [ENTER] Confirm";

        switch (activeModal) {
            case CONTINUE_NOTICE:
                title = "CONTINUING PILGRIMAGE";
                line1 = "Resuming Chapter I: The Orphan of Kailash";
                line2 = "Location: Sacred Lake Mansarovar Altar";
                line3 = "Preparing Vedic shaders and prana conduits...";
                break;
            case NEW_GAME_DIALOG:
                title = "BEGIN NEW PILGRIMAGE";
                line1 = "Awaken the scion of Aryavarta-Loka upon Earth.";
                line2 = "Select Path: [1] Seeker (Story)   [2] Dharma (Normal)   [3] Tapasya (Hard)";
                line3 = "Are you prepared to face the legions of Kali Asura?";
                break;
            case LOAD_GAME_DIALOG:
                title = "VEDIC MEMORY ARCHIVES";
                line1 = "Slot 1: Kashi Vishwanath - Subterranean Sanctum (Level 12)";
                line2 = "Slot 2: Kedarnath Peak - Trial of Tyaga (Level 8)";
                line3 = "Slot 3: [Empty Tapasya Memory Slot]";
                break;
            case CHAPTERS_DIALOG:
                title = "CHRONICLES OF DHARMA";
                line1 = "Act I: The Orphan of Kailash   •   Act II: The Clarion Call of Kashi";
                line2 = "Act III: The Three Trials of Dharma (Bodh Gaya, Ganga, Kedarnath)";
                line3 = "Act IV: Reclaiming the Mani   •   Act V: The Stand of the Dharma Yoddha";
                break;
            case COLLECTION_DIALOG:
                title = "SACRED DIVYA ASTRAS & RELICS";
                line1 = "• Divya-Yuga Mani: Primordial Solar Pendant of Surya & Trideva";
                line2 = "• Surya Kavacha: Indestructible Golden Armor of Tejas";
                line3 = "• Agneyastra: Pure Purifying Solar Arrows of Lord Surya";
                break;
            case OPTIONS_DIALOG:
                title = "VEDIC HARMONICS & DISPLAY";
                line1 = "Master Volume: [ <  100%  > ]       Music Volume: [ <  90%  > ]";
                line2 = "Sound FX: [ <  85%  > ]            Display: [ Windowed (1248x832) ]";
                line3 = "V-Sync: [ Enabled ]                Language: [ English / Sanskrit ]";
                break;
            case EXIT_CONFIRM:
                title = "DEPART FROM BHARATVARSHA?";
                line1 = "The sacred soil of Bhumi Devi awaits your vigilance.";
                line2 = "Dharma is not given. It is chosen.";
                line3 = "Do you truly wish to exit to desktop?";
                footer = "[ENTER / Y] Exit Game       [ESC / N] Remain in Dharma";
                break;
            default:
                break;
        }

        // Draw Title
        glyphLayout.setText(font, title);
        font.draw(batch, title, (VIRTUAL_WIDTH - glyphLayout.width) / 2f, boxY + boxH - 45f);

        // Draw Content Lines
        font.setColor(parchmentBase);
        glyphLayout.setText(font, line1);
        font.draw(batch, line1, (VIRTUAL_WIDTH - glyphLayout.width) / 2f, boxY + boxH - 120f);

        glyphLayout.setText(font, line2);
        font.draw(batch, line2, (VIRTUAL_WIDTH - glyphLayout.width) / 2f, boxY + boxH - 170f);

        font.setColor(keshariSaffron);
        glyphLayout.setText(font, line3);
        font.draw(batch, line3, (VIRTUAL_WIDTH - glyphLayout.width) / 2f, boxY + boxH - 220f);

        // Draw Footer Hint
        font.setColor(suryaGold);
        glyphLayout.setText(font, footer);
        font.draw(batch, footer, (VIRTUAL_WIDTH - glyphLayout.width) / 2f, boxY + 45f);

        batch.end();
    }

    private void updateEmbers(float delta) {
        for (Ember e : embers) {
            e.life -= delta;
            e.x += (e.vx + MathUtils.sin(e.phase + animTime * 3f) * 12f) * delta;
            e.y += e.vy * delta;

            if (e.life <= 0 || e.y > 550f) {
                e.reset();
            }
        }
    }

    private void selectMenuItem(MenuItem item) {
        switch (item) {
            case CONTINUE:
                activeModal = ActiveModal.CONTINUE_NOTICE;
                break;
            case NEW_GAME:
                activeModal = ActiveModal.NEW_GAME_DIALOG;
                break;
            case LOAD_GAME:
                activeModal = ActiveModal.LOAD_GAME_DIALOG;
                break;
            case CHAPTERS:
                activeModal = ActiveModal.CHAPTERS_DIALOG;
                break;
            case COLLECTION:
                activeModal = ActiveModal.COLLECTION_DIALOG;
                break;
            case OPTIONS:
                activeModal = ActiveModal.OPTIONS_DIALOG;
                break;
            case EXIT:
                activeModal = ActiveModal.EXIT_CONFIRM;
                break;
        }
    }

    // InputProcessor Implementation

    @Override
    public boolean keyDown(int keycode) {
        if (activeModal != ActiveModal.NONE) {
            if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.N) {
                activeModal = ActiveModal.NONE;
                return true;
            }
            if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE || keycode == Input.Keys.Y) {
                if (activeModal == ActiveModal.EXIT_CONFIRM) {
                    Gdx.app.exit();
                } else if (activeModal == ActiveModal.CONTINUE_NOTICE || activeModal == ActiveModal.NEW_GAME_DIALOG) {
                    game.setScreen(new GameScreen(game));
                } else {
                    activeModal = ActiveModal.NONE;
                }
                return true;
            }
            return false;
        }

        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = MenuItem.values().length - 1;
            }
            return true;
        }

        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            selectedIndex++;
            if (selectedIndex >= MenuItem.values().length) {
                selectedIndex = 0;
            }
            return true;
        }

        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
            selectMenuItem(MenuItem.values()[selectedIndex]);
            return true;
        }

        if (keycode == Input.Keys.ESCAPE) {
            activeModal = ActiveModal.EXIT_CONFIRM;
            return true;
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        viewport.unproject(touchPoint.set(screenX, screenY, 0));

        if (activeModal != ActiveModal.NONE) {
            float boxW = 680f;
            float boxH = 420f;
            float boxX = (VIRTUAL_WIDTH - boxW) / 2f;
            float boxY = (VIRTUAL_HEIGHT - boxH) / 2f;

            // Click outside closes modal
            if (touchPoint.x < boxX || touchPoint.x > boxX + boxW || touchPoint.y < boxY || touchPoint.y > boxY + boxH) {
                activeModal = ActiveModal.NONE;
                return true;
            }
            // Click inside closes modal as confirm
            if (activeModal == ActiveModal.EXIT_CONFIRM) {
                Gdx.app.exit();
            } else if (activeModal == ActiveModal.CONTINUE_NOTICE || activeModal == ActiveModal.NEW_GAME_DIALOG) {
                game.setScreen(new GameScreen(game));
            } else {
                activeModal = ActiveModal.NONE;
            }
            return true;
        }

        // Check if clicked any menu item
        if (touchPoint.x >= MENU_X_MIN && touchPoint.x <= MENU_X_MAX) {
            MenuItem[] items = MenuItem.values();
            for (int i = 0; i < items.length; i++) {
                float y = items[i].centerY;
                if (touchPoint.y >= y - MENU_ITEM_HEIGHT / 2f && touchPoint.y <= y + MENU_ITEM_HEIGHT / 2f) {
                    selectedIndex = i;
                    selectMenuItem(items[i]);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (activeModal != ActiveModal.NONE) return false;

        viewport.unproject(touchPoint.set(screenX, screenY, 0));

        if (touchPoint.x >= MENU_X_MIN && touchPoint.x <= MENU_X_MAX) {
            MenuItem[] items = MenuItem.values();
            for (int i = 0; i < items.length; i++) {
                float y = items[i].centerY;
                if (touchPoint.y >= y - MENU_ITEM_HEIGHT / 2f && touchPoint.y <= y + MENU_ITEM_HEIGHT / 2f) {
                    selectedIndex = i;
                    return true;
                }
            }
        }
        return false;
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (glowTexture != null) glowTexture.dispose();
        if (whitePixel != null) whitePixel.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
    }
}
