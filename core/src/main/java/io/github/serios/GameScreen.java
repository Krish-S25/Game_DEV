package io.github.serios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
  private final Main game;
  private Stage stage;
  private Skin skin;
  private SpriteBatch batch;
  private Texture background;
  private Texture backButtonRawTex;

  public GameScreen(final Main game) {
    this.game = game;
    batch = game.batch; // Use global batch
    background = game.assets.get("hero.png", Texture.class); // Use global assets

    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);
    skin = new Skin(Gdx.files.internal("uiskin.json"));
    skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
//      BitmapFont font = new BitmapFont(Gdx.files.internal("default.fnt"), Gdx.files.internal("default.png"), false);
//      font.getData().setScale(0.5f);
//      font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//      skin.add("default-font", font);

    // Load the loose raw PNG since it is not baked into uiskin.atlas!
    backButtonRawTex = new Texture(Gdx.files.internal("backbutton.png"));
    Button backButton = new Button(new TextureRegionDrawable(new TextureRegion(backButtonRawTex)));
    
    // Standardizing a perfect 3:2 rectangle layout for your 1536x1024 Canvas design
    backButton.setSize(120, 80); 
    // Shifted to the top right corner instead of top left!
    backButton.setPosition(Gdx.graphics.getWidth() - 140, Gdx.graphics.getHeight() - 100);

    backButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            game.setScreen(new MenuScreen(game));
          }
        });

    stage.addActor(backButton);
    int numColumns = 5;
    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);
    
    try {
        skin.getFont("default-font").getData().setScale(1.2f);
    } catch (Exception e) {}

    for (int i = 1; i <= 20; i++) {
      TextButton levelButton = new TextButton("Level " + i, skin, (i <= 3 ? "levelbutton-style" : "lockedlevel-style"));
      levelButton.getLabelCell().pad(10);

      final int level = i;
        if (i <= 3) {
            levelButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (level == 1) {
                            game.setScreen(new LevelScreen(game));
                        } else if (level == 2) {
                            game.setScreen(new Level2Screen(game));
                        } else if (level == 3) {
                            game.setScreen(new Level3Screen(game));
                        }
                    }
                });
        }

      table.add(levelButton).width(130f * (478f / 508f)).height(130f).pad(20);

      if (i % numColumns == 0) {
        table.row();
      }
    }
  }

  @Override
  public void show() {}

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0.2f, 0, 1);
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
  public void resize(int width, int height) {}

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
    if (backButtonRawTex != null) backButtonRawTex.dispose();
  }
}
