# 🐦 AngBirbsGame - Box2D & LibGDX Angry Birds Clone

AngBirbsGame is a fully-featured, physics-based Angry Birds clone built in **Java** utilizing the powerful **LibGDX** game framework and **Box2D** physics engine. The game includes customized levels, elastic slingshot mechanics, projectile flight path tracing, a modular screen routing system, and custom physics interactions.

---

## 🎮 Key Features

* **Box2D Physics Integration**: Fully simulated physical world featuring realistic gravity, friction, mass density, and elastic restitution for structures, projectiles, and pigs.
* **Elastic Slingshot Mechanism**: Drag-and-release slingshot system featuring mouse joint constraints and physical drag boundaries.
* **Dynamic Projectile Flight Path Tracing**: Renders color-coded, fade-away trail particles matching the projectile's type (Red trail for Red Bird, Black trail for Bomb Bird).
* **Distinct Bird Types**:
  * **Red Bird**: The classic high-velocity projectile.
  * **Bomb Bird**: Larger, heavier, and features a custom visual scale and dense mass profile for breaking through reinforced barriers.
* **Multiple Challenging Levels**:
  * **Level 1 (The Basics)**: Introduce slingshot dynamics against a single pig on a standard box.
  * **Level 2 (The Tower)**: Break through a 3-high vertical box tower with multiple targets.
  * **Level 3 (The Fort)**: Penetrate a highly fortified structure protected by a sloped ramp constructed out of custom rotated triangle blocks.
* **Polished Screen Routing & UI**:
  * **Main Menu**: Stylized entry screen with dynamic button scaling and visual theme.
  * **Level Selection Screen**: Grid system displaying unlocked/locked levels using custom atlas skins.
  * **Pause Menu**: In-game overlays providing instant "Resume/Redo" and exit triggers.
  * **Game Over Screen**: Win/loss detection triggers tailored retry states.

---

## 📂 Project Architecture

The project follows the standard LibGDX multi-module layout:

```text
├── assets/                  # Centralized game assets (textures, level config, UI skins)
├── core/                    # Core Java source code containing all screens and physics
│   └── src/main/java/io/github/serios/
│       ├── Main.java             # Main Game entry point & Asset Manager setup
│       ├── BaseLevelScreen.java  # Core gameplay loop, Box2D world, & Slingshot input
│       ├── LevelScreen.java      # Level 1 configuration
│       ├── Level2Screen.java     # Level 2 configuration
│       ├── Level3Screen.java     # Level 3 configuration
│       ├── GameScreen.java       # Level Selection Screen (unlocked grid)
│       ├── MenuScreen.java       # Main Menu Screen
│       ├── PauseScreen.java      # In-game Pause overlay
│       └── GameOver.java         # Post-match Win/Loss summary screen
└── lwjgl3/                  # Desktop launcher using the LWJGL 3 backend
```

---

## 🚀 Getting Started

### Prerequisites

Ensure you have **Java 17** or higher installed and configured in your environment variable paths.

### Running Locally

To run the game, open a terminal in the root folder (`AngBird-main`) and execute:

**On Windows (Command Prompt / PowerShell):**
```bash
./gradlew lwjgl3:run
```

**On macOS / Linux:**
```bash
chmod +x gradlew
./gradlew lwjgl3:run
```

---

## 🛠️ Gradle Build Tasks

This project is built using Gradle wrapper (`gradlew` / `gradlew.bat`). Below are the primary tasks:

* **Run Desktop App**: `./gradlew lwjgl3:run`
* **Clean Build Directories**: `./gradlew clean`
* **Build Project**: `./gradlew build`
* **Generate Runnable JAR**: `./gradlew lwjgl3:jar` (Output will be located in `lwjgl3/build/libs/`)
* **IntelliJ project generation**: `./gradlew idea`

---

## 🎮 Gameplay Controls

* **Left Click & Drag (Mouse/Touch)**: Pull the bird back on the slingshot.
* **Release Left Click**: Launch the projectile.
* **Escape (ESC) or Pause Button**: Access Pause Menu.
