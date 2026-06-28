package io.github.serios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
  private final Main game;
  private Stage stage;
  private Skin skin;
  private SpriteBatch batch;
  private Texture background;

  public MenuScreen(final Main game) {
    this.game = game;
    batch = game.batch; // Use global batch
    background = game.assets.get("hero.png", Texture.class); // Use global assets

    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);

    skin = new Skin(Gdx.files.internal("uiskin.json"));

    try {
        skin.getFont("default-font").getData().setScale(1.2f);
    } catch (Exception e) {}

    // Create a button
    TextButton button = new TextButton("Start Game", skin);
    button.setPosition(Gdx.graphics.getWidth() / 2f - 125, Gdx.graphics.getHeight() / 2f);
    button.setSize(250, 75); // Scaled up
    button.setColor(com.badlogic.gdx.graphics.Color.FOREST); // Green button

    button.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            game.setScreen(new GameScreen(game));
          }
        });

    TextButton exitButton = new TextButton("Exit to Menu", skin);
    exitButton.setSize(150, 60); // Scaled up
    exitButton.setPosition(Gdx.graphics.getWidth() - 170, 20); // Bottom right
    exitButton.setColor(com.badlogic.gdx.graphics.Color.FIREBRICK);
    exitButton.addListener(
        new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

    stage.addActor(button);
    stage.addActor(exitButton);
  }

  @Override
  public void show() {}

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.2f, 0.2f, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();
    
    // Golden Yellow Theme Background
    batch.setColor(1.0f, 0.85f, 0.2f, 1.0f);

    int screenWidth = Gdx.graphics.getWidth();
    int screenHeight = Gdx.graphics.getHeight();

    int imageWidth = background.getWidth();
    int imageHeight = background.getHeight();

    float scaleX = (float) screenWidth / imageWidth;
    float scaleY = (float) screenHeight / imageHeight;
    float scale = Math.max(scaleX, scaleY);

    float x = (screenWidth - imageWidth * scale) / 2;
    float y = (screenHeight - imageHeight * scale) / 2;

    batch.draw(background, x, y, imageWidth * scale, imageHeight * scale);
    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    batch.end();

    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    stage.dispose();
    skin.dispose();
  }
}
