package io.github.serios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseLevelScreen implements Screen {
    protected World world;
    protected final Main game;
    protected Box2DDebugRenderer debugRenderer;
    protected OrthographicCamera camera;
    protected Viewport viewport;
    
    // Core Textures
    protected Texture background, pauseButtonTexture, slingTexture;
    
    // Core Bodies
    protected Body groundBody, pauseButtonBody, dummyBody, slingBody;
    protected Body birdObj;
    
    // Joint logic
    protected MouseJoint mouseJoint;
    protected boolean birdFired = false;
    protected Vector2 dragStart = new Vector2();
    
    // Tracking lists for generic rendering
    protected List<Body> targetBodies = new ArrayList<>();
    protected List<Body> structureBodies = new ArrayList<>();
    protected List<Body> triangleBodies = new ArrayList<>();
    protected Texture targetTexture, structureTexture, triangleTexture;
    
    // Trail effect
    protected static class TrailPoint {
        public Vector2 position;
        public float spawnTime;
        public TrailPoint(Vector2 position, float spawnTime) {
            this.position = position;
            this.spawnTime = spawnTime;
        }
    }
    protected List<TrailPoint> flightPath = new ArrayList<>();
    private float stateTime = 0f;
    private int frameCount = 0;
    
    // Level tracking
    protected int levelId;
    
    public BaseLevelScreen(Main game, int levelId, Texture projectileTexture, Texture targetTexture, Texture structureTexture) {
        this.game = game;
        this.levelId = levelId;
        this.targetTexture = targetTexture;
        this.structureTexture = structureTexture;
        
        camera = new OrthographicCamera();
        // Updated to 854 width to natively support 16:9 aspect ratios without black side bars
        viewport = new com.badlogic.gdx.utils.viewport.FitViewport(854 / 100f, 480 / 100f, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();

        // Load common textures from AssetManager
        background = game.assets.get("levelBackground.png", Texture.class);
        pauseButtonTexture = game.assets.get("pausebutton.png", Texture.class);
        slingTexture = game.assets.get("slingshot.png", Texture.class);
        triangleTexture = game.assets.get("woodtriangle.png", Texture.class);

        world = new World(new Vector2(0, -11.0f), true);
        debugRenderer = new Box2DDebugRenderer();

        createGroundBody();
        createSlingshotBody();
        createPauseButton();
        
        BodyDef dummyBodyDef = new BodyDef();
        dummyBodyDef.type = BodyDef.BodyType.StaticBody;
        dummyBody = world.createBody(dummyBodyDef);
        
        createProjectile(projectileTexture);
        
        setupInputProcessor();
        setupContactListener();
    }
    
    protected void createGroundBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(viewport.getWorldWidth() / 2, 0.904f); // Shifted down by ~2% of screen height
        groundBody = world.createBody(bodyDef);
        EdgeShape groundShape = new EdgeShape();
        groundShape.set(-viewport.getWorldWidth() / 2, 0, viewport.getWorldWidth() / 2, 0);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundShape;
        fixtureDef.friction = 0.09f; // Increased by 1.8X from 0.05f
        groundBody.createFixture(fixtureDef);
        groundShape.dispose();
    }
    
    protected void createSlingshotBody() {
        BodyDef slingBodyDef = new BodyDef();
        slingBodyDef.type = BodyDef.BodyType.StaticBody;
        slingBodyDef.position.set(viewport.getWorldWidth() / 4.5f - viewport.getWorldWidth() * 0.06f, 1.4f); // Pushed back 6% total 
        slingBody = world.createBody(slingBodyDef);
    }
    
    protected void createPauseButton() {
        BodyDef buttonBodyDef = new BodyDef();
        buttonBodyDef.type = BodyDef.BodyType.StaticBody;
        buttonBodyDef.position.set(0.5f, viewport.getWorldHeight() - 0.5f);
        pauseButtonBody = world.createBody(buttonBodyDef);
        PolygonShape buttonShape = new PolygonShape();
        buttonShape.setAsBox(0.5f, 0.5f);
        FixtureDef buttonFixtureDef = new FixtureDef();
        buttonFixtureDef.shape = buttonShape;
        buttonFixtureDef.isSensor = true;
        pauseButtonBody.createFixture(buttonFixtureDef);
        buttonShape.dispose();
    }
    
    protected void createProjectile(Texture tex) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Start static
        
        // Calculate the exact crotch of the scaled slingshot
        float slingScale = 7.2f; // Visual multiplier (increased by 20% from 6.0)
        float slingHeight = (slingTexture.getHeight() / 100f / 30f) * slingScale;
        float crotchOffset = slingHeight / 2.2f;
        
        bodyDef.position.set(slingBody.getPosition().x, slingBody.getPosition().y + crotchOffset);
        birdObj = world.createBody(bodyDef);
        birdObj.setUserData(tex);

        PolygonShape shape = new PolygonShape();
        float birdScale = (levelId == 2) ? 2.2f : 1.1f; // 2.2X size for Bomb bird, 1.1x for Red bird
        float hw = (tex.getWidth() / 2f / 100f / 30f) * birdScale;
        float hh = (tex.getHeight() / 2f / 100f / 30f) * birdScale;
        shape.setAsBox(hw, hh);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 2.8f; // Tweaked to 2.8f as requested
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.2f;

        birdObj.createFixture(fixtureDef);
        birdObj.setFixedRotation(true); // Don't spin while dragging
        shape.dispose();
    }
    
    protected void addTarget(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body b = world.createBody(bodyDef);
        b.setUserData("target");
        
        PolygonShape shape = new PolygonShape();
        float hw = targetTexture.getWidth() / 2f / 100f / 20.87f; // 15% larger than 24f
        float hh = targetTexture.getHeight() / 2f / 100f / 20.87f;
        shape.setAsBox(hw, hh);
        
        FixtureDef fDef = new FixtureDef();
        fDef.shape = shape;
        fDef.density = 1f;
        fDef.friction = 0.5f;
        fDef.restitution = 0.2f;
        
        b.createFixture(fDef);
        shape.dispose();
        targetBodies.add(b);
    }
    
    protected void addStructure(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body b = world.createBody(bodyDef);
        b.setUserData("structure");
        
        PolygonShape shape = new PolygonShape();
        float hw = structureTexture.getWidth() / 2f / 100f / 3.636f; // 10% larger than 4f
        float hh = structureTexture.getHeight() / 2f / 100f / 3.636f;
        shape.setAsBox(hw, hh);
        
        FixtureDef fDef = new FixtureDef();
        fDef.shape = shape;
        fDef.density = 1f;
        fDef.friction = 0.5f;
        fDef.restitution = 0.2f;
        
        b.createFixture(fDef);
        shape.dispose();
        structureBodies.add(b);
    }
    
    protected void addTriangle(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.angle = (float) (Math.PI / 2f); // Rotate 90 CCW to form a ramp facing left
        Body b = world.createBody(bodyDef);
        b.setUserData("structure"); // Use same damage alias
        
        PolygonShape shape = new PolygonShape();
        float hw = triangleTexture.getWidth() / 2f / 100f / 4f; 
        float hh = triangleTexture.getHeight() / 2f / 100f / 4f;
        // Vertices for right-angle at bottom-left in local/texture space
        shape.set(new float[] { -hw, -hh,  hw, -hh,  -hw, hh });
        
        FixtureDef fDef = new FixtureDef();
        fDef.shape = shape;
        fDef.density = 1f;
        fDef.friction = 0.5f;
        fDef.restitution = 0.2f;
        
        b.createFixture(fDef);
        shape.dispose();
        triangleBodies.add(b);
    }
    
    private void setupContactListener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fA = contact.getFixtureA();
                Fixture fB = contact.getFixtureB();
                
                if (fA.getBody() == birdObj && fB.getBody().getUserData() instanceof String) {
                    fB.getBody().setUserData("destroy");
                }
                if (fB.getBody() == birdObj && fA.getBody().getUserData() instanceof String) {
                    fA.getBody().setUserData("destroy");
                }
                
                if (fA.getBody() == groundBody && "target".equals(fB.getBody().getUserData())) {
                    fB.getBody().setUserData("destroy");
                }
                if (fB.getBody() == groundBody && "target".equals(fA.getBody().getUserData())) {
                    fA.getBody().setUserData("destroy");
                }
            }
            @Override
            public void endContact(Contact contact) {}
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }
    
    private void setupInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));
                
                for (Fixture f : pauseButtonBody.getFixtureList()) {
                    if (f.testPoint(worldCoords)) {
                        game.setScreen(new PauseScreen(game, levelId));
                        return true;
                    }
                }
                
                if (!birdFired && birdObj.getFixtureList().first().testPoint(worldCoords)) {
                    birdObj.setType(BodyDef.BodyType.DynamicBody);
                    MouseJointDef def = new MouseJointDef();
                    def.bodyA = dummyBody;
                    def.bodyB = birdObj;
                    def.collideConnected = true;
                    def.target.set(worldCoords);
                    def.maxForce = 1000.0f * birdObj.getMass();
                    mouseJoint = (MouseJoint) world.createJoint(def);
                    dragStart.set(worldCoords);
                    return true;
                }
                return false;
            }
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (mouseJoint != null) {
                    Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));
                    if (worldCoords.dst(dragStart) <= 1.10f) { // Increased physical drag bound by 2% buffer 
                        mouseJoint.setTarget(worldCoords);
                    }
                }
                return true;
            }
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (mouseJoint != null && !birdFired) {
                    Vector2 releasePos = viewport.unproject(new Vector2(screenX, screenY));
                    Vector2 dragDir = dragStart.sub(releasePos);
                    // Base 5.2f speed multiplier scaling the slingshot band
                    birdObj.setLinearVelocity(dragDir.scl(5.2f * 1.09f)); 
                    birdObj.setFixedRotation(false); // Let it tumble
                    world.destroyJoint(mouseJoint);
                    mouseJoint = null;
                    birdFired = true;
                }
                return true;
            }
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    game.setScreen(new PauseScreen(game, levelId));
                    return true;
                }
                return false;
            }
        });
    }

    private float stoppedTime = 0f;

    @Override
    public void render(float delta) {
        stateTime += delta;
        // Dark brown dirt color for the bottom gap
        Gdx.gl.glClearColor(0.2f, 0.1f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        
        // Original Background Color
        game.batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        // Reset background
        float yOffset = 0.0f;
        game.batch.draw(background, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2 + yOffset, camera.viewportWidth, camera.viewportHeight);
        
        // Darker Slingshot
        game.batch.setColor(0.5f, 0.3f, 0.1f, 1.0f);
        float sW = (slingTexture.getWidth() / 100f / 30f) * 7.2f; // Increased by 20%
        float sH = (slingTexture.getHeight() / 100f / 30f) * 7.2f;
        game.batch.draw(slingTexture, slingBody.getPosition().x - sW / 2, slingBody.getPosition().y - sH / 2, sW, sH);
        
        Texture projTex = (Texture)birdObj.getUserData();
        
        // Draw Temporary Translucent Trail
        if (levelId == 2) {
            game.batch.setColor(0.1f, 0.1f, 0.1f, 0.35f); // Black trail
        } else {
            game.batch.setColor(1.0f, 0.1f, 0.1f, 0.35f); // Red trail
        }
        
        for (int i = flightPath.size() - 1; i >= 0; i--) {
            TrailPoint pt = flightPath.get(i);
            if (stateTime - pt.spawnTime > 0.5f) {
                flightPath.remove(i);
            } else {
                float bw = projTex.getWidth() / 100f / 30f * 0.3f; // Smaller trail size
                float bh = projTex.getHeight() / 100f / 30f * 0.3f;
                game.batch.draw(projTex, pt.position.x - bw / 2, pt.position.y - bh / 2, bw, bh);
            }
        }
        
        // Draw Projectile
        game.batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        float drawScale = (levelId == 2) ? (30f / 2.2f) : (30f / 1.1f);
        drawBody(birdObj, projTex, drawScale);
        
        // Reset Color to normal for everything else
        game.batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        for (int i = structureBodies.size() - 1; i >= 0; i--) {
            Body b = structureBodies.get(i);
            if ("destroy".equals(b.getUserData())) {
                world.destroyBody(b);
                structureBodies.remove(i);
            } else {
                drawBody(b, structureTexture, 3.636f); // Size adjusted by 10%
            }
        }
        
        for (int i = triangleBodies.size() - 1; i >= 0; i--) {
            Body b = triangleBodies.get(i);
            if ("destroy".equals(b.getUserData())) {
                world.destroyBody(b);
                triangleBodies.remove(i);
            } else {
                drawBody(b, triangleTexture, 4f); 
            }
        }
        
        for (int i = targetBodies.size() - 1; i >= 0; i--) {
            Body b = targetBodies.get(i);
            if ("destroy".equals(b.getUserData())) {
                world.destroyBody(b);
                targetBodies.remove(i);
            } else {
                drawBody(b, targetTexture, 20.87f); // Size adjusted by 15% (24f -> 20.87f)
            }
        }
        
        game.batch.draw(pauseButtonTexture, pauseButtonBody.getPosition().x - 0.5f, pauseButtonBody.getPosition().y, 0.5f, 0.5f);

        game.batch.end();

        if (targetBodies.isEmpty()) {
            game.setScreen(new GameOver(game, levelId, true)); // Won
        } else if (birdFired && birdObj != null) {
            if (birdObj.getLinearVelocity().len() < 0.5f) {
                stoppedTime += delta;
                if (stoppedTime > 3.0f) { // Waited 3 seconds after stopping
                    game.setScreen(new GameOver(game, levelId, false)); // Lost
                }
            } else {
                stoppedTime = 0f; // Reset if it moves again
            }
        }

        // Slightly slower physics (80% of normal speed)
        world.step((1 / 60f) * 0.8f, 6, 2);
        
        if (birdFired && birdObj != null) {
            frameCount++;
            if (frameCount % 4 == 0) {
                if (flightPath.size() > 50) flightPath.remove(0); // Memory leak fix limit 
                flightPath.add(new TrailPoint(birdObj.getPosition().cpy(), stateTime));
            }
        }
    }
    
    private void drawBody(Body b, Texture tex, float scaleFactor) {
        if (b == null || tex == null) return;
        float bw = tex.getWidth() / 100f / scaleFactor;
        float bh = tex.getHeight() / 100f / scaleFactor;
        Vector2 pos = b.getPosition();
        game.batch.draw(tex, pos.x - bw / 2, pos.y - bh / 2, bw / 2, bh / 2, bw, bh, 1f, 1f, (float) Math.toDegrees(b.getAngle()), 0, 0, tex.getWidth(), tex.getHeight(), false, false);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }
    
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void show() {}
}
