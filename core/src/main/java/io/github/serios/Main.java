package io.github.serios;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
  public SpriteBatch batch;
  public com.badlogic.gdx.assets.AssetManager assets;

  @Override
  public void create() {
    batch = new SpriteBatch();
    assets = new com.badlogic.gdx.assets.AssetManager();
    // Load all common textures here
    assets.load("levelBackground.png", Texture.class);
    assets.load("red.png", Texture.class);
    assets.load("chuck.png", Texture.class);
    assets.load("bomb.png", Texture.class);
    assets.load("pig.png", Texture.class);
    assets.load("woodbox.png", Texture.class);
    assets.load("woodstickhorizontal.png", Texture.class);
    assets.load("slingshot.png", Texture.class);
    assets.load("pausebutton.png", Texture.class);
    assets.load("hero.png", Texture.class); // For Menu/Game screen background
    assets.load("woodtriangle.png", Texture.class); // Load the triangle asset
    assets.finishLoading(); // Block until loaded for simplicity

    // Set windowed mode for easier screen recording
    Gdx.graphics.setWindowedMode(1280, 720);
    this.setScreen(new MenuScreen(this));
  }

  @Override
  public void render() {
    super.render();
  }

  @Override
  public void dispose() {
    batch.dispose();
    assets.dispose();
  }
}
