package com.github.viseking.yuga.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.github.viseking.yuga.world.IsometricMap;
import com.github.viseking.yuga.world.IsometricTile;

/**
 * PlayerCharacter: Yuga, Prince of Aryavarta-Loka.
 * Loads and renders 8-directional animated sprites from assets/MeleeCharacter/.
 * Handles movement on the 2:1 isometric grid, collision sliding, and martial combat.
 */
public class PlayerCharacter implements Disposable {

    public static final int FRAME_WIDTH = 126;
    public static final int FRAME_HEIGHT = 132;

    public enum State {
        IDLE,
        RUNNING,
        ATTACKING
    }

    // 8-Directional Indices matching MeleeCharacter dir1 through dir8
    public static final int DIR_SW = 0; // dir1: South-West
    public static final int DIR_W  = 1; // dir2: West
    public static final int DIR_NW = 2; // dir3: North-West
    public static final int DIR_N  = 3; // dir4: North
    public static final int DIR_NE = 4; // dir5: North-East
    public static final int DIR_E  = 5; // dir6: East
    public static final int DIR_SE = 6; // dir7: South-East
    public static final int DIR_S  = 7; // dir8: South

    private float gridX = 11.5f; // Spawn in center of 24x24 temple courtyard
    private float gridY = 11.5f;

    private float screenX;
    private float screenY;

    private int direction = DIR_S; // Start facing camera
    private State state = State.IDLE;
    private float stateTime = 0f;

    // Movement parameters
    private float baseSpeed = 4.2f;   // Tiles per second
    private float sprintSpeed = 6.5f; // Holding shift

    // Stats
    private float health = 100f;
    private float maxHealth = 100f;
    private float prana = 100f;
    private float maxPrana = 100f;

    // Animations: 8 directions for each state
    @SuppressWarnings("unchecked")
    private final Animation<TextureRegion>[] idleAnims = new Animation[8];
    @SuppressWarnings("unchecked")
    private final Animation<TextureRegion>[] runAnims = new Animation[8];
    @SuppressWarnings("unchecked")
    private final Animation<TextureRegion>[] attackAnims = new Animation[8];

    private final Array<Texture> loadedTextures = new Array<Texture>();
    private Texture shadowTexture;
    private Texture glowTexture;

    public PlayerCharacter() {
        loadAnimations();
        createProceduralTextures();
        updateScreenPosition();
    }

    /**
     * Loads 8-directional spritesheets for Idle, Run, and Attack states.
     */
    private void loadAnimations() {
        for (int d = 1; d <= 8; d++) {
            int dirIdx = d - 1;

            // 1. Idle1: 31 frames, 0.045s duration
            String idlePath = "MeleeCharacter/Idle1/Boxer__Idle1_dir" + d + ".png";
            idleAnims[dirIdx] = loadSheetAnimation(idlePath, 31, 0.045f, Animation.PlayMode.LOOP);

            // 2. Run1: 12 frames, 0.065s duration
            String runPath = "MeleeCharacter/Run1/Boxer__Run1_dir" + d + ".png";
            runAnims[dirIdx] = loadSheetAnimation(runPath, 12, 0.065f, Animation.PlayMode.LOOP);

            // 3. Attack (LeftJab): 15 frames, 0.035s duration
            String attackPath = "MeleeCharacter/LeftJab/Boxer__LeftJab_dir" + d + ".png";
            attackAnims[dirIdx] = loadSheetAnimation(attackPath, 15, 0.035f, Animation.PlayMode.NORMAL);
        }
    }

    private Animation<TextureRegion> loadSheetAnimation(String path, int maxFrames, float frameDuration, Animation.PlayMode mode) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        loadedTextures.add(texture);

        int cols = texture.getWidth() / FRAME_WIDTH;
        int rows = texture.getHeight() / FRAME_HEIGHT;
        TextureRegion[][] split = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);

        Array<TextureRegion> frames = new Array<TextureRegion>();
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (count < maxFrames) {
                    frames.add(split[r][c]);
                    count++;
                }
            }
        }
        return new Animation<TextureRegion>(frameDuration, frames, mode);
    }

    private void createProceduralTextures() {
        // Soft elliptical drop shadow
        int sw = 64;
        int sh = 32;
        Pixmap shadowMap = new Pixmap(sw, sh, Pixmap.Format.RGBA8888);
        for (int x = 0; x < sw; x++) {
            for (int y = 0; y < sh; y++) {
                float dx = (x - sw / 2f) / (sw / 2f);
                float dy = (y - sh / 2f) / (sh / 2f);
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float alpha = Math.max(0f, 1f - dist);
                shadowMap.setColor(0f, 0f, 0f, alpha * 0.45f);
                shadowMap.drawPixel(x, y);
            }
        }
        shadowTexture = new Texture(shadowMap);
        shadowTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        shadowMap.dispose();

        // Prana radial glow
        int gw = 48;
        Pixmap glowMap = new Pixmap(gw, gw, Pixmap.Format.RGBA8888);
        for (int x = 0; x < gw; x++) {
            for (int y = 0; y < gw; y++) {
                float dx = (x - gw / 2f) / (gw / 2f);
                float dy = (y - gw / 2f) / (gw / 2f);
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float alpha = Math.max(0f, 1f - dist);
                glowMap.setColor(1f, 1f, 1f, alpha * alpha);
                glowMap.drawPixel(x, y);
            }
        }
        glowTexture = new Texture(glowMap);
        glowTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        glowMap.dispose();
    }

    /**
     * Handles keyboard inputs, updates physics, states, and collision bounds.
     */
    public void update(float delta, IsometricMap map) {
        stateTime += delta;

        // Recharging prana
        if (prana < maxPrana) {
            prana = Math.min(maxPrana, prana + delta * 8f);
        }

        // If in attack animation, wait until finished
        if (state == State.ATTACKING) {
            if (attackAnims[direction].isAnimationFinished(stateTime)) {
                state = State.IDLE;
                stateTime = 0f;
            }
            return;
        }

        // Trigger Attack
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.J) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            state = State.ATTACKING;
            stateTime = 0f;
            prana = Math.max(0, prana - 5f);
            return;
        }

        // Check screen movement inputs
        float moveX = 0f;
        float moveY = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    moveY += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))  moveY -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveX += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))  moveX -= 1f;

        boolean isMoving = (moveX != 0f || moveY != 0f);

        if (isMoving) {
            // Determine 8-way facing direction based on screen vector
            direction = computeDirection(moveX, moveY);

            // Normalize diagonal vector
            if (moveX != 0f && moveY != 0f) {
                moveX *= 0.7071f;
                moveY *= 0.7071f;
            }

            boolean isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
            float currentSpeed = isSprinting ? sprintSpeed : baseSpeed;

            // Convert screen-relative movement vector to isometric grid displacement
            float halfW = IsometricTile.TILE_WIDTH / 2f;
            float halfH = IsometricTile.TILE_HEIGHT / 2f;
            float dGridX = (moveX / halfW + moveY / halfH) * 0.5f * currentSpeed * delta * 25f;
            float dGridY = (moveY / halfH - moveX / halfW) * 0.5f * currentSpeed * delta * 25f;

            // Move with axis-independent sliding on walls
            float newGridX = gridX + dGridX;
            float newGridY = gridY + dGridY;

            if (map.isWalkable(newGridX, gridY)) {
                gridX = newGridX;
            }
            if (map.isWalkable(gridX, newGridY)) {
                gridY = newGridY;
            }

            if (state != State.RUNNING) {
                state = State.RUNNING;
                stateTime = 0f;
            }
        } else {
            if (state != State.IDLE) {
                state = State.IDLE;
                stateTime = 0f;
            }
        }

        updateScreenPosition();
    }

    private int computeDirection(float moveX, float moveY) {
        int sx = (int) Math.signum(moveX);
        int sy = (int) Math.signum(moveY);

        if (sx < 0 && sy < 0) return DIR_SW; // dir1
        if (sx < 0 && sy == 0) return DIR_W;  // dir2
        if (sx < 0 && sy > 0) return DIR_NW; // dir3
        if (sx == 0 && sy > 0) return DIR_N;  // dir4
        if (sx > 0 && sy > 0) return DIR_NE; // dir5
        if (sx > 0 && sy == 0) return DIR_E;  // dir6
        if (sx > 0 && sy < 0) return DIR_SE; // dir7
        return DIR_S;                         // dir8 (sx == 0 && sy < 0)
    }

    private void updateScreenPosition() {
        screenX = (gridX - gridY) * (IsometricTile.TILE_WIDTH / 2f);
        screenY = (gridX + gridY) * (IsometricTile.TILE_HEIGHT / 2f);
    }

    /**
     * Renders shadow, character sprite frame, and dynamic prana aura.
     */
    public void render(SpriteBatch batch) {
        // 1. Draw Drop Shadow beneath feet
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(shadowTexture, screenX - 30f, screenY - 14f, 60f, 24f);

        // 2. Draw Active Animated Character Frame
        Animation<TextureRegion> currentAnim;
        switch (state) {
            case RUNNING:
                currentAnim = runAnims[direction];
                break;
            case ATTACKING:
                currentAnim = attackAnims[direction];
                break;
            case IDLE:
            default:
                currentAnim = idleAnims[direction];
                break;
        }

        TextureRegion frame = currentAnim.getKeyFrame(stateTime);

        // Feet are anchored at (screenX, screenY)
        float drawX = screenX - FRAME_WIDTH / 2f;
        float drawY = screenY - 20f;

        batch.setColor(Color.WHITE);
        batch.draw(frame, drawX, drawY);

        // 3. Additive Golden Prana Glow when attacking or sprinting
        if (state == State.ATTACKING || (state == State.RUNNING && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            batch.setColor(1.0f, 0.84f, 0.0f, 0.40f);
            batch.draw(glowTexture, screenX - 32f, screenY + 20f, 64f, 64f);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public float getGridX() { return gridX; }
    public float getGridY() { return gridY; }
    public float getScreenX() { return screenX; }
    public float getScreenY() { return screenY; }
    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public float getPrana() { return prana; }
    public float getMaxPrana() { return maxPrana; }
    public State getState() { return state; }
    public int getDirection() { return direction; }

    @Override
    public void dispose() {
        for (Texture tex : loadedTextures) {
            if (tex != null) {
                tex.dispose();
            }
        }
        if (shadowTexture != null) shadowTexture.dispose();
        if (glowTexture != null) glowTexture.dispose();
    }
}

