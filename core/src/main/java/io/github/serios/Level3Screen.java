package io.github.serios;

import com.badlogic.gdx.graphics.Texture;

public class Level3Screen extends BaseLevelScreen {

    public Level3Screen(Main game) {
        // Pass specific textures for Level 3
        super(game, 3,
                game.assets.get("red.png", Texture.class),
                game.assets.get("pig.png", Texture.class),
                game.assets.get("woodbox.png", Texture.class));

        // Define specific level 3 layout: Tower based on Level 2
        float startX = viewport.getWorldWidth() / 1.2f;
        float startY = 1.4f;

        // A stack of 5 boxes
        addStructure(startX, startY);
        addStructure(startX, startY + 1f);
        addStructure(startX, startY + 2f);
        addStructure(startX, startY + 3f);
        addStructure(startX, startY + 4f);

        // Defensive ramp on the left side (blocks incoming bird)
        float stepX = startX - 0.45f;
        addStructure(stepX, startY);
        addStructure(stepX, startY + 1f);
        addStructure(stepX, startY + 2f);
        addTriangle(stepX, startY + 3f);

        stepX -= 0.45f; // shift closer
        addStructure(stepX, startY);
        addStructure(stepX, startY + 1f);
        addTriangle(stepX, startY + 2f);

        stepX -= 0.45f; // shift closer
        addStructure(stepX, startY);
        addTriangle(stepX, startY + 1f);

        stepX -= 0.45f; // shift closer
        addTriangle(stepX, startY);

        // Target at the top
        addTarget(startX, startY + 5f);
    }
}
