package com.github.viseking.yuga.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * Procedurally generates authentic 2:1 isometric diamond slab tiles
 * following the Vedic & Dharmic aesthetic specified in STYLE.md.
 */
public class IsometricTile implements Disposable {

    public static final int TILE_WIDTH = 96;
    public static final int TILE_HEIGHT = 48;
    public static final int TILE_DEPTH = 16;
    public static final int TEXTURE_HEIGHT = TILE_HEIGHT + TILE_DEPTH; // 64

    public enum Type {
        SANDSTONE_PAVER,
        SURYA_GOLD,
        TERRACOTTA_BORDER,
        GANGA_WATER,
        TAPOVAN_GRASS,
        TEMPLE_PILLAR
    }

    private final Type type;
    private final Texture texture;
    private final TextureRegion region;

    public IsometricTile(Type type) {
        this.type = type;
        this.texture = generateTileTexture(type);
        this.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.region = new TextureRegion(texture);
    }

    public Type getType() {
        return type;
    }

    public TextureRegion getRegion() {
        return region;
    }

    private static Texture generateTileTexture(Type type) {
        int w = TILE_WIDTH;
        int h = TEXTURE_HEIGHT;
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.SourceOver);

        // Vertex coordinates on the 96x64 Pixmap
        int xTop = w / 2;       // 48
        int yTop = 0;
        int xRight = w - 1;     // 95
        int yRight = TILE_HEIGHT / 2; // 24
        int xBottom = w / 2;    // 48
        int yBottom = TILE_HEIGHT - 1; // 47
        int xLeft = 0;
        int yLeft = TILE_HEIGHT / 2;  // 24

        int depth = TILE_DEPTH; // 16

        Color topColor, leftColor, rightColor, borderColor, accentColor;

        switch (type) {
            case SURYA_GOLD:
                // Consecrated Surya Gold Sanctum tile
                topColor = new Color(1.000f, 0.843f, 0.000f, 1.0f);     // #FFD700
                leftColor = new Color(0.720f, 0.525f, 0.043f, 1.0f);    // Dark gold shade
                rightColor = new Color(0.855f, 0.647f, 0.125f, 1.0f);   // Medium gold shade
                borderColor = new Color(0.25f, 0.15f, 0.05f, 1.0f);     // Burnt umber ink
                accentColor = new Color(1.000f, 0.950f, 0.700f, 1.0f);   // Radiant sun core
                break;

            case TERRACOTTA_BORDER:
                // Kashi Terracotta carved perimeter slabs
                topColor = new Color(0.608f, 0.220f, 0.133f, 1.0f);     // #9B3822
                leftColor = new Color(0.376f, 0.137f, 0.082f, 1.0f);
                rightColor = new Color(0.486f, 0.176f, 0.106f, 1.0f);
                borderColor = new Color(0.18f, 0.08f, 0.05f, 1.0f);
                accentColor = new Color(0.85f, 0.45f, 0.35f, 1.0f);
                break;

            case GANGA_WATER:
                // Shimmering sacred Ganga basin tile
                topColor = new Color(0.220f, 0.714f, 1.000f, 0.90f);    // #38B6FF
                leftColor = new Color(0.102f, 0.137f, 0.494f, 0.95f);   // Shiva Lapis
                rightColor = new Color(0.157f, 0.392f, 0.706f, 0.95f);
                borderColor = new Color(0.10f, 0.15f, 0.35f, 0.80f);
                accentColor = new Color(0.75f, 0.92f, 1.00f, 0.85f);    // Crystalline wave crest
                break;

            case TAPOVAN_GRASS:
                // Sacred Tapovan meadow/moss tile
                topColor = new Color(0.180f, 0.490f, 0.196f, 1.0f);     // #2E7D32
                leftColor = new Color(0.106f, 0.302f, 0.122f, 1.0f);
                rightColor = new Color(0.141f, 0.400f, 0.161f, 1.0f);
                borderColor = new Color(0.08f, 0.20f, 0.08f, 1.0f);
                accentColor = new Color(0.35f, 0.65f, 0.30f, 1.0f);
                break;

            case TEMPLE_PILLAR:
                // Elevated pedestal block
                topColor = new Color(0.855f, 0.757f, 0.588f, 1.0f);     // Aged vellum
                leftColor = new Color(0.45f, 0.35f, 0.25f, 1.0f);
                rightColor = new Color(0.60f, 0.50f, 0.38f, 1.0f);
                borderColor = new Color(0.173f, 0.102f, 0.063f, 1.0f);
                accentColor = new Color(1.00f, 0.84f, 0.00f, 1.0f);     // Golden cap
                break;

            case SANDSTONE_PAVER:
            default:
                // Temple Sandstone paver
                topColor = new Color(0.780f, 0.482f, 0.345f, 1.0f);     // #C77B58
                leftColor = new Color(0.553f, 0.306f, 0.196f, 1.0f);    // Dark sandstone
                rightColor = new Color(0.639f, 0.369f, 0.243f, 1.0f);   // Medium sandstone
                borderColor = new Color(0.173f, 0.102f, 0.063f, 1.0f);  // Burnt umber ink
                accentColor = new Color(0.92f, 0.65f, 0.50f, 1.0f);     // Sandstone highlight
                break;
        }

        // 1. Draw Left Wall (2 triangles forming quad)
        pixmap.setColor(leftColor);
        pixmap.fillTriangle(xLeft, yLeft, xBottom, yBottom, xBottom, yBottom + depth);
        pixmap.fillTriangle(xLeft, yLeft, xBottom, yBottom + depth, xLeft, yLeft + depth);

        // 2. Draw Right Wall (2 triangles forming quad)
        pixmap.setColor(rightColor);
        pixmap.fillTriangle(xBottom, yBottom, xRight, yRight, xRight, yRight + depth);
        pixmap.fillTriangle(xBottom, yBottom, xRight, yRight + depth, xBottom, yBottom + depth);

        // 3. Draw Top Diamond Face (2 triangles forming rhombus)
        pixmap.setColor(topColor);
        pixmap.fillTriangle(xTop, yTop, xRight, yRight, xBottom, yBottom);
        pixmap.fillTriangle(xTop, yTop, xBottom, yBottom, xLeft, yLeft);

        // 4. Draw Tile-Specific Surface Patterns
        if (type == Type.SURYA_GOLD) {
            // Inner golden sun yantra diamond
            pixmap.setColor(accentColor);
            pixmap.fillTriangle(xTop, yTop + 8, xRight - 16, yRight, xBottom, yBottom - 8);
            pixmap.fillTriangle(xTop, yTop + 8, xBottom, yBottom - 8, xLeft + 16, yLeft);

            // Center glowing bindu dot
            pixmap.setColor(Color.WHITE);
            pixmap.fillCircle(w / 2, yRight, 3);
        } else if (type == Type.SANDSTONE_PAVER) {
            // Subtle chiseled inner groove
            pixmap.setColor(accentColor);
            pixmap.drawLine(xTop, yTop + 4, xRight - 8, yRight);
            pixmap.drawLine(xTop, yTop + 4, xLeft + 8, yLeft);
            pixmap.setColor(borderColor);
            pixmap.drawLine(xLeft + 8, yLeft, xBottom, yBottom - 4);
            pixmap.drawLine(xRight - 8, yRight, xBottom, yBottom - 4);
        } else if (type == Type.GANGA_WATER) {
            // Concentric crystalline ripple waves
            pixmap.setColor(accentColor);
            pixmap.drawLine(xLeft + 20, yRight - 3, xRight - 20, yRight - 3);
            pixmap.drawLine(xLeft + 14, yRight + 2, xRight - 14, yRight + 2);
            pixmap.drawLine(xLeft + 26, yRight + 7, xRight - 26, yRight + 7);
        } else if (type == Type.TAPOVAN_GRASS) {
            // Little grass tufts
            pixmap.setColor(accentColor);
            pixmap.drawLine(xTop - 4, yRight - 4, xTop - 4, yRight - 8);
            pixmap.drawLine(xTop + 4, yRight - 2, xTop + 4, yRight - 6);
            pixmap.drawLine(xTop, yRight + 4, xTop, yRight);
        } else if (type == Type.TEMPLE_PILLAR) {
            // Ornate stone pillar plinth
            pixmap.setColor(accentColor);
            pixmap.fillRectangle(w / 2 - 12, yRight - 6, 24, 12);
            pixmap.setColor(borderColor);
            pixmap.drawRectangle(w / 2 - 12, yRight - 6, 24, 12);
        }

        // 5. Crisp Contour Ink Lines (#2C1A10)
        pixmap.setColor(borderColor);
        // Top diamond perimeter
        pixmap.drawLine(xTop, yTop, xRight, yRight);
        pixmap.drawLine(xRight, yRight, xBottom, yBottom);
        pixmap.drawLine(xBottom, yBottom, xLeft, yLeft);
        pixmap.drawLine(xLeft, yLeft, xTop, yTop);

        // Vertical corner drops
        pixmap.drawLine(xBottom, yBottom, xBottom, yBottom + depth);
        pixmap.drawLine(xLeft, yLeft, xLeft, yLeft + depth);
        pixmap.drawLine(xRight, yRight, xRight, yRight + depth);

        // Bottom depth rim
        pixmap.drawLine(xLeft, yLeft + depth, xBottom, yBottom + depth);
        pixmap.drawLine(xBottom, yBottom + depth, xRight, yRight + depth);

        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}

