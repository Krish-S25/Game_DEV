package io.github.serios;

import com.badlogic.gdx.graphics.Texture;

public class LevelScreen extends BaseLevelScreen {

    public LevelScreen(Main game) {
        // Pass specific textures for Level 1 (e.g., red bird)
        super(game, 1,
                game.assets.get("red.png", Texture.class),
                game.assets.get("pig.png", Texture.class),
                game.assets.get("woodbox.png", Texture.class));

        // One pig on a box
        addStructure(viewport.getWorldWidth() / 1.2f, 1.4f); // Ground structure
        addTarget(viewport.getWorldWidth() / 1.2f, 2.4f); // Pig on top

    }
}
