# Yuga

> **A mythic action-adventure concept set in a future Bharat, where a lost prince must awaken the power of Dharma to face a cosmic darkness.**

**Yuga: The Chronicles of the Dharma Warrior** is a Java game project built with [libGDX](https://libgdx.com/). Its story and art direction draw on Indian mythology, Vedic cosmology, sacred geography, and the contrast between divine radiance and cosmic entropy.

The repository currently contains the libGDX desktop starter application alongside the game's story and visual-design documents. The full game described in those documents is a creative direction, not yet implemented gameplay.

## The world of Yuga

In the 31st century, Yuga—a lost prince from the parallel realm of Aryavarta-Loka—is raised by sages near Mount Kailash. When the darkness of Kali Asura threatens Earth, Yuga's journey leads across sacred places including Kashi, Kedarnath, Bodh Gaya, and the Himalayas. He must face trials of truth, selflessness, and mastery before he can defend the world from the Andhaka Void.

The project's visual identity, **Vedic Illuminated Cartography**, imagines the world as a living pilgrimage map: hand-painted landmarks, parchment textures, sacred geometry, luminous rivers, and a visual conflict between warm, solar radiance and shadowed corruption.

## Project status

Yuga is an early-stage project. The current runnable application is the generated libGDX starter screen; the story and visual style are documented as the project's creative and design direction. Features such as exploration, combat, map progression, custom shaders, and the story campaign are not yet implemented.

## Project structure

This is a Gradle multi-project build:

| Module / file | Purpose |
| --- | --- |
| `core/` | Shared Java application code and game dependencies. |
| `lwjgl3/` | Desktop launcher and LWJGL3 backend. |
| `assets/` | Shared runtime assets. |
| `STORY.md` | Story premise, characters, trials, and sacred geography. |
| `STYLE.md` | Visual-art direction and technical design reference. |
| `MEMORY.md` | Compact project lore and style reference. |
| `LEVELS.md` | Reserved for level-design documentation. |

The core module declares libGDX and supporting libraries including Ashley, gdx-ai, Box2D, FreeType, and Box2DLights. The desktop module launches the shared core application.

## Getting started

### Requirements

- A Git client
- A JDK supported by the included Gradle wrapper

### Run the desktop starter

Clone the repository and launch the LWJGL3 desktop application:

```bash
git clone https://github.com/Viseking/Yuga.git
cd Yuga
./gradlew lwjgl3:run
```

On Windows, use `gradlew.bat` instead of `./gradlew`:

```powershell
.\gradlew.bat lwjgl3:run
```

### Build

Build all modules:

```bash
./gradlew build
```

Build the runnable desktop JAR:

```bash
./gradlew lwjgl3:jar
```

The JAR is written to `lwjgl3/build/libs/`. To remove generated build outputs, run `./gradlew clean`.

## Design references

- [Story and lore](STORY.md)
- [Visual style guide](STYLE.md)
- [Project memory](MEMORY.md)

## Technology

- **Language:** Java (Java 8 source and target compatibility)
- **Game framework:** libGDX
- **Desktop backend:** LWJGL3
- **Build system:** Gradle Wrapper

## Contributing

Yuga is at an early stage, and contributions that help turn its documented vision into a playable game are welcome. Before proposing substantial gameplay, art, or level changes, please consult `STORY.md` and `STYLE.md` so new work stays aligned with the project's narrative and visual direction.

## License

See [LICENSE](LICENSE) for the terms that apply to this repository.
