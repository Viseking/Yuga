package com.github.viseking.yuga.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.github.viseking.yuga.entity.PlayerCharacter;

/**
 * Manages the 2:1 isometric grid, procedural map layout, coordinate transforms,
 * and depth-sorted rendering for the temple environment.
 */
public class IsometricMap implements Disposable {

    public static final int COLS = 24;
    public static final int ROWS = 24;

    private final IsometricTile.Type[][] grid = new IsometricTile.Type[COLS][ROWS];
    private final IsometricTile[] tiles = new IsometricTile[IsometricTile.Type.values().length];

    public IsometricMap() {
        // Initialize tile textures
        for (IsometricTile.Type type : IsometricTile.Type.values()) {
            tiles[type.ordinal()] = new IsometricTile(type);
        }

        generateTempleArena();
    }

    /**
     * Procedurally constructs a sacred Vedic temple arena:
     * - Outer border: Sacred Ganga water canals
     * - Perimeter walkway: Kashi Terracotta border slabs
     * - Courtyard floor: Sandstone pavers
     * - Central altar (6x6): Surya Gold consecrated sanctum
     * - Quadrant corners: Tapovan sacred moss gardens
     * - 4 Temple boundary pillars
     */
    private void generateTempleArena() {
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                // Outer perimeter water canals
                if (col == 0 || col == COLS - 1 || row == 0 || row == ROWS - 1) {
                    grid[col][row] = IsometricTile.Type.GANGA_WATER;
                }
                // Terracotta perimeter steps
                else if (col == 1 || col == COLS - 2 || row == 1 || row == ROWS - 2) {
                    grid[col][row] = IsometricTile.Type.TERRACOTTA_BORDER;
                }
                // Central Surya Gold Altar (6x6)
                else if (col >= 9 && col <= 14 && row >= 9 && row <= 14) {
                    grid[col][row] = IsometricTile.Type.SURYA_GOLD;
                }
                // Tapovan corner gardens
                else if ((col >= 2 && col <= 5 && row >= 2 && row <= 5) ||
                         (col >= 18 && col <= 21 && row >= 2 && row <= 5) ||
                         (col >= 2 && col <= 5 && row >= 18 && row <= 21) ||
                         (col >= 18 && col <= 21 && row >= 18 && row <= 21)) {
                    grid[col][row] = IsometricTile.Type.TAPOVAN_GRASS;
                }
                // Default sacred sandstone pavers
                else {
                    grid[col][row] = IsometricTile.Type.SANDSTONE_PAVER;
                }
            }
        }

        // Place 4 Temple Boundary Pillars
        grid[3][3] = IsometricTile.Type.TEMPLE_PILLAR;
        grid[20][3] = IsometricTile.Type.TEMPLE_PILLAR;
        grid[3][20] = IsometricTile.Type.TEMPLE_PILLAR;
        grid[20][20] = IsometricTile.Type.TEMPLE_PILLAR;
    }

    /**
     * Converts isometric grid coordinates (col, row) to world screen X.
     */
    public float gridToScreenX(float col, float row) {
        return (col - row) * (IsometricTile.TILE_WIDTH / 2f);
    }

    /**
     * Converts isometric grid coordinates (col, row) to world screen Y.
     */
    public float gridToScreenY(float col, float row) {
        return (col + row) * (IsometricTile.TILE_HEIGHT / 2f);
    }

    /**
     * Converts world screen X to fractional grid X.
     */
    public float screenToGridX(float screenX, float screenY) {
        float halfW = IsometricTile.TILE_WIDTH / 2f;
        float halfH = IsometricTile.TILE_HEIGHT / 2f;
        return (screenX / halfW + screenY / halfH) * 0.5f;
    }

    /**
     * Converts world screen Y to fractional grid Y.
     */
    public float screenToGridY(float screenX, float screenY) {
        float halfW = IsometricTile.TILE_WIDTH / 2f;
        float halfH = IsometricTile.TILE_HEIGHT / 2f;
        return (screenY / halfH - screenX / halfW) * 0.5f;
    }

    /**
     * Checks if a grid position is inside the walkable temple courtyard.
     */
    public boolean isWalkable(float gridX, float gridY) {
        // Must stay inside the outer terracotta walkway
        if (gridX < 1.6f || gridX > COLS - 2.6f || gridY < 1.6f || gridY > ROWS - 2.6f) {
            return false;
        }

        // Check collision against boundary pillars
        int c = Math.round(gridX);
        int r = Math.round(gridY);
        if (c >= 0 && c < COLS && r >= 0 && r < ROWS) {
            if (grid[c][r] == IsometricTile.Type.TEMPLE_PILLAR) {
                float distSq = (gridX - c) * (gridX - c) + (gridY - r) * (gridY - r);
                if (distSq < 0.35f) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Renders the entire isometric map with back-to-front depth sorting,
     * seamlessly interleaving the player character at their correct depth slice.
     */
    public void render(SpriteBatch batch, PlayerCharacter player) {
        int playerDepthSlice = Math.round(player.getGridX() + player.getGridY());
        boolean playerRendered = false;

        // Iterate diagonal slices from back (max col+row) to front (min col+row)
        for (int slice = (COLS - 1) + (ROWS - 1); slice >= 0; slice--) {
            // Render all tiles on this depth slice
            for (int col = 0; col < COLS; col++) {
                int row = slice - col;
                if (row >= 0 && row < ROWS) {
                    IsometricTile.Type type = grid[col][row];
                    TextureRegion region = tiles[type.ordinal()].getRegion();

                    float centerX = gridToScreenX(col, row);
                    float centerY = gridToScreenY(col, row);

                    // Offset so the diamond top face center is at (centerX, centerY)
                    float drawX = centerX - IsometricTile.TILE_WIDTH / 2f;
                    float drawY = centerY - (IsometricTile.TEXTURE_HEIGHT - IsometricTile.TILE_HEIGHT / 2f);

                    batch.draw(region, drawX, drawY);
                }
            }

            // Draw player when reaching their depth slice
            if (!playerRendered && slice <= playerDepthSlice) {
                player.render(batch);
                playerRendered = true;
            }
        }

        // Fallback in case player was in front of all tiles
        if (!playerRendered) {
            player.render(batch);
        }
    }

    @Override
    public void dispose() {
        for (IsometricTile tile : tiles) {
            if (tile != null) {
                tile.dispose();
            }
        }
    }
}

