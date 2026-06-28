package io.github.serios;

import com.badlogic.gdx.graphics.Texture;

public class Level2Screen extends BaseLevelScreen {

    public Level2Screen(Main game) {
        // Pass specific textures for Level 2
        super(game, 2,
                game.assets.get("bomb.png", Texture.class),
                game.assets.get("pig.png", Texture.class),
                game.assets.get("woodbox.png", Texture.class));

        // Define specific level 2 layout (e.g., a tower)
        float startX = viewport.getWorldWidth() / 1.2f;
        float startY = 1.4f;

        // A stack of 3 boxes
        addStructure(startX, startY);
        addStructure(startX, startY + 1f);
        addStructure(startX, startY + 2f);

        // Target at the top
        addTarget(startX, startY + 3f);

        // Extra target nearby
        addTarget(startX - 1f, startY + 0.5f);
    }
}
