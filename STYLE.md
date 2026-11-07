# Visual Style Guide: Pictorial Map Art Style
**Project: Yuga (The Chronicles of the Dharma Warrior)**  
**Style Identifier:** `vedic-pictorial-map-v1`  
**Target Engine:** libGDX 2D Engine (LWJGL3 / Desktop & Scalable Mobile)

---

## 1. Executive Vision & Visual Identity

The visual signature of *Yuga* is **"Vedic Illuminated Cartography"**—a living, interactive pictorial pilgrimage map (*Tirtha Yatra Pata*).

The world is not presented as a cold, satellite-accurate topographic grid. Instead, it is rendered as an ancient, illuminated sacred scroll created by the rishis and cosmic cartographers of the 31st century. It fuses:
1. **Classical Indian Miniature & Pilgrimage Painting**: Kangra/Pahari line delicacy, Orissan *Patachitra* bold ink contours, Rajasthani *Pichwai* natural pigment warmth, and Jain/Tibetan cosmological *Mandala* geometry.
2. **Golden Age Pictorial Cartography**: Heroic, illustrated 3D-oblique vignettes of cities, shrines, sacred peaks, and legendary beasts in the tradition of pictorial cartographers (e.g., MacDonald Gill, Jo Mora).
3. **Vedic Solar-Punk / Cosmic Dharma**: Luminous gold prana circuits, crystalline sacred rivers, astral yantras, and celestial vimana light-trails contrasted against the necrotic entropy of Kali Asura's *Andhaka Void*.

> **Core Visual Tenet for AI Agents & Developers:**  
> *"Every landmark is a painted heroic vignette, every river is a ribbon of flowing prana, and every border is consecrated with sacred geometry. If an asset looks like a generic modern GPS map or flat sci-fi vector graphic, it violates the style."*

---

## 2. Core Visual Pillars

| Pillar | Principle | AI / Developer Rule of Thumb |
| :--- | :--- | :--- |
| **I. Narrative Geography** | Spiritual significance dictates visual scale. Mount Kailash and Kashi are monumental; empty wilderness compresses gently. | Scale sacred landmarks 2x–3x larger than realistic proportions. Shrines face the player in 3/4 axonometric view. |
| **II. Manuscript Materiality** | The map lives on woven organic canvas: aged *Bhojpatra* (birch bark), temple vellum, or oiled cloth with hand-ground mineral ink outlines. | Use warm parchment undertones (`#F4E8D1`), textured paper grain, deckled border edges, and rich sepia-umber ink contour lines (`#2A1A10`). |
| **III. Divine Radiance vs Void** | Visual conflict between solar dharma (*Tejas*) and cosmic entropy (*Andhaka Void*). | Holy sanctuaries radiate warm amber/gold rim-light (`#FFD700`); corrupted zones bleed into violet-black necrotic miasma (`#160A22`). |
| **IV. Cartographic Flourishes** | The canvas is embellished with functional mythological ornaments (yantra compass, river makaras, Sanskrit scrolls). | Never use generic arrows or modern HUD icons. Use conch shells, lotus buds, trishula pins, and yantra compass roses. |

---

## 3. Perspective, Projection & Scale Hierarchy

### 3.1 Projection & Camera Angle
- **Camera Angle:** High-Angle Oblique Axonometric (30° to 40° downward tilt).
- **Landmark Vignette Alignment:** Illustrated in **3/4 elevated front-isometric view**. Buildings, temples, and mountain peaks stand upright facing the screen, allowing player visibility of shikharas, ghats, stupas, and banners.
- **Base Terrain:** Tilted plane with organic rolling relief, painted elevation contours, and stylized terracing.

```
       [Sun/Cosmic Plane]
              ▲
              │   (3/4 Elevated Face)
          [Landmark]  ◄── 30°-40° Camera Tilt
         /          \
   ═════════════════════ [Aged Manuscript Ground Plane]
```

### 3.2 Landmark Scale Tiers
When generating, rendering, or positioning landmarks:
1. **Tier 1: Cosmic Sanctuaries (Major Hubs & Act Climax)**
   - *Examples:* Mount Kailash & Lake Mansarovar, Varanasi / Kashi Manikarnika Ghats, Kedarnath Temple Citadel.
   - *Pixel Footprint:* `384x384` to `512x512` px.
   - *Visual Weight:* Complex multi-structure illustrations, animated prana aura, fluttering temple flags, glowing sanctum fires.
2. **Tier 2: Sacred Regional Tirthas (Pilgrimage Trial Sites)**
   - *Examples:* Bodh Gaya Mahabodhi Stupa & Bodhi Tree, Nalanda Great Library Vihara, Rishikesh Triveni Ghat, Badrinath Dham.
   - *Pixel Footprint:* `192x192` to `256x256` px.
   - *Visual Weight:* Self-contained illustrated compound, distinct architectural silhouette, glowing central node marker.
3. **Tier 3: Minor Waypoints & Shrines**
   - *Examples:* Mountain caves, hermit ashramas, ancient step-wells, dimensional tear waystations.
   - *Pixel Footprint:* `96x96` to `128x128` px.
   - *Visual Weight:* Single shrine structure, prayer wheel pavilion, or rock altar.
4. **Tier 4: Environmental & Geographical Fillers**
   - *Examples:* Himalayan snow ridges, Peepal/Banyan tree clusters, Deodar pine forests, sand dunes, lotus ponds.
   - *Pixel Footprint:* `48x48` to `96x96` px per cluster.

---

## 4. Color Palette Specifications

All assets, shaders, UI elements, and sprite atlases must adhere to these curated palettes:

### 4.1 Base Manuscript & Cartographic Foundation
Used for terrain ground planes, map boundaries, scrolls, and contour lines.

| Swatch Name | Hex Code | LibGDX RGBA (0.0-1.0) | Usage |
| :--- | :--- | :--- | :--- |
| **Bhojpatra Parchment** | `#F4E8D1` | `0.957, 0.910, 0.820, 1.0` | Primary base ground canvas fill |
| **Aged Vellum Shade** | `#DAC196` | `0.855, 0.757, 0.588, 1.0` | Mountain shading, terrain elevation contours |
| **Burnt Umber Ink** | `#2C1A10` | `0.173, 0.102, 0.063, 1.0` | All primary line-art, contours, borders, text |
| **Sepia Etch Tone** | `#5C3A21` | `0.361, 0.227, 0.129, 1.0` | Hatching, secondary linework, topographic isolines |
| **Temple Sandstone** | `#C77B58` | `0.780, 0.482, 0.345, 1.0` | Temple masonry, stupas, stone bridge paths |
| **Kashi Terracotta** | `#9B3822` | `0.608, 0.220, 0.133, 1.0` | River ghat steps, brick plinths, sacred roofs |

### 4.2 Sacred Dharma & Divine Radiance (Consecrated State)
Used for awakened nodes, energy conduits, astras, and divine blessings.

| Swatch Name | Hex Code | LibGDX RGBA (0.0-1.0) | Usage |
| :--- | :--- | :--- | :--- |
| **Surya Gold** | `#FFD700` | `1.000, 0.843, 0.000, 1.0` | Divya-Yuga Mani radiance, sunburst halos, unlocked nodes |
| **Keshari Saffron** | `#FF7A18` | `1.000, 0.478, 0.094, 1.0` | Sacred flags (*dhvaja*), tapasya flames, warrior auras |
| **Kumkum Vermilion** | `#C62828` | `0.776, 0.157, 0.157, 1.0` | Temple domes, sacred tilak marks, fire astra runes |
| **Ganga Crystalline Blue** | `#38B6FF` | `0.220, 0.714, 1.000, 1.0` | Sacred living rivers, pure energy springs, healing water |
| **Neelkanth Shiva Lapis** | `#1A237E` | `0.102, 0.137, 0.494, 1.0` | Cosmic night sky, deep river shadows, Mahadeva relics |
| **Kailash Glacier White** | `#F0F8FF` | `0.941, 0.973, 1.000, 1.0` | Snow peaks, conch shells (*shankha*), pure lotus petals |
| **Tapovan Forest Jade** | `#2E7D32` | `0.180, 0.490, 0.196, 1.0` | Sacred groves, Bodhi foliage, energized valleys |

### 4.3 Kali Asura & Andhaka Void (Corrupted State)
Used when a region, shrine, or trial site is infected by cosmic entropy.

| Swatch Name | Hex Code | LibGDX RGBA (0.0-1.0) | Usage |
| :--- | :--- | :--- | :--- |
| **Andhaka Void Black** | `#0E0C16` | `0.055, 0.047, 0.086, 1.0` | Void tears, abyssal fog of war, dead earth |
| **Tamas Miasma Purple** | `#3B134A` | `0.231, 0.075, 0.290, 1.0` | Entropy fog, corrupted ley lines, demonic rifts |
| **Eclipse Blood Crimson** | `#6A0D25` | `0.416, 0.051, 0.145, 1.0` | Asura ward marks, bleeding waters, locked corrupted nodes |
| **Necrotic Ash Grey** | `#4D4757` | `0.302, 0.278, 0.341, 1.0` | Desolated forests, scorched stone, burned shrines |

---

## 5. Pictorial Cartographic Anatomy & Motifs

Every element on the map follows specific illustrated cartographic conventions:

### 5.1 Sacred Riverways (*Nadi*)
- **Line Style:** Double-bordered ink ribbons with parallel wave crest curves (inspired by Pahari miniature paintings).
- **Interior Fill:** Translucent gradient of Ganga Crystalline Blue (`#38B6FF`) fading into deep Lapis (`#1A237E`).
- **Dynamic Particle Flow:** Flowing golden flecks (*prana currents*) traveling along spline paths toward holy confluences (*Sangams*).
- **Water Fauna:** Mythological *Makaras* (sacred aquatic chimeras with crocodile head and peacock tail) leaping occasionally from ocean boundaries and river mouths.

### 5.2 Mountain Ranges (*Giri & Parvata*)
- **Style:** Hand-drawn conical layered peaks with rhythmic chisel-shading on the eastern slopes and warm golden highlight washes on the western crests.
- **Mount Kailash Special Asset:** Crowned with a perpetual subtle rotating golden Dharma wheel and white glacial mist aura.

### 5.3 Pilgrim Paths & Cosmic Ley Lines (*Brahma-Sutra*)
- **Locked / Undiscovered:** Faint, dashed sepia etch lines (`#5C3A21`), barely visible through parchment grain.
- **Active / Unlocked:** Golden braided cord line (`#FFD700`) connecting shrines with soft additive bloom.
- **Currently Traversed:** Pulsing light pulse traveling along the bezier curve between two nodes.
- **Corrupted Path:** Jagged, fractured black-violet thorns disrupting the golden route.

### 5.4 Cartographic Ornaments
1. **Surya-Yantra Compass Rose:** Located in an upper or lower quadrant. Features an 8-spoke or 12-spoke solar chakra with Sanskrit cardinal labels:
   - North: *Uttara* (उत्तर)
   - South: *Dakshina* (दक्षिण)
   - East: *Purva* (पूर्व)
   - West: *Paschima* (पश्चिम)
2. **Map Border & Corner Cartouches:** 
   - A multi-layered frieze border: outer running vine with lotus buds (*Padma-Lata*), inner golden geometric meander, and four corner *Kirtimukha* (protective lion-faced gargoyle) brackets.
3. **Location Name Banners (Cartouches):**
   - Flowing parchment scroll banner unfurled beneath the landmark.
   - Text rendered in classical serif / Indic-inspired display font with a burnt-umber drop shadow.
   - Status badge (Lotus icon for Consecrated, Void sigil for Corrupted).

---

## 6. Technical Specifications for libGDX

### 6.1 Viewport, Coordinate System & Resolution
- **Virtual Viewport Resolution:** `1920 x 1080` (16:9 aspect ratio standard, scales via `ExtendViewport`).
- **World Coordinate Units:** Virtual pixel units where `1 Unit = 1 World Pixel` at 1.0 zoom.
- **Camera Configuration:** `OrthographicCamera` with smooth clamped panning and zoom range `[0.65x, 2.0x]`.
- **Y-Down vs Y-Up:** Standard libGDX Y-Up coordinate space (`(0,0)` at bottom-left).

### 6.2 Render Layer Stacking Order
When coding the map rendering loop in `core/.../screens/MapScreen.java` or custom `MapRenderer`:

| Layer Index | Layer Identifier | Description & Blend Mode |
| :---: | :--- | :--- |
| `0` | `CANVAS_BASE` | Parchment texture mesh with tileable Bhojpatra texture + subtle vignette. Blend: `Normal`. |
| `1` | `TERRAIN_RELIEF` | Painted landmass, elevation washes, coastline watercolor fringes. Blend: `Normal`. |
| `2` | `WATER_SURFACE` | Spline-meshed rivers and lakes with animated UV flow shader. Blend: `Normal`. |
| `3` | `PILGRIM_PATHS` | Spline paths, dotted lines, and active golden prana ribbons. Blend: `Additive` for glow. |
| `4` | `TERRAIN_DECOR` | Mountain peaks, forests, banyan groves (Y-sorted with landmarks). Blend: `Normal`. |
| `5` | `LANDMARKS` | Heroic 3/4 architectural vignettes (Kailash, Kashi, Kedarnath). Depth-sorted by `Y` position. |
| `6` | `NODES_INTERACTIVE`| Clickable shrine beacons, interactive pins, selection halos. Blend: `Normal` + `Additive`. |
| `7` | `WEATHER_FX` | Particle effects: Himalayan snow, drifting lotus pollen, prana sparks, rain. |
| `8` | `FOG_OF_WAR` | Cloud mask shader layer. Swirling clouds of cosmic mist that part over unlocked lands. |
| `9` | `CARTOGRAPHIC_UI` | Surya compass rose, parchment borders, location cartouches, act objective banner. |

### 6.3 Texture Atlas Architecture
Assets must be compiled using libGDX `TexturePacker` into these dedicated atlases under `assets/map/`:

```
assets/
└── map/
    ├── atlases/
    │   ├── map_terrain.atlas       # Tiles, coastlines, water patches, mountains, trees
    │   ├── map_landmarks.atlas     # Tier 1-3 landmark vignettes (Kailash, Kashi, etc.)
    │   ├── map_ui.atlas            # Compass, frames, cartouches, pins, icons, buttons
    │   └── map_fx.atlas            # Particle textures, rune decals, aura flares
    ├── textures/
    │   └── parchment_seamless.png  # 2048x2048 seamless parchment base
    └── shaders/
        ├── parchment.vert / .frag  # Paper grain, paper age, subtle ambient warmth
        ├── water_flow.vert / .frag # Animated UV river displacement
        ├── prana_glow.frag         # Additive pulsing glow for active holy nodes
        └── void_corruption.frag    # Threshold dissolve into necrotic purple veins
```

---

## 7. AI Agent Operational Instructions & Recipes

When an AI Agent is tasked with generating art, editing code, or designing levels for *Yuga*, it **MUST** follow the protocols below.

### 7.1 Asset Generation: Diffusion / Image Model Prompts

When generating 2D pictorial map assets with Midjourney, Flux, Stable Diffusion, or DALL-E:

#### A. Master Style Prompt Header (Prepend to all image prompts)
```text
Pictorial map illustration, Indian miniature art style, Pahari and Kangra painting influence, Orissan Patachitra ink line art, Jo Mora cartographic aesthetic, aged parchment paper texture, hand-painted mineral pigments, burnt umber ink outlines, isometric 3/4 elevated view, illuminated manuscript details, warm golden hour palette, clean alpha cut-out silhouette --no modern photography, no 3D photorealism, no flat modern vector, no digital neon, no text watermark
```

#### B. Prompt Template: Major Landmark (e.g., Mount Kailash)
```text
A heroic pictorial map vignette of Mount Kailash and Lake Mansarovar, sacred Tibetan Himalayan peak crowned in snow and subtle golden solar aura, sacred Lake Mansarovar below with crystalline turquoise water, tiny traditional stone ashrama and prayer stupa at the base, elevated 3/4 front view, Indian miniature and pictorial cartography style, hand-inked outlines, painted on aged parchment canvas, rich details, isolated on transparent background --ar 1:1 --stylize 250
```

#### C. Prompt Template: Temple Citadel (e.g., Kashi / Varanasi)
```text
A heroic pictorial map vignette of ancient Varanasi Kashi on the sacred Ganges river, stepped terracotta and sandstone ghats with ornate North Indian Hindu temple spires, Shiva lingam sanctum, stone steps descending into holy river, tiny brass oil lamps and holy flags, elevated 3/4 axonometric perspective, illuminated manuscript and vintage pictorial cartography style, rich cinnabar and burnt umber ink --ar 1:1 --stylize 250
```

#### D. Prompt Template: Cartographic Ornament (e.g., Surya Yantra Compass)
```text
An ornate antique pictorial map compass rose, 8-pointed golden Surya sun yantra, sacred geometric solar disc with concentric lotus petals and celestial rays, aged brass and gold leaf on weathered vellum, fine sepia ink linework, classical Vedic sacred geometry, isolated on white background --ar 1:1
```

### 7.2 Map Node Data Model (JSON Schema for Levels & Cartography)
When an AI agent designs level data, nodes, or progression graphs, write them in this schema:

```json
{
  "nodeId": "kashi_sanctum",
  "name": "Varanasi (Kashi)",
  "region": "Gangetic_Plains",
  "tier": 1,
  "worldPosition": { "x": 960, "y": 480 },
  "visualAsset": {
    "atlas": "map_landmarks",
    "regionName": "vignette_kashi_ghats",
    "pixelWidth": 384,
    "pixelHeight": 384,
    "anchorOffset": { "x": 0.5, "y": 0.2 }
  },
  "cartouche": {
    "titleDevanagari": "काशी विश्वानाथ",
    "subtitle": "The Eternal City on Shiva's Trishula",
    "element": "Agni_Prithvi"
  },
  "state": "UNLOCKED",
  "corruptionLevel": 0.0,
  "connections": [
    { "targetNodeId": "rishikesh_ashrama", "pathType": "GANGA_RIVER_SPLINE", "unlocked": true },
    { "targetNodeId": "bodh_gaya_shrine", "pathType": "PILGRIM_CORD", "unlocked": false }
  ]
}
```

### 7.3 Actionable Validation Checklist for AI Agents
Before concluding any task involving visual assets, UI screens, or map code, verify each question:

- [ ] **Line-art Authenticity:** Does every structure feature hand-inked burnt umber / sepia contours (`#2C1A10`), avoiding harsh `#000000` pitch black or sterile vector strokes?
- [ ] **Ground Materiality:** Is the background textured with authentic Bhojpatra / parchment tone (`#F4E8D1`), free of plain solid white or generic grey flat fields?
- [ ] **Perspective Consistency:** Are all landmark vignettes drawn in 3/4 elevated front-isometric view so facades, roofs, and flags are clearly visible?
- [ ] **Color Alignment:** Do holy elements utilize the Surya Gold (`#FFD700`) and Keshari Saffron (`#FF7A18`) palette, and corrupted areas use Andhaka Void (`#0E0C16`) / Tamas Purple (`#3B134A`)?
- [ ] **Cultural Integrity:** Are decorative cartouches, compass roses, and frames based on authentic Vedic geometry (Yantras, Mandalas, Padma lotuses, Shankhas) rather than European renaissance or generic sci-fi tropes?
- [ ] **libGDX Layer Order:** Does the rendering code respect the 10-layer depth stacking order (Layer 0 `CANVAS_BASE` to Layer 9 `CARTOGRAPHIC_UI`)?
- [ ] **Sprite Density:** Are asset dimensions sized according to the Landmark Scale Hierarchy (Tier 1: `384-512px`, Tier 2: `192-256px`, Tier 3: `96-128px`, Tier 4: `48-96px`)?

---

## 8. Summary of Map Locations from Story & Canon

| Location | Spiritual Trial / Role | Visual Atmosphere & Landmark Vignette |
| :--- | :--- | :--- |
| **Mount Kailash & Lake Mansarovar** | Supreme Cosmic Nexus & Divya-Yuga Mani resting altar (Acts I, IV, V). | Snow-capped silver pyramid peak, floating golden Surya pendant aura, glacial turquoise lake, meditating hermits, Himalayan white lion. |
| **Varanasi / Kashi** | Subterranean Adi-Ananta Chamber & Jyotirlinga shield (Act II, Trial of Pancha Mahabhuta). | Golden temple spires (*shikharas*), stone ghat steps descending into glistening Ganga, burning Aarti brass lamps, subterranean cavern gateway. |
| **Kedarnath & Badrinath** | Trial of Tyaga & Tapasya; Nandi Sentinel duel (Act III). | High alpine stone sanctum flanked by jagged ice cliffs, massive stone Nandi bull sentinel, prayer flag ropes fluttering in Himalayan winds. |
| **Bodh Gaya & Nalanda** | Trial of Satya & Maya; piercing illusions (Act III). | Spreading ancient sacred Bodhi tree with luminous golden leaves, brick stupas of Nalanda Mahavihara, reflecting lotus pools, meditating monks. |
| **Rishikesh & Haridwar** | Confluence of sacred rivers; pranayama trials. | Suspension wooden footbridges across foaming azure rapids, riverbank ashramas, cedar forests, ringing brass temple bells. |
| **Aryavarta-Loka (Rift View)** | Lost parallel realm visible through cosmic rifts. | Celestial floating golden vimanas, crystalline temple citadels in space, besieged by swirling purple-black Andhaka void tentacles. |

